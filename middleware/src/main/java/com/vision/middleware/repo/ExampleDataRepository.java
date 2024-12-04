package com.vision.middleware.repo;

import com.vision.middleware.domain.ExampleData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExampleDataRepository extends JpaRepository<ExampleData, String> {
    Optional<ExampleData> findById(String id);
}