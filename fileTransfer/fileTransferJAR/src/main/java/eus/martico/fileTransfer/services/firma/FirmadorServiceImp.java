package eus.martico.fileTransfer.services.firma;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import eus.martico.fileTransfer.dao.firmador.FirmadorException;
import eus.martico.fileTransfer.dao.firmador.FirmadorIDAO;
import eus.martico.fileTransfer.dao.usuario.UsuarioDAOException;
import eus.martico.fileTransfer.model.firma.PedidoFirmadorVO;
import eus.martico.fileTransfer.model.firma.PipelineFirmaDTO;
import eus.martico.fileTransfer.model.vo.BuscarDTO;
import eus.martico.fileTransfer.model.vo.ValidateDTO;
import eus.martico.fileTransfer.services.MyServiceContext;
import eus.martico.fileTransfer.services.pedido.files.StorageFileNotFoundException;
import eus.martico.fileTransfer.utils.FileTransferUtils;


/**
 * NEW
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@Service(value = "FirmadorIService")
public class FirmadorServiceImp implements FirmadorIService {
	
	protected static final Logger logger = LoggerFactory.getLogger(FirmadorServiceImp.class);
	
	@Autowired
	FirmadorIDAO firmadorDAO;

	
	
	@Override
	public BuscarDTO<PedidoFirmadorVO> getPedidoFirma(MyServiceContext context, String _track, String _exp) 
			throws FirmadorException {
		try {
			return new BuscarDTO<PedidoFirmadorVO>(firmadorDAO.getPedidoFirma(_track, _exp));
		} catch (FirmadorException e) {
			throw new FirmadorException(e.getMessage());
		}
	}
	
	@Override
	public List<PedidoFirmadorVO> getPedidosFiltrados(MyServiceContext _context, String _expCod, String _provName, String _track, String _refCharge) 
			throws FirmadorServiceException, FirmadorException, UsuarioDAOException {
		try {
			return firmadorDAO.getPedidosFiltrados(_context, _expCod, _provName, _track, _refCharge);
		} catch (FirmadorException e) {
			throw new FirmadorServiceException(e.getMessage());
		}
	}
	
	public ResponseEntity<Resource> loadFirmaCMRasResource(MyServiceContext _context, String _track, String _exp) 
			throws FirmadorException {
		try {
			Path filePath = null;
			PedidoFirmadorVO pedidoF = firmadorDAO.getPedidoFirma(_track, _exp);
			
			String rutaStr = pedidoF.getF3rutCMR();
			String fileExt = FileTransferUtils.getExt(rutaStr);
			String filename = FileTransferUtils.getFileName(rutaStr);
			logger.info("{} :: Obteniendo CMR :: {}.{}", _context.getUsername(), filename, fileExt);
		
			Path ruta = Paths.get(rutaStr);
			filePath = ruta.normalize(); 
			Resource resource = new UrlResource(filePath.toUri());
			
			if(resource.exists() || resource.isReadable()) {
				MediaType mtValue = FileTransferUtils.getMediaType(fileExt);
				
				ResponseEntity<Resource> file = ResponseEntity.ok()
						.contentType(mtValue)
						.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
						.body(resource);
				return file;
			} else {
				logger.error("Could not read file: {}", filename);
				throw new StorageFileNotFoundException("Could not read file: " + filename);
			}
		} catch (MalformedURLException e) {
			logger.error("malformed URL has occurred: ", e.getMessage());
			throw new StorageFileNotFoundException(e.getMessage());
		} catch (FirmadorException e) {
			logger.error(e.getMessage());
			throw new FirmadorException(e.getMessage());
		}
	}
	
	@Override
	public ValidateDTO<PedidoFirmadorVO> añadirFirmaIncremental(MyServiceContext _context, PipelineFirmaDTO _firmaDTO) 
			throws FirmadorServiceException, FirmadorException {
		try {
			return firmadorDAO.añadirFirmaIncremental(_context, _firmaDTO);
		} catch (FirmadorException e) {
			throw new FirmadorServiceException(e.getMessage());
		}
	}
	
}
