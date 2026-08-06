package eus.martico.fileTransfer.seguridad.auth.api;

import java.util.Map;
import java.util.Optional;

import eus.martico.fileTransfer.seguridad.token.api.TokenServiceException;
import eus.martico.fileTransfer.seguridad.user.entity.SegurUser;

public interface UserAuthenticationService {

  /**
   * Logs in with the given {@code username} and {@code password}.
   *
   * @param username
   * @param password
   * @return an {@link Optional} of a user when login succeeds
   */
  Optional<String> login(SegurUser user);

  /**
   * Finds a user by its dao-key.
   *
   * @param token user dao key
   * @return
   */
  Optional<SegurUser> findByToken(String token);

  /**
   * Logs out the given input {@code user}.
   *
   * @param user the user to logout
   */
  void logout(SegurUser user);
  
  Optional<String> refreshToken(SegurUser user);
  
  int getExpirationMin();
  
  boolean estaLogeado(SegurUser user);
  
  Map<String, String> verifyToken(String token) throws TokenServiceException;
  
}
