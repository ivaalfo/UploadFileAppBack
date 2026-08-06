package eus.martico.fileTransfer.seguridad.security;

import static java.util.Optional.ofNullable;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang.StringUtils.removeStart;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import eus.martico.fileTransfer.config.FileTransferConfiguration;
import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.seguridad.config.SEGConstantes;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = PRIVATE, makeFinal = true)
final class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
  
  TokenAuthenticationFilter(final RequestMatcher requiresAuth) {
    super(requiresAuth);
  }

  @Override
  public Authentication attemptAuthentication(
    final HttpServletRequest request,
    final HttpServletResponse response) {
//	  Enumeration<String> headerNames = request.getHeaderNames();
//
//      while (headerNames.hasMoreElements()) {
//
//          String headerName = headerNames.nextElement();
//          System.out.println("Nombre: " +headerName);
//
//          Enumeration<String> headers = request.getHeaders(headerName);
//          while (headers.hasMoreElements()) {
//              String headerValue = headers.nextElement();
//              System.out.println("Valor: " + headerValue);
//          }
//
//      }
    final String param = ofNullable(request.getHeader(FileTransferConstantes.SECURITY_AUTHORIZATION)).orElse(request.getParameter("t"));
    String token = null;
    // Si param es null puede ser que sea la primera vez que entra el usuario en home y en este caso le dejamos si el token esta en sesion
    if ( param==null && FileTransferConfiguration.getBooProperty(FileTransferConstantes.PROPERTY_KEY_LOGIN_MODO_TEST, false)	) {
   		token = FileTransferConfiguration.getProperty(FileTransferConstantes.PROPERTY_KEY_LOGIN_MODO_TEST_TOKEN);
    } else {
	    token = ofNullable(param)
	      .map(value -> removeStart(value, FileTransferConstantes.SECURITY_BEARER))
	      .map(String::trim)
	      .orElseThrow(() -> new BadCredentialsException("Missing Authentication Token"));
    }
    request.setAttribute(SEGConstantes.TOKEN_TOVERIFY_KEY, token);
    final Authentication auth = new UsernamePasswordAuthenticationToken(token, token);
    Authentication returnAuth = getAuthenticationManager().authenticate(auth);
    return returnAuth;
  }

  @Override
  protected void successfulAuthentication(
    final HttpServletRequest request,
    final HttpServletResponse response,
    final FilterChain chain,
    final Authentication authResult) throws IOException, ServletException {
    super.successfulAuthentication(request, response, chain, authResult);
    chain.doFilter(request, response);
  }
  
}