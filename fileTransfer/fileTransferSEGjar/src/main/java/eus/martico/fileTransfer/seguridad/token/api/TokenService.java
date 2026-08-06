package eus.martico.fileTransfer.seguridad.token.api;

import java.util.Map;


/**
 * Creates and validates credentials.
 */
public interface TokenService {

  String permanent(Map<String, Object> attributes);

  String expiring(Map<String, Object> attributes);

  /**
   * Checks the validity of the given credentials.
   *
   * @param token
   * @return attributes if verified
   */
  Map<String, String> untrusted(String token);

  /**
   * Checks the validity of the given credentials.
   *
   * @param token
   * @return attributes if verified
   */
  Map<String, String> verify(String token);
  
  Map<String, String> verifyWithException(final String token) 
		  throws TokenServiceException;

  String refreshToken(final Map<String, Object> attributes);
  
  int getExpirationMin();

  int getRefreshExpirationMin();
}
