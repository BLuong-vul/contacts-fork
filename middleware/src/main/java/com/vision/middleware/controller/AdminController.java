package com.vision.middleware.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@CrossOrigin("*") // todo: change this later
public class AdminController {

    @GetMapping("/")
    public String helloAdminController() {
        return "Admin level access";
    }

}
