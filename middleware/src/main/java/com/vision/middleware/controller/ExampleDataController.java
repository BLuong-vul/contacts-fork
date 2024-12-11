package com.vision.middleware.controller;

import com.vision.middleware.domain.ExampleData;
import com.vision.middleware.service.ExampleDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * REST controller for managing ExampleData entities.
 * Provides endpoints for creating, retrieving, and listing ExampleData instances.
 */
@RestController
@RequestMapping("/exampledata")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000","https://contacts-5min.onrender.com/", "*"}) // todo: change this later
public class ExampleDataController {
    /**
     * Service layer dependency for ExampleData operations.
     */
    private final ExampleDataService service;

    /**
     * Creates a new ExampleData entity.
     *
     * @param exampleData the ExampleData object to be created
     * @return the newly created ExampleData with HTTP 201 (Created) status
     * @see ExampleDataService#createExampleData(ExampleData)
     */
    @PostMapping
    public ResponseEntity<ExampleData> createExampleData(@RequestBody ExampleData exampleData) {
        return ResponseEntity.created(URI.create("/exampledata/uuid")).body(service.createExampleData(exampleData));
    }

    /**
     * Retrieves an ExampleData entity by its identifier.
     *
     * @param id the identifier of the ExampleData entity to retrieve
     * @return the ExampleData entity with HTTP 200 (OK) status if found
     * @throws RuntimeException if no ExampleData entity exists with the given id (wrapped in HTTP response)
     * @see ExampleDataService#getExampleData(String)
     */
    @CrossOrigin(origins = {"http://localhost:3000","https://contacts-5min.onrender.com/"})
    @GetMapping("/{id}")
    public ResponseEntity<ExampleData> getExampleData(@PathVariable(value = "id") String id) {
        return ResponseEntity.ok(service.getExampleData(id));
    }

    /**
     * Retrieves a paginated list of ExampleData entities.
     *
     * @param page the page number to retrieve (0-indexed, default: 0)
     * @param size the number of entities per page (default: 10)
     * @return a Page of ExampleData entities with HTTP 200 (OK) status
     * @see ExampleDataService#getAllExampleData(int, int)
     */
    @GetMapping
    public ResponseEntity<Page<ExampleData>> getExampleData(@RequestParam(value = "page", defaultValue = "0") int page,
                                                            @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getAllExampleData(page, size));
    }
}
