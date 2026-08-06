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
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eus.martico.fileTransfer.config.FileTransferConfiguration;
import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.model.LoginException;
import eus.martico.fileTransfer.model.usuario.UsuarioVO;
import eus.martico.fileTransfer.seguridad.model.login.LoginDTO;
import eus.martico.fileTransfer.seguridad.model.login.LoginErrorDTO;
import eus.martico.fileTransfer.seguridad.model.usuario.UsuarioDTO;
import eus.martico.fileTransfer.seguridad.service.loginToken.LoginTokenIService;
import eus.martico.fileTransfer.seguridad.service.loginToken.LoginTokenServiceException;
import eus.martico.fileTransfer.seguridad.user.entity.SegurUser;
import eus.martico.fileTransfer.services.seguridad.password.PasswordException;
import eus.martico.fileTransfer.services.usuario.UsuarioIService;
import eus.martico.fileTransfer.services.usuario.UsuarioServiceException;
import eus.martico.fileTransfer.utils.HttpUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;


/**
 * <p>
 * Login controller
 * </p>
 *
 * @author Martico
 * @HttpServlet}. The {@link HelloService} is injected by CDI.
 */
@SuppressWarnings("serial")
@RestController
@RequestMapping({ "/login" })
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor(access = PACKAGE)
public class LoginController extends HttpServlet {

	protected static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@NonNull
	UsuarioIService usuarioService;


	@NonNull
	@Autowired
	LoginTokenIService loginTokenService;

	
	@RequestMapping(method = RequestMethod.GET)
	LoginDTO getLoginPassword(@AuthenticationPrincipal final SegurUser _user, HttpServletRequest _req,
			HttpServletResponse _resp) throws LoginException, IOException {
		return LoginPassword(_user, _req, _resp);
	}

	@RequestMapping(method = RequestMethod.POST)
	LoginDTO LoginPassword(@AuthenticationPrincipal final SegurUser user, HttpServletRequest req,
			HttpServletResponse resp) throws LoginException, IOException {

		String username = user.getUsername();
		String password = user.getPassword();

		UsuarioVO usuario = null;
		if (username == null) {
			throw new LoginException(LoginException.LOGIN_ERROR_USERNAME, "El usuario está vacío");
		} else {
			try {
				usuario = usuarioService.findById(username);
				if (usuario == null) {
					throw new LoginException(LoginException.LOGIN_ERROR_USERNAME, "El usuario no existe");
				} else {
					//Si el usuario no tiene contraseña no es correcto
					if (password == null || password.length() == 0) {
						throw new LoginException(LoginException.LOGIN_ERROR_PASSWORD, "La contraseña está vacía");
					} else if (usuario.isBaja()) {
						throw new LoginException(LoginException.LOGIN_ERROR_PASSWORD, "El usuario está dado de baja");
					} else if (usuario.getRol().equals(FileTransferConstantes.PROPERTY_KEY_ROL_NO_ACCESS)){
						throw new LoginException(LoginException.LOGIN_ERROR_USERROL, "El usuario no tiene permisos de acceso");
					} else {
						LoginDTO loginDTO = null;
						if (FileTransferConfiguration.getBooProperty(FileTransferConstantes.PROPERTY_KEY_LOGIN_MODO_MOCK, false)) {
							String passwordMock = FileTransferConfiguration.getProperty(FileTransferConstantes.PROPERTY_KEY_LOGIN_MODO_MOCK_PASSWORD);
							if (passwordMock != null && passwordMock.equals(password)) {
								loginDTO = loginTokenService.generateTokens(user, usuario);
							}
						}
						if (loginDTO == null) {
							//Comprobamos la password
							if (usuarioService.verifyPassword(usuario, password)) {
								loginDTO = loginTokenService.generateTokens(user, usuario);
							} else {
								throw new LoginException(LoginException.LOGIN_ERROR_PASSWORD, "La contraseña es incorrecta");
							}
						}
						if (loginDTO != null) {
							if (!loginDTO.isError() && loginDTO.getLoginToken() != null
									&& !StringUtils.isEmpty(loginDTO.getLoginToken().getJWToken())) {
								//Si el login es correcto, lanzamos proceso asincrono
								try {
									HttpUtils.consumirServicioRESTAsyncAuthorBearer("http", "localhost", 9080,
											"/fileTransferWAR/api/v1/postlogin", "GET", null,
											loginDTO.getLoginToken().getJWToken(), 10);
									logger.info("despues de consumirServicioRESTAsync");
								} catch (Exception e) {
									logger.error("consumirServicioRESTAsyncAuthorBearer", e);
								}
							}
						}
						String[] loginRol = loginDTO.getLoginDatos().getRoles();
						if(loginRol[0].equals("FTEXTER") || loginRol[0].equals("FTCONSUL") || loginRol[0].equals("FTINTER") || loginRol[0].equals("FTADM")){
							String manualFNes = FileTransferConfiguration.getProperty(FileTransferConstantes.MAN_PROV_FILE_NAME_ES);
							loginDTO.getLoginDatos().setManFileNameES(manualFNes);
							String manualFNeng = FileTransferConfiguration.getProperty(FileTransferConstantes.MAN_PROV_FILE_NAME_EN);
							loginDTO.getLoginDatos().setManFileNameEN(manualFNeng);
						}
						return loginDTO;
					}
				}
			} catch (UsuarioServiceException e) {
				throw new LoginException(e.getCodigoError(), e.getMsgError());
			} catch (PasswordException e) {
				throw new LoginException(e.getMessage());
			} catch (LoginTokenServiceException e) {
				throw new LoginException(e.getCodigoError(), e.getMsgError());
			}
		}
	}

