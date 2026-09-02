//package eus.martico.fileTransfer.control.firmador;
//
//import java.io.IOException;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.Resource;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import eus.martico.fileTransfer.config.FileTransferConstantes;
//import eus.martico.fileTransfer.control.MyControlUtils;
//import eus.martico.fileTransfer.control.bloqueo.BloqueoComponentIService;
//import eus.martico.fileTransfer.dao.firmador.FirmadorException;
//import eus.martico.fileTransfer.dao.usuario.UsuarioDAOException;
//import eus.martico.fileTransfer.model.bloqueo.BloqueoControlVO;
//import eus.martico.fileTransfer.model.error.vo.ErrorVO;
//import eus.martico.fileTransfer.model.firma.PedidoFirmadorVO;
//import eus.martico.fileTransfer.model.firma.PipelineFirmaDTO;
//import eus.martico.fileTransfer.model.vo.BuscarDTO;
//import eus.martico.fileTransfer.model.vo.ValidateDTO;
//import eus.martico.fileTransfer.seguridad.user.entity.SegurUser;
//import eus.martico.fileTransfer.services.MyServiceContext;
//import eus.martico.fileTransfer.services.bloqueo.BloqueoServiceException;
//import eus.martico.fileTransfer.services.firma.FirmadorIService;
//import eus.martico.fileTransfer.services.firma.FirmadorServiceException;
//import eus.martico.fileTransfer.services.firma.FirmadorServiceImp;
//
//
///**
// * NEW descartado
// * @author Martico S.L. :: ialfonso
// * @version 1.0
// * @see https://www.martico.es/
// *
// */
//@RestController()
//@RequestMapping(value = "/api/v1/firmador")
//public class FirmadorControllerV1 extends FirmadorServiceImp {
//	
//	@Autowired(required=true)
//	private FirmadorIService firmadorService;
//	
//	@Autowired()
//	private BloqueoComponentIService bloqueoService;
//
//	
//	
//	@RequestMapping(method = RequestMethod.GET, path="/getPedidoFirmaVO/{track}/{exp}")
//	public @ResponseBody() BuscarDTO<PedidoFirmadorVO> getPedidoFirmaVO(
//			@AuthenticationPrincipal final SegurUser user, 
//			@PathVariable(required=true) String track,
//			@PathVariable(required=true) String exp) throws FirmadorException {
//		
//		MyServiceContext context = MyControlUtils.getServiceContext(user);
//		return firmadorService.getPedidoFirma(context, track, exp);
//	}
//	
//	
//	@RequestMapping(method = RequestMethod.GET, path="/getByFilter")
//	public @ResponseBody() List<PedidoFirmadorVO> getPedidosFiltrados(
//			@AuthenticationPrincipal final SegurUser user,
//			@RequestParam(required=false) String expFilterValue,
//	        @RequestParam(required=false) String provName,
//	        @RequestParam(required=false) String trFilterValue,
//	        @RequestParam(required=false) String refCharge)	
//	        		throws FirmadorServiceException, FirmadorException, UsuarioDAOException {
//		
//		MyServiceContext context = MyControlUtils.getServiceContext(user);
//		return firmadorService.getPedidosFiltrados(context, expFilterValue, provName, trFilterValue, refCharge);
//	}
//	
//	
//	@RequestMapping(method = RequestMethod.GET, path="/getCMRfirma/{track}/{exp}")
//	public @ResponseBody() ResponseEntity<Resource> getCMRfirma(
//			@AuthenticationPrincipal final SegurUser _user,
//			@PathVariable String track,
//			@PathVariable String exp) throws FirmadorException {
//		
//		MyServiceContext context = MyControlUtils.getServiceContext(_user);
//		return firmadorService.loadFirmaCMRasResource(context, track, exp);
//	}
//	
//	
//	@RequestMapping(method = RequestMethod.POST, path = "/postSigned/{lockType}")
//	public @ResponseBody() ValidateDTO<PedidoFirmadorVO> postSignedFile(
//			@AuthenticationPrincipal final SegurUser _user,
//			@PathVariable(required = true) String lockType,
//			@RequestBody PipelineFirmaDTO firmaDTO) 
//					throws IOException, FirmadorException, BloqueoServiceException, FirmadorServiceException {
//		
//		MyServiceContext context = MyControlUtils.getServiceContext(_user);
//		ValidateDTO<PedidoFirmadorVO> validate = new ValidateDTO<PedidoFirmadorVO>();
//		String track = firmaDTO.getTrack();
//	    String exp = firmaDTO.getExp();
//		
//		String bloqCod = "";
//		if(lockType.equalsIgnoreCase(FileTransferConstantes.BLOQ_VALIDADOR)){
//			bloqCod = "VAL_";
//		}
//		if(lockType.equalsIgnoreCase(FileTransferConstantes.BLOQ_FIRMADOR)){
//			bloqCod = "FIR_";
//		}
//		String codigo = bloqCod +track.toString();
//		if(track.startsWith("GRU") && exp != null){
//			codigo = codigo+"-"+exp;
//		}
//		if(codigo.length()>32){
//			codigo = codigo.substring(0, 32);
//		}
//		BloqueoControlVO bloqueoAct = bloqueoService.comprobarBloqueoUsuarioSession(context, lockType, codigo, _user.getNi());
//		if(bloqueoAct.getBloqueo() != null){
//			if(!bloqueoAct.getBloqueo().getUsuarioNi().equals(context.getUsuarioNi())){
//				validate.addError(new ErrorVO(track, "El usuario: '"+bloqueoAct.getBloqueo().getUsername()+"', está bloqueando este CMR."));
//			} else {
//				validate = firmadorService.añadirFirmaIncremental(context, firmaDTO);
//			}
//		}
//		return validate;
//	}
//	
//	
//	@ExceptionHandler(FirmadorServiceException.class)
//	public ResponseEntity<?> handleStorageFileNotFound(FirmadorServiceException exc) {
//		return ResponseEntity.notFound().build();
//	}
//
//}
