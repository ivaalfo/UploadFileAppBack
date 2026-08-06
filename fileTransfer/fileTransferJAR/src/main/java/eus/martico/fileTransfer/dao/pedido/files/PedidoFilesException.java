package eus.martico.fileTransfer.dao.pedido.files;

import eus.martico.fileTransfer.services.MyServiceException;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@SuppressWarnings("serial")
public class PedidoFilesException extends MyServiceException {

	public PedidoFilesException(Exception _ex) {
		super(_ex);
	}
	public PedidoFilesException(String _msgError) {
		super(_msgError);
	}
	public PedidoFilesException(String _codigoError, String _msgError) {
		super(_codigoError, _msgError);
	}
}

