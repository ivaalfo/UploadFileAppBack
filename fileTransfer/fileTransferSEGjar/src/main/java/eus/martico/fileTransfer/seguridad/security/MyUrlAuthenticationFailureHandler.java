package eus.martico.fileTransfer.seguridad.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import eus.martico.fileTransfer.model.error.vo.ErrorVO;
import eus.martico.fileTransfer.model.vo.ValidateDTO;
import eus.martico.fileTransfer.seguridad.config.SEGConstantes;
import eus.martico.fileTransfer.seguridad.token.api.TokenService;
import eus.martico.fileTransfer.seguridad.token.api.TokenServiceException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

public class MyUrlAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private String defaultFailureUrl;
	private boolean forwardToDestination = false;

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Autowired
	TokenService tokenService;

	/**
	 * Performs the redirect or forward to the {@code defaultFailureUrl} if set, otherwise
	 * returns a 401 error code.
	 * <p>
	 * If redirecting or forwarding, {@code saveException} will be called to cache the
	 * exception for use in the target view.
	 */
	@Override
	public void onAuthenticationFailure(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {

		if (this.defaultFailureUrl == null) {
			logger.info("No failure URL set, sending 401 Unauthorized error");
			ValidateDTO<ErrorVO> validacion = new ValidateDTO<ErrorVO>();
			if (request.getAttribute(SEGConstantes.TOKEN_TOVERIFY_KEY)!=null) {
				String token = (String) request.getAttribute(SEGConstantes.TOKEN_TOVERIFY_KEY);
				try {
					tokenService.verifyWithException(token);
					// No deberia pasar por aqui
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					validacion.addError(new ErrorVO("04", HttpStatus.UNAUTHORIZED.getReasonPhrase()));
				} catch (TokenServiceException e) {
					if (e.getCause() instanceof ExpiredJwtException) {
//						response.sendError(HttpStatus.UNAUTHORIZED.value(),
//								"Token expired");
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						validacion.addError(new ErrorVO("02", "Token expired"));
					}
					else if (e.getCause() instanceof MalformedJwtException) {
//						response.sendError(HttpStatus.UNAUTHORIZED.value(),
//								"Token malformed");
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						validacion.addError(new ErrorVO("03", "Token malformed"));
					} else {
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						validacion.addError(new ErrorVO("05", "Token error"));						
					}
				} catch(Exception e) {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					validacion.addError(new ErrorVO("05", e.getMessage()));					
				}
			} else {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				validacion.addError(new ErrorVO("01", "Token not found"));
//				response.sendError(HttpStatus.UNAUTHORIZED.value(),
//						"Token not found");
			}
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			ObjectMapper objectMapper = new ObjectMapper();
			response.getWriter().write(objectMapper.writeValueAsString(validacion));
		} else {
			saveException(request, exception);

			if (forwardToDestination) {
				logger.debug("Forwarding to " + defaultFailureUrl);

				request.getRequestDispatcher(defaultFailureUrl).forward(request, response);
			} else {
				logger.debug("Redirecting to " + defaultFailureUrl);
				redirectStrategy.sendRedirect(request, response, defaultFailureUrl);
			}
		}
	}

}
