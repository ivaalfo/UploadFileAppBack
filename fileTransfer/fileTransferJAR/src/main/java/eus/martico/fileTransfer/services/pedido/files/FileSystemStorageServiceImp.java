package eus.martico.fileTransfer.services.pedido.files;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import eus.martico.fileTransfer.config.FileTransferConfiguration;
import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesException;
import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesIDAO;
import eus.martico.fileTransfer.model.error.vo.ErrorVO;
import eus.martico.fileTransfer.model.pedido.files.PedidoFilesVO;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoProveedorVO;
import eus.martico.fileTransfer.model.vo.BuscarDTO;
import eus.martico.fileTransfer.model.vo.ValidateDTO;
import eus.martico.fileTransfer.services.MyServiceContext;
import eus.martico.fileTransfer.services.firma.FileSystemCertSignService;
import eus.martico.fileTransfer.utils.FileTransferUtils;


/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@Service(value = "FileSystemStorageIService")
public class FileSystemStorageServiceImp implements FileSystemStorageIService {
	
	protected static final Logger logger = LoggerFactory.getLogger(FileSystemStorageServiceImp.class);
	
	@Autowired
	PedidoFilesIDAO pedidoFilesDAO;
	
	
	/**
	 * getPedidoProv
	 * 
	 * @param _context
	 * @param _pedidoTrack
	 * @param _pedidoExp
	 * @return
	 * @throws PedidoFilesException
	 */
	@Override
	public PedidoProveedorVO getPedidoProv(MyServiceContext _context, String _pedidoTrack, String _pedidoExp) 
			throws PedidoFilesException {
		try {
			return pedidoFilesDAO.getPedidoProv(_context, _pedidoTrack, _pedidoExp);
		} catch (PedidoFilesException e) {
			logger.error(e.getMessage());
			throw new PedidoFilesException(e.getMessage());
		}
	}
	
	/**
	 * getPedidoFiles
	 * @param context MyServiceContext
	 * @param track String
	 * @param exp String
	 * 
	 * @return BuscarDTO<PedidoFilesVO>
	 * @throws PedidoFilesException 
	 */
	@Override
	public BuscarDTO<PedidoFilesVO> getPedidoFiles(MyServiceContext context, String _track, String _exp) 
			throws PedidoFilesException {
		try {
			return new BuscarDTO<PedidoFilesVO>(pedidoFilesDAO.getPedidoFiles(_track, _exp));
		} catch (PedidoFilesException e) {
			logger.error(e.getMessage());
			throw new PedidoFilesException(e.getMessage());
		}
	}
    
