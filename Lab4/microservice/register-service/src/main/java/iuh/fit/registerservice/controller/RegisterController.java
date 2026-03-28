package iuh.fit.registerservice.controller;

import iuh.fit.registerservice.dto.response.ApiResponse;
import iuh.fit.registerservice.dto.response.AuthResponse;
import iuh.fit.registerservice.dto.request.RegisterRequest;
import iuh.fit.registerservice.service.RegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/register")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = registerService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Registration successful", response));
    }
}