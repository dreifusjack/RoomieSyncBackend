package roomiesync.roomiesync_backend.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JWTService {
  String generateToken(String email);

  String extractEmail(String token);

  boolean validateToken(String token, UserDetails userDetails);
}
