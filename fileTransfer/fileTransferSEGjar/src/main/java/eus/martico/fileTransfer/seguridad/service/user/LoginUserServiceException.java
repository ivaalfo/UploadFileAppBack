package eus.martico.fileTransfer.seguridad.service.user;

import eus.martico.fileTransfer.services.MyServiceException;

@SuppressWarnings("serial")
public class LoginUserServiceException extends MyServiceException {

	public LoginUserServiceException(String _msgError) {
		super(_msgError);
	}

	public LoginUserServiceException(Exception _ex) {
		super(_ex);
	}

	public LoginUserServiceException(String _codigoError, String _msgError) {
		super(_codigoError, _msgError);
	}

}
