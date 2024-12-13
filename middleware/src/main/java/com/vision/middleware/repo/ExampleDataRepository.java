package com.vision.middleware.repo;

import com.vision.middleware.domain.ExampleData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing ExampleData entities.
 */
@Repository
public interface ExampleDataRepository extends JpaRepository<ExampleData, String> {

    /**
     * Retrieves an ExampleData entity by its unique identifier.
     *
     * @param id the unique identifier (UUID) of the ExampleData to retrieve
     * @return an Optional containing the ExampleData if found, otherwise an empty Optional
     */
    Optional<ExampleData> findById(String id);
}
