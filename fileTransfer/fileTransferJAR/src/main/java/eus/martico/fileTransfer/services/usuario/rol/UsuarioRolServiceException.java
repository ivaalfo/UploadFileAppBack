package eus.martico.fileTransfer.services.usuario.rol;

import eus.martico.fileTransfer.services.MyServiceException;

@SuppressWarnings("serial")
public class UsuarioRolServiceException extends MyServiceException {

	public UsuarioRolServiceException(String _msgError) {
		super(_msgError);
	}

	public UsuarioRolServiceException(Exception _ex) {
		super(_ex);
	}

	public UsuarioRolServiceException(String _codigoError, String _msgError) {
		super(_codigoError, _msgError);
	}

}
