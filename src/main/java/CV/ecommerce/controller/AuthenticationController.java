package CV.ecommerce.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CV.ecommerce.dto.request.login.AuthenticationRequest;
import CV.ecommerce.dto.request.login.RefreshTokenRequest;
import CV.ecommerce.dto.response.APIResponse;
import CV.ecommerce.dto.response.AuthenticationResponse;
import CV.ecommerce.dto.response.UserResponse;
import CV.ecommerce.entity.User;
import CV.ecommerce.mapper.UserMapper;
import CV.ecommerce.service.AuthenticationService;
import CV.ecommerce.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public APIResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        return new APIResponse<>(1000, "Login success !", authenticationService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public APIResponse<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        if (!refreshTokenService.isValid(request.getRefreshToken())) {
            return new APIResponse<>(1001, "Refresh token invalid", null);
        }

        User user = refreshTokenService.getUserFromToken(request.getRefreshToken());
        String newAccessToken = authenticationService.createAccessToken(user.getId(), user.getRole().toString());

        UserResponse userResponse = userMapper.toUserResponse(user);

        AuthenticationResponse authResponse = AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.getRefreshToken())
                .authenticated(true)
                .roles(user.getRole().toString())
                .userResponse(userResponse)
                .build();

        return new APIResponse<>(1000, "Refresh token success !", authResponse);
    }
}
