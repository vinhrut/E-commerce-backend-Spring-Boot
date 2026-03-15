package CV.ecommerce.configuration;

import CV.ecommerce.dto.response.AuthenticationResponse;
import CV.ecommerce.dto.response.UserResponse;
import CV.ecommerce.entity.Cart;
import CV.ecommerce.entity.User;
import CV.ecommerce.enums.Role;
import CV.ecommerce.mapper.UserMapper;
import CV.ecommerce.repository.CartRepository;
import CV.ecommerce.repository.UserRepository;
import CV.ecommerce.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        DefaultOAuth2User principal = (DefaultOAuth2User) oauthToken.getPrincipal();

        String email = principal.getAttribute("email");
        String fullName = principal.getAttribute("name");

        if (email == null || fullName == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Email or full name not found from Google.\"}");
            return;
        }

        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            user = User.builder()
                    .id(UUID.randomUUID().toString())
                    .email(email)
                    .fullName(fullName)
                    .password(new BCryptPasswordEncoder().encode(UUID.randomUUID().toString()))
                    .role(Role.USER)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .lockUser(false)
                    .build();

            userRepository.save(user);

            Cart cart = new Cart();
            cart.setUser(user);
            cart.setCartItems(null);
            cartRepository.save(cart);
        }

        UserResponse userResponse = userMapper.toUserResponse(user);

        AuthenticationResponse authResponse = authenticationService.authenticateByGoogle(user);

        authResponse.setUserResponse(userResponse);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), authResponse);
    }
}
