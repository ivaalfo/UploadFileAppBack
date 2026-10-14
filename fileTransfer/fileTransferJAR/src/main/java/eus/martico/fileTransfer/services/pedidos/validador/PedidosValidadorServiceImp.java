package eus.martico.fileTransfer.services.pedidos.validador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesException;
import eus.martico.fileTransfer.dao.pedidos.validador.PedidosValidadorException;
import eus.martico.fileTransfer.dao.pedidos.validador.PedidosValidadorIDAO;
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
@Service(value = "PedidosValidadorIService")
public class PedidosValidadorServiceImp implements PedidosValidadorIService {
	
	@Autowired
	PedidosValidadorIDAO pedidoValidadorDAO;
	
	
	@Override
	@Transactional
	public ValidateDTO<PedidoFilesVO> marcarAnotacionCMR(MyServiceContext _context, String _pedidoTrack, String _pedidoExp, String _anotacion) 
			throws PedidoFilesException {
		ValidateDTO<PedidoFilesVO> validate = new ValidateDTO<PedidoFilesVO>();
		try {
			validate = pedidoValidadorDAO.updateAnotaciones(_context, _pedidoTrack, _pedidoExp, _anotacion);
		} catch (PedidoFilesException e) {
			throw new PedidoFilesException(e.getMessage());
		}
		return validate;
	}

	@Override
	public List<PedidoProveedorVO> getPedidosFiltrados(MyServiceContext _context, String _expCod, String _provName, String _track, String _refCharge, String _fecDesde, String _fecHasta) 
			throws PedidosValidadorServiceException, PedidoFilesException {
		try {
			return pedidoValidadorDAO.getPedidosFiltrados(_context, _expCod, _provName, _track, _refCharge, _fecDesde, _fecHasta);
		} catch (PedidosValidadorException e) {
			throw new PedidosValidadorServiceException(e.getMessage());
		}
	}
	
}
