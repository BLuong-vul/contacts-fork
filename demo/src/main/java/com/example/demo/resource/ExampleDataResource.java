package com.example.demo.resource;

import java.net.URI;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.ExampleData;
import com.example.demo.service.ExampleDataService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/exampledata")
@RequiredArgsConstructor
public class ExampleDataResource {
    private final ExampleDataService service;

    @PostMapping
    public ResponseEntity<ExampleData> createExampleData(@RequestBody ExampleData exampleData) {
        return ResponseEntity.created(URI.create("/exampledata/uuid")).body(service.createExampleData(exampleData));
    }

    @CrossOrigin(origins = {"http://localhost:3000","https://contacts-5min.onrender.com/"})
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
