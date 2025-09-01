package com.davonlibrary.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
  private Long id;
  private Long bookId;
  private String bookTitle;
  private LocalDate reservationDate;
  private String status;
  private Integer queuePosition;
}
