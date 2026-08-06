/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eus.martico.fileTransfer.login.control;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import eus.martico.fileTransfer.model.LoginException;
import eus.martico.fileTransfer.model.usuario.UsuarioVO;
import eus.martico.fileTransfer.seguridad.auth.api.UserAuthenticationService;
import eus.martico.fileTransfer.seguridad.model.JWTScopes;
import eus.martico.fileTransfer.seguridad.model.login.LoginDTO;
import eus.martico.fileTransfer.seguridad.model.login.LoginErrorDTO;
import eus.martico.fileTransfer.seguridad.service.loginToken.LoginTokenIService;
import eus.martico.fileTransfer.seguridad.service.loginToken.LoginTokenServiceException;
import eus.martico.fileTransfer.seguridad.service.user.LoginUserIService;
import eus.martico.fileTransfer.seguridad.user.entity.SegurUser;
import eus.martico.fileTransfer.services.usuario.UsuarioIService;
import eus.martico.fileTransfer.services.usuario.UsuarioServiceException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;


/**
 * <p>
 * A simple servlet taking advantage of features added in 3.0.
 * </p>
 * <p>
 * The servlet is registered and mapped to /HelloServlet using the {@linkplain WebServlet
 * </p>
 *
 * @author Martico SL
 * @HttpServlet}. The {@link HelloService} is injected by CDI.
 */
@SuppressWarnings("serial")
@RestController
@RequestMapping({"/refresh"})
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor(access = PACKAGE)
public class RefreshController extends HttpServlet {

	protected static final Logger logger = LoggerFactory.getLogger(RefreshController.class);
//	private static boolean test_mode = LoginConfiguration.getBooProperty("login.test");
	
    @NonNull
    UsuarioIService usuarioService;

    @NonNull
    LoginUserIService loginUser;

    @NonNull
    UserAuthenticationService authentication;

    @NonNull
    @Autowired
    LoginTokenIService loginTokenService;