	protected LoginDTO doRespuesta(HttpServletRequest _req, HttpServletResponse _resp, UsuarioDTO _usuarioDTO,
			LoginErrorDTO _loginError) throws LoginException, IOException {
		return doRespuesta(_req, _resp, _usuarioDTO, _loginError, false);
	}

	protected LoginDTO doRespuesta(HttpServletRequest _req, HttpServletResponse _resp, UsuarioDTO _usuarioDTO,
			LoginErrorDTO _loginError, boolean test) throws LoginException, IOException {
		LoginDTO loginDTO = null;
		LoginErrorDTO loginError;
		if(_loginError == null){
			loginError = new LoginErrorDTO();
		} else {
			loginError = _loginError;
		}
		_resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
		try {
			if(!StringUtils.isEmpty(loginError.getCodigo())) {
				//Si viene con error
				if(loginDTO == null) {
					loginDTO = new LoginDTO();
					loginDTO.setLoginError(loginError);
				}
			} else {
				if(_usuarioDTO != null) {
					loginDTO = generateTokensPaso1(_req, _usuarioDTO, test);
				} else {
					loginError.setCodigo("01");
					loginError.setDescripcion("Error obteniendo los datos del usuario");
				}
			}
		} catch (LoginTokenServiceException ex) {
			logger.error((ex.getMsgError() == null) ? ex.getMessage() : ex.getMsgError(), ex);
			loginError.setCodigo(LoginException.LOGIN_EXCEPTION_93);
			loginError.setDescripcion(ex.getMessage());
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			loginError.setCodigo(LoginException.LOGIN_EXCEPTION_94);
			loginError.setDescripcion(ex.getMessage());
		} finally {
			try {
				if(loginDTO == null){
					loginDTO = new LoginDTO();
				}
				loginDTO.setLoginError(loginError);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				_resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
		return loginDTO;
	}

	private LoginDTO generateTokensPaso1(HttpServletRequest request, UsuarioDTO usuarioDTO, boolean _usernameYpassw)
			throws IOException, LoginTokenServiceException, LoginException, UsuarioServiceException {

		SegurUser user = null;
		UsuarioVO usuario = null;
		//Buscamos el notario que le corresponde al usuario mediante el NIF y si hay CIF
		usuario = usuarioService.findById(usuarioDTO.getName());
		if(usuario == null) {
			//El usuario no esta en la BD
			StringBuilder texto = new StringBuilder("Usuario no encontrado en BBDD.");
			try {
				texto.append(" dni=");
				texto.append(usuarioDTO.getDni());
				//texto.append(" notaria=");
				//texto.append(usuarioDTO.getCodNotaria());
			} catch (Exception e) {
				// e.printStackTrace();
			}
			throw new NoSuchElementException(texto.toString());
		}
		if(!_usernameYpassw) {
			//Creamos un usuario con los datos que tenemos, lo guardamos y lo logeamos
			user = new SegurUser(usuario.getNi().toString(), usuarioDTO.getCif(), usuarioDTO.getName(),
					usuarioDTO.getSerialNumber());
		} else {
			//Creamos un usuario con los datos que tenemos, lo guardamos y lo logeamos
			user = new SegurUser(usuario.getNi().toString(), usuario.getNif(), usuario.getUsername(),
					usuario.getVisiblePassword());
		}
		return loginTokenService.generateTokens(user, usuario);
	}

	@ExceptionHandler({ IOException.class, LoginException.class })
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
