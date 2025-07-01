package CV.ecommerce.dto.response;

import CV.ecommerce.enums.Role;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

    private String id;

    private String fullName;

    private String email;

    private String phone;

    private Role role;

}
