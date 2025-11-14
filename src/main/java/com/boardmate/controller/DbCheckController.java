package com.boardmate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DbCheckController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/db-check")
    public String checkDb() {
        try {
            String result = jdbcTemplate.queryForObject("SELECT 1", String.class);
            return "DB Connection OK: " + result;
        } catch (Exception e) {
            return "DB Connection FAILED: " + e.getMessage();
        }
    }
}
