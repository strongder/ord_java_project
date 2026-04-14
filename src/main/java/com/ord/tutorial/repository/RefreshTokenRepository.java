package com.ord.tutorial.repository;

import com.ord.core.crud.repository.OrdEntityRepository;
import com.ord.tutorial.entity.RefreshToken;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends OrdEntityRepository<RefreshToken, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE refresh_token SET is_revoked = 1 WHERE refresh_token = :token", nativeQuery = true)
    void revokeToken(@Param("token") String token);

    @Query(value = "Select * from refresh_token rt WHERE rt.refresh_token = :token and is_revoked = 0", nativeQuery = true)
    RefreshToken findByToken(@Param("token") String token);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM refresh_token WHERE is_revoked = true AND expired_at < NOW()", nativeQuery = true)
    void deleteExpiredAndRevoked();
}
