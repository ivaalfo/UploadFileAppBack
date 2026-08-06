package eus.martico.fileTransfer.services.login;

import eus.martico.fileTransfer.services.MyServiceException;

@SuppressWarnings("serial")
public class LoginServiceException extends MyServiceException {

	public LoginServiceException(String _msgError) {
		super(_msgError);
	}

	public LoginServiceException(Exception _ex) {
		super(_ex);
	}

	public LoginServiceException(String _codigoError, String _msgError) {
		super(_codigoError, _msgError);
	}

}
