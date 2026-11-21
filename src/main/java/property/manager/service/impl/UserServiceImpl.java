package property.manager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import property.manager.dto.user.UserResponseDto;
import property.manager.exception.EntityNotFoundException;
import property.manager.mapper.UserMapper;
import property.manager.model.User;
import property.manager.repository.UserRepository;
import property.manager.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto getLoggedInUserInfo(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow(
                () -> new EntityNotFoundException("User " + authentication.getName()
                        + " not found in database"));
        return userMapper.toDto(user);
    }
}