	/**
	 * loadSign: carga la firma/sello de la ubicación local
	 * @param _filename String
	 * 
	 * @return Resource
	 */
	public Resource loadSignWatermark(String _filename){
		try {
			Path signFilePath = FileTransferUtils.signLocation.resolve(_filename);
			Resource resource = new UrlResource(signFilePath.toUri());
			
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				logger.error("Could not read sign file: {}", _filename);
				throw new StorageFileNotFoundException("Could not read sign file: " + _filename);
			}
		} catch (MalformedURLException e) {
			logger.error("Could not read sign file: {}", _filename);
			throw new StorageFileNotFoundException("Could not read sign file: " + _filename, e);
		}
	}
	
	/**
	 * loadCMRasResource
	 * @param _context MyServiceContext
	 * @param _track String
	 * @param _exp String
	 * 
	 * @return ResponseEntity<Resource>
	 * @throws PedidoFilesException
	 */
	public ResponseEntity<Resource> loadCMRasResource(MyServiceContext _context, String _track, String _exp) 
			throws PedidoFilesException {
		try {
			Path filePath = null;
			PedidoFilesVO pedidoF = pedidoFilesDAO.getPedidoFiles(_track, _exp);
			
			String rutaStr = pedidoF.getRutaCMR();
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
		} catch (PedidoFilesException e) {
			logger.error(e.getMessage());
			throw new PedidoFilesException(e.getMessage());
		}
	}
	
	/**
	 * loadValidCMRasResource
	 * @param _context MyServiceContext
	 * @param _filename String
	 * @param _track String
	 * @param _exp String
	 * 
	 * @return Resource
	 * @throws PedidoFilesException
	 */
	public Resource loadValidCMRasResource(MyServiceContext _context, String _filename, String _track, String _exp) throws PedidoFilesException {
		logger.info("{} :: Obteniendo CMR Validado :: {}", _context.getUsername(), _filename);
		try {
			PedidoFilesVO pedidoReg = pedidoFilesDAO.getPedidoFiles(_track, _exp);
			Path filePath = Paths.get(pedidoReg.getRutaCMR());
			Resource resource = new UrlResource(filePath.toUri());
			
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				logger.error("Could not read valid CMR file: {}", _filename);
				throw new StorageFileNotFoundException("Could not read valid CMR file: " + _filename);
			}
		} catch (MalformedURLException e) {
			logger.error("Could not read valid CMR file: {}", _filename);
			throw new StorageFileNotFoundException("Could not read valid CMR file: " + _filename, e);
		}
	}
	
	/**
	 * loadFACasResource
	 * @param _context MyServiceContext
	 * @param _filename String
	 * @param _track String
	 * @param _exp String
	 * 
	 * @return Resource
	 * @throws PedidoFilesException
	 */
	public Resource loadFACasResource(MyServiceContext _context, String _filename, String _track, String _exp) throws PedidoFilesException {
		logger.info("{} :: Obteniendo FAC :: {}", _context.getUsername(), _filename);
		try {
			PedidoFilesVO pedidoReg = pedidoFilesDAO.getPedidoFiles(_track, _exp);
			Path filePath = Paths.get(pedidoReg.getRutaFAC());
			Resource resource = new UrlResource(filePath.toUri());
			
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				logger.error("Could not read valid FAC file: {}", _filename);
				throw new StorageFileNotFoundException("Could not read valid FAC file: " + _filename);
			}
		} catch (MalformedURLException e) {
			logger.error("Could not read valid FAC file: {}", _filename);
			throw new StorageFileNotFoundException("Could not read valid FAC file: " + _filename, e);
		}
	}
	
	/**
	 * loadFileAsResource
	 * @param _context MyServiceContext
	 * @param _fileType String
	 * @param _filename String
	 * @param _track String
	 * @param _exp String
	 * 
	 * @return Resource
	 * @throws PedidoFilesException
	 */
	public Resource loadFileAsResource(MyServiceContext _context, String _fileType, String _filename, String _track, String _exp) 
			throws PedidoFilesException {
		logger.info("{} :: Descargando Archivo :: {} :: {}", _context.getUsername(), _fileType, _filename);
		try {
			Path downloadPath = null;
			PedidoFilesVO pedidoReg = new PedidoFilesVO();
			if(_track != null){
				pedidoReg = pedidoFilesDAO.getPedidoFiles(_track, _exp);
			}
			switch(_fileType){
			case "MAN_PROV":
				downloadPath = FileTransferUtils.getPath(_fileType, null, _filename);
				break;
			case "CMR":
				downloadPath = Paths.get(pedidoReg.getRutaCMR());
				break;
			case "FAC":
				downloadPath = Paths.get(pedidoReg.getRutaFAC());
				break;
			case "OTR":
				Path totalPath = Paths.get(pedidoReg.getRutaOTR());
				String[] partes = totalPath.toString().split("~~");
				for(int i = 0; i <= partes.length-1; i++) {
					if(partes[i].contains(_filename)){
						downloadPath = Paths.get(partes[i]);
					}
		        }
				break;
			}
			Resource resource = new UrlResource(downloadPath.toUri());
			
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				logger.error("Could not read file: {}", _filename);
				throw new StorageFileNotFoundException("Could not read file: " + _filename);
			}
		} catch (MalformedURLException e) {
			logger.error("Could not read file: {}", _filename);
			throw new StorageFileNotFoundException("Could not read file: " + _filename, e);
		}
	}
	
	/**
	 * registerFiles
	 * @param _context MyServiceContext
	 * @param _cmrFile MultipartFile[]
	 * @param _facFile MultipartFile[]
	 * @param _otrFiles MultipartFile[]
	 * @param _track String
	 * @param _expediente String
	 * @param _refCarga String
	 * 
	 * @return ValidateDTO<PedidoFilesVO>
	 * @throws IOException
	 * @throws PedidoFilesException
	 */
	@Transactional(rollbackFor = {IOException.class, PedidoFilesException.class})
	public ValidateDTO<PedidoFilesVO> registerFiles (MyServiceContext _context, 
			MultipartFile[] _cmrFile,
			MultipartFile[] _facFile,
			MultipartFile[] _otrFiles, 
			String _track,
			String _exp,
			String _refCarga) 
					throws IOException, PedidoFilesException {

		//Crea los directorios si no existen, en base al año actual
		FileTransferUtils.createDynamicDirs();

		//Ver si el pedido está ya registrado en FTARCHI
		PedidoFilesVO pedidoReg = pedidoFilesDAO.getPedidoFiles(_track, _exp);
		logger.info("{} :: Subida de documentacion :: TR:{} : EXP:{}", _context.getUsername(), _track, _exp);
		
		ValidateDTO<PedidoFilesVO> validate = new ValidateDTO<PedidoFilesVO>();
		
		//PRE-VALIDACIÓN: Sin CMR los demás no pasan
	    if(_cmrFile.length > 0){
	        try {
	            String newFname = StringUtils.cleanPath(_cmrFile[0].getOriginalFilename());
	            FileTransferUtils.validacionAntiMalware(_context, _cmrFile[0], newFname, "CMR");
	        } catch (PedidoFilesException e) {
	            logger.error("Error de integridad de archivo CMR: " + e.getMessage());
	            validate.addError(new ErrorVO(_track, "Error de integridad de archivo CMR: " + e.getMessage()));
	            return validate;
	        }
	    }
		
		if(_cmrFile.length > 0){
			logger.info("Registar CMR :: TR:{} :: EXP:{}", _track, _exp);
			validate = this.registrarCMR(_context, _cmrFile, pedidoReg, validate, _refCarga);
		}
		if(_facFile.length > 0){
			logger.info("Registar FAC :: TR:{} :: EXP:{}", _track, _exp);
			validate = this.registrarFAC(_context, _facFile, pedidoReg, validate);
		}
		if(_otrFiles.length > 0){
			logger.info("Registar OTROS :: TR:{} :: EXP:{}", _track, _exp);
			validate = this.registrarOTR(_context, _otrFiles, pedidoReg, validate);
		}
		
		logger.info("registerFiles :: FIN");
		return validate;
	}
	
	/**
	 * validateCMRFile
	 * @param _context MyServiceContext
	 * @param _fileReceived MultipartFile[]
	 * @param _track String
	 * @param _expediente String
	 * 
	 * @return ValidateDTO<PedidoFilesVO>
	 * @throws IOException 
	 * @throws StorageException
	 * @throws PedidoFilesException 
	 */
	@Transactional(rollbackFor = {IOException.class, PedidoFilesException.class})
	public ValidateDTO<PedidoFilesVO> validateCMRFile (MyServiceContext _context, MultipartFile[] _fileReceived, String _track, String _exp) 
			throws PedidoFilesException, StorageException, IOException {
		
		logger.info("{} :: Validacion de CMR :: {}", _context.getUsername(), _fileReceived[0].getOriginalFilename());
		
		//El pedido tiene que estar ya registrado en FTARCHI
		PedidoFilesVO pedidoReg = pedidoFilesDAO.getPedidoFiles(_track, _exp);
		
		ValidateDTO<PedidoFilesVO> validate = new ValidateDTO<PedidoFilesVO>();
		if(_fileReceived.length > 0){
			validate = this.validarCMR(_context, _fileReceived, pedidoReg, validate);
			logger.info("validateCMRFile :: CMR validado :: {}", _track);
		}
		return validate;
	}
	
	/**
	 * rejectCMRFile
	 * @param _context MyServiceContext
	 * @param _track String
	 * @param _expediente String
	 * @param _motivoRechazo String
	 * 
	 * @return ValidateDTO<PedidoFilesVO>
	 * @throws PedidoFilesException
	 * @throws StorageException
	 * @throws IOException
	 */
	@Override
	@Transactional
	public ValidateDTO<PedidoFilesVO> rejectCMRFile (MyServiceContext _context, String _track, String _exp, String _motivoRechazo) 
			throws PedidoFilesException, StorageException, IOException {
		
		ValidateDTO<PedidoFilesVO> validate = new ValidateDTO<PedidoFilesVO>();
		logger.info("{} :: Rechazo de CMR :: TR:{} :: EXP:{}", _context.getUsername(), _track, _exp);
		logger.info("Motivo del Rechazo :: {}", _motivoRechazo);
		
		//El pedido tiene que estar ya registrado en FTARCHI
		PedidoFilesVO pedidoReg = pedidoFilesDAO.getPedidoFiles(_track, _exp);
		
		if(pedidoReg.getEstadoCMR().equals(FileTransferConstantes.ESTADO_1_FICH_SUBIDO)){
			validate = this.rechazarCMR(_context, pedidoReg, _motivoRechazo);
			logger.info("rejectCMRFile :: CMR rechazado :: {}", _track);
		}
		return validate;
	}
	
	/**
	 * rejectFACFile
	 * @param _context MyServiceContext
	 * @param _track String
	 * @param _expediente String
	 * @param _motivoRechazo String
	 * 
	 * @return ValidateDTO<PedidoFilesVO>
	 * @throws PedidoFilesException
	 * @throws StorageException
	 * @throws IOException
	 */
	@Override
	@Transactional
	public ValidateDTO<PedidoFilesVO> rejectFACFile (MyServiceContext _context, String _track, String _exp, String _motivoRechazo) 
			throws PedidoFilesException, StorageException, IOException {
		
		ValidateDTO<PedidoFilesVO> validate = new ValidateDTO<PedidoFilesVO>();
		logger.info("{} :: Rechazo de FAC :: TR:{} :: EXP:{}", _context.getUsername(), _track, _exp);
		logger.info("Motivo del Rechazo :: {}", _motivoRechazo);
		
		//El pedido tiene que estar ya registrado en FTARCHI
		PedidoFilesVO pedidoReg = pedidoFilesDAO.getPedidoFiles(_track, _exp);
		
		if(pedidoReg.getEstadoFAC().equals(FileTransferConstantes.ESTADO_1_FICH_SUBIDO)){
			validate = this.rechazarFAC(_context, pedidoReg, _motivoRechazo);
			logger.info("rejectFACFile :: FAC rechazada :: {}", _track);
		}
		return validate;
	}
    
    private ValidateDTO<PedidoFilesVO> registrarCMR (MyServiceContext _context, 
    		MultipartFile[] _cmrFile, 
			PedidoFilesVO _pedidoReg, 
			ValidateDTO<PedidoFilesVO> _validate,
			String _refCarga)
					throws PedidoFilesException, StorageException, IOException {
		
    	//Registrar CMR
    	if(_cmrFile.length > 0){
    		String track = _pedidoReg.getRefPed();
    		String exp = _pedidoReg.getExpediente();
    		String newFname = StringUtils.cleanPath(_cmrFile[0].getOriginalFilename());
    		
			boolean cmrToValid = _pedidoReg.getEstadoCMR().equals(FileTransferConstantes.ESTADO_1_FICH_SUBIDO);
			boolean cmrValid = _pedidoReg.getEstadoCMR().equals(FileTransferConstantes.ESTADO_2_FICH_VALIDADO);
    		boolean cmrRechazado = _pedidoReg.getEstadoCMR().equals(FileTransferConstantes.ESTADO_3_FICH_RECHAZADO);
			
    		//OPERACIONES FILES ::
    		if(cmrRechazado){
    			//DELETE si ya existe de ./rechazados 
    			String oldRutaCMR = _pedidoReg.getRutaCMR();
    			Path oldPath = Paths.get(oldRutaCMR);
    			String fExt = FileTransferUtils.getExt(oldRutaCMR);
				String fName = FileTransferUtils.getFileName(oldRutaCMR);
				String completeFname = fName.concat("."+fExt);
    			FileTransferUtils.deleteFile(oldPath, completeFname);
    		}
    		
			if(!cmrToValid && !cmrValid){//Pasan los 0s o los 3s
				
				_refCarga = _refCarga.replaceAll("[\\\\/.%,';&]", "-");
				
				if(track.startsWith("GRU")){
					String gruTR = track.replaceAll("GRULOG", "-GRU");
					_refCarga = _refCarga.concat(gruTR);
				} else {
					_refCarga = track.concat("_"+_refCarga);
				}
				
	    		//Nuevo CMR se guarda siempre en 'CURRENT_YEAR' venga de rechazo o no
				Path newPathCMR = FileTransferUtils.getPath("CMR", _refCarga, newFname);
				//Se cambia CMR_track por CMR_track_refCarga para el name del DOC y las rutas
				
				//SAVE CMR en ./subidos
				FileTransferUtils.storeFile(newPathCMR, _cmrFile[0]);
				logger.info("registrarCMR :: CMR almacenado");

				//OPERACIONES DDBB ::
				PedidoFilesVO pedidoAux = new PedidoFilesVO();
				pedidoAux.setRefPed(track);
				pedidoAux.setExpediente(exp);
				//Datos del CMR : siempre UPDATE del registro SIN tocar la fecREAL
				pedidoAux.setRutaCMR(newPathCMR.toString());
				pedidoAux.setUsuSubidaCMR(_context.getUsername());
				pedidoAux.setEstadoCMR(FileTransferConstantes.ESTADO_1_FICH_SUBIDO);
				pedidoAux.setFechaSubidaCMR(LocalDateTime.now());

				//Inicializamos el motRec y anota por si viene de rechazo
				pedidoAux.setAnotaciones(FileTransferConstantes.SIN_ANOTACIONES);
				pedidoAux.setMotivoRechazo("");

				pedidoAux.setFechaEstadoCMR(LocalDateTime.parse("0000-01-01T00:00:00"));
				pedidoAux.setUsuOkCMR("");

				//UPDATE Solo los campos del CMR
				PedidoFilesVO pedidoUpdate = new PedidoFilesVO();
				pedidoUpdate = pedidoFilesDAO.updateCMR(pedidoAux);
				logger.info("registrarCMR :: CMR registrado :: TR:{} :: EXP:{}", track, exp);

				pedidoUpdate = pedidoFilesDAO.getPedidoFiles(track, exp);
				_validate.setDatos(pedidoUpdate);

				Integer estado = FileTransferUtils.determinarEstadoGeneral(pedidoUpdate, track, exp);
				pedidoFilesDAO.updateESTADO(track, exp, estado);
				logger.info("registrarCMR :: Actualizado Estado General :: TR:{}", track);
			}
			if(cmrToValid){		//Con los botones disabled ya no entra por aqui BORRAR
				_validate.addError(new ErrorVO(track, "Fichero/s no guardado/s"));
				logger.error("registrarCMR :: TR: {} :: Fichero no guardado: {} : El CMR esta siendo validado.", track, newFname);
				return _validate;
			}
			if(cmrValid) {		//Con los botones disabled ya no entra por aqui BORRAR
				_validate.addError(new ErrorVO(track, "Fichero/s no guardado/s"));
				logger.error("registrarCMR :: TR:: {} :: Fichero no guardado: {} : CMR ya validado.", track, newFname);
				return _validate;
			}
		}
		return _validate;
	}
	
	private ValidateDTO<PedidoFilesVO> registrarFAC (MyServiceContext _context, 
			MultipartFile[] _facFile, 
			PedidoFilesVO _pedidoReg,
			ValidateDTO<PedidoFilesVO> _validate) 
					throws PedidoFilesException, StorageException, IOException {
		
		//Registrar FAC 
		if(_facFile.length > 0){
			String track = _pedidoReg.getRefPed();
			String exp = _pedidoReg.getExpediente();
			String trackExp = track.concat(exp);
			String fileName = StringUtils.cleanPath(_facFile[0].getOriginalFilename());
			
			boolean facToValid = _pedidoReg.getEstadoFAC().equals(FileTransferConstantes.ESTADO_1_FICH_SUBIDO) 
					|| _pedidoReg.getEstadoFAC().equals(FileTransferConstantes.ESTADO_4_FAC_BLOQUEADA)
					|| _pedidoReg.getEstadoFAC().equals(FileTransferConstantes.ESTADO_5_FAC_SIN_CONTABILIZAR)
					|| _pedidoReg.getEstadoFAC().equals(FileTransferConstantes.ESTADO_6_FAC_NO_CONTABILIZAR);
			boolean facValid = _pedidoReg.getEstadoFAC().equals(FileTransferConstantes.ESTADO_2_FICH_VALIDADO);
			boolean facRechazada = _pedidoReg.getEstadoFAC().equals(FileTransferConstantes.ESTADO_3_FICH_RECHAZADO);
			
			//OPERACIONES FILES ::
			if(facRechazada){
    			//DELETE si ya existe de ./rechazadas 
    			String oldRutaFAC = _pedidoReg.getRutaFAC();
    			Path oldPath = Paths.get(oldRutaFAC);
    			String fExt = FileTransferUtils.getExt(oldRutaFAC);
				String fName = FileTransferUtils.getFileName(oldRutaFAC);
				String fname = fName.concat("."+fExt);
    			FileTransferUtils.deleteFile(oldPath, fname);
    		}
			
			if(!facToValid && !facValid){		//Pasan los 0s o los 3s
				//Nueva FAC se guarda siempre en 'CURRENT_YEAR' venga de rechazo o no
				Path pathFAC = FileTransferUtils.getPath("FAC", trackExp, fileName);
				
				//Validación Archivos Maliciosos y Estructurales
				try {
					FileTransferUtils.validacionAntiMalware(_context, _facFile[0], fileName, "FAC");
				} catch (PedidoFilesException ex){
				    logger.error("Error de integridad de archivo FAC: " + ex.getMessage());
				    _validate.addError(new ErrorVO(track, "Error de integridad de archivo Factura: "+ex.getMessage())); 
				    //Devolvemos el validate con el error integrado sin romper la ejecución de los otros archivos (CMR/OTROS)
				    return _validate;
				}
				
				//SAVE FAC :: Para fileTransfer
				FileTransferUtils.storeFile(pathFAC, _facFile[0]);
				logger.info("registrarFAC :: TR:{} :: EXP:{} :: Factura almacenada.", track, exp);
				
				//SAVE FAC :: Para Matrix :: SOLO Si CMR=2
				boolean cmrValid = _pedidoReg.getEstadoCMR().equals(FileTransferConstantes.ESTADO_2_FICH_VALIDADO);
				if(cmrValid){		
					Path pathFACdocPRO = FileTransferUtils.getPath("FACMATRIX_B", trackExp, fileName);
					FileTransferUtils.storeFile(pathFACdocPRO, _facFile[0]);
					logger.info("registrarFAC :: TR:{} :: EXP:{} :: Factura almacenada en Matrix.", track, exp);
				}
				
				//OPERACIONES DDBB ::
				PedidoFilesVO pedidoAux = new PedidoFilesVO();
				pedidoAux.setRefPed(track);
				pedidoAux.setExpediente(exp);
				//Datos de la FAC : siempre UPDATE del registro sin tocar la fecREAL
				pedidoAux.setRutaFAC(pathFAC.toString());
				pedidoAux.setUsuSubidaFAC(_context.getUsername());
				pedidoAux.setEstadoFAC(FileTransferConstantes.ESTADO_1_FICH_SUBIDO);
				pedidoAux.setFechaSubidaFAC(LocalDateTime.now());
				pedidoAux.setFechaEstadoFAC(LocalDateTime.parse("0000-01-01T00:00:00"));
				pedidoAux.setUsuOkFAC("");
				
				//Inicializamos el motRec y anota por si viene de rechazo
				pedidoAux.setAnotaciones(FileTransferConstantes.SIN_ANOTACIONES);
				pedidoAux.setMotivoRechazo("");
				
				//UPDATE Solo los campos de la FAC
				PedidoFilesVO pedidoUpdate = new PedidoFilesVO();
				pedidoUpdate = pedidoFilesDAO.updateFAC(pedidoAux);
				logger.info("registrarFAC :: TR:{} :: EXP:{} :: Factura registrada.", track, exp);
				
				pedidoUpdate = pedidoFilesDAO.getPedidoFiles(track, exp);
				_validate.setDatos(pedidoUpdate);
				
				Integer estado = FileTransferUtils.determinarEstadoGeneral(pedidoUpdate, track, exp);
				pedidoFilesDAO.updateESTADO(track, exp, estado);
				logger.info("registrarFAC :: TR:{} :: EXP:{} :: Actualizado Estado General.", track, exp);
				
			}
			if(facToValid){		//Con los botones disabled ya no entra por aqui BORRAR
				_validate.addError(new ErrorVO(track, "Fichero/s no guardado/s"));
				logger.error("registrarFAC :: TR:{} :: EXP:{} :: Fichero no guardado: {} :: La Factura esta siendo validada.", track, exp, fileName);
				return _validate;
			}
			if(facValid){		//Con los botones disabled ya no entra por aqui BORRAR
				_validate.addError(new ErrorVO(track, "Fichero/s no guardado/s"));
				logger.error("registrarFAC :: TR:{} :: EXP:{} :: Fichero no guardado: {} :: Factura ya validada.", track, exp, fileName);
				return _validate;
			}
		}
		return _validate;
	}
	
	private ValidateDTO<PedidoFilesVO> registrarOTR (MyServiceContext _context, 
			MultipartFile[] _otrFiles, 
			PedidoFilesVO _pedidoReg,
			ValidateDTO<PedidoFilesVO> _validate) 
					throws PedidoFilesException, StorageException, IOException {
		
		//Registrar OTR
		MultipartFile[] OTR_filesToSend = null;
		String track = _pedidoReg.getRefPed();
		String exp = _pedidoReg.getExpediente();
		String trackExp = track.concat(exp);
		
		//OPERACIONES FILES ::
		if(_otrFiles.length > 0){
			String sPathOTR = "";
			OTR_filesToSend = FileTransferUtils.eliminarFicherosRepetidos(_otrFiles);
			
			for(int i=0; i < OTR_filesToSend.length; i++){
				String completeFname = StringUtils.cleanPath(OTR_filesToSend[i].getOriginalFilename());
				Path newPath = FileTransferUtils.getPath("OTR", trackExp, completeFname);
				sPathOTR = newPath.toString()+"~~"+sPathOTR;
			}
			
			if(OTR_filesToSend.length > 0){
				for(int i=0; i < OTR_filesToSend.length; i++){
					String completeFname = StringUtils.cleanPath(OTR_filesToSend[i].getOriginalFilename());
					//Nuevos OTRs se guardan siempre en 'CURRENT_YEAR'
					Path pathOTR = FileTransferUtils.getPath("OTR", trackExp, completeFname);
					
					//Validación Archivos Maliciosos y Estructurales
					try {
						FileTransferUtils.validacionAntiMalware(_context, OTR_filesToSend[i], completeFname, "OTR");
					} catch (PedidoFilesException ex){
					    logger.error("Error de integridad de archivo OTR: " + ex.getMessage());
					    _validate.addError(new ErrorVO(track, "Error de integridad de archivo Otros: "+ex.getMessage())); 
					    //Devolvemos el validate con el error integrado sin romper la ejecución de los otros archivos (CMR/FAC)
					    return _validate;
					}
					
					//SAVE OTRs :: no se hace nada con ellos, solo guardarlos
					//No hace falta ni validarlos, ni rechazarlos, ni conversiones
					FileTransferUtils.storeFile(pathOTR, OTR_filesToSend[i]);
					logger.info("registrarOTR :: TR:{} :: EXP:{} :: OTR almacenado.", track, exp);
				}
			}
			
			//OPERACIONES DDBB ::
			PedidoFilesVO pedidoAux = new PedidoFilesVO();
			pedidoAux.setRefPed(track);
			pedidoAux.setExpediente(exp);
			//Será siempre UPDATE del registro sin tocar la fecREAL
			//Datos de los OTR
			pedidoAux.setRutaOTR(sPathOTR);
			pedidoAux.setUsuSubidaOTR(_context.getUsername());
			pedidoAux.setFechaSubidaOTR(LocalDateTime.now());
			
			//UPDATE Solo los campos de OTRs
			PedidoFilesVO pedidoUpdate = new PedidoFilesVO();
			pedidoUpdate = pedidoFilesDAO.updateOTR(pedidoAux);
			logger.info("registrarOTR :: TR:{} :: EXP:{} :: OTR registrado.", track, exp);
			_validate.setDatos(pedidoUpdate);
		}
		return _validate;
	}
	
	private ValidateDTO<PedidoFilesVO> validarCMR (MyServiceContext _context, 
			MultipartFile[] _fileReceived, 
			PedidoFilesVO _pedidoReg, 
			ValidateDTO<PedidoFilesVO> _validate) 
					throws IOException, StorageException, PedidoFilesException {
		
		if(_fileReceived.length > 0){
			String track = _pedidoReg.getRefPed();
			String exp = _pedidoReg.getExpediente();
			
			//Solo si estadoCMR = 1 :: subido_NO_validado 
			if(_pedidoReg.getEstadoCMR().equals(FileTransferConstantes.ESTADO_1_FICH_SUBIDO)){
				
				//OPERACIONES FILES ::
				String rutaCMR = _pedidoReg.getRutaCMR();
				String fExt = FileTransferUtils.getExt(rutaCMR);
				String fName = FileTransferUtils.getFileName(rutaCMR);
				String originFname = fName.concat("."+fExt);
				String completeFnamePDF = fName.concat(".pdf");
				Path oldPath = Paths.get(rutaCMR);
				Path pathVALCMRpdf = FileTransferUtils.getNewPath("VAL_CMR", oldPath, completeFnamePDF);
				String _dirDes = oldPath.getParent()+"/validados/";
				
				//Variable común donde depositaremos el PDF listo (con marca de agua) para ser firmado
				byte[] pdfConMarcaDeAguaBytes = null;
				
				//Guardar ficheros dependiendo de su extensión
				if(!fExt.toLowerCase().equals("pdf")){
					//Si no es .pdf viene ya con el sello
					//Lo transformamos a .pdf
					byte[] imgConvertidaEnPDF = FileTransferUtils.convertirImagenApdf(_fileReceived[0].getBytes(), fExt);
					logger.info("validarCMR :: TR:{} :: EXP:{} :: Imagen convertida a PDF.", track, exp);
					
					//SAVE IMG_CONVERT.PDF
					pdfConMarcaDeAguaBytes = imgConvertidaEnPDF;
					
				} else {
					try {
						//Si es .pdf lo pasamos por 'convertAPI' para poner el sello
						FileSystemCertSignService.watermarkPDF(rutaCMR, _dirDes);
						logger.info("validarCMR :: TR:{} :: EXP:{} :: watermarkPDF almacenado.", track, exp);
					} catch (PedidoFilesException ex){
						logger.info("ERROR al firmar el documento: " + ex.getMessage());
						_validate.addError(new ErrorVO(track, "ERROR al firmar el documento"));
						return _validate;
					}
					//Como 'watermarkPDF' lo escribe en disco, leemos sus bytes para poder aplicarle la firma criptográfica
					Path pathPdfConSelloConvertApi = Paths.get(_dirDes, completeFnamePDF);
					pdfConMarcaDeAguaBytes = Files.readAllBytes(pathPdfConSelloConvertApi);
				}
				
				// =============================================
				// BLOQUE DE CERTIFICACIÓN CRIPTOGRÁFICA (PAdES)
				// =============================================
				logger.info("validarCMR :: TR:{} :: EXP:{} :: Iniciando certificación digital del PDF...", track, exp);
				String p12certPath = FileTransferConfiguration.getProperty(FileTransferConstantes.CERT_PATH);
				String p12certPass = FileTransferConfiguration.getProperty(FileTransferConstantes.CERT_PASS);

				try {
					byte[] pdfCertificadoBytes = FileSystemCertSignService.firmarPDFconCertificado(pdfConMarcaDeAguaBytes, p12certPath, p12certPass);
					//GUARDADO: Guardamos el PDF final firmado y certificado en su ubicación destino
					FileTransferUtils.storeByteFile(pathVALCMRpdf, pdfCertificadoBytes);
					logger.info("validarCMR :: TR:{} :: EXP:{} :: PDF Certificado digitalmente y almacenado con éxito.", track, exp);
				} catch (PedidoFilesException ex){
					logger.info("ERROR en la certificación digital PAdES: " + ex.getMessage());
					_validate.addError(new ErrorVO(track, "ERROR en la certificación digital PAdES"));
					return _validate;
				}
				// =============================================
				// BLOQUE DE CERTIFICACIÓN CRIPTOGRÁFICA (PAdES) - FIN
				// =============================================
				
				//SAVE FAC :: Para Matrix
				if(_pedidoReg.getEstadoFAC().equals(FileTransferConstantes.ESTADO_1_FICH_SUBIDO)){
					String rutaFAC = _pedidoReg.getRutaFAC();
					String facExt = FileTransferUtils.getExt(rutaFAC);
					String facName = FileTransferUtils.getFileName(rutaFAC);
					String completeFACfname = facName.concat("."+facExt);
					
					Path pathFACdocPRO = FileTransferUtils.getPath("FACMATRIX_A", null, completeFACfname);
					FileTransferUtils.renameFile("FACMATRIX", pathFACdocPRO, completeFACfname, null);
					logger.info("validarCMR :: TR:{} :: EXP:{} :: Factura almacenada en Matrix.", track, exp);
				}
				
				//OPERACIONES DDBB ::
				PedidoFilesVO pedidoAux = new PedidoFilesVO();
				pedidoAux.setRefPed(track);
				pedidoAux.setExpediente(exp);
				//Datos del CMR
				pedidoAux.setRutaCMR(pathVALCMRpdf.toString());
				pedidoAux.setUsuSubidaCMR(_pedidoReg.getUsuSubidaCMR());
				pedidoAux.setEstadoCMR(FileTransferConstantes.ESTADO_2_FICH_VALIDADO);
				pedidoAux.setFechaSubidaCMR(_pedidoReg.getFechaSubidaCMR());
				pedidoAux.setFechaEstadoCMR(LocalDateTime.now());
				pedidoAux.setUsuOkCMR(_context.getUsername());
				
				//Mantenemos lo que tenga (puede tener anotaciones)
				pedidoAux.setAnotaciones(_pedidoReg.getAnotaciones());
				pedidoAux.setMotivoRechazo(_pedidoReg.getMotivoRechazo());
					
				PedidoFilesVO pedidoUpdate = new PedidoFilesVO();
				//UPDATE Solo los campos del CMR
				pedidoUpdate = pedidoFilesDAO.updateCMR(pedidoAux);
				logger.info("validarCMR :: TR:{} :: EXP:{} :: Actualizado registro CMR.", track, exp);
				
				pedidoUpdate = pedidoFilesDAO.getPedidoFiles(track, exp);
				_validate.setDatos(pedidoUpdate);
				
				//UPDATE Estado General del pedido
				Integer estado = FileTransferUtils.determinarEstadoGeneral(pedidoUpdate, track, exp);
				pedidoFilesDAO.updateESTADO(track, exp, estado);
				logger.info("validarCMR :: TR:{} :: EXP:{} :: Actualizado Estado General.", track, exp);
				
				//DELETE de ./subidos
				try {
					FileTransferUtils.deleteFile(oldPath, originFname);
					logger.info("validarCMR :: TR:{} :: EXP:{} :: CMR de origen borrado.", track, exp);
				} catch(StorageException e) {
					logger.error("validarCMR :: TR:{} :: EXP:{} :: No se pudo eliminar el original de ./subidos: {}", track, exp, e.getMessage());
				}

			} else {
				_validate.addError(new ErrorVO(track, "CMR no validado: "+track));
				logger.error("validarCMR :: CMR no validado :: TR:{} :: EXP:{}", track, exp);
			}
		}
		return _validate;
	}
	
	private ValidateDTO<PedidoFilesVO> rechazarCMR (MyServiceContext _context, 
			PedidoFilesVO _pedidoReg,
			String _motivoRechazo) 
					throws PedidoFilesException, StorageException, IOException {
		
		//OPERACIONES FILES ::
		String rutaCMR = _pedidoReg.getRutaCMR();
		String fName = FileTransferUtils.getFileName(rutaCMR);
		String fExt = FileTransferUtils.getExt(rutaCMR);
		String completeFname = fName.concat("."+fExt);
		Path oldPath = Paths.get(rutaCMR);
		Path pathREJCMR = FileTransferUtils.getNewPath("REJ_CMR", oldPath, completeFname);
		
		//RENAME de ./subidos a ./rechazados
		FileTransferUtils.renameFile("REJ_CMR", pathREJCMR, fName, fExt);
		
		String track = _pedidoReg.getRefPed();
		String exp = _pedidoReg.getExpediente();
		
		//OPERACIONES DDBB ::
		PedidoFilesVO pedidoAux = new PedidoFilesVO();
		pedidoAux.setRefPed(track);
		pedidoAux.setExpediente(exp);
		pedidoAux.setMotivoRechazo(_motivoRechazo);
		pedidoAux.setAnotaciones(FileTransferConstantes.SIN_ANOTACIONES);
		//Datos del CMR
		pedidoAux.setRutaCMR(pathREJCMR.toString());
		pedidoAux.setUsuSubidaCMR(_pedidoReg.getUsuSubidaCMR());
		pedidoAux.setEstadoCMR(FileTransferConstantes.ESTADO_3_FICH_RECHAZADO);
		pedidoAux.setFechaSubidaCMR(_pedidoReg.getFechaSubidaCMR());
		pedidoAux.setFechaEstadoCMR(LocalDateTime.now());
		pedidoAux.setUsuOkCMR(_context.getUsername());
		
		//UPDATE los campos del CMR
		PedidoFilesVO pedidoUpdate = new PedidoFilesVO();
		pedidoUpdate = pedidoFilesDAO.updateCMR(pedidoAux);
		logger.info("rechazarCMR :: Actualizado registro CMR.");
		
		pedidoUpdate = pedidoFilesDAO.getPedidoFiles(track, exp);
				
		//UPDATE Estado General del pedido
		Integer estado = FileTransferUtils.determinarEstadoGeneral(pedidoUpdate, track, exp);
		pedidoFilesDAO.updateESTADO(track, exp, estado);
		logger.info("rechazarCMR :: Actualizado Estado General: {}", estado);
		
		ValidateDTO<PedidoFilesVO> validate = new ValidateDTO<PedidoFilesVO>();
		validate.setDatos(pedidoUpdate);

		return validate;
	}
	
	private ValidateDTO<PedidoFilesVO> rechazarFAC (MyServiceContext _context, 
			PedidoFilesVO _pedidoReg,
			String _motivoRechazo) 
					throws PedidoFilesException, StorageException, IOException {
		
		//OPERACIONES FILES ::
		String rutaFAC = _pedidoReg.getRutaFAC();
		String fName = FileTransferUtils.getFileName(rutaFAC);
		String fExt = FileTransferUtils.getExt(rutaFAC);
		String completeFname = fName.concat("."+fExt);
		Path oldPath = Paths.get(rutaFAC);
		Path pathREJFAC = FileTransferUtils.getNewPath("REJ_FAC", oldPath, completeFname);
		
		//RENAME de ./subidos a ./rechazadas
		FileTransferUtils.renameFile("REJ_FAC", pathREJFAC, fName, fExt);
		
		String track = _pedidoReg.getRefPed();
		String exp = _pedidoReg.getExpediente();
		
		//OPERACIONES DDBB ::
		PedidoFilesVO pedidoAux = new PedidoFilesVO();
		pedidoAux.setRefPed(track);
		pedidoAux.setExpediente(exp);
		pedidoAux.setMotivoRechazo(_motivoRechazo);
		pedidoAux.setAnotaciones(FileTransferConstantes.CON_ANOTACIONES);
		//Datos de la FAC
		pedidoAux.setRutaFAC(pathREJFAC.toString());
		pedidoAux.setUsuSubidaFAC(_pedidoReg.getUsuSubidaFAC());
		pedidoAux.setEstadoFAC(FileTransferConstantes.ESTADO_3_FICH_RECHAZADO);
		pedidoAux.setFechaSubidaFAC(_pedidoReg.getFechaSubidaFAC());
		pedidoAux.setFechaEstadoFAC(LocalDateTime.now());
		pedidoAux.setUsuOkFAC(_context.getUsername());
		
		//UPDATE solo los campos de la FAC
		PedidoFilesVO pedidoUpdate = new PedidoFilesVO();
		pedidoUpdate = pedidoFilesDAO.updateFAC(pedidoAux);
		logger.info("rechazarFAC :: Actualizado registro FAC.");
		
		pedidoUpdate = pedidoFilesDAO.getPedidoFiles(track, exp);
				
		//UPDATE Estado General del pedido
		Integer estado = FileTransferUtils.determinarEstadoGeneral(pedidoUpdate, track, exp);
		pedidoFilesDAO.updateESTADO(track, exp, estado);
		logger.info("rechazarFAC :: Actualizado Estado General: {}", estado);
		
		ValidateDTO<PedidoFilesVO> validate = new ValidateDTO<PedidoFilesVO>();
		validate.setDatos(pedidoUpdate);

		return validate;
	}
	
}
