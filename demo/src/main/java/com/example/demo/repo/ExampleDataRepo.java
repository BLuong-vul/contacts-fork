package com.example.demo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.ExampleData;

@Repository
public interface ExampleDataRepo extends JpaRepository<ExampleData, String> {
    @SuppressWarnings("null")
    Optional<ExampleData> findById(String id);
}