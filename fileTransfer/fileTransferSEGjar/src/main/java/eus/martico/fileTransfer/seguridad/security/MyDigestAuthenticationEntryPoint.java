package eus.martico.fileTransfer.seguridad.security;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.util.StringUtils;

import eus.martico.fileTransfer.config.FileTransferConfiguration;
import eus.martico.fileTransfer.config.FileTransferConstantes;

public class MyDigestAuthenticationEntryPoint extends DigestAuthenticationEntryPoint {

	protected static final Logger logger = LoggerFactory.getLogger(MyDigestAuthenticationEntryPoint.class);
	
	private static final String PROPERTY_HTTP_ERROR_RESPONSE_HEADER = FileTransferConfiguration.getProperty(FileTransferConstantes.PROPERTY_KEY_LOGIN_HTTP_ERROR_RESPONSE_HEADER); 
	private static final Boolean PROPERTY_HTTP_ERROR_RESPONSE_MODO = FileTransferConfiguration.getBooProperty(FileTransferConstantes.PROPERTY_KEY_LOGIN_HTTP_ERROR_RESPONSE_MODO, false);
	private static final Integer PROPERTY_HTTP_ERROR_RESPONSE_CODE = FileTransferConfiguration.getIntProperty(FileTransferConstantes.PROPERTY_KEY_LOGIN_HTTP_ERROR_RESPONSE_CODE);
	
	private static final int HTTP_ERROR_RESPONSE_CODE = 
			(PROPERTY_HTTP_ERROR_RESPONSE_CODE==null?HttpStatus.UNAUTHORIZED.value():PROPERTY_HTTP_ERROR_RESPONSE_CODE);
	private static final String HTTP_ERROR_RESPONSE_HEADER = 
			(PROPERTY_HTTP_ERROR_RESPONSE_HEADER==null?null:PROPERTY_HTTP_ERROR_RESPONSE_HEADER);
	private static final boolean HTTP_ERROR_RESPONSE_MODO = 
			(PROPERTY_HTTP_ERROR_RESPONSE_MODO==null?false:PROPERTY_HTTP_ERROR_RESPONSE_MODO);
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		// compute a nonce (do not use remote IP address due to proxy farms)
		// format of nonce is:
		// base64(expirationTime + ":" + md5Hex(expirationTime + ":" + key))
		boolean isCliente = false;
		if (!StringUtils.isEmpty(HTTP_ERROR_RESPONSE_HEADER)) {
			if (!StringUtils.isEmpty(request.getHeader(HTTP_ERROR_RESPONSE_HEADER))){
				isCliente = request.getHeader(HTTP_ERROR_RESPONSE_HEADER).equals("true");
			}
		}
		
		/*
		long expiryTime = System.currentTimeMillis() + (super.getNonceValiditySeconds() * 1000);
//		String signatureValue = DigestAuthUtils.md5Hex(expiryTime + ":" + key);
		String signatureValue = DigestUtils.md5DigestAsHex(new String(
				expiryTime + ":" + super.getKey()).getBytes());
		String nonceValue = expiryTime + ":" + signatureValue;
		String nonceValueBase64 = new String(Base64.getEncoder().encode(nonceValue.getBytes()));
*/
		long expiryTime = System.currentTimeMillis() + (this.getNonceValiditySeconds() * 1000);
		String signatureValue = md5Hex(expiryTime + ":" + this.getKey());
		String nonceValue = expiryTime + ":" + signatureValue;
		String nonceValueBase64 = new String(Base64.getEncoder().encode(nonceValue.getBytes()));

		// qop is quality of protection, as defined by RFC 2617.
		// we do not use opaque due to IE violation of RFC 2617 in not
		// representing opaque on subsequent requests in same session.
		String authenticateHeader = "Digest realm=\"" + this.getRealmName() + "\", "
				+ "qop=\"auth\", nonce=\"" + nonceValueBase64 + "\"";

		if (authException instanceof NonceExpiredException) {
			authenticateHeader = authenticateHeader + ", stale=\"true\"";
		}

		if (logger.isDebugEnabled()) {
			logger.debug("WWW-Authenticate header sent to user agent: "
					+ authenticateHeader);
		}
//		if (authException instanceof BadCredentialsException) {
//			httpResponse.addHeader("WWW-Authenticate", authenticateHeader);
//			httpResponse.sendError(HttpStatus.FORBIDDEN.value(),
//					HttpStatus.UNAUTHORIZED.getReasonPhrase());
//		} else {
			httpResponse.addHeader("WWW-Authenticate", authenticateHeader);
			logger.debug("isCliente="+isCliente);
			logger.info("HTTP_ERROR_RESPONSE_MODO="+HTTP_ERROR_RESPONSE_MODO);
			if (isCliente || HTTP_ERROR_RESPONSE_MODO)
				httpResponse.sendError(HTTP_ERROR_RESPONSE_CODE, HttpStatus.UNAUTHORIZED.getReasonPhrase());
			else
				httpResponse.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
//		}
	}

	static String md5Hex(String data) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("No MD5 algorithm available!");
		}

		return new String(Hex.encode(digest.digest(data.getBytes())));
	}

}
