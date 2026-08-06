package eus.martico.fileTransfer.dao.pedidos.activos;

import java.time.LocalDateTime;
import java.util.List;

import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesException;
import eus.martico.fileTransfer.model.pedido.files.PedidoFilesVO;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoProveedorVO;
import eus.martico.fileTransfer.services.MyServiceContext;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public interface PedidosActivosIDAO {
	
	PedidoProveedorVO getPedidoProv(MyServiceContext _context, String _pedidoTrack, String _pedidoExp) 
			throws PedidosActivosException, PedidoFilesException;
	
	Integer insertFecREAL(final PedidoFilesVO _pedidoF) 
			throws PedidoFilesException;
	
	Integer updateFecREAL(MyServiceContext _context, String _pedidoTrack, String _pedidoExp, LocalDateTime _fechaEntReal) 
			throws PedidoFilesException;
	
	Integer anularPedido(MyServiceContext _context, String _pedidoTrack, String _pedidoExp, String _motivo) 
			throws PedidoFilesException;
	
	List<PedidoProveedorVO> getPedidosFiltrados(MyServiceContext _context, String _expCod, String _provName, String _track, String _refCharge) 
			throws PedidosActivosException, PedidoFilesException;
	
}
