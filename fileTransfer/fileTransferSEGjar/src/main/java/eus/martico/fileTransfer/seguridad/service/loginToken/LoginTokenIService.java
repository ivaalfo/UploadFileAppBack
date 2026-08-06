package eus.martico.fileTransfer.seguridad.service.loginToken;

import java.io.IOException;

import eus.martico.fileTransfer.model.LoginException;
import eus.martico.fileTransfer.model.usuario.UsuarioVO;
import eus.martico.fileTransfer.seguridad.model.login.LoginDTO;
import eus.martico.fileTransfer.seguridad.user.entity.SegurUser;

public interface LoginTokenIService {

	LoginDTO generateTokens(final SegurUser _user, final UsuarioVO _usuario) throws IOException, LoginTokenServiceException, LoginException;
	LoginDTO generateTokens(final SegurUser _user, final UsuarioVO _usuario, final boolean _guardarLoginEnBD) throws IOException, LoginTokenServiceException, LoginException;
	void logout(final SegurUser _user) throws LoginTokenServiceException, LoginException;

}
