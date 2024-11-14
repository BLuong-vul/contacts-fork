package com.vision.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision.middleware.controller.ExampleDataController;
import com.vision.middleware.domain.ExampleData;
import com.vision.middleware.service.ExampleDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ExampleDataControllerTest {

    @Mock
    private ExampleDataService exampleDataService;

    @InjectMocks
    private ExampleDataController exampleDataController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(exampleDataController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testCreateExampleData() throws Exception {
        ExampleData exampleData = new ExampleData();
        exampleData.setId("uuid");
        exampleData.setName("Test Data");

        when(exampleDataService.createExampleData(any(ExampleData.class))).thenReturn(exampleData);

        mockMvc.perform(post("/exampledata")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test Data\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("uuid"))
                .andExpect(jsonPath("$.name").value("Test Data"));
    }

    @Test
    public void testGetExampleDataById() throws Exception {
        ExampleData exampleData = new ExampleData();
        exampleData.setId("uuid");
        exampleData.setName("Test Data");

        when(exampleDataService.getExampleData(anyString())).thenReturn(exampleData);

        mockMvc.perform(get("/exampledata/uuid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("uuid"))
                .andExpect(jsonPath("$.name").value("Test Data"));
    }

    @Test
    public void testGetAllExampleData() throws Exception {
        ExampleData exampleData = new ExampleData();
        exampleData.setId("uuid");
        exampleData.setName("Test Data");

        Page<ExampleData> page = new PageImpl<>(Collections.singletonList(exampleData));

        when(exampleDataService.getAllExampleData(any(Integer.class), any(Integer.class))).thenReturn(page);

        mockMvc.perform(get("/exampledata")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("uuid"))
                .andExpect(jsonPath("$.content[0].name").value("Test Data"));
    }
}