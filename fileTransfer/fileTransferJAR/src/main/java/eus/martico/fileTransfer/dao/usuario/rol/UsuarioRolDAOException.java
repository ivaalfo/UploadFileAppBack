package eus.martico.fileTransfer.dao.usuario.rol;

import eus.martico.fileTransfer.dao.DAOException;

@SuppressWarnings("serial")
public class UsuarioRolDAOException extends DAOException {
	
	public UsuarioRolDAOException(String message) {
		super(message);
	}
	public UsuarioRolDAOException(String codigoErr, String message) {
		super(codigoErr, message);
	}

}
