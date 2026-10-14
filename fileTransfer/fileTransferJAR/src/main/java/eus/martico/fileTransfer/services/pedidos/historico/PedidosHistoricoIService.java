package eus.martico.fileTransfer.services.pedidos.historico;

import java.util.List;

import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesException;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoProveedorVO;
import eus.martico.fileTransfer.services.MyServiceContext;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public interface PedidosHistoricoIService {
	
	/**
	 * setReactivarPedido
	 * 
	 * @param _context
	 * @param _pedidoTrack
	 * @param _pedidoExp
	 * 
	 * @return Integer
	 * @throws PedidoFilesException
	 */
	Integer setReactivarPedido(MyServiceContext _context, String _pedidoTrack, String _pedidoExp) 
			throws PedidoFilesException;

	/**
	 * getPedidosFiltrados
	 * @param _context MyServiceContext
	 * @param _expCod String
	 * @param _provName String
	 * @param _track String
	 * @param _facNum String
	 * @param refCharge String
	 * @param fecHasta String
	 * @param fecDesde String
	 * @return List<PedidoProveedorVO>
	 * @throws PedidosHistoricoServiceException
	 * @throws PedidoFilesException
	 */
	List<PedidoProveedorVO> getPedidosFiltrados(MyServiceContext _context, String _expCod, String _provName, String _track,	String _facNum, String refCharge, String fecDesde, String fecHasta) 
			throws PedidosHistoricoServiceException, PedidoFilesException;
	
}
