package eus.martico.fileTransfer.dao.usuario;

import eus.martico.fileTransfer.dao.DAOException;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@SuppressWarnings("serial")
public class UsuarioDAOException extends DAOException {


	public UsuarioDAOException(DAOException _ex) {
		super(_ex);
	}
	public UsuarioDAOException(String message) {
		super(message);
	}
	public UsuarioDAOException(String codigoErr, String message) {
		super(codigoErr, message);
	}
}

