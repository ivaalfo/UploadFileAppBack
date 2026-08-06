package eus.martico.fileTransfer.services.tabladetablas;

import eus.martico.fileTransfer.services.MyServiceException;

@SuppressWarnings("serial")
public class TablaDeTablasServiceException extends MyServiceException {

	public TablaDeTablasServiceException(String _msgError) {
		super(_msgError);
	}

	public TablaDeTablasServiceException(Exception _ex) {
		super(_ex);
	}

	public TablaDeTablasServiceException(String _codigoError, String _msgError) {
		super(_codigoError, _msgError);
	}

}
