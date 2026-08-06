package eus.martico.fileTransfer.control.upload;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import eus.martico.fileTransfer.config.FileTransferConfiguration;
import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.control.MyControlUtils;
import eus.martico.fileTransfer.control.bloqueo.BloqueoComponentIService;
import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesException;
import eus.martico.fileTransfer.model.bloqueo.BloqueoControlVO;
import eus.martico.fileTransfer.model.error.vo.ErrorVO;
import eus.martico.fileTransfer.model.pedido.files.PedidoFilesVO;
import eus.martico.fileTransfer.model.vo.BuscarDTO;
import eus.martico.fileTransfer.model.vo.ValidateDTO;
import eus.martico.fileTransfer.seguridad.user.entity.SegurUser;
import eus.martico.fileTransfer.services.MyServiceContext;
import eus.martico.fileTransfer.services.bloqueo.BloqueoServiceException;
import eus.martico.fileTransfer.services.pedido.files.FileSystemStorageIService;
import eus.martico.fileTransfer.services.pedido.files.StorageFileNotFoundException;
import eus.martico.fileTransfer.utils.FileTransferUtils;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@RestController()
@RequestMapping(value = "/api/v1/files")
public class FileUploadControllerV1 {

	public final static String signFile = FileTransferConfiguration.getProperty(FileTransferConstantes.SIGN_FILE_NAME);
	
	protected static Logger logger = LoggerFactory.getLogger(FileUploadControllerV1.class);
	
	
	@Autowired()
	private FileSystemStorageIService storageService;
	
	@Autowired()
	private BloqueoComponentIService bloqueoService;
	
	
	
	@RequestMapping(method = RequestMethod.GET, path="/getPedidoFilesVO/{track}/{exp}")
	public @ResponseBody()
	BuscarDTO<PedidoFilesVO> getPedidoFilesVO(
			@AuthenticationPrincipal final SegurUser user, 
			@PathVariable(required=true) String track,
			@PathVariable(required=true) String exp) 
					throws PedidoFilesException {
		MyServiceContext context = MyControlUtils.getServiceContext(user);
		return storageService.getPedidoFiles(context, track, exp);
	}
	
	@RequestMapping(method = RequestMethod.GET, path="/getSIGN")
	public @ResponseBody()
	ResponseEntity<Resource> getSignFile(
			@AuthenticationPrincipal final SegurUser _user) {
		
    	String signfilename = signFile;
		MyServiceContext context = MyControlUtils.getServiceContext(_user);
		logger.info("{} :: Obteniendo Firma :: {}", context.getUsername(), signfilename);
		
		Resource file = storageService.loadSign(signfilename);
		String fileExt = FileTransferUtils.getExt(signfilename);
		MediaType MediaTypeValue = FileTransferUtils.getMediaType(fileExt);
		
		return ResponseEntity.ok()
				.contentType(MediaTypeValue)
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}
	
	@RequestMapping(method = RequestMethod.GET, path="/getCMR/{track}/{exp}")
	public @ResponseBody() ResponseEntity<Resource> getCMRFile(
			@AuthenticationPrincipal final SegurUser _user,
			@PathVariable String track,
			@PathVariable String exp) 
					throws PedidoFilesException {
		MyServiceContext context = MyControlUtils.getServiceContext(_user);
		return storageService.loadCMRasResource(context, track, exp);
	}
	
	
	@RequestMapping(method = RequestMethod.GET, path="/getValidCMR/{track}/{exp}/{filename:.+}")
	public @ResponseBody() ResponseEntity<Resource> getValidCMRFile(
			@AuthenticationPrincipal final SegurUser _user,
			@PathVariable String track,
			@PathVariable String exp,
			@PathVariable String filename) 
					throws PedidoFilesException {
		
		MyServiceContext context = MyControlUtils.getServiceContext(_user);
		
		Resource file = storageService.loadValidCMRasResource(context, filename, track, exp);
		String fileExt = FileTransferUtils.getExt(filename);
		MediaType MediaTypeValue = FileTransferUtils.getMediaType(fileExt);
		
		return ResponseEntity.ok()
				.contentType(MediaTypeValue)
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}
	
