1. Sử dụng JPA để viết lại hàm tìm kiếm phân trang WardApiResource ( bổ sung thêm provinceName từ bảng provinces)
2. Thêm đa ngữ cho phần thông báo (message) của api
3. Phát triển tính năng phân quyền :
    - Các permission định nghĩa theo cấu trúc : "province.get-paged", "province.create", "province.update", "province.remove"...
    - Các bảng :
     + roles(id, name)
     + role_permissions(roleId, permissionName)
     + user_roles(roleId, userId)
4. Bổ sung thêm các trường audit cho bảng  provinces, wards
 - Các trường audit thường bao gồm:
    + createdBy
    + createdDate
    + lastModifiedBy
    + lastModifiedDate    
5. Tạo api get data cho dropdown list - (Sử dụng cache). Có cơ chế xóa cache khi insert/update/remove
6. Bổ sung thêm AuditLogFilter để ghi lại thông tin AuditLogDto trong (src\main\java\com\ord\core\logging\audit_log) cho mỗi request theo luồng
App -> Kafka -> logstash -> elastic                  