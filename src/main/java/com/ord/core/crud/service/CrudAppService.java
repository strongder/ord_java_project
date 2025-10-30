package com.ord.core.crud.service;

import com.ord.core.crud.dto.CommonResultDto;
import com.ord.core.crud.dto.EncodedIdDto;
import com.ord.core.crud.dto.PagedResultDto;
import com.ord.core.crud.dto.PagedResultRequestDto;
import com.ord.core.crud.entity.BaseEntity;
import com.ord.core.crud.enums.CommonResultCode;
import com.ord.core.crud.repository.OrdEntityRepository;
import com.ord.core.exception.NotFoundException;
import com.ord.core.exception.OrdBusinessException;
import com.ord.core.security.IdCodec;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service cung cấp các thao tác CRUD cơ bản (Create, Read, Update, Delete)
 * cho các entity trong hệ thống.
 *
 * @param <TEntity>         Kiểu entity trong database
 * @param <TKey>            Kiểu dữ liệu của khóa chính
 * @param <TEntityDto>      Kiểu DTO đầy đủ của entity
 * @param <TGetListInput>   Kiểu input cho việc phân trang
 * @param <TPagedOutputDto> Kiểu DTO cho từng item trong danh sách phân trang
 * @param <TCreateInput>    Kiểu input cho thao tác tạo mới
 * @param <TUpdateInput>    Kiểu input cho thao tác cập nhật
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public abstract class CrudAppService<
        TEntity extends BaseEntity<TKey>,
        TKey,
        TEntityDto,
        TGetListInput extends PagedResultRequestDto,
        TPagedOutputDto extends EncodedIdDto<TKey>,
        TCreateInput,
        TUpdateInput> {

    @Autowired
    protected ModelMapper objectMapper;

    @Autowired
    protected IdCodec idCodec;
    // Thêm vào CrudAppService
    private static final ConcurrentHashMap<Class<?>, Class<?>[]> GENERIC_CACHE = new ConcurrentHashMap<>();

    /**
     * API endpoint: Lấy thông tin entity theo ID đã mã hóa
     *
     * @param encodedId ID đã được mã hóa
     * @return CommonResultDto chứa thông tin entity hoặc thông báo lỗi nếu không tìm thấy
     */
    @GetMapping(path = "/get-by-id/{id}")
    public CommonResultDto<TEntityDto> getByEncodedId(@PathVariable("id") String encodedId) {
        var dto = getEntityDtoByEncodedId(encodedId);

        // Kiểm tra nếu không tìm thấy entity
        if (dto == null) {
            throwNotFound(encodedId);
        }

        return CommonResultDto.success(dto);
    }

    /**
     * Lấy DTO của entity theo ID đã mã hóa
     *
     * @param encodedId ID đã được mã hóa
     * @return DTO của entity hoặc null nếu không tìm thấy
     */
    protected TEntityDto getEntityDtoByEncodedId(String encodedId) {
        var entity = findEntityByEncodedId(encodedId);

        if (entity == null) {
            return null;
        }

        // Chuyển đổi entity sang DTO
        return convertEntityToDto(entity);
    }

    /**
     * Tìm entity trong database theo ID đã mã hóa
     *
     * @param encodedId ID đã được mã hóa
     * @return Entity tìm được hoặc null nếu không tồn tại
     */
    protected TEntity findEntityByEncodedId(String encodedId) {
        // Giải mã ID từ chuỗi encoded sang kiểu thực
        TKey realId = decodeId(encodedId);

        return getRepository().findById(realId)
                .orElse(null);
    }

    /**
     * API endpoint: Lấy danh sách entity có phân trang
     *
     * @param input Tham số đầu vào cho việc phân trang (số trang, kích thước trang, filter, sort)
     * @return Kết quả phân trang với danh sách entity và tổng số bản ghi
     */
    @PostMapping(path = "/get-paged", produces = "application/json")
    public PagedResultDto<TPagedOutputDto> getPagedList(
            @RequestBody TGetListInput input) {
        // Kiểm tra quyền truy cập
        checkGetPagedListPolicy();

        // Lấy dữ liệu phân trang
        var result = buildPagedResult(input);

        // Mã hóa ID cho tất cả các item trong kết quả
        encodeIdsInPagedResult(result);

        return result;
    }

    /**
     * Xây dựng kết quả phân trang dựa trên input
     * Có thể sử dụng Specification hoặc custom query
     *
     * @param input Tham số phân trang
     * @return Kết quả phân trang
     */
    @SuppressWarnings("unchecked")
    protected PagedResultDto<TPagedOutputDto> buildPagedResult(TGetListInput input) {
        var pageRequest = input.getPageRequest();

        // Tạo specification cho query (nếu có)
        var spec = buildSpecificationForPaging(input);

        // Nếu có specification, sử dụng findAll với spec
        if (spec != null) {
            var pageResult = getRepository().findAll(spec, pageRequest);
            return convertPageToPagedDto(pageResult);
        }

        // Nếu không có specification, sử dụng custom logic
        // Lấy tổng số bản ghi
        var total = getTotalCount(input);

        // Nếu không có dữ liệu, trả về empty result
        if (total == 0) {
            return PagedResultDto.empty();
        }

        // Lấy danh sách items cho trang hiện tại
        var items = fetchPagedItems(input);

        return new PagedResultDto<>(total, items);
    }

    /**
     * Xây dựng Specification để filter và query dữ liệu
     * Method này có thể được override để implement logic filter phức tạp
     *
     * @param input Tham số phân trang với các điều kiện filter
     * @return Specification hoặc null nếu không sử dụng
     */
    protected Specification<TEntity> buildSpecificationForPaging(TGetListInput input) {
        return null;
    }

    /**
     * Tính tổng số bản ghi thỏa mãn điều kiện
     * Method này được sử dụng khi không dùng Specification
     *
     * @param input Tham số phân trang
     * @return Tổng số bản ghi
     */
    protected Integer getTotalCount(TGetListInput input) {
        return 0;
    }

    /**
     * Lấy danh sách items cho trang hiện tại
     * Method này được sử dụng khi không dùng Specification
     *
     * @param input Tham số phân trang
     * @return Danh sách items
     */
    protected List<TPagedOutputDto> fetchPagedItems(TGetListInput input) {
        return Collections.emptyList();
    }

    /**
     * Chuyển đổi Spring Page thành PagedResultDto
     *
     * @param page Spring Data Page object
     * @return DTO kết quả phân trang
     */
    protected PagedResultDto<TPagedOutputDto> convertPageToPagedDto(Page<TEntity> page) {
        if (page == null) {
            return new PagedResultDto<>(0, new ArrayList<>());
        }

        // Nếu không có dữ liệu
        if (page.getContent().isEmpty()) {
            return new PagedResultDto<>(page.getTotalElements(), new ArrayList<>());
        }

        // Chuyển đổi từng entity sang DTO
        var items = page.getContent().stream()
                .map(this::convertEntityToPagedDto)
                .toList();

        return new PagedResultDto<>(page.getTotalElements(), items);
    }

    /**
     * Mã hóa ID cho tất cả các item trong kết quả phân trang
     * Để bảo mật, ID thực sẽ được mã hóa trước khi trả về client
     *
     * @param pagedResult Kết quả phân trang cần mã hóa ID
     */
    protected void encodeIdsInPagedResult(PagedResultDto<TPagedOutputDto> pagedResult) {
        if (pagedResult.getItems() != null && !pagedResult.getItems().isEmpty()) {
            for (TPagedOutputDto item : pagedResult.getItems()) {
                // Mã hóa ID thực thành chuỗi encoded
                item.setEncodedId(encodeId(item.getId()));
            }
        }
    }

    /**
     * API endpoint: Tạo mới một entity
     *
     * @param createInput Dữ liệu đầu vào để tạo entity
     * @return Kết quả tạo mới với thông tin entity đã tạo
     */
    @Transactional(rollbackFor = {OrdBusinessException.class, Throwable.class, Exception.class})
    @PostMapping(path = "/create", produces = "application/json")
    public CommonResultDto<TEntityDto> create(@RequestBody @Valid TCreateInput createInput) {
        // Validate input không được null
        if (createInput == null) {
            return CommonResultDto.fail(CommonResultCode.BAD_REQUEST, "CrudErr.InputNull");
        }

        // Kiểm tra quyền tạo mới
        checkCreatePolicy();

        // Validation logic tùy chỉnh trước khi tạo
        validationBeforeCreate(createInput);

        // Chuyển đổi CreateInput sang Entity
        var newEntity = convertCreateInputToEntity(createInput);

        // Lưu entity vào database
        var repository = getRepository();
        newEntity = repository.saveAndFlush(newEntity);

        // Xử lý logic sau khi tạo thành công (nếu có)
        handleAfterCreate(newEntity, createInput);

        // Map entity mới tạo trở lại input để cập nhật các trường generated (như ID)
        objectMapper.map(newEntity, createInput);

        return CommonResultDto.success(
                convertCreatedEntityToDto(newEntity, createInput),
                "CreateSuccessfully"
        );
    }

    /**
     * API endpoint: Cập nhật một entity theo ID
     *
     * @param encodedId   ID đã mã hóa của entity cần cập nhật
     * @param updateInput Dữ liệu cập nhật
     * @return Kết quả cập nhật với thông tin entity đã cập nhật
     */
    @Transactional(rollbackFor = {OrdBusinessException.class, Throwable.class, Exception.class})
    @PostMapping(path = "/update/{id}", produces = "application/json")
    public CommonResultDto<TEntityDto> update(
            @PathVariable("id") String encodedId,
            @RequestBody @Valid TUpdateInput updateInput) {
        // Validate input không được null
        if (updateInput == null) {
            return CommonResultDto.fail(CommonResultCode.BAD_REQUEST, "CrudErr.InputNull");
        }

        // Kiểm tra quyền cập nhật
        checkUpdatePolicy();

        // Tìm entity cần cập nhật
        var entityToUpdate = findEntityByEncodedId(encodedId);
        if (entityToUpdate == null) {
            throwNotFound(encodedId);
        }
        // Validation logic tùy chỉnh trước khi cập nhật
        validationBeforeUpdate(updateInput, entityToUpdate);

        // Áp dụng các thay đổi từ input vào entity
        applyUpdateToEntity(updateInput, entityToUpdate);
        // gán lại id
        entityToUpdate.setId(decodeId(encodedId));

        // Lưu entity đã cập nhật
        var repository = getRepository();
        repository.save(entityToUpdate);

        // Xử lý logic sau khi cập nhật thành công (nếu có)
        handleAfterUpdate(entityToUpdate, updateInput);

        // Map entity đã cập nhật trở lại input
        objectMapper.map(entityToUpdate, updateInput);

        return CommonResultDto.success(
                convertUpdatedEntityToDto(entityToUpdate, updateInput),
                "UpdateSuccessfully"
        );
    }

    /**
     * API endpoint: Xóa một entity theo ID
     *
     * @param encodedId ID đã mã hóa của entity cần xóa
     * @return Kết quả xóa với thông tin entity đã xóa
     */
    @Transactional(rollbackFor = {OrdBusinessException.class, Throwable.class, Exception.class})
    @PostMapping(path = "/remove/{id}", produces = "application/json")
    public CommonResultDto<TEntityDto> remove(@PathVariable("id") String encodedId) {
        // Kiểm tra quyền xóa
        checkRemovePolicy();

        // Tìm entity cần xóa
        var entityToRemove = findEntityByEncodedId(encodedId);
        if (entityToRemove == null) {
            throwNotFound(encodedId);
        }

        // Validation logic trước khi xóa (kiểm tra ràng buộc, dependencies, etc.)
        validationBeforeRemove(entityToRemove);

        // Xóa entity khỏi database
        var repository = getRepository();
        repository.delete(entityToRemove);

        // Xử lý logic sau khi xóa thành công (nếu có)
        handleAfterRemove(entityToRemove);

        return CommonResultDto.success(
                convertRemovedEntityToDto(entityToRemove),
                "RemoveSuccessfully"
        );
    }

    protected String getGetPagedListPolicy() {
        return null;
    }

    protected String getCreatePolicy() {
        return null;
    }

    protected String getUpdatePolicy() {
        return null;
    }

    protected String getRemovePolicy() {
        return null;
    }

    protected String getPolicyForAction(String action) {
        return getEntityName() + "." + action;
    }
    // ==================== Policy Check Methods ====================
    // Các method này có thể được override để implement authorization logic

    /**
     * Kiểm tra quyền truy cập danh sách phân trang
     * Override method này để implement logic phân quyền
     */
    protected void checkGetPagedListPolicy() {
        var policy = getGetPagedListPolicy();
        if (policy != null) {
            hasRole(policy);
        }
    }

    /**
     * Kiểm tra quyền tạo mới
     * Override method này để implement logic phân quyền
     */
    protected void checkCreatePolicy() {
        var policy = getCreatePolicy();
        if (policy != null) {
            hasRole(policy);
        }
    }

    /**
     * Kiểm tra quyền cập nhật
     * Override method này để implement logic phân quyền
     */
    protected void checkUpdatePolicy() {
        var policy = getUpdatePolicy();
        if (policy != null) {
            hasRole(policy);
        }
    }

    /**
     * Kiểm tra quyền xóa
     * Override method này để implement logic phân quyền
     */
    protected void checkRemovePolicy() {
        var policy = getRemovePolicy();
        if (policy != null) {
            hasRole(policy);
        }
    }

    // ==================== Validation Methods ====================
    // Các method này có thể được override để implement validation logic

    /**
     * Validation logic tùy chỉnh trước khi tạo entity
     * Override để implement business rules validation
     *
     * @param createInput Dữ liệu đầu vào cần validate
     */
    protected void validationBeforeCreate(TCreateInput createInput) {
    }

    /**
     * Validation logic tùy chỉnh trước khi cập nhật entity
     * Override để implement business rules validation
     *
     * @param updateInput    Dữ liệu cập nhật cần validate
     * @param entityToUpdate Entity đang được cập nhật
     */
    protected void validationBeforeUpdate(TUpdateInput updateInput, TEntity entityToUpdate) {
    }

    /**
     * Validation logic tùy chỉnh trước khi xóa entity
     * Override để kiểm tra dependencies, constraints, etc.
     *
     * @param entityToRemove Entity cần được xóa
     */
    protected void validationBeforeRemove(TEntity entityToRemove) {
    }

    protected void hasRole(String role) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Người dùng không có quyền truy cập ");
        }
        boolean hasRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(r -> r.equalsIgnoreCase(role));
        if (!hasRole) {
            throw new AccessDeniedException("Người dùng không có quyền truy cập ");
        }
    }
    // ==================== Mapping Methods ====================
    // Các method chuyển đổi giữa Entity và DTO

    /**
     * Chuyển đổi Entity sang DTO cho danh sách phân trang
     *
     * @param entity Entity cần chuyển đổi
     * @return DTO cho output phân trang
     */
    protected TPagedOutputDto convertEntityToPagedDto(TEntity entity) {
        try {
            return objectMapper.map(entity, getPagedOutputDtoClass());
        } catch (Exception e) {
            log.error("Error mapping entity to paged output DTO: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Chuyển đổi CreateInput sang Entity mới
     *
     * @param createInput Input từ client
     * @return Entity mới được tạo
     */
    protected TEntity convertCreateInputToEntity(TCreateInput createInput) {
        return objectMapper.map(createInput, getEntityClass());
    }

    /**
     * Áp dụng dữ liệu từ UpdateInput vào Entity hiện có
     *
     * @param updateInput    Dữ liệu cập nhật
     * @param entityToUpdate Entity cần được cập nhật
     */

    protected void applyUpdateToEntity(TUpdateInput updateInput, TEntity entityToUpdate) {
        objectMapper.map(updateInput, entityToUpdate);
    }

    /**
     * Chuyển đổi Entity sang EntityDto đầy đủ
     *
     * @param entity Entity cần chuyển đổi
     * @return DTO đầy đủ của entity
     */
    protected TEntityDto convertEntityToDto(TEntity entity) {
        return objectMapper.map(entity, getEntityDtoClass());
    }

    /**
     * Chuyển đổi Entity vừa tạo sang DTO để trả về
     *
     * @param entity      Entity vừa được tạo
     * @param createInput Input đã sử dụng để tạo
     * @return DTO kết quả
     */
    protected TEntityDto convertCreatedEntityToDto(TEntity entity, TCreateInput createInput) {
        return objectMapper.map(entity, getEntityDtoClass());
    }

    /**
     * Chuyển đổi Entity vừa cập nhật sang DTO để trả về
     *
     * @param entity      Entity vừa được cập nhật
     * @param updateInput Input đã sử dụng để cập nhật
     * @return DTO kết quả
     */
    protected TEntityDto convertUpdatedEntityToDto(TEntity entity, TUpdateInput updateInput) {
        return objectMapper.map(entity, getEntityDtoClass());
    }

    /**
     * Chuyển đổi Entity vừa xóa sang DTO để trả về
     *
     * @param entity Entity vừa được xóa
     * @return DTO kết quả
     */
    protected TEntityDto convertRemovedEntityToDto(TEntity entity) {
        return objectMapper.map(entity, getEntityDtoClass());
    }

    // ==================== Lifecycle Hook Methods ====================
    // Các method được gọi sau khi hoàn thành thao tác CRUD

    /**
     * Hook method được gọi sau khi tạo entity thành công
     * Override để implement logic bổ sung như: gửi notification, log audit, etc.
     *
     * @param createdEntity Entity vừa được tạo
     * @param createInput   Input đã sử dụng để tạo
     */
    protected void handleAfterCreate(TEntity createdEntity, TCreateInput createInput) {
    }

    /**
     * Hook method được gọi sau khi cập nhật entity thành công
     * Override để implement logic bổ sung như: gửi notification, log audit, etc.
     *
     * @param updatedEntity Entity vừa được cập nhật
     * @param updateInput   Input đã sử dụng để cập nhật
     */
    protected void handleAfterUpdate(TEntity updatedEntity, TUpdateInput updateInput) {
    }

    /**
     * Hook method được gọi sau khi xóa entity thành công
     * Override để implement logic bổ sung như: cleanup, gửi notification, log audit, etc.
     *
     * @param removedEntity Entity vừa được xóa
     */
    protected void handleAfterRemove(TEntity removedEntity) {
    }

    // ==================== ID Encoding/Decoding Methods ====================

    /**
     * Giải mã ID từ chuỗi encoded sang kiểu dữ liệu thực
     *
     * @param encodedId ID đã được mã hóa dạng string
     * @return ID thực với kiểu TKey
     */
    protected TKey decodeId(String encodedId) {
        return idCodec.decode(encodedId, getKeyClassFromRepository());
    }

    /**
     * Mã hóa ID từ kiểu dữ liệu thực sang chuỗi
     *
     * @param id ID thực cần mã hóa
     * @return Chuỗi ID đã được mã hóa
     */
    protected String encodeId(TKey id) {
        return idCodec.encode(id);
    }


    // ================== throw Ex =========================
    protected void throwBusiness(String msg) {
        throw new OrdBusinessException(msg);
    }

    protected void throwNotFound(String id) {
        throw new NotFoundException().withEntityName(getEntityName())
                .withId(id);
    }

    @SuppressWarnings("unchecked")
    private Class<?>[] getGenericTypes() {
        return GENERIC_CACHE.computeIfAbsent(getClass(), cls ->
                GenericTypeResolver.resolveTypeArguments(cls, CrudAppService.class)
        );
    }

    /**
     * Lấy class của khóa chính từ repository
     *
     * @return Class của khóa chính
     */
    private Class<TKey> getKeyClassFromRepository() {
        // Dùng generic type cache luôn để tối ưu
        return (Class<TKey>) getGenericTypes()[1];
    }

    /**
     * Lấy class của DTO output cho phân trang
     *
     * @return Class của PagedOutputDto
     */
    protected Class<TPagedOutputDto> getPagedOutputDtoClass() {
        return (Class<TPagedOutputDto>) getGenericTypes()[4];
    }

    /**
     * Lấy class của Entity
     *
     * @return Class của Entity
     */
    protected Class<TEntity> getEntityClass() {
        return (Class<TEntity>) getGenericTypes()[0];
    }

    /**
     * Lấy class của EntityDto
     *
     * @return Class của EntityDto
     */
    protected Class<TEntityDto> getEntityDtoClass() {
        return (Class<TEntityDto>) getGenericTypes()[2];
    }

    // ==================== Abstract Methods ====================
    // Các method bắt buộc phải implement trong class con

    /**
     * Lấy repository tương ứng với entity
     *
     * @return Repository của entity
     */
    protected abstract OrdEntityRepository<TEntity, TKey> getRepository();

    protected abstract String getEntityName();
}