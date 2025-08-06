package com.davonlibrary.service;

import com.davonlibrary.entity.User;
import com.davonlibrary.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class UserService {

  @Inject UserRepository userRepository;

  /**
   * Validates if a user object is valid.
   *
   * @param user the user to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidUser(User user) {
    return user != null
        && user.firstName != null
        && !user.firstName.trim().isEmpty()
        && user.lastName != null
        && !user.lastName.trim().isEmpty()
        && user.email != null
        && !user.email.trim().isEmpty();
  }

  /**
   * Validates if a user ID is valid.
   *
   * @param id the user ID to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidUserId(Long id) {
    return id != null && id > 0;
  }

  /**
   * Validates if an email is valid.
   *
   * @param email the email to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidEmail(String email) {
    return email != null && !email.trim().isEmpty() && email.contains("@");
  }

  /**
   * Validates if a name search term is valid.
   *
   * @param name the name to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidNameSearch(String name) {
    return name != null && !name.trim().isEmpty() && name.length() >= 2;
  }

  /**
   * Validates if a user can borrow books (basic validation without database).
   *
   * @param userId the user ID to validate
   * @return true if user ID is valid (basic check)
   */
  public boolean canUserBorrowBooks(Long userId) {
    return isValidUserId(userId);
  }

  public List<User> getRecentUsers(int limit) {
    return userRepository.findRecent(limit);
  }
}
