package eus.martico.fileTransfer.services.pedidos.validador;

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
public interface PedidosValidadorIService {
	
	/**
	 * marcarAnotacionCMR
	 * 
	 * @param _context MyServiceContext
	 * @param _pedidoTrack String
	 * @param _pedidoExp String
	 * @param _anotacion String
	 * 
	 * @return ValidateDTO<PedidoFilesVO>
	 * @throws PedidoFilesException
	 */
	ValidateDTO<PedidoFilesVO> marcarAnotacionCMR(MyServiceContext _context, String _pedidoTrack, String _pedidoExp, String _anotacion) 
			throws PedidoFilesException;

	/**
	 * getPedidosFiltrados
	 * 
	 * @param _context MyServiceContext
	 * @param _expCod String
	 * @param _provName String
	 * @param _track String
	 * @param _refCharge String
	 * @param _fecHasta String
	 * @param _fecDesde String
	 * 
	 * @return List<PedidoProveedorVO>
	 * @throws PedidosValidadorServiceException
	 * @throws PedidoFilesException
	 */
	List<PedidoProveedorVO> getPedidosFiltrados(MyServiceContext _context, String _expCod, String _provName, String _track, String _refCharge, String _fecDesde, String _fecHasta) 
			throws PedidosValidadorServiceException, PedidoFilesException;

}
