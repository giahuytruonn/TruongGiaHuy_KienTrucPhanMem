package iuh.fit.registerservice.service;

import iuh.fit.registerservice.client.LoginServiceClient;
import iuh.fit.registerservice.dto.response.ApiResponse;
import iuh.fit.registerservice.dto.response.AuthResponse;
import iuh.fit.registerservice.dto.request.CreateUserRequest;
import iuh.fit.registerservice.dto.request.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterService {

    private final LoginServiceClient loginServiceClient;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {
        //  Bước 1: Gọi Login Service kiểm tra username tồn tại chưa
        Boolean usernameExists = loginServiceClient.checkExists(request.getUsername(), null);
        if (Boolean.TRUE.equals(usernameExists)) {
            throw new RuntimeException("Username already taken: " + request.getUsername());
        }

        //  Bước 2: Gọi Login Service kiểm tra email tồn tại chưa
        Boolean emailExists = loginServiceClient.checkExists(null, request.getEmail());
        if (Boolean.TRUE.equals(emailExists)) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        //  Bước 3: Encode password trước khi gửi sang
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        //  Bước 4: Gọi Login Service để tạo user và nhận JWT token về
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(encodedPassword)
                .fullName(request.getFullName())
                .build();

        ApiResponse<AuthResponse> response = loginServiceClient.createUser(createUserRequest);

        log.info("User registered successfully via Login Service: {}", request.getUsername());
        return response.getData();
    }
}