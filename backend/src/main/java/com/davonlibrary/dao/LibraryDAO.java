package com.davonlibrary.dao;

import com.davonlibrary.entity.Library;
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
public class LibraryDAO {

  private static final Logger LOGGER = LoggerFactory.getLogger(LibraryDAO.class);

  @Inject EntityManager entityManager;

  @Transactional
  public Optional<Library> findById(Long id) {
    try {
      Library library = entityManager.find(Library.class, id);
      return Optional.ofNullable(library);
    } catch (Exception e) {
      LOGGER.error("Error finding library by ID: {}", id, e);
      return Optional.empty();
    }
  }

  @Transactional
  public List<Library> findAll() {
    try {
      TypedQuery<Library> query =
          entityManager.createQuery("SELECT l FROM Library l ORDER BY l.name", Library.class);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding all libraries", e);
      return List.of();
    }
  }

  @Transactional
  public Library save(Library library) {
    try {
      entityManager.persist(library);
      entityManager.flush();
      LOGGER.info("Library saved successfully: {}", library.name);
      return library;
    } catch (Exception e) {
      LOGGER.error("Error saving library: {}", library.name, e);
      throw new RuntimeException("Failed to save library", e);
    }
  }

  @Transactional
  public Library update(Library library) {
    try {
      Library updatedLibrary = entityManager.merge(library);
      LOGGER.info("Library updated successfully: {}", library.name);
      return updatedLibrary;
    } catch (Exception e) {
      LOGGER.error("Error updating library: {}", library.name, e);
      throw new RuntimeException("Failed to update library", e);
    }
  }

  @Transactional
  public boolean deleteById(Long id) {
    try {
      Library library = entityManager.find(Library.class, id);
      if (library != null) {
        entityManager.remove(library);
        LOGGER.info("Library deleted successfully: {}", library.name);
        return true;
      }
      return false;
    } catch (Exception e) {
      LOGGER.error("Error deleting library with ID: {}", id, e);
      throw new RuntimeException("Failed to delete library", e);
    }
  }

  @Transactional
  public long count() {
    try {
      TypedQuery<Long> query =
          entityManager.createQuery("SELECT COUNT(l) FROM Library l", Long.class);
      return query.getSingleResult();
    } catch (Exception e) {
      LOGGER.error("Error counting libraries", e);
      return 0;
    }
  }
}
