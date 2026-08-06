package eus.martico.fileTransfer.services.pedidos.validador;

import eus.martico.fileTransfer.services.MyServiceException;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@SuppressWarnings("serial")
public class PedidosValidadorServiceException extends MyServiceException {

	public PedidosValidadorServiceException(String _msgError) {
		super(_msgError);
	}

	public PedidosValidadorServiceException(Exception _ex) {
		super(_ex);
	}

	public PedidosValidadorServiceException(String _codigoError, String _msgError) {
		super(_codigoError, _msgError);
	}

}
