package CV.ecommerce.dto.request.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdatePassword {

    private String oldPassword;

    private String newPassword;

    private String confirmPassword;

}
