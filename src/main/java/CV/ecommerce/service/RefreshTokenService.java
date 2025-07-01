package CV.ecommerce.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import CV.ecommerce.entity.RefreshToken;
import CV.ecommerce.entity.User;
import CV.ecommerce.exception.AppException;
import CV.ecommerce.repository.RefreshTokenRepository;
import CV.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshToken createRefreshToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(04, "User email not found"));

        // Xóa token cũ nếu tồn tại
        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshTokenRepository::delete);

        // Tạo mới
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));

        return refreshTokenRepository.save(refreshToken);
    }

    public boolean isValid(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(rt -> rt.getExpiryDate().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    public User getUserFromToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new AppException(04, "Refresh token not found"))
                .getUser();
    }

    public void deleteToken(String token) {
        refreshTokenRepository.deleteById(token);
    }

    public void deleteByUser(User user) {
        refreshTokenRepository.findByUser(user)
                .ifPresent(rt -> refreshTokenRepository.deleteById(rt.getToken()));
    }
}
