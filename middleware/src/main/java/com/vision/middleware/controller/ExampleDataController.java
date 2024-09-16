package com.vision.middleware.controller;

import com.vision.middleware.domain.ExampleData;
import com.vision.middleware.service.ExampleDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/exampledata")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000","https://contacts-5min.onrender.com/", "*"}) // todo: change this later
public class ExampleDataController {
    private final ExampleDataService service;

    @PostMapping
    public ResponseEntity<ExampleData> createExampleData(@RequestBody ExampleData exampleData) {
        return ResponseEntity.created(URI.create("/exampledata/uuid")).body(service.createExampleData(exampleData));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExampleData> getExampleData(@PathVariable(value = "id") String id) {
        return ResponseEntity.ok(service.getExampleData(id));
    }

    @GetMapping
    public ResponseEntity<Page<ExampleData>> getExampleData(@RequestParam(value = "page", defaultValue = "0") int page,
                                                            @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getAllExampleData(page, size));
    }
}
