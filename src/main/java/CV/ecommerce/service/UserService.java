package CV.ecommerce.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import CV.ecommerce.dto.request.user.UserCreate;
import CV.ecommerce.dto.request.user.UserUpdateEmail;
import CV.ecommerce.dto.request.user.UserUpdatePassword;
import CV.ecommerce.dto.request.user.UserUpdateProfile;
import CV.ecommerce.entity.Cart;
import CV.ecommerce.entity.User;
import CV.ecommerce.enums.ChangePassStatus;
import CV.ecommerce.enums.Role;
import CV.ecommerce.repository.CartRepository;
import CV.ecommerce.repository.UserRepository;
import CV.ecommerce.validation.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    UserValidator userValidator;
    CartRepository cartRepository;

    public User createUser(UserCreate request) {
        userValidator.validateCreate(request);
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(Role.USER);
        user.setLockUser(false);
        userRepository.save(user);

        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setCartItems(List.of());
        cartRepository.save(newCart);

        return user;
    }

    public User lockUser(String id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null)
            return null;

        user.setLockUser(true);

        return userRepository.save(user);
    }

    public User unlockUser(String id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null)
            return null;

        user.setLockUser(false);
        return userRepository.save(user);
    }

    public List<User> getAllUser() {
        List<User> listUser = new ArrayList<>();
        listUser = userRepository.findByLockUser(false);
        return listUser;
    }

    public User findUserById(String id) {
        User user = userRepository.findById(id).orElse(null);
        return user;
    }

    public User findUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        return user;
    }

    public User findUserByPhone(String phone) {
        User user = userRepository.findByPhone(phone).orElse(null);
        return user;
    }

    public User updateUserProfile(String id, UserUpdateProfile update) {
        userValidator.validateUpdateProfile(update);
        User user = userRepository.findById(id).orElse(null);
        if (user == null)
            return null;

        user.setFullName(update.getFullName());
        user.setPhone(update.getPhone());

        return userRepository.save(user);
    }

    public ChangePassStatus updateUserPassword(String id, UserUpdatePassword request) {
        userValidator.validateUpdatePassword(request);
        User user = userRepository.findById(id).orElse(null);
        if (user == null)
            return ChangePassStatus.Fail;

        boolean oldPasswordMatches = passwordEncoder.matches(request.getOldPassword(), user.getPassword());
        boolean newPasswordsMatch = request.getNewPassword().equals(request.getConfirmPassword());

        if (oldPasswordMatches && newPasswordsMatch) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
            return ChangePassStatus.Success;
        }
        return ChangePassStatus.NotMatch;
    }

    public String updateEmail(String id, UserUpdateEmail email) {
        userValidator.validateUpdateEmail(email);
        User user = userRepository.findById(id).orElse(null);
        if (user == null)
            return null;

        user.setEmail(email.getEmail());
        userRepository.save(user);

        return user.getEmail();
    }

    ////////////////////////// PAGE ////////////////////////
    public Page<User> getAllUserPageable(Pageable pageable) {
        return userRepository.findByLockUser(false, pageable);
    }
}
