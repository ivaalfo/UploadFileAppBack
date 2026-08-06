package eus.martico.fileTransfer.dao.tabladetablas;

import eus.martico.fileTransfer.dao.DAOException;

@SuppressWarnings("serial")
public class TablaDeTablasDAOException extends DAOException{

	public TablaDeTablasDAOException(DAOException daoException) {
		super(daoException);
	}
	public TablaDeTablasDAOException(String message) {
		super(message);
	}
	public TablaDeTablasDAOException(String codigoErr, String message) {
		super(codigoErr, message);
	}
}

