package eus.martico.fileTransfer.services.pedidos.historico;

import eus.martico.fileTransfer.services.MyServiceException;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@SuppressWarnings("serial")
public class PedidosHistoricoServiceException extends MyServiceException {

	public PedidosHistoricoServiceException(String _msgError) {
		super(_msgError);
	}

	public PedidosHistoricoServiceException(Exception _ex) {
		super(_ex);
	}

	public PedidosHistoricoServiceException(String _codigoError, String _msgError) {
		super(_codigoError, _msgError);
	}

}