    @RequestMapping(method = RequestMethod.POST)
    LoginDTO RefreshLogin(@AuthenticationPrincipal final SegurUser user,	
    						HttpServletRequest req, 
    						HttpServletResponse resp) throws LoginException, IOException  {
        
    	if(user == null){
    		throw new LoginException(LoginException.LOGIN_REFRESH_ERROR, "Token invalido");
    	}
        boolean esRefreshToken=false;
        Collection<? extends GrantedAuthority> au = user.getAuthorities();
        for (GrantedAuthority ga: au) {
        	if (ga.getAuthority().equals(JWTScopes.REFRESH_TOKEN.authority())) {
        		esRefreshToken = true;
        	}
        }
        UsuarioVO usuario = null;
        if (esRefreshToken) {
	        // Si estamos en test_mode hacemos el login desde archivo...txt sin certificado
	        /*********************************************************
	         * FALTA quitar la validacion del certificado ??????????
	         * si decidimos no volver a verificarlo ???????????? 
	         */
//	        if (!test_mode) {
//		        try {
//			        X509Certificate certificate = extractCertificate(req);
//			        logger.debug(certificate.getIssuerDN().getName());
//			        logger.debug(certificate.getSerialNumber().toString());
//			        //logger.debug(certificate.getSubjectDN().getName());
//			        logger.debug(certificate.getNotAfter().toString());
//			        logger.debug(certificate.getNotBefore().toString());
//			        logger.debug(certificate.getSubjectX500Principal().toString());
//			        try {
//				        usuarioDTO = VerificacionCertificadoX509.verificar(certificate);
//					} catch (Exception ex) {
//						logger.error(ex.getMessage());
//						if (ex instanceof LoginException ) {
//							LoginException loginex = (LoginException) ex;
//							loginError.setCodigo(loginex.getCodigo());
//							loginError.setDescripcion(ex.getMessage());										
//						} else {
//							loginError.setCodigo(LoginException.LOGIN_EXCEPTION_92);
//							loginError.setDescripcion(ex.getMessage());					
//						}
//					}
//				} catch (Exception ex) {
//					logger.error(ex.getMessage(), ex);
//					loginError.setCodigo(LoginException.LOGIN_EXCEPTION_91);
//					loginError.setDescripcion(ex.getMessage());
//				}
	    		if(user.getUsername() == null) {
	    			throw new LoginException(LoginException.LOGIN_ERROR_USERNAME, "El usuario esta vacio");
	    		} else {
    				try {
						usuario = usuarioService.findById(user.getUsername());
					} catch (UsuarioServiceException e) {
						throw new LoginException(e.getCodigoError(), e.getMsgError());
					}
    				if(usuario == null) {
    					throw new LoginException(LoginException.LOGIN_ERROR_USERNAME, "El usuario no existe");
    				}
    				try {
						return loginTokenService.generateTokens(user, usuario, !esRefreshToken);
					} catch (LoginTokenServiceException e) {
						throw new LoginException(e.getCodigoError(), e.getMsgError());
					}
	    		}
//	        } else {
//	        	logger.debug("RefreshLogin modo test");
//	        	logger.info("RefreshLogin modo test");
//	        	logger.warn("RefreshLogin modo test");
//	        	logger.trace("RefreshLogin modo test");
//	        	logger.error("RefreshLogin modo test");
//	        	usuarioDTO = VerificacionCertificadoX509.obtenerDatosUsuarioFromSubject();
//	        }
        } else {
			throw new LoginException(LoginException.LOGIN_REFRESH_ERROR, "Token invalido");
        }
//        return doRespuesta(req, resp, usuarioDTO, loginError);
    }

//    protected LoginDTO doRespuesta(HttpServletRequest _req, HttpServletResponse _resp, UsuarioDTO _usuarioDTO, LoginErrorDTO _loginError) throws LoginException, IOException  {
//    	return doRespuesta( _req,  _resp,  _usuarioDTO,  _loginError, false);
//    }
//    protected LoginDTO doRespuesta(HttpServletRequest _req, HttpServletResponse _resp, UsuarioDTO _usuarioDTO, LoginErrorDTO _loginError, boolean test) throws LoginException, IOException  {
//        LoginDTO loginDTO = null;
//        LoginErrorDTO loginError;
//        if (_loginError==null)
//        	loginError = new LoginErrorDTO();
//        else
//        	loginError = _loginError;
//        
//        _resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        try {
//			if (!StringUtils.isEmpty(loginError.getCodigo())) {
//				// Si viene con error
//				if (loginDTO==null) {
//					loginDTO = new LoginDTO();
//					loginDTO.setLoginError(loginError);
//				}
//			} else {
//		        if (_usuarioDTO!=null) {
//		        	loginDTO = generateTokens(_req, _usuarioDTO, test);
//		        } else {
//					loginError.setCodigo("01");
//					loginError.setDescripcion("Error obteniendo los datos del usuario");
//		        }
//			}
//		} catch (DAOException ex) {
//			logger.error(ex.getMessage(), ex);
//			loginError.setCodigo(LoginException.LOGIN_EXCEPTION_93);
//			loginError.setDescripcion(ex.getMessage());
//		} catch (Exception ex) {
//			logger.error(ex.getMessage(), ex);
//			loginError.setCodigo(LoginException.LOGIN_EXCEPTION_94);
//			loginError.setDescripcion(ex.getMessage());
//		} finally {
//			try {
//		        if (loginDTO==null)
//		        	loginDTO = new LoginDTO();
//				loginDTO.setLoginError(loginError);
//			} catch (Exception e) {
//				logger.error(e.getMessage(), e);
//				_resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			}
//		}
//		return loginDTO;
//    }
//
//
//    protected X509Certificate extractCertificate(HttpServletRequest req) throws LoginException {
//        X509Certificate[] certs = (X509Certificate[]) req.getAttribute("javax.servlet.request.X509Certificate");
//        if (null != certs && certs.length > 0) {
//            return certs[0];
//        }
//       	throw new LoginException(LoginException.LOGIN_ERROR_CERTIFICADO, "No se ha encontrado un certificado digital del cliente.");
//    }
//
////    public static String getPemFromCertificate(X509Certificate certificate) {
////        if (certificate != null) {
////            StringBuilder writer = new StringBuilder();
////            writer.append("<BR/>");
////            writer.append("getSubjectDN = ");
////            writer.append(certificate.getSubjectDN());
////            writer.append("<BR/>");
////            writer.append("getIssuerDN = ");
////            writer.append(certificate.getIssuerDN());
////            writer.append("<BR/>");
////            writer.append("getSerialNumber = ");
////            writer.append(certificate.getSerialNumber());
////            writer.append("<BR/>");
////            writer.append("getSubjectX500Principal = ");
////            writer.append(certificate.getSubjectX500Principal());
////            writer.append("<BR/>");
////            writer.append("getType = ");
////            writer.append(certificate.getType());
////
////            return writer.toString();
////        } else {
////            return null;
////        }
////    }
//
//    private LoginDTO generateTokens(HttpServletRequest request, UsuarioDTO usuarioDTO, boolean _usernameYpassw)
//            throws IOException, DAOException, LoginException {
//
//		SegurUser user = null;
//		UsuarioVO usuario = null;
//		// Buscamos el notario que le corresponde al usuario mediante el NIF y si hay CIF
//		//EnisUserNotarioIService userNotarioIService = new EnisUserNotarioServiceImp(marticoDS);
//		usuario = usuarioService.findById(usuarioDTO.getName());
//		if (usuario==null) {
//			// El usuario no esta en la BD
//			StringBuilder texto = new StringBuilder("Usuario no encontrado en ENIS.");
//			try {
//				texto.append(" dni=");
//				texto.append(usuarioDTO.getDni());
//				texto.append(" notaria=");
//				texto.append(usuarioDTO.getCodNotaria());
//			} catch (Exception e) {
//				//e.printStackTrace();
//			}
//			throw new NoSuchElementException(texto.toString());				
//		}
//		if (!_usernameYpassw) {
//			// Creamos un usuario con los datos que tenemos, lo guardamos y lo logeamos 
//			user = new SegurUser(usuario.getNi().toString(), 
//									usuarioDTO.getDni(), 
//									usuarioDTO.getName(),
//									usuarioDTO.getSerialNumber());
//			
//		} else {
//			// Creamos un usuario con los datos que tenemos, lo guardamos y lo logeamos 
//			user = new SegurUser(usuario.getNi().toString(), 
//									usuario.getNif(),
//									usuarioDTO.getName(),
//									usuarioDTO.getSerialNumber());	
//		}
//		for (String role: usuario.getRoles()) {
//			user.addAuthority(new SimpleGrantedAuthority(role));				
//		}
//		
//		boolean usuarioYaExiste = false;
//		//LoginUserIService loginUser = new LoginUserServiceImp(marticoDS);
//		try {
//			loginUser.save(user);
//		} catch (DAOException ex) {
//			usuarioYaExiste=ex.isClaveDuplicada();
//		}
//
//		// hacemos login en el sistema antiguo username-password
//		Optional<String> opToken = authentication.login(user);
//		if (!opToken.isPresent()) {
//			throw new LoginException("No se ha podido generar un token");
//		}
//		
//		Optional<String> opRefreshToken = authentication.refreshToken(user);
//		if (!opRefreshToken.isPresent()) {
//			throw new LoginException("No se ha podido generar un refresh token");
//		}
//		
//		// Montamos la respuesta
//		LoginTokenDTO loginToken = new LoginTokenDTO();
//		loginToken.setJWToken(opToken.get());
//		loginToken.setRefreshToken(opRefreshToken.get());
//		loginToken.setTimeStamp(LocalDateTime.now());
//		loginToken.setExpirationMin(authentication.getExpirationMin());
//		
//		LoginDatosDTO loginDatos = new LoginDatosDTO();
//		loginDatos.setNi(usuario.getNi().toString());
//		loginDatos.setNif(usuario.getNif());
//		loginDatos.setUsername(usuario.getNif());
//		loginDatos.setNombre(usuario.getNombre());
//		
//		LoginDTO loginDTO = new LoginDTO();
//		
//		loginDTO.setLoginDatos(loginDatos);
//		loginDTO.setLoginToken(loginToken);
//		
//		return loginDTO;
//		
//	}

    @ExceptionHandler({IOException.class, LoginException.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public LoginDTO handleException(Exception ex) {
    	//Quitar el mensaje de error y poner otro
    	LoginErrorDTO loginError = new LoginErrorDTO();
    	if(ex instanceof LoginException) {
    		LoginException loginEx = (LoginException) ex;
	    	loginError.setCodigo(loginEx.getCodigo());
	    	loginError.setDescripcion(ex.getMessage());
    	} else {
	    	loginError.setCodigo(Integer.toString(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
	    	loginError.setDescripcion(ex.getMessage());
    	}
    	LoginDTO loginDTO = new LoginDTO();
    	loginDTO.setLoginError(loginError);
		return loginDTO;
    }

}
