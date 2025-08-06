package com.davonlibrary.dao;

import com.davonlibrary.entity.LibraryMembership;
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
public class LibraryMembershipDAO {

  private static final Logger LOGGER = LoggerFactory.getLogger(LibraryMembershipDAO.class);

  @Inject EntityManager entityManager;

  @Transactional
  public Optional<LibraryMembership> findById(Long id) {
    try {
      LibraryMembership membership = entityManager.find(LibraryMembership.class, id);
      return Optional.ofNullable(membership);
    } catch (Exception e) {
      LOGGER.error("Error finding library membership by ID: {}", id, e);
      return Optional.empty();
    }
  }

  @Transactional
  public List<LibraryMembership> findByUserId(Long userId) {
    try {
      TypedQuery<LibraryMembership> query =
          entityManager.createQuery(
              "SELECT lm FROM LibraryMembership lm WHERE lm.user.id = :userId ORDER BY lm.createdAt DESC",
              LibraryMembership.class);
      query.setParameter("userId", userId);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding library memberships by user ID: {}", userId, e);
      return List.of();
    }
  }

  @Transactional
  public List<LibraryMembership> findByLibraryId(Long libraryId) {
    try {
      TypedQuery<LibraryMembership> query =
          entityManager.createQuery(
              "SELECT lm FROM LibraryMembership lm WHERE lm.library.id = :libraryId ORDER BY lm.createdAt DESC",
              LibraryMembership.class);
      query.setParameter("libraryId", libraryId);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding library memberships by library ID: {}", libraryId, e);
      return List.of();
    }
  }

  @Transactional
  public LibraryMembership save(LibraryMembership membership) {
    try {
      entityManager.persist(membership);
      entityManager.flush();
      LOGGER.info(
          "Library membership saved successfully: User={}, Library={}",
          membership.user.id,
          membership.library.name);
      return membership;
    } catch (Exception e) {
      LOGGER.error(
          "Error saving library membership: User={}, Library={}",
          membership.user.id,
          membership.library.name,
          e);
      throw new RuntimeException("Failed to save library membership", e);
    }
  }

  @Transactional
  public LibraryMembership update(LibraryMembership membership) {
    try {
      LibraryMembership updatedMembership = entityManager.merge(membership);
      LOGGER.info("Library membership updated successfully: ID={}", membership.id);
      return updatedMembership;
    } catch (Exception e) {
      LOGGER.error("Error updating library membership: ID={}", membership.id, e);
      throw new RuntimeException("Failed to update library membership", e);
    }
  }

  @Transactional
  public boolean deleteById(Long id) {
    try {
      LibraryMembership membership = entityManager.find(LibraryMembership.class, id);
      if (membership != null) {
        entityManager.remove(membership);
        LOGGER.info("Library membership deleted successfully: ID={}", id);
        return true;
      }
      return false;
    } catch (Exception e) {
      LOGGER.error("Error deleting library membership with ID: {}", id, e);
      throw new RuntimeException("Failed to delete library membership", e);
    }
  }

  @Transactional
  public List<LibraryMembership> findAll() {
    try {
      TypedQuery<LibraryMembership> query =
          entityManager.createQuery(
              "SELECT lm FROM LibraryMembership lm ORDER BY lm.createdAt DESC",
              LibraryMembership.class);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding all library memberships", e);
      return List.of();
    }
  }

  @Transactional
  public long count() {
    try {
      TypedQuery<Long> query =
          entityManager.createQuery("SELECT COUNT(lm) FROM LibraryMembership lm", Long.class);
      return query.getSingleResult();
    } catch (Exception e) {
      LOGGER.error("Error counting library memberships", e);
      return 0;
    }
  }
}
