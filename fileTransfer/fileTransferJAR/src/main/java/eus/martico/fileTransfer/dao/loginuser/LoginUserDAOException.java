package eus.martico.fileTransfer.dao.loginuser;

import eus.martico.fileTransfer.dao.DAOException;

@SuppressWarnings("serial")
public class LoginUserDAOException extends DAOException  {

	public LoginUserDAOException(String message) {
		super(message);
	}
	public LoginUserDAOException(String codigoErr, String message) {
		super(codigoErr, message);
	}
}
