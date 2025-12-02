package com.boardmate.controller;

import com.boardmate.domain.user.User;
import com.boardmate.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "테스트", description = "개발/테스트용 API (운영 환경에서는 비활성화) - 테스트는 auth만료되어도 접근 가능")
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final UserService userService;

    @Operation(summary = "전체 사용자 목록 조회", description = "개발/테스트용 API입니다. users 테이블의 모든 사용자 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "ID별 사용자 조회", description = "개발/테스트용 API입니다. 특정 ID의 사용자 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "사용자 없음")
    @GetMapping("/userbyid/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "이메일별 사용자 조회", description = "개발/테스트용 API입니다. 특정 이메일의 사용자 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "사용자 없음")
    @GetMapping("/userbyemail")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
