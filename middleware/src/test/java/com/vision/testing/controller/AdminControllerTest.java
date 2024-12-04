package com.vision.testing.controller;

import com.vision.middleware.controller.AdminController;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminControllerTest {

    private final AdminController adminController = new AdminController();
    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();

    @Test
    void helloAdminController_shouldReturnAdminLevelAccess() throws Exception {
        // Act
        ResponseEntity<String> response = adminController.helloAdminController();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Admin level access");
    }

    @Test
    void helloAdminController_shouldReturnAdminLevelAccess_usingMockMvc() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/admin/"))
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isEqualTo("Admin level access"));
    }
}