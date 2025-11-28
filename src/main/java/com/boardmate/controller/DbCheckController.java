package com.boardmate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "시스템", description = "시스템 상태 확인 API")
@RestController
public class DbCheckController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Operation(summary = "MySQL 연결 확인", description = "MySQL 데이터베이스 연결 상태를 확인합니다.")
    @GetMapping("/db-check")
    public String checkDb() {
        try {
            String result = jdbcTemplate.queryForObject("SELECT 1", String.class);
            return "MySQL Connection OK: " + result;
        } catch (Exception e) {
            return "MySQL Connection FAILED: " + e.getMessage();
        }
    }

}
