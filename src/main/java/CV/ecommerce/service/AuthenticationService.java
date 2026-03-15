package CV.ecommerce.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;

import CV.ecommerce.dto.request.login.AuthenticationRequest;
import CV.ecommerce.dto.response.AuthenticationResponse;
import CV.ecommerce.entity.User;
import CV.ecommerce.exception.AppException;
import CV.ecommerce.mapper.UserMapper;
import CV.ecommerce.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper; // thêm mapper

    @Value("${jwt.signerKey}")
    private String SIGN_KEY;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(04, "User email not found"));

        PasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(04, "pass not match");
        }

        String accessToken = createAccessToken(user.getId(), String.join(" ", user.getRole().toString()));
        String refreshToken = refreshTokenService.createRefreshToken(user.getEmail()).getToken();

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .authenticated(true)
                .roles(user.getRole().toString())
                .userResponse(userMapper.toUserResponse(user))
                .build();
    }

    public AuthenticationResponse authenticateByGoogle(User user) {
        String accessToken = createAccessToken(user.getId(), user.getRole().toString());
        String refreshToken = refreshTokenService.createRefreshToken(user.getEmail()).getToken();

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .authenticated(true)
                .roles(user.getRole().toString())
                .userResponse(userMapper.toUserResponse(user))
                .build();
    }

    public String createAccessToken(String userId, String scope) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(userId)
                    .issuer("ecommerce")
                    .issueTime(new Date())
                    .expirationTime(Date.from(Instant.now().plus(30, ChronoUnit.MINUTES)))
                    .claim("scope", scope)
                    .build();

            JWSObject jwsObject = new JWSObject(header, new Payload(claims.toJSONObject()));
            jwsObject.sign(new MACSigner(SIGN_KEY.getBytes()));
            return jwsObject.serialize();

        } catch (JOSEException e) {
            throw new AppException(04, "Create token fail");
        }
    }
}
