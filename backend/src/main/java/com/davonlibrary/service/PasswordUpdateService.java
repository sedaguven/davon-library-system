package com.davonlibrary.service;

import com.davonlibrary.entity.User;
import com.davonlibrary.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class PasswordUpdateService {

  @Inject UserRepository userRepository;

  @Transactional
  public Map<String, String> assignPasswordsToExistingUsers() {
    Map<String, String> results = new HashMap<>();

    // Get all users without password hashes
    List<User> users = userRepository.list("passwordHash", "");

    if (users.isEmpty()) {
      results.put("message", "No users found without password hashes");
      return results;
    }

    int updatedCount = 0;

    for (User user : users) {
      String password;

      // Assign specific passwords for admin users
      if ("john.smith@email.com".equals(user.email)) {
        password = "admin123";
      } else if ("sarah.johnson@email.com".equals(user.email)) {
        password = "admin456";
      } else if ("michael.brown@email.com".equals(user.email)) {
        password = "admin789";
      } else {
        // Generate password based on user ID for regular users
        password = "user" + user.id;
      }

      // Hash the password
      String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
      user.passwordHash = passwordHash;

      // Store the email and password for reporting
      results.put(user.email, password);
      updatedCount++;
    }

    results.put("message", "Updated " + updatedCount + " users with passwords");
    return results;
  }

  @Transactional
  public Map<String, String> getCurrentUserPasswords() {
    Map<String, String> results = new HashMap<>();

    List<User> allUsers = userRepository.listAll();

    for (User user : allUsers) {
      if (user.passwordHash != null && !user.passwordHash.isEmpty()) {
        // For users with password hashes, show their assigned passwords
        if ("john.smith@email.com".equals(user.email)) {
          results.put(user.email, "admin123");
        } else if ("sarah.johnson@email.com".equals(user.email)) {
          results.put(user.email, "admin456");
        } else if ("michael.brown@email.com".equals(user.email)) {
          results.put(user.email, "admin789");
        } else {
          results.put(user.email, "user" + user.id);
        }
      } else {
        results.put(user.email, "NO_PASSWORD_SET");
      }
    }

    return results;
  }
}
