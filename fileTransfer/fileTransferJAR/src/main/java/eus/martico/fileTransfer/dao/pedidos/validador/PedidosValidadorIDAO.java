package eus.martico.fileTransfer.dao.pedidos.validador;

import java.util.List;

import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesException;
import eus.martico.fileTransfer.model.pedido.files.PedidoFilesVO;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoProveedorVO;
import eus.martico.fileTransfer.model.vo.ValidateDTO;
import eus.martico.fileTransfer.services.MyServiceContext;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public interface PedidosValidadorIDAO {

	ValidateDTO<PedidoFilesVO> updateAnotaciones(MyServiceContext _context, String _pedidoTrack, String _pedidoExp, String _anotacion) 
			throws PedidoFilesException;

	List<PedidoProveedorVO> getPedidosFiltrados(MyServiceContext _context, String _expCod, String _provName, String _track, String _refCharge) 
			throws PedidosValidadorException, PedidoFilesException;
}
