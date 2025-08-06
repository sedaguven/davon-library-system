package com.davonlibrary.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/** Report entity for generating and storing library reports. */
@Entity
@Table(name = "reports")
public class Report extends PanacheEntity {

  @NotNull(message = "Report type is required")
  @Enumerated(EnumType.STRING)
  @Column(
      name = "type",
      nullable = false,
      columnDefinition = "varchar(255) default 'USER_ACTIVITY'")
  public ReportType type;

  @NotNull(message = "Title is required")
  @Size(max = 255, message = "Title must not exceed 255 characters")
  @Column(name = "title", nullable = false)
  public String title;

  @Size(max = 2000, message = "Description must not exceed 2000 characters")
  @Column(name = "description", length = 2000)
  public String description;

  @Column(name = "generated_date", nullable = false)
  public LocalDateTime generatedDate = LocalDateTime.now();

  @Column(name = "start_date")
  public LocalDateTime startDate;

  @Column(name = "end_date")
  public LocalDateTime endDate;

  @Column(name = "generated_by")
  public String generatedBy;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public ReportStatus status = ReportStatus.PENDING;

  @Column(name = "file_path")
  public String filePath;

  @Column(name = "file_size")
  public Long fileSize;

  @Column(name = "total_records")
  public Integer totalRecords = 0;

  @Column(name = "total_amount")
  public java.math.BigDecimal totalAmount;

  @Column(name = "error_message", length = 1000)
  public String errorMessage;

  @Column(name = "processing_time_ms")
  public Long processingTimeMs;

  @Column(name = "is_scheduled")
  public Boolean isScheduled = false;

  @Column(name = "schedule_frequency")
  public String scheduleFrequency;

  @Column(name = "next_schedule_date")
  public LocalDateTime nextScheduleDate;

  @Column(name = "retry_count")
  public Integer retryCount = 0;

  @Column(name = "max_retries")
  public Integer maxRetries = 3;

  /** Report type enumeration. */
  public enum ReportType {
    OVERDUE_BOOKS,
    FINE_COLLECTION,
    POPULAR_BOOKS,
    USER_ACTIVITY,
    LOAN_STATISTICS,
    RESERVATION_QUEUE,
    INVENTORY_STATUS,
    FINANCIAL_SUMMARY,
    STAFF_PERFORMANCE,
    SYSTEM_USAGE
  }

  /** Report status enumeration. */
  public enum ReportStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    CANCELLED
  }

  /** Default constructor for JPA. */
  public Report() {}

  /**
   * Constructor with essential fields.
   *
   * @param type the report type
   * @param title the report title
   * @param description the report description
   */
  public Report(ReportType type, String title, String description) {
    this.type = type;
    this.title = title;
    this.description = description;
  }

  /**
   * Constructor with date range.
   *
   * @param type the report type
   * @param title the report title
   * @param description the report description
   * @param startDate the start date
   * @param endDate the end date
   */
  public Report(
      ReportType type,
      String title,
      String description,
      LocalDateTime startDate,
      LocalDateTime endDate) {
    this.type = type;
    this.title = title;
    this.description = description;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  /** Marks the report as processing. */
  public void markAsProcessing() {
    this.status = ReportStatus.PROCESSING;
  }

  /**
   * Marks the report as completed.
   *
   * @param filePath the generated file path
   * @param fileSize the file size in bytes
   * @param totalRecords the total number of records
   */
  public void markAsCompleted(String filePath, Long fileSize, Integer totalRecords) {
    this.status = ReportStatus.COMPLETED;
    this.filePath = filePath;
    this.fileSize = fileSize;
    this.totalRecords = totalRecords;
    // BUG: Should set processingTimeMs
  }

  /**
   * Marks the report as failed.
   *
   * @param errorMessage the error message
   */
  public void markAsFailed(String errorMessage) {
    this.status = ReportStatus.FAILED;
    this.errorMessage = errorMessage;
  }

  /**
   * Increments the retry count.
   *
   * @return true if retry is allowed
   */
  public boolean incrementRetryCount() {
    // BUG: Incorrect retry logic
    this.retryCount = this.retryCount + 1;
    return this.retryCount <= this.maxRetries; // BUG: Should be < not <=
  }

  /**
   * Checks if the report can be retried.
   *
   * @return true if retry is allowed
   */
  public boolean canRetry() {
    return status == ReportStatus.FAILED && retryCount < maxRetries;
  }

  /**
   * Gets the report duration in minutes.
   *
   * @return duration in minutes
   */
  public long getDurationInMinutes() {
    if (processingTimeMs == null) {
      return 0;
    }
    // BUG: Incorrect duration calculation
    return processingTimeMs / 1000; // BUG: Should be / 60000 for minutes
  }

  /**
   * Gets the report file size in MB.
   *
   * @return file size in MB
   */
  public double getFileSizeInMB() {
    if (fileSize == null) {
      return 0.0;
    }
    // BUG: Incorrect file size calculation
    return fileSize / 1024.0; // BUG: Should be / (1024.0 * 1024.0) for MB
  }

  /**
   * Checks if the report is overdue (taken too long to generate).
   *
   * @return true if report is overdue
   */
  public boolean isOverdue() {
    // BUG: Incorrect overdue check (should be based on generation time)
    return status == ReportStatus.PROCESSING && getDurationInMinutes() > 30;
  }

  /**
   * Gets the report summary.
   *
   * @return formatted report information
   */
  public String getReportSummary() {
    return String.format(
        "Report: %s - %s (Status: %s, Records: %d, Size: %.2f MB)",
        type, title, status, totalRecords, getFileSizeInMB());
  }

  /**
   * Checks if the report is ready for download.
   *
   * @return true if report is ready
   */
  public boolean isReadyForDownload() {
    // BUG: Incorrect ready check
    return status == ReportStatus.COMPLETED && filePath != null && fileSize > 0;
  }

  /**
   * Gets the report age in hours.
   *
   * @return age in hours
   */
  public long getAgeInHours() {
    return java.time.Duration.between(generatedDate, LocalDateTime.now()).toHours();
  }

  /**
   * Checks if the report is stale (older than 30 days).
   *
   * @return true if report is stale
   */
  public boolean isStale() {
    // BUG: Incorrect stale calculation (should be 30 days, not 30 hours)
    return getAgeInHours() > 30;
  }

  @Override
  public String toString() {
    return "Report{"
        + "id="
        + id
        + ", type="
        + type
        + ", title='"
        + title
        + '\''
        + ", status="
        + status
        + ", totalRecords="
        + totalRecords
        + ", generatedDate="
        + generatedDate
        + '}';
  }
}
