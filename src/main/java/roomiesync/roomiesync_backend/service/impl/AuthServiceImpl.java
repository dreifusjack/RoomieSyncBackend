package roomiesync.roomiesync_backend.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import lombok.AllArgsConstructor;
import roomiesync.roomiesync_backend.dto.UserDto;
import roomiesync.roomiesync_backend.entity.User;
import roomiesync.roomiesync_backend.exception.ResourceNotFoundException;
import roomiesync.roomiesync_backend.mapper.UserMapper;
import roomiesync.roomiesync_backend.repository.UserRepository;
import roomiesync.roomiesync_backend.service.AuthService;
import roomiesync.roomiesync_backend.service.JWTService;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
  private UserRepository userRepository;
  private BCryptPasswordEncoder passwordEncoder;
  private AuthenticationManager authManager;
  private JWTService jwtService;

  @Override
  public UserDto registerUser(UserDto userDto) {
    User user = UserMapper.mapToUser(userDto);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    User savedUser = userRepository.save(user);

    return authResponse(savedUser, jwtService.generateToken(savedUser.getEmail()));
  }

  @Override
  public UserDto verifyUser(UserDto userDto) {
    User user = UserMapper.mapToUser(userDto);
    String userEmail = user.getEmail();
    Authentication authentication = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(userEmail, user.getPassword()));
    User verifiedUser = userRepository.findByEmail(userEmail).orElse(null);

    if (!authentication.isAuthenticated() || verifiedUser == null) {
      throw new BadCredentialsException("Invalid username or password");
    }

    return authResponse(verifiedUser, jwtService.generateToken(userEmail));
  }

  @Override
  public UserDto getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    return UserMapper.mapToUserDto(user);
  }

  @Override
  public User getCurrentUserEntity() {
    UUID userId = getCurrentUser().getId();
    return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }

  private UserDto authResponse(User savedUser, String token) {
    UserDto response = UserMapper.mapToUserDto(savedUser);
    response.setToken(token);
    response.setTokenType("Bearer");
    response.setExpiresIn(JWTServiceImpl.EXPIRATION_TIME);

    return response;
  }
}
