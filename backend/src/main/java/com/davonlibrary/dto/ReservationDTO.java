package com.davonlibrary.dto;

import com.davonlibrary.entity.Book;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
  private Long id;
  private Book book;
  private LocalDate reservationDate;
  private String status;
  private Integer queuePosition;
}
