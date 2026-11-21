package property.manager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import property.manager.dto.user.UserLoginRequestDto;
import property.manager.dto.user.UserLoginResponseDto;
import property.manager.model.User;
import property.manager.security.JwtUtil;
import property.manager.service.AuthenticationService;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    @Value("${demo.admin.username}")
    private String demoUsername;
    @Value("${demo.admin.password}")
    private String demoPassword;

    @Override
    public UserLoginResponseDto authenticate(UserLoginRequestDto request) {
        return new UserLoginResponseDto(getToken(request.username(), request.password()));
    }

    @Override
    public UserLoginResponseDto authenticateDemo() {
        return new UserLoginResponseDto(getToken(demoUsername, demoPassword));
    }

    private String getToken(String username, String password) {
        Authentication authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        username.trim().toLowerCase(), password));
        User user = (User) authenticate.getPrincipal();
        return jwtUtil.generateToken(user.getId(), user.getUsername());
    }
}
