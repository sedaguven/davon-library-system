package com.davonlibrary.dao;

import com.davonlibrary.entity.Staff;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Access Object for Staff entity operations. Provides a clean abstraction layer over database
 * operations.
 */
@ApplicationScoped
public class StaffDAO {

  private static final Logger LOGGER = LoggerFactory.getLogger(StaffDAO.class);

  @Inject EntityManager entityManager;

  /**
   * Find a staff member by ID.
   *
   * @param id the staff ID
   * @return Optional containing the staff member if found
   */
  @Transactional
  public Optional<Staff> findById(Long id) {
    try {
      Staff staff = entityManager.find(Staff.class, id);
      return Optional.ofNullable(staff);
    } catch (Exception e) {
      LOGGER.error("Error finding staff by ID: {}", id, e);
      return Optional.empty();
    }
  }

  /**
   * Find staff by email.
   *
   * @param email the email address
   * @return Optional containing the staff member if found
   */
  @Transactional
  public Optional<Staff> findByEmail(String email) {
    try {
      TypedQuery<Staff> query =
          entityManager.createQuery("SELECT s FROM Staff s WHERE s.email = :email", Staff.class);
      query.setParameter("email", email);
      return query.getResultList().stream().findFirst();
    } catch (Exception e) {
      LOGGER.error("Error finding staff by email: {}", email, e);
      return Optional.empty();
    }
  }

  /**
   * Find staff by role.
   *
   * @param role the staff role
   * @return list of staff members with the specified role
   */
  @Transactional
  public List<Staff> findByRole(String role) {
    try {
      TypedQuery<Staff> query =
          entityManager.createQuery(
              "SELECT s FROM Staff s WHERE s.role = :role ORDER BY s.lastName, s.firstName",
              Staff.class);
      query.setParameter("role", role);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding staff by role: {}", role, e);
      return List.of();
    }
  }

  /**
   * Find staff by library.
   *
   * @param libraryId the library ID
   * @return list of staff members at the library
   */
  @Transactional
  public List<Staff> findByLibraryId(Long libraryId) {
    try {
      TypedQuery<Staff> query =
          entityManager.createQuery(
              "SELECT s FROM Staff s WHERE s.library.id = :libraryId ORDER BY s.lastName, s.firstName",
              Staff.class);
      query.setParameter("libraryId", libraryId);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding staff by library ID: {}", libraryId, e);
      return List.of();
    }
  }

  /**
   * Find active staff members.
   *
   * @return list of active staff members
   */
  @Transactional
  public List<Staff> findActive() {
    try {
      TypedQuery<Staff> query =
          entityManager.createQuery(
              "SELECT s FROM Staff s WHERE s.isActive = true ORDER BY s.lastName, s.firstName",
              Staff.class);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding active staff", e);
      return List.of();
    }
  }

  /**
   * Save a new staff member.
   *
   * @param staff the staff member to save
   * @return the saved staff member with generated ID
   */
  @Transactional
  public Staff save(Staff staff) {
    try {
      entityManager.persist(staff);
      entityManager.flush();
      LOGGER.info("Staff saved successfully: {} {}", staff.firstName, staff.lastName);
      return staff;
    } catch (Exception e) {
      LOGGER.error("Error saving staff: {} {}", staff.firstName, staff.lastName, e);
      throw new RuntimeException("Failed to save staff", e);
    }
  }

  /**
   * Update an existing staff member.
   *
   * @param staff the staff member to update
   * @return the updated staff member
   */
  @Transactional
  public Staff update(Staff staff) {
    try {
      Staff updatedStaff = entityManager.merge(staff);
      LOGGER.info("Staff updated successfully: {} {}", staff.firstName, staff.lastName);
      return updatedStaff;
    } catch (Exception e) {
      LOGGER.error("Error updating staff: {} {}", staff.firstName, staff.lastName, e);
      throw new RuntimeException("Failed to update staff", e);
    }
  }

  /**
   * Delete a staff member by ID.
   *
   * @param id the staff ID
   * @return true if deleted successfully
   */
  @Transactional
  public boolean deleteById(Long id) {
    try {
      Staff staff = entityManager.find(Staff.class, id);
      if (staff != null) {
        entityManager.remove(staff);
        LOGGER.info("Staff deleted successfully: {} {}", staff.firstName, staff.lastName);
        return true;
      }
      return false;
    } catch (Exception e) {
      LOGGER.error("Error deleting staff with ID: {}", id, e);
      throw new RuntimeException("Failed to delete staff", e);
    }
  }

  /**
   * Find all staff members.
   *
   * @return list of all staff members
   */
  @Transactional
  public List<Staff> findAll() {
    try {
      TypedQuery<Staff> query =
          entityManager.createQuery(
              "SELECT s FROM Staff s ORDER BY s.lastName, s.firstName", Staff.class);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding all staff", e);
      return List.of();
    }
  }

  /**
   * Count total number of staff members.
   *
   * @return total count
   */
  @Transactional
  public long count() {
    try {
      TypedQuery<Long> query =
          entityManager.createQuery("SELECT COUNT(s) FROM Staff s", Long.class);
      return query.getSingleResult();
    } catch (Exception e) {
      LOGGER.error("Error counting staff", e);
      return 0;
    }
  }

  /**
   * Count staff by role.
   *
   * @param role the staff role
   * @return number of staff members with the role
   */
  @Transactional
  public long countByRole(String role) {
    try {
      TypedQuery<Long> query =
          entityManager.createQuery(
              "SELECT COUNT(s) FROM Staff s WHERE s.role = :role", Long.class);
      query.setParameter("role", role);
      return query.getSingleResult();
    } catch (Exception e) {
      LOGGER.error("Error counting staff by role: {}", role, e);
      return 0;
    }
  }

  /**
   * Get staff statistics.
   *
   * @return array with [total staff, active staff, librarians, administrators, managers]
   */
  @Transactional
  public Object[] getStaffStatistics() {
    try {
      // Total staff
      TypedQuery<Long> totalQuery =
          entityManager.createQuery("SELECT COUNT(s) FROM Staff s", Long.class);
      long totalStaff = totalQuery.getSingleResult();

      // Active staff
      TypedQuery<Long> activeQuery =
          entityManager.createQuery(
              "SELECT COUNT(s) FROM Staff s WHERE s.isActive = true", Long.class);
      long activeStaff = activeQuery.getSingleResult();

      // Librarians
      TypedQuery<Long> librarianQuery =
          entityManager.createQuery(
              "SELECT COUNT(s) FROM Staff s WHERE s.role = 'LIBRARIAN'", Long.class);
      long librarians = librarianQuery.getSingleResult();

      // Administrators
      TypedQuery<Long> adminQuery =
          entityManager.createQuery(
              "SELECT COUNT(s) FROM Staff s WHERE s.role = 'ADMINISTRATOR'", Long.class);
      long administrators = adminQuery.getSingleResult();

      // Managers
      TypedQuery<Long> managerQuery =
          entityManager.createQuery(
              "SELECT COUNT(s) FROM Staff s WHERE s.role = 'MANAGER'", Long.class);
      long managers = managerQuery.getSingleResult();

      return new Object[] {totalStaff, activeStaff, librarians, administrators, managers};
    } catch (Exception e) {
      LOGGER.error("Error getting staff statistics", e);
      return new Object[] {0L, 0L, 0L, 0L, 0L};
    }
  }
}
