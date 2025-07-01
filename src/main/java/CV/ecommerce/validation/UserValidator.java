package CV.ecommerce.validation;

import CV.ecommerce.dto.request.user.*;
import CV.ecommerce.exception.AppException;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    public void validateCreate(UserCreate request) {
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new AppException(1011, "FULL_NAME_REQUIRED");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new AppException(1012, "EMAIL_REQUIRED");
        }

        if (!request.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new AppException(1013, "EMAIL_INVALID");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new AppException(1014, "PASSWORD_REQUIRED");
        }

        if (request.getPassword().length() < 6 || request.getPassword().length() > 32) {
            throw new AppException(1015, "PASSWORD_LENGTH_INVALID");
        }

        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            throw new AppException(1016, "PHONE_REQUIRED");
        }

        if (!request.getPhone().matches("^\\d{10}$")) {
            throw new AppException(1017, "PHONE_INVALID");
        }
    }

    public void validateUpdateProfile(UserUpdateProfile request) {
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new AppException(1001, "FULL_NAME_REQUIRED");
        }

        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            throw new AppException(1002, "PHONE_REQUIRED");
        }

        if (!request.getPhone().matches("^\\d{10}$")) {
            throw new AppException(1003, "PHONE_INVALID");
        }
    }

    public void validateUpdatePassword(UserUpdatePassword request) {
        if (request.getOldPassword() == null || request.getOldPassword().trim().isEmpty()) {
            throw new AppException(1004, "OLD_PASSWORD_REQUIRED");
        }

        if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
            throw new AppException(1005, "NEW_PASSWORD_REQUIRED");
        }

        if (request.getNewPassword().length() < 6 || request.getNewPassword().length() > 32) {
            throw new AppException(1006, "NEW_PASSWORD_LENGTH_INVALID");
        }

        if (request.getConfirmPassword() == null || request.getConfirmPassword().trim().isEmpty()) {
            throw new AppException(1007, "CONFIRM_PASSWORD_REQUIRED");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(1008, "CONFIRM_PASSWORD_NOT_MATCH");
        }
    }

    public void validateUpdateEmail(UserUpdateEmail request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new AppException(1009, "EMAIL_REQUIRED");
        }

        if (!request.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new AppException(1010, "EMAIL_INVALID");
        }
    }
}
