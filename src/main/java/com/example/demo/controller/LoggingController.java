package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@RestController
public class LoggingController {

    @GetMapping("/logs")
    public String getLogs() {
        try {
            return Files.lines(Paths.get("logs/user-actions.log"))
                    .collect(Collectors.joining("\n" + " </br>"));
        } catch (IOException e){
            return "Error reading log file: " + e.getMessage();
        }
    }
}
