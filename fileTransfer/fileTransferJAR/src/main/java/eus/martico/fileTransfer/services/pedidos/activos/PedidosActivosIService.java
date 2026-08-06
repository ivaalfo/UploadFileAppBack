package eus.martico.fileTransfer.services.pedidos.activos;

import java.util.List;

import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesException;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoGrupajeVO;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoGrupajeVO.GrupajeItem;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoProveedorVO;
import eus.martico.fileTransfer.services.MyServiceContext;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public interface PedidosActivosIService {
		
	PedidoProveedorVO getPedidoProv(MyServiceContext _context, String _pedidoTrack, String _pedidoExp) 
			throws PedidosActivosServiceException, PedidoFilesException;
	
	PedidoGrupajeVO getGrupaje(MyServiceContext _context, String _track) 
			throws PedidoFilesException;
	
	Integer setGrupaje(MyServiceContext _context, String _grupTR, List<GrupajeItem> _seleccionados) 
			throws PedidoFilesException;
	
	Integer setFecReal(MyServiceContext _context, String _pedidoTrack, String _pedidoExp, String _fechaEntReal) 
			throws PedidoFilesException;
	
	Integer setPedidoNulo(MyServiceContext _context, String _pedidoTrack, String _pedidoExp, String _motivo) 
			throws PedidoFilesException;
	
	List<PedidoProveedorVO> getPedidosFiltrados(MyServiceContext _context, String _expCod, String _provName, String _track, String _refCharge) 
			throws PedidosActivosServiceException, PedidoFilesException;
}