package CV.ecommerce.mapper;

import org.springframework.stereotype.Component;

import CV.ecommerce.dto.response.UserResponse;
import CV.ecommerce.entity.User;

@Component
public class UserMapper {

    public UserResponse toUserResponse(User user) {
        if (user == null)
            return null;

        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());

        return dto;
    }

}
