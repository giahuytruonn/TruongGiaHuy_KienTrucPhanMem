package iuh.fit.bai2.auth;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/api/auth/login")
    public Map<String, Object> login(
            @RequestBody Map<String, String> loginRequest) {

        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        if ("admin".equals(username) && "admin123".equals(password)) {
            return Map.of(
                    "status", "success",
                    "access_token", jwtUtil.generateToken(username, "ADMIN"),
                    "refresh_token", jwtUtil.generateRefreshToken(username)
            );
        }

        if ("staff".equals(username) && "staff123".equals(password)) {
            return Map.of(
                    "status", "success",
                    "access_token", jwtUtil.generateToken(username, "STAFF")
            );
        }

        return Map.of("status", "error", "message", "Invalid credentials");
    }


}
