package com.vision.middleware.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.vision.middleware.repo.ExampleDataRepo;
import com.vision.middleware.domain.ExampleData;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class ExampleDataService {
    private final ExampleDataRepo repo;

    public Page<ExampleData> getAllExampleData(int page, int size) {
        return repo.findAll(PageRequest.of(page, size, Sort.by("someString")));
    }

    public ExampleData getExampleData(String id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Data not found."));
    }

    public ExampleData createExampleData(ExampleData exampleData) {
        return repo.save(exampleData);
    }
}
