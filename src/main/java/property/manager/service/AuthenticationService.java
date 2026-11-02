package property.manager.service;

import property.manager.dto.user.UserLoginRequestDto;
import property.manager.dto.user.UserLoginResponseDto;

public interface AuthenticationService {
    UserLoginResponseDto authenticate(UserLoginRequestDto request);
}
