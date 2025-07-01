package CV.ecommerce.dto.request.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreate {

    String fullName;
    String email;
    String password;
    String phone;
}
