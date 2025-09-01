package com.davonlibrary.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {
  private Long id;
  private String title;
  private LocalDate dueDate;
  private LocalDateTime returnedDate; // null if active
  private Integer daysLeft; // negative if overdue
} 