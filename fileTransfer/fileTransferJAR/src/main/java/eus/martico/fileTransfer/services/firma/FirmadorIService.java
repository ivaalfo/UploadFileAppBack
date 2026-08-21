package eus.martico.fileTransfer.services.firma;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import eus.martico.fileTransfer.dao.firmador.FirmadorException;
import eus.martico.fileTransfer.dao.usuario.UsuarioDAOException;
import eus.martico.fileTransfer.model.firma.PedidoFirmadorVO;
import eus.martico.fileTransfer.model.firma.PipelineFirmaDTO;
import eus.martico.fileTransfer.model.vo.BuscarDTO;
import eus.martico.fileTransfer.model.vo.ValidateDTO;
import eus.martico.fileTransfer.services.MyServiceContext;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public interface FirmadorIService {
	
	/**
	 * getPedidoFirma
	 * @param context MyServiceContext
	 * @param track String
	 * @param exp String
	 * 
	 * @return BuscarDTO<PedidoFirmadorVO>
	 * @throws FirmadorException
	 */
	BuscarDTO<PedidoFirmadorVO> getPedidoFirma(MyServiceContext context, String track, String exp) 
			throws FirmadorException;
	
	/**
	 * getPedidosFiltrados
	 * @param _context MyServiceContext
	 * @param _expCod String
	 * @param _provName String
	 * @param _track String
	 * @param refCharge String
	 * 
	 * @return List<PedidoFirmadorVO>
	 * @throws FirmadorServiceException
	 * @throws FirmadorException
	 * @throws UsuarioDAOException 
	 */
	List<PedidoFirmadorVO> getPedidosFiltrados(MyServiceContext _context, String _expCod, String _provName, String _track,	String refCharge) 
			throws FirmadorServiceException, FirmadorException, UsuarioDAOException;

	/**
	 * loadSignCMRasResource
	 * @param _context MyServiceContext
	 * @param _track String
	 * @param _exp String
	 * 
	 * @return ResponseEntity<Resource>
	 * @throws FirmadorException
	 */
	ResponseEntity<Resource> loadFirmaCMRasResource(MyServiceContext _context, String _track, String _exp) 
			throws FirmadorException;
	
	/**
	 * añadirFirmaIncremental
	 * @param _context MyServiceContext
	 * @param firmaDTO PipelineFirmaDTO
	 * 
	 * @return ValidateDTO<PedidoFirmadorVO>
	 * @throws FirmadorServiceException
	 * @throws FirmadorException
	 */
	ValidateDTO<PedidoFirmadorVO> añadirFirmaIncremental(MyServiceContext _context, PipelineFirmaDTO firmaDTO) 
			throws FirmadorServiceException, FirmadorException;
	
}
