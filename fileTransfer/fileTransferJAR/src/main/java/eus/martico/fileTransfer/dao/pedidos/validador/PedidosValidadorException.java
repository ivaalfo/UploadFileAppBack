package eus.martico.fileTransfer.dao.pedidos.validador;

import eus.martico.fileTransfer.services.MyServiceException;

@SuppressWarnings("serial")
public class PedidosValidadorException extends MyServiceException {
	
	public PedidosValidadorException(Exception _ex) {
		super(_ex);
	}
	public PedidosValidadorException(String _msgError) {
		super(_msgError);
	}
	public PedidosValidadorException(String _codigoError, String _msgError) {
		super(_codigoError, _msgError);
	}
}

