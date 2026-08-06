package eus.martico.fileTransfer.services.bloqueo;

import eus.martico.fileTransfer.services.MyServiceException;

@SuppressWarnings("serial")
public class BloqueoServiceException extends MyServiceException {

	public BloqueoServiceException(String _msgError) {
		super(_msgError);
	}

	public BloqueoServiceException(Exception _ex) {
		super(_ex);
	}

	public BloqueoServiceException(String _codigoError, String _msgError) {
		super(_codigoError, _msgError);
	}

	public BloqueoServiceException(MyServiceException _ex) {
		super(_ex);
	}

}
