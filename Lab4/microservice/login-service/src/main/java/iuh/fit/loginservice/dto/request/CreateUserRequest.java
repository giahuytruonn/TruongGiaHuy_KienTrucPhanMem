package iuh.fit.loginservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateUserRequest {
    @NotBlank
    private String username;

    @NotBlank @Email
    private String email;

    @NotBlank
    private String password; // đã được encode bên Register Service

    private String fullName;
}