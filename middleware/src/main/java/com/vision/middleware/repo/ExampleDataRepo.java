package com.vision.middleware.repo;

import com.vision.middleware.domain.ExampleData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExampleDataRepo extends JpaRepository<ExampleData, String> {
    @SuppressWarnings("null")
    Optional<ExampleData> findById(String id);
}