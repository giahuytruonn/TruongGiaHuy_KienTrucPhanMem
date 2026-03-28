package iuh.fit.registerservice.client;

import iuh.fit.registerservice.dto.response.ApiResponse;
import iuh.fit.registerservice.dto.response.AuthResponse;
import iuh.fit.registerservice.dto.request.CreateUserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "login-service", url = "${login-service.url}")
public interface LoginServiceClient {

    // Tạo user mới trong Login Service DB
    @PostMapping("/internal/users")
    ApiResponse<AuthResponse> createUser(@RequestBody CreateUserRequest request);

    // Kiểm tra username đã tồn tại chưa
    // Gọi: GET /internal/users/exists?username=john
    @GetMapping("/internal/users/exists")
    Boolean checkExists(@RequestParam(required = false) String username,
                        @RequestParam(required = false) String email);
}