package eus.martico.fileTransfer.seguridad.service.loginToken;

import eus.martico.fileTransfer.services.MyServiceException;

@SuppressWarnings("serial")
public class LoginTokenServiceException extends MyServiceException {

	public LoginTokenServiceException(String _msgError) {
		super(_msgError);
	}

	public LoginTokenServiceException(Exception _ex) {
		super(_ex);
	}

	public LoginTokenServiceException(String _codigoError, String _msgError) {
		super(_codigoError, _msgError);
	}

}
