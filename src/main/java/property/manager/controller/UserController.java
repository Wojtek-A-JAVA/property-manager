package property.manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import property.manager.dto.user.UserResponseDto;
import property.manager.service.UserService;

@Tag(name = "13. Users")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get logged in user info",
            description = "Provides user information for logged in user")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    @GetMapping("/me")
    public UserResponseDto getLoggedInUserInfo(Authentication authentication) {
        return userService.getLoggedInUserInfo(authentication);
    }
}
