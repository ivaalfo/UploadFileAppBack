package eus.martico.fileTransfer.services.pedidos.historico;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eus.martico.fileTransfer.dao.DAOException;
import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesException;
import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesIDAO;
import eus.martico.fileTransfer.dao.pedidos.historico.PedidosHistoricoException;
import eus.martico.fileTransfer.dao.pedidos.historico.PedidosHistoricoIDAO;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoGrupajeVO;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoProveedorVO;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoGrupajeVO.GrupajeItem;
import eus.martico.fileTransfer.services.MyServiceContext;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@Service(value = "PedidosHistoricoIService")
public class PedidosHistoricoServiceImp implements PedidosHistoricoIService {

	@Autowired
	PedidoFilesIDAO pedidoFilesDAO;
	
	@Autowired
	PedidosHistoricoIDAO pedidoHistoricoDAO;

	
	@Override
	public Integer setReactivarPedido(MyServiceContext _context, String _pedidoTrack, String _pedidoExp)
			throws PedidoFilesException {
		try {
			if(_pedidoTrack.startsWith("GRU")){
				PedidoGrupajeVO grupo = getGrupaje(_context, _pedidoTrack);
				if(grupo.getSeleccionados() != null){
					//si gru por cada pedido del grupo
					for(GrupajeItem pGR : grupo.arrayGrulog){
						String completeTR = grupo.grupTR.concat(pGR.grupNum);
						String realGruExp = pGR.expediente;
						pedidoHistoricoDAO.reactivarPedido(_context, completeTR, realGruExp);
					}
				}
				return 1;
			} else {
				return pedidoHistoricoDAO.reactivarPedido(_context, _pedidoTrack, _pedidoExp);
			}
		} catch (PedidoFilesException e) {
			throw new PedidoFilesException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}

	@Override
	public List<PedidoProveedorVO> getPedidosFiltrados(MyServiceContext _context, String _expCod, String _provName, String _track, String _facNum, String _refCharge) 
			throws PedidosHistoricoServiceException, PedidoFilesException {
		try {
			return pedidoHistoricoDAO.getPedidosFiltrados(_context, _expCod, _provName, _track, _facNum, _refCharge);
		} catch (PedidosHistoricoException e) {
			throw new PedidosHistoricoServiceException(e.getMessage());
		}
	}
	
	/**
	 * getGrupaje
	 * @param _context MyServiceContext
	 * @param _track String
	 * 
	 * @return PedidoGrupajeVO
	 * @throws PedidoFilesException
	 */
	private PedidoGrupajeVO getGrupaje(MyServiceContext _context, String _track) throws PedidoFilesException {
		try {
			return pedidoFilesDAO.getGrupList(_context, _track);
		} catch (PedidoFilesException e) {
			throw new PedidoFilesException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}
}
