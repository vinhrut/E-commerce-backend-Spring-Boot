package CV.ecommerce.configuration;

import CV.ecommerce.dto.response.APIResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
        private final ObjectMapper objectMapper;

        private static final String[] PUBLIC_ENDPOINTS = {
                        "/api/users",
                        "/api/auth/login",
                        "/api/auth/refresh-token",
                        "/api/payment/vnpay-return"
        };

        private static final String[] PUBLIC_GET_ENDPOINTS = {
                        "/api/products", // GET tất cả sản phẩm - public
                        "/api/products/**", // GET sản phẩm theo ID - public
                        "/api/image/**" // GET URL ảnh MinIO - public
        };

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(List.of(
                                "http://localhost:5173", // Vite dev server
                                "http://localhost:3000" // CRA dev server (nếu dùng)
                ));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);
                return source;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                                                .requestMatchers(org.springframework.http.HttpMethod.GET, PUBLIC_GET_ENDPOINTS).permitAll()
                                                .anyRequest().authenticated())
                                .oauth2Login(oauth2 -> oauth2
                                                .successHandler(customOAuth2SuccessHandler)
                                                .failureHandler((request, response, exception) -> {
                                                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                                        response.setContentType("application/json");
                                                        response.setCharacterEncoding("UTF-8");
                                                        objectMapper.writeValue(response.getWriter(),
                                                                        new APIResponse<>(401, "Google login failed: "
                                                                                        + exception.getMessage(),
                                                                                        null));
                                                }))
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint((request, response, authException) -> {
                                                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                                        response.setContentType("application/json");
                                                        response.setCharacterEncoding("UTF-8");
                                                        objectMapper.writeValue(response.getWriter(),
                                                                        new APIResponse<>(401,
                                                                                        "Unauthorized - please login",
                                                                                        null));
                                                })
                                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                                        response.setContentType("application/json");
                                                        response.setCharacterEncoding("UTF-8");
                                                        objectMapper.writeValue(response.getWriter(),
                                                                        new APIResponse<>(403,
                                                                                        "Access denied - please login",
                                                                                        null));
                                                }))
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder(10);
        }
}
