package eus.martico.fileTransfer.dao.bloqueo;

import eus.martico.fileTransfer.dao.DAOException;

@SuppressWarnings("serial")
public class BloqueoDAOException extends DAOException {

	public BloqueoDAOException(DAOException _ex) {
		super(_ex);
	}
	public BloqueoDAOException(String message) {
		super(message);
	}
	public BloqueoDAOException(String codigoErr, String message) {
		super(codigoErr, message);
	}
	

}
