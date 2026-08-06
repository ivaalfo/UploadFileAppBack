package eus.martico.fileTransfer.services.pedidos.activos;

import eus.martico.fileTransfer.services.MyServiceException;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@SuppressWarnings("serial")
public class PedidosActivosServiceException extends MyServiceException {

	public PedidosActivosServiceException(String _msgError) {
		super(_msgError);
	}

	public PedidosActivosServiceException(Exception _ex) {
		super(_ex);
	}

	public PedidosActivosServiceException(String _codigoError, String _msgError) {
		super(_codigoError, _msgError);
	}

}
