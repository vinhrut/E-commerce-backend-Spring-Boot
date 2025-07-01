package CV.ecommerce.controller;

import CV.ecommerce.dto.request.user.*;
import CV.ecommerce.dto.response.APIResponse;
import CV.ecommerce.dto.response.UserResponse;
import CV.ecommerce.entity.User;
import CV.ecommerce.enums.ChangePassStatus;
import CV.ecommerce.mapper.UserMapper;
import CV.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final UserMapper userMapper;

  @PostMapping
  public APIResponse<UserResponse> createUser(@RequestBody UserCreate request) {
    return new APIResponse<>(1000, "Create user success !",
        userMapper.toUserResponse(userService.createUser(request)));
  }

  @PutMapping("/lock/{id}")
  @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
  public APIResponse<UserResponse> lockUser(@PathVariable String id) {
    UserResponse lockedUser = userMapper.toUserResponse(userService.lockUser(id));
    if (lockedUser == null) {
      return new APIResponse<>(1001, "User not found !", null);
    }
    return new APIResponse<>(1000, "Lock user success !", lockedUser);
  }

  @PutMapping("/unlock/{id}")
  @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
  public APIResponse<UserResponse> unlockUser(@PathVariable String id) {
    UserResponse unlockedUser = userMapper.toUserResponse(userService.unlockUser(id));
    if (unlockedUser == null) {
      return new APIResponse<>(1001, "User not found !", null);
    }
    return new APIResponse<>(1000, "Unlock user success !", unlockedUser);
  }

  @GetMapping
  @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
  public APIResponse<List<UserResponse>> getAllUser() {
    List<User> users = userService.getAllUser();
    if (users == null || users.isEmpty()) {
      return new APIResponse<>(1001, "No user available !", null);
    }

    List<UserResponse> userResponses = new ArrayList<>();
    for (User user : users) {
      userResponses.add(userMapper.toUserResponse(user));
    }
    return new APIResponse<>(1000, "Get all user success !", userResponses);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
  public APIResponse<UserResponse> getUserById(@PathVariable String id) {
    UserResponse user = userMapper.toUserResponse(userService.findUserById(id));
    if (user == null) {
      return new APIResponse<>(1001, "User's id not found !", null);
    }
    return new APIResponse<>(1000, "Get user by id success !", user);
  }

  @GetMapping("/email/{email}")
  @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
  public APIResponse<UserResponse> getUserByEmail(@PathVariable String email) {
    UserResponse user = userMapper.toUserResponse(userService.findUserByEmail(email));
    if (user == null) {
      return new APIResponse<>(1001, "User's email not found !", null);
    }
    return new APIResponse<>(1000, "Get user by email success !", user);
  }

  @GetMapping("/phone/{phone}")
  @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
  public APIResponse<UserResponse> getUserByPhone(@PathVariable String phone) {
    UserResponse user = userMapper.toUserResponse(userService.findUserByPhone(phone));
    if (user == null) {
      return new APIResponse<>(1001, "User's phone number not found !", null);
    }
    return new APIResponse<>(1000, "Get user by phone number success !", user);
  }

  @PutMapping("/updateUserProfile/{id}")
  @PreAuthorize("#id == authentication.name or hasAuthority('ADMIN')")
  public APIResponse<UserResponse> updateUserProfile(@PathVariable String id,
      @RequestBody UserUpdateProfile update) {
    UserResponse user = userMapper.toUserResponse(userService.updateUserProfile(id, update));
    if (user == null) {
      return new APIResponse<>(1001, "User not found !", null);
    }
    return new APIResponse<>(1000, "Update user success !", user);
  }

  @PutMapping("/updateUserPassword/{id}")
  @PreAuthorize("#id == authentication.name or hasAuthority('ADMIN')")
  public APIResponse<UserResponse> updateUserPassword(@PathVariable String id,
      @RequestBody UserUpdatePassword update) {
    ChangePassStatus status = userService.updateUserPassword(id, update);

    return switch (status) {
      case Success -> new APIResponse<>(1000, "Change password success !", null);
      case Fail -> new APIResponse<>(1001, "User not found !", null);
      case NotMatch -> new APIResponse<>(1002, "Password not match !", null);
      default -> new APIResponse<>(1003, "Error !", null);
    };
  }

  @PutMapping("/email/{id}")
  @PreAuthorize("#id == authentication.name or hasAuthority('ADMIN')")
  public APIResponse<String> updateUserEmail(@PathVariable String id,
      @RequestBody UserUpdateEmail email) {
    String result = userService.updateEmail(id, email);
    if (result == null) {
      return new APIResponse<>(1001, "User not found !", null);
    }
    return new APIResponse<>(1000, "Update email success !", result);
  }

  /////////////// PAGE /////////////

  @GetMapping("/paged")
  @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
  public APIResponse<Page<UserResponse>> getAllUserPaged(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("fullName").ascending());
    Page<User> userPage = userService.getAllUserPageable(pageable);
    Page<UserResponse> userResponsePage = userPage.map(userMapper::toUserResponse);
    return new APIResponse<>(1000, "Get paginated users success!", userResponsePage);
  }

}
