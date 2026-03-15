package CV.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import CV.ecommerce.entity.RefreshToken;
import CV.ecommerce.entity.User;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);

    // Thêm method này để tìm token theo user
    Optional<RefreshToken> findByUser(User user);
}
