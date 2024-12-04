package com.vision.testing.service;

import com.vision.middleware.domain.ExampleData;
import com.vision.middleware.repo.ExampleDataRepo;
import com.vision.middleware.service.ExampleDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExampleDataServiceTest {

    @Mock
    private ExampleDataRepo exampleDataRepo;

    @InjectMocks
    private ExampleDataService exampleDataService;

    private ExampleData exampleData;

    @BeforeEach
    public void setUp() {
        exampleData = new ExampleData();
        exampleData.setId("1");
        exampleData.setName("Test Data");
    }

    @Test
    public void testGetAllExampleData() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));
        Page<ExampleData> page = new PageImpl<>(List.of(exampleData));
        when(exampleDataRepo.findAll(pageRequest)).thenReturn(page);

        // Act
        Page<ExampleData> result = exampleDataService.getAllExampleData(0, 10);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo("1");
    }

    @Test
    public void testGetExampleData_Found() {
        // Arrange
        when(exampleDataRepo.findById("1")).thenReturn(Optional.of(exampleData));

        // Act
        ExampleData result = exampleDataService.getExampleData("1");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("1");
    }

    @Test
    public void testGetExampleData_NotFound() {
        // Arrange
        when(exampleDataRepo.findById("2")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> exampleDataService.getExampleData("2"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Data not found.");
    }

    @Test
    public void testCreateExampleData() {
        // Arrange
        when(exampleDataRepo.save(any(ExampleData.class))).thenReturn(exampleData);

        // Act
        ExampleData result = exampleDataService.createExampleData(exampleData);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("1");
    }
}