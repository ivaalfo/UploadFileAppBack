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

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import eus.martico.fileTransfer.model.LoginException;
import eus.martico.fileTransfer.seguridad.auth.api.UserAuthenticationService;
import eus.martico.fileTransfer.seguridad.model.login.LoginDTO;
import eus.martico.fileTransfer.seguridad.model.login.LoginErrorDTO;
import eus.martico.fileTransfer.seguridad.service.loginToken.LoginTokenIService;
import eus.martico.fileTransfer.seguridad.service.loginToken.LoginTokenServiceException;
import eus.martico.fileTransfer.seguridad.service.user.LoginUserIService;
import eus.martico.fileTransfer.seguridad.user.entity.SegurUser;
import eus.martico.fileTransfer.services.usuario.UsuarioIService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

/**
 *
 * @author Martico SL
 */
@SuppressWarnings("serial")
@RestController
@RequestMapping({"/logout"})
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor(access = PACKAGE)
public class LogoutController extends HttpServlet {

	protected static final Logger logger = LoggerFactory.getLogger(LogoutController.class);
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
    void logout(@AuthenticationPrincipal final SegurUser user	
    /*,						HttpServletRequest req, 
    						HttpServletResponse resp*/) throws LoginException, IOException  {
        
        //Si estamos en test_mode hacemos el login desde archivo...txt sin certificado
		if(user == null) {
			throw new LoginException(LoginException.LOGIN_ERROR_USERNAME, "El usuario no existe");
		}
		if(user.getUsername() == null) {
			throw new LoginException(LoginException.LOGIN_ERROR_USERNAME, "El usuario esta vacio");
		}
		try {
			loginTokenService.logout(user);
		} catch (LoginTokenServiceException e) {
			throw new LoginException(e.getCodigoError(), e.getMsgError());
		}
    }

    @ExceptionHandler({IOException.class, LoginException.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public LoginDTO handleException(Exception ex) {
    	//Quitar el mensaje de error y poner otro
    	LoginErrorDTO loginError = new LoginErrorDTO();
    	if (ex instanceof LoginException) {
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