	@RequestMapping(method = RequestMethod.GET, path="/getFAC/{track}/{exp}/{filename:.+}")
	public @ResponseBody() ResponseEntity<Resource> getFACFile(
			@AuthenticationPrincipal final SegurUser _user,
			@PathVariable String track,
			@PathVariable String exp,
			@PathVariable String filename) 
					throws PedidoFilesException {
		
		MyServiceContext context = MyControlUtils.getServiceContext(_user);
		
		Resource file = storageService.loadFACasResource(context, filename, track, exp);
		String fileExt = FileTransferUtils.getExt(filename);
		MediaType MediaTypeValue = FileTransferUtils.getMediaType(fileExt);
		
		return ResponseEntity.ok()
				.contentType(MediaTypeValue)
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/getFile/{fileType}/{track}/{exp}/{filename:.+}")
	public @ResponseBody() ResponseEntity<Resource> getFile(
			@AuthenticationPrincipal final SegurUser _user,
			@PathVariable(required = true) String fileType,
			@PathVariable String track,
			@PathVariable String exp,
			@PathVariable(required = true) String filename) 
					throws PedidoFilesException {
		
		MyServiceContext context = MyControlUtils.getServiceContext(_user);
		
		Resource file = storageService.loadFileAsResource(context, fileType, filename, track, exp);
		String fileExt = FileTransferUtils.getExt(filename);
		MediaType MediaTypeValue = FileTransferUtils.getMediaType(fileExt);

		return ResponseEntity.ok()
				.contentType(MediaTypeValue)
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}
	
	
	@RequestMapping(method = RequestMethod.POST, path="/post/{lockType}")
	public @ResponseBody()
	ValidateDTO<PedidoFilesVO> postFiles(
			@AuthenticationPrincipal final SegurUser _user,
			@PathVariable(required=true) String lockType,
			@RequestParam("CMR_file") MultipartFile CMR_fileReceived[],
			@RequestParam("FAC_file") MultipartFile FAC_fileReceived[],
			@RequestParam("OTR_files") MultipartFile OTR_filesReceived[],
			@RequestParam("pedidoTrack") String _pedidoTrack,
			@RequestParam("pedidoExp") String _pedidoExp,
			@RequestParam("refCarga") String _refCarga)
					throws IOException, PedidoFilesException, BloqueoServiceException {
		
		MyServiceContext context = MyControlUtils.getServiceContext(_user);
		
		ValidateDTO<PedidoFilesVO> validate = new ValidateDTO<PedidoFilesVO>();
		char charCod = lockType.charAt(0);
		String codigo = charCod +"TR_" +_pedidoTrack.toString();
		if(_pedidoTrack.startsWith("GRU") && _pedidoExp != null){
			codigo = codigo+"-"+_pedidoExp;
		}
		if(codigo.length()>32){
			codigo = codigo.substring(0, 32);
		}
		
		BloqueoControlVO bloqueoAct = bloqueoService.comprobarBloqueoUsuarioSession(context, lockType, codigo, _user.getNi());
		if(CMR_fileReceived.length > 0){
			if(bloqueoAct.getBloqueo() != null){
				//Bloqueado si quieren subir un CMR mientras se está validando el ya subido anteriormente
				validate.addError(new ErrorVO(_pedidoTrack, "CMR bloqueado para validación."));
			} else {
				validate = storageService.registerFiles(context, CMR_fileReceived, FAC_fileReceived, OTR_filesReceived, _pedidoTrack, _pedidoExp, _refCarga);
			}
		} else {
			validate = storageService.registerFiles(context, CMR_fileReceived, FAC_fileReceived, OTR_filesReceived, _pedidoTrack, _pedidoExp, _refCarga);
		}
		
		return validate;
	}
	
	
	@RequestMapping(method = RequestMethod.POST, path = "/postSigned/{lockType}")
	public @ResponseBody()
	ValidateDTO<PedidoFilesVO> postSignCMRFile(
			@AuthenticationPrincipal final SegurUser _user,
			@PathVariable(required = true) String lockType,
			@RequestParam("signedFile") MultipartFile _signedFileReceived[],
			@RequestParam("orderTrack") String _pedidoTrack,
			@RequestParam("orderExp") String _pedidoExp) 
					throws IOException, PedidoFilesException, BloqueoServiceException {
		
		MyServiceContext context = MyControlUtils.getServiceContext(_user);
		
		ValidateDTO<PedidoFilesVO> validate = new ValidateDTO<PedidoFilesVO>();
		char charCod = lockType.charAt(0);
		String codigo = charCod +"TR_"+_pedidoTrack.toString();
		if(_pedidoTrack.startsWith("GRU") && _pedidoExp != null){
			codigo = codigo+"-"+_pedidoExp;
		}
		if(codigo.length()>32){
			codigo = codigo.substring(0, 32);
		}
		
		BloqueoControlVO bloqueoAct = bloqueoService.comprobarBloqueoUsuarioSession(context, lockType, codigo, _user.getNi());
		if(bloqueoAct.getBloqueo() != null){
			if(!bloqueoAct.getBloqueo().getUsuarioNi().equals(context.getUsuarioNi())){
				validate.addError(new ErrorVO(_pedidoTrack, "El usuario: '"+bloqueoAct.getBloqueo().getUsername()+"', está bloqueando este CMR."));
			} else {
				validate = storageService.validateCMRFile(context, _signedFileReceived, _pedidoTrack, _pedidoExp);
			}
		}
		return validate;
	}
	
	
	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}
	
}
