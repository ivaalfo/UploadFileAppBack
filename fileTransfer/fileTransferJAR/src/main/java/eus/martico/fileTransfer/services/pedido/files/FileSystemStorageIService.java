package eus.martico.fileTransfer.services.pedido.files;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesException;
import eus.martico.fileTransfer.model.pedido.files.PedidoFilesVO;
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
public interface FileSystemStorageIService {
	
	
	BuscarDTO<PedidoFilesVO> getPedidoFiles(MyServiceContext context, String track, String exp) 
			throws PedidoFilesException;

	
	Resource loadSign(String _filename);

	
	ResponseEntity<Resource> loadCMRasResource(MyServiceContext _context, String _track, String _exp) 
			throws PedidoFilesException;

	
	Resource loadValidCMRasResource(MyServiceContext _context, String _filename, String _track, String _exp) 
			throws PedidoFilesException;
	
	
	Resource loadFACasResource(MyServiceContext _context, String _filename, String _track, String _exp) 
			throws PedidoFilesException;

	
	Resource loadFileAsResource(MyServiceContext _context, String _fileType, String _filename, String _track, String _exp) 
			throws PedidoFilesException;
	
	
	ValidateDTO<PedidoFilesVO> registerFiles(MyServiceContext _context, 
			MultipartFile[] _cmrFile,
			MultipartFile[] _facFile, 
			MultipartFile[] _otrFiles, 
			String _track, 
			String _expediente, 
			String _refCarga) 
					throws IOException, PedidoFilesException;

	
	ValidateDTO<PedidoFilesVO> validateCMRFile(MyServiceContext _context, MultipartFile[] _signedFileReceived, String _track, String _expediente) 
			throws PedidoFilesException, StorageException, IOException;
	
	ValidateDTO<PedidoFilesVO> rejectCMRFile(MyServiceContext _context, String _track, String _expediente, String _motivoRechazo) 
			throws PedidoFilesException, StorageException, IOException;
	
	ValidateDTO<PedidoFilesVO> rejectFACFile(MyServiceContext _context, String _track, String _expediente, String _motivoRechazo) 
			throws PedidoFilesException, StorageException, IOException;


}
