package com.davonlibrary.dao;

import com.davonlibrary.entity.Report;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ReportDAO {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReportDAO.class);

  @Inject EntityManager entityManager;

  @Transactional
  public Optional<Report> findById(Long id) {
    try {
      Report report = entityManager.find(Report.class, id);
      return Optional.ofNullable(report);
    } catch (Exception e) {
      LOGGER.error("Error finding report by ID: {}", id, e);
      return Optional.empty();
    }
  }

  @Transactional
  public List<Report> findByUserId(Long userId) {
    try {
      TypedQuery<Report> query =
          entityManager.createQuery(
              "SELECT r FROM Report r WHERE r.user.id = :userId ORDER BY r.createdAt DESC",
              Report.class);
      query.setParameter("userId", userId);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding reports by user ID: {}", userId, e);
      return List.of();
    }
  }

  @Transactional
  public List<Report> findByStatus(String status) {
    try {
      TypedQuery<Report> query =
          entityManager.createQuery(
              "SELECT r FROM Report r WHERE r.status = :status ORDER BY r.createdAt DESC",
              Report.class);
      query.setParameter("status", status);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding reports by status: {}", status, e);
      return List.of();
    }
  }

  @Transactional
  public Report save(Report report) {
    try {
      entityManager.persist(report);
      entityManager.flush();
      LOGGER.info(
          "Report saved successfully: GeneratedBy={}, Type={}", report.generatedBy, report.type);
      return report;
    } catch (Exception e) {
      LOGGER.error(
          "Error saving report: GeneratedBy={}, Type={}", report.generatedBy, report.type, e);
      throw new RuntimeException("Failed to save report", e);
    }
  }

  @Transactional
  public Report update(Report report) {
    try {
      Report updatedReport = entityManager.merge(report);
      LOGGER.info("Report updated successfully: ID={}", report.id);
      return updatedReport;
    } catch (Exception e) {
      LOGGER.error("Error updating report: ID={}", report.id, e);
      throw new RuntimeException("Failed to update report", e);
    }
  }

  @Transactional
  public boolean deleteById(Long id) {
    try {
      Report report = entityManager.find(Report.class, id);
      if (report != null) {
        entityManager.remove(report);
        LOGGER.info("Report deleted successfully: ID={}", id);
        return true;
      }
      return false;
    } catch (Exception e) {
      LOGGER.error("Error deleting report with ID: {}", id, e);
      throw new RuntimeException("Failed to delete report", e);
    }
  }

  @Transactional
  public List<Report> findAll() {
    try {
      TypedQuery<Report> query =
          entityManager.createQuery(
              "SELECT r FROM Report r ORDER BY r.createdAt DESC", Report.class);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding all reports", e);
      return List.of();
    }
  }

  @Transactional
  public long count() {
    try {
      TypedQuery<Long> query =
          entityManager.createQuery("SELECT COUNT(r) FROM Report r", Long.class);
      return query.getSingleResult();
    } catch (Exception e) {
      LOGGER.error("Error counting reports", e);
      return 0;
    }
  }
}
