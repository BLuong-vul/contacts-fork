package com.vision.middleware.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles HTTP requests for administrative functionalities.
 */
@RestController
@RequestMapping("/admin")
@CrossOrigin("*") // todo: change this later
public class AdminController {

    /**
     * Returns a success message indicating admin-level access to the requester.
     *
     * @return ResponseEntity with a simple "Admin level access" string and an HTTP 200 OK status.
     */
    @GetMapping("/")
    public ResponseEntity<String> helloAdminController() {
        return ResponseEntity.ok("Admin level access");
    }

}
