package eus.martico.fileTransfer.dao.pedidos.historico;

import eus.martico.fileTransfer.services.MyServiceException;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@SuppressWarnings("serial")
public class PedidosHistoricoException extends MyServiceException {

	public PedidosHistoricoException(Exception _ex) {
		super(_ex);
	}
	public PedidosHistoricoException(String _msgError) {
		super(_msgError);
	}
	public PedidosHistoricoException(String _codigoError, String _msgError) {
		super(_codigoError, _msgError);
	}
}

