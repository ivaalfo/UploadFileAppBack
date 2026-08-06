package eus.martico.fileTransfer.services.usuario;

import eus.martico.fileTransfer.services.MyServiceException;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@SuppressWarnings("serial")
public class UsuarioServiceException extends MyServiceException {

	public UsuarioServiceException(String _msgError) {
		super(_msgError);
	}

	public UsuarioServiceException(Exception _ex) {
		super(_ex);
	}

	public UsuarioServiceException(String _codigoError, String _msgError) {
		super(_codigoError, _msgError);
	}

}
