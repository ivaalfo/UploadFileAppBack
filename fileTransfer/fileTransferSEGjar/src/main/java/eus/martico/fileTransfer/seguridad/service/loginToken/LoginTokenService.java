package eus.martico.fileTransfer.seguridad.service.loginToken;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import eus.martico.fileTransfer.model.LoginException;
import eus.martico.fileTransfer.model.usuario.UsuarioVO;
import eus.martico.fileTransfer.seguridad.auth.api.UserAuthenticationService;
import eus.martico.fileTransfer.seguridad.model.login.LoginDTO;
import eus.martico.fileTransfer.seguridad.model.login.LoginDatosDTO;
import eus.martico.fileTransfer.seguridad.model.login.LoginTokenDTO;
import eus.martico.fileTransfer.seguridad.service.user.LoginUserIService;
import eus.martico.fileTransfer.seguridad.service.user.LoginUserServiceException;
import eus.martico.fileTransfer.seguridad.token.api.TokenServiceException;
import eus.martico.fileTransfer.seguridad.user.entity.SegurUser;
import lombok.NonNull;

@Service
public class LoginTokenService implements LoginTokenIService {

    @NonNull
    @Autowired
    LoginUserIService loginUserService;

    @NonNull
    @Autowired
    UserAuthenticationService authenticationService;

    @Override
    public LoginDTO generateTokens(final SegurUser _user, final UsuarioVO _usuario) 
    		throws IOException, LoginTokenServiceException, LoginException {
    	return generateTokens( _user, _usuario, true);
	}
    
    @Override
    public LoginDTO generateTokens(final SegurUser _user, final UsuarioVO _usuario, boolean _guardarLoginEnBD)
            throws IOException, LoginTokenServiceException, LoginException {

    	if (_user.getAuthorities().isEmpty()) {
			for (String role: _user.getRoles()) {
				_user.addAuthority(new SimpleGrantedAuthority(role));				
			}
    	}
		
		@SuppressWarnings("unused")
		boolean usuarioYaExiste = false;
		try {
	    	//Registar los loggings, podemos quitar el registro en BD poniendo _guardarLoginEnBD=false siempre 			
			loginUserService.save(_user, _guardarLoginEnBD);
		} catch (LoginUserServiceException ex) {
			usuarioYaExiste=ex.isClaveDuplicada();
		}

		//Hacemos login en el sistema antiguo username-password
		Optional<String> opToken = authenticationService.login(_user);
		if (!opToken.isPresent()) {
			throw new LoginException("No se ha podido generar un token");
		}
		
		try {
			@SuppressWarnings("unused")
			Map<String, String> mapToken = authenticationService.verifyToken(opToken.get());
		} catch (TokenServiceException e) {
			throw new LoginException(LoginException.LOGIN_EXCEPTION_91, e);
		}
		
		Optional<String> opRefreshToken = authenticationService.refreshToken(_user);
		if (!opRefreshToken.isPresent()) {
			throw new LoginException("No se ha podido generar un refresh token");
		}
		
		//Montamos la respuesta
		LoginTokenDTO loginToken = new LoginTokenDTO();
		loginToken.setJWToken(opToken.get());
		loginToken.setRefreshToken(opRefreshToken.get());
		loginToken.setTimeStamp(LocalDateTime.now());
		loginToken.setExpirationMin(authenticationService.getExpirationMin());
		
		LoginDatosDTO loginDatos = new LoginDatosDTO();
		loginDatos.setNi(_usuario.getW2usuky().toString());
		loginDatos.setNif(_usuario.getClicif());
		loginDatos.setUsername(_usuario.getW2usuario());
		loginDatos.setNombre(_usuario.getW2usuario());
		loginDatos.setRoles(_user.getRoles());
		
		LoginDTO loginDTO = new LoginDTO();
		
		loginDTO.setLoginDatos(loginDatos);
		loginDTO.setLoginToken(loginToken);
		
		return loginDTO;
		
	}

	@Override
	public void logout(final SegurUser _user) throws LoginTokenServiceException, LoginException {
		authenticationService.logout(_user);
	}

}
