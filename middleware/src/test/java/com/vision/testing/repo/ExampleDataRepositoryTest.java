package com.vision.testing.repo;

import com.vision.middleware.Application;
import com.vision.middleware.domain.ExampleData;
import com.vision.middleware.repo.ExampleDataRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(classes = Application.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ExampleDataRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("username")
            .withPassword("password");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private ExampleDataRepository exampleDataRepository;

    @Test
    void testFindById() {
        // Given
        ExampleData exampleData = new ExampleData(null, "John Doe", "123-45-6789", "1234-5678-9012-3456");
        ExampleData savedExampleData = exampleDataRepository.save(exampleData);
        String uuid = savedExampleData.getId();

        // When
        Optional<ExampleData> foundExampleData = exampleDataRepository.findById(uuid);

        // Then
        assertThat(foundExampleData).isPresent();
        assertThat(foundExampleData.get().getId()).isEqualTo(uuid);
        assertThat(foundExampleData.get().getName()).isEqualTo("John Doe");
        assertThat(foundExampleData.get().getSsn()).isEqualTo("123-45-6789");
        assertThat(foundExampleData.get().getCreditCardNumber()).isEqualTo("1234-5678-9012-3456");
    }

    @Test
    void testFindById_DNE() {
        // Given nothing is saved:
        String uuid = UUID.randomUUID().toString();

        //When
        Optional<ExampleData> missingExampleData = exampleDataRepository.findById(uuid);

        // Then
        assertThat(missingExampleData).isEmpty();
    }
}