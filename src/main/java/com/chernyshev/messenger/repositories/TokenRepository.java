package com.chernyshev.messenger.repositories;

import com.chernyshev.messenger.models.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface TokenRepository extends JpaRepository<TokenEntity,Long> {

    @Query("""
            from TokenEntity  t 
            join fetch t.user
            where t.user.id=:userId and (t.expired=false or t.revoked=false)
    """)
    Optional<List<TokenEntity>> findAllValidTokensByUser(Long userId);

    Optional<TokenEntity> findByToken(String token);

}
