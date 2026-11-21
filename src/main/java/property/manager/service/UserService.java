package property.manager.service;

import org.springframework.security.core.Authentication;
import property.manager.dto.user.UserResponseDto;

public interface UserService {
    UserResponseDto getLoggedInUserInfo(Authentication authentication);
}
