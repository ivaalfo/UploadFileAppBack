package eus.martico.fileTransfer.dao.pedidos.historico;

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
public interface PedidosHistoricoIDAO {
	
	/**
	 * reactivarPedido
	 * @param _context MyServiceContext
	 * @param _pedidoTrack String
	 * @param _pedidoExp String
	 * 
	 * @return Integer
	 * @throws PedidoFilesException
	 */
	Integer reactivarPedido(MyServiceContext _context, String _pedidoTrack, String _pedidoExp)
			throws PedidoFilesException;
	
	/**
	 * getPedidosFiltrados
	 * @param _context MyServiceContext
	 * @param _expCod String
	 * @param _provName String
	 * @param _track String
	 * @param _facNum String
	 * @param _refCharge String
	 * @param _fecHasta String
	 * @param _fecDesde String
	 * @return List<PedidoProveedorVO>
	 * @throws PedidosHistoricoException
	 * @throws PedidoFilesException
	 */
	List<PedidoProveedorVO> getPedidosFiltrados(MyServiceContext _context, String _expCod, String _provName, String _track, String _facNum, String _refCharge, String _fecDesde, String _fecHasta) 
			throws PedidosHistoricoException, PedidoFilesException;

	
}
