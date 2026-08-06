package eus.martico.fileTransfer.dao.pedidos.activos;

import eus.martico.fileTransfer.services.MyServiceException;

@SuppressWarnings("serial")
public class PedidosActivosException extends MyServiceException {
	
	public PedidosActivosException(Exception _ex) {
		super(_ex);
	}
	public PedidosActivosException(String _msgError) {
		super(_msgError);
	}
	public PedidosActivosException(String _codigoError, String _msgError) {
		super(_codigoError, _msgError);
	}
}
