package property.manager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import property.manager.dto.user.UserLoginRequestDto;
import property.manager.dto.user.UserLoginResponseDto;
import property.manager.model.User;
import property.manager.repository.UserRepository;
import property.manager.security.JwtUtil;
import property.manager.service.AuthenticationService;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Override
    public UserLoginResponseDto authenticate(UserLoginRequestDto request) {
        Authentication authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        request.username().trim().toLowerCase(),
                        request.password()));
        User user = (User) authenticate.getPrincipal();
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        return new UserLoginResponseDto(token);
    }
}
