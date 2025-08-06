package com.davonlibrary.service;

import com.davonlibrary.dto.ActivityDTO;
import com.davonlibrary.entity.Loan;
import com.davonlibrary.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class ActivityService {

  @Inject LoanService loanService;

  @Inject UserService userService;

  public List<ActivityDTO> getRecentActivities(int limit) {
    List<Loan> recentLoans = loanService.getRecentLoans(limit);
    List<User> recentUsers = userService.getRecentUsers(limit);

    Stream<ActivityDTO> loanActivities =
        recentLoans.stream()
            .map(
                loan ->
                    new ActivityDTO(
                        "Loan",
                        "Book loaned to " + loan.user.getFullName(),
                        loan.loanDate,
                        loan.user.getFullName(),
                        loan.bookCopy.book.title));

    Stream<ActivityDTO> userActivities =
        recentUsers.stream()
            .map(
                user ->
                    new ActivityDTO(
                        "New User",
                        "New user registered: " + user.getFullName(),
                        user.joinDate.atStartOfDay(),
                        user.getFullName(),
                        null));

    return Stream.concat(loanActivities, userActivities)
        .sorted(Comparator.comparing(ActivityDTO::getTimestamp).reversed())
        .limit(limit)
        .collect(Collectors.toList());
  }
}
