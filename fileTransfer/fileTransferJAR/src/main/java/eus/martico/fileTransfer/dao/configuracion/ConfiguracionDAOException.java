package eus.martico.fileTransfer.dao.configuracion;

import eus.martico.fileTransfer.dao.DAOException;

public class ConfiguracionDAOException extends DAOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3085927689446556821L;

	public ConfiguracionDAOException(String _msgError) {
		super(_msgError);
	}

	public ConfiguracionDAOException(String _codigoError, String _msgError) {
		super(_codigoError, _msgError);
	}

	public ConfiguracionDAOException(Exception _ex) {
		super(_ex);
	}

	public ConfiguracionDAOException(DAOException _ex) {
		super(_ex);
	}

}
