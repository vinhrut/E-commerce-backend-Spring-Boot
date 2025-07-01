package CV.ecommerce.configuration;

import CV.ecommerce.entity.User;
import CV.ecommerce.enums.Role;
import CV.ecommerce.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (!userRepository.existsByRole(Role.ADMIN)) {
                User admin = User.builder()
                        .fullName("Default Admin")
                        .email("admin@ecommerce.com")
                        .password(passwordEncoder.encode("admin"))
                        .phone("0999999999")
                        .role(Role.ADMIN)
                        .lockUser(false)
                        .build();

                userRepository.save(admin);
                log.warn(
                        "Admin created with admin@ecommerce.com & password: admin (default) !! Please change password.");
            }
        };
    }
}
