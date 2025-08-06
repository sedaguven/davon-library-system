package com.davonlibrary.dto;

import java.time.LocalDateTime;

public class ActivityDTO {
  public String type;
  public String description;
  public LocalDateTime timestamp;
  public String user;
  public String bookTitle;

  public ActivityDTO(
      String type, String description, LocalDateTime timestamp, String user, String bookTitle) {
    this.type = type;
    this.description = description;
    this.timestamp = timestamp;
    this.user = user;
    this.bookTitle = bookTitle;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }
}
