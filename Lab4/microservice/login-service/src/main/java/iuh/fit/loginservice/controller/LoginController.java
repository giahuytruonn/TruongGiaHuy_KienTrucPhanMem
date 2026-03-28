package iuh.fit.loginservice.controller;

import iuh.fit.loginservice.dto.request.CreateUserRequest;
import iuh.fit.loginservice.dto.request.LoginRequest;
import iuh.fit.loginservice.dto.response.ApiResponse;
import iuh.fit.loginservice.dto.response.AuthResponse;
import iuh.fit.loginservice.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;


    @PostMapping("/api/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = loginService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/internal/users")
    public ResponseEntity<ApiResponse<AuthResponse>> createUser(
            @RequestBody CreateUserRequest request) {
        AuthResponse response = loginService.createUser(request);
        return ResponseEntity.ok(ApiResponse.success("User created", response));
    }

    @GetMapping("/internal/users/exists")
    public ResponseEntity<Boolean> checkExists(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email) {
        if (username != null) {
            return ResponseEntity.ok(loginService.existsByUsername(username));
        }
        if (email != null) {
            return ResponseEntity.ok(loginService.existsByEmail(email));
        }
        return ResponseEntity.ok(false);
    }
}