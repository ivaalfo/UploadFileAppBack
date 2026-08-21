package eus.martico.fileTransfer.dao.firmador;

import java.util.List;

import eus.martico.fileTransfer.dao.usuario.UsuarioDAOException;
import eus.martico.fileTransfer.model.firma.PedidoFirmadorVO;
import eus.martico.fileTransfer.model.firma.PipelineFirmaDTO;
import eus.martico.fileTransfer.model.vo.ValidateDTO;
import eus.martico.fileTransfer.services.MyServiceContext;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public interface FirmadorIDAO {
	
	PedidoFirmadorVO getPedidoFirma(String _track, String _exp) throws FirmadorException;
	
	
	List<PedidoFirmadorVO> getPedidosFiltrados(MyServiceContext _context, String _expCod, String _provName, String _track, String _refCharge) 
			throws FirmadorException, UsuarioDAOException;

	
	ValidateDTO<PedidoFirmadorVO> añadirFirmaIncremental(MyServiceContext _context, PipelineFirmaDTO _firmaDTO)
			throws FirmadorException;

}
