package iuh.fit.monolithic.controller;

import iuh.fit.monolithic.dto.response.ApiResponse;
import iuh.fit.monolithic.entity.User;
import iuh.fit.monolithic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    //  Public endpoint - no token needed
    @GetMapping("/public/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Server is running!", "OK"));
    }

    // Protected - any authenticated user
    @GetMapping("/users/profile")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProfile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> profile = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "fullName", user.getFullName() != null ? user.getFullName() : "",
                "role", user.getRole().name(),
                "createdAt", user.getCreatedAt().toString()
        );

        return ResponseEntity.ok(ApiResponse.success("Profile fetched", profile));
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<String>>> getAllUsers() {
        List<String> usernames = userRepository.findAll()
                .stream()
                .map(User::getUsername)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("All users", usernames));
    }
}
