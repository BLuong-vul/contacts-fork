package com.vision.middleware.service;

import com.vision.middleware.domain.ExampleData;
import com.vision.middleware.repo.ExampleDataRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Service layer component responsible for managing Example Data operations.
 * Provides methods for retrieving, creating, and handling ExampleData entities.
 * All operations are transactional, rolling back on any Exception.
 */
@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class ExampleDataService {

    /**
     * Final repository instance for data access, autowired by constructor.
     */
    private final ExampleDataRepository repo;

    /**
     * Retrieves a paginated list of all ExampleData entities, sorted by 'name'.
     *
     * @param page the page number to retrieve (0 indexed)
     * @param size the number of entities per page
     * @return a Page object containing the requested ExampleData entities
     */
    public Page<ExampleData> getAllExampleData(int page, int size) {
        return repo.findAll(PageRequest.of(page, size, Sort.by("name")));
    }

    /**
     * Fetches an ExampleData entity by its identifier.
     *
     * @param id the unique identifier of the ExampleData to retrieve
     * @return the ExampleData entity associated with the provided id
     * @throws RuntimeException if no ExampleData is found with the given id
     */
    public ExampleData getExampleData(String id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Data not found."));
    }

    /**
     * Creates a new ExampleData entity or updates an existing one if the id is already set.
     *
     * @param exampleData the ExampleData entity to save
     * @return the saved ExampleData entity
     */
    public ExampleData createExampleData(ExampleData exampleData) {
        return repo.save(exampleData);
    }
}
