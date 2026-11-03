package property.manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import property.manager.dto.user.UserLoginRequestDto;
import property.manager.dto.user.UserLoginResponseDto;
import property.manager.service.AuthenticationService;

@Tag(name = "01. Authentication")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "User login",
            description = "Authenticates the user and returns a JWT token")
    @PostMapping("/login")
    public UserLoginResponseDto login(@Valid @RequestBody UserLoginRequestDto request) {
        return authenticationService.authenticate(request);
    }
}
