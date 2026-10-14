package eus.martico.fileTransfer.control.pedidos.validador;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import eus.martico.fileTransfer.control.MyControlUtils;
import eus.martico.fileTransfer.control.bloqueo.BloqueoComponentIService;
import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesException;
import eus.martico.fileTransfer.model.bloqueo.BloqueoControlVO;
import eus.martico.fileTransfer.model.error.vo.ErrorVO;
import eus.martico.fileTransfer.model.pedido.files.PedidoFilesVO;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoProveedorVO;
import eus.martico.fileTransfer.model.vo.ValidateDTO;
import eus.martico.fileTransfer.seguridad.user.entity.SegurUser;
import eus.martico.fileTransfer.services.MyServiceContext;
import eus.martico.fileTransfer.services.bloqueo.BloqueoServiceException;
import eus.martico.fileTransfer.services.pedido.files.FileSystemStorageIService;
import eus.martico.fileTransfer.services.pedidos.validador.PedidosValidadorIService;
import eus.martico.fileTransfer.services.pedidos.validador.PedidosValidadorServiceException;
import eus.martico.fileTransfer.services.pedidos.validador.PedidosValidadorServiceImp;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@RestController()
@RequestMapping(value = "/api/v1/pedidosValidador")
public class PedidosValidadorControllerV1 extends PedidosValidadorServiceImp {
	
	@Autowired(required=true)
	private PedidosValidadorIService pedidosValidadorService;
	
	@Autowired()
	private FileSystemStorageIService storageService;
	
	@Autowired(required=true)
	private BloqueoComponentIService bloqueoService;
	
	
	
	@RequestMapping(method = RequestMethod.PUT, path="/updateNotes/{lockType}/{track}/{exp}")
	public @ResponseBody()
	ValidateDTO<PedidoFilesVO> putAnotacion(@AuthenticationPrincipal final SegurUser _user,
			@PathVariable(required=true) String lockType,
			@PathVariable(required=true) String track,
			@PathVariable(required=true) String exp,
			@RequestBody String anota) 
					throws PedidoFilesException, IOException, BloqueoServiceException {
		
		MyServiceContext context = MyControlUtils.getServiceContext(_user);
		
		ValidateDTO<PedidoFilesVO> validate = new ValidateDTO<PedidoFilesVO>();
		String codigo = "VAL_" +track.toString();
		if(track.startsWith("GRU") && exp != null){
			codigo = codigo+"-"+exp;
		}
		if(codigo.length()>32){
			codigo = codigo.substring(0, 32);
		}
		
		BloqueoControlVO bloqueoAct = bloqueoService.comprobarBloqueoUsuarioSession(context, lockType, codigo, _user.getNi());
		if(bloqueoAct.getBloqueo() != null && !bloqueoAct.isMismoUsuario()){
			validate.addError(new ErrorVO(track, "El CMR está bloqueado por el usuario: "+bloqueoAct.getBloqueo().getUsername()));
		} else {
			if (anota != null && anota.trim().startsWith("{")) {
			    try {
			        ObjectMapper mapper = new ObjectMapper();
			        JsonNode node = mapper.readTree(anota);
			        // "anota" es la clave que manda el front {"anota":"..."}
			        if (node.has("anota")) {
			            anota = node.get("anota").asText(); 
			        }
			    } catch (Exception e) {
			        //logger.error("Error parseando el comentario: {}", e.getMessage());
			        //Si falla el parseo, aplicamos un fallback limpio por si acaso
			        anota = "";
			    }
			}
			validate = pedidosValidadorService.marcarAnotacionCMR(context, track, exp, anota);
		}
		return validate;
	}
	
	@RequestMapping(method = RequestMethod.PUT, path="/rejectCMR/{lockType}/{track}/{exp}")
	public @ResponseBody()
	ValidateDTO<PedidoFilesVO> putRechazoCMR(
			@AuthenticationPrincipal final SegurUser _user,
			@PathVariable(required=true) String lockType,
			@PathVariable(required=true) String track,
			@PathVariable(required=true) String exp,
			@RequestBody String motRec) 
					throws PedidoFilesException, IOException, BloqueoServiceException {
		
		MyServiceContext context = MyControlUtils.getServiceContext(_user);
		
		ValidateDTO<PedidoFilesVO> validate = new ValidateDTO<PedidoFilesVO>();
		String codigo = "VAL_" +track.toString();
		if(track.startsWith("GRU") && exp != null){
			codigo = codigo+"-"+exp;
		}
		if(codigo.length()>32){
			codigo = codigo.substring(0, 32);
		}
		
		BloqueoControlVO bloqueoAct = bloqueoService.comprobarBloqueoUsuarioSession(context, lockType, codigo, _user.getNi());
		if(bloqueoAct.getBloqueo() != null){
			if(!bloqueoAct.getBloqueo().getUsuarioNi().equals(context.getUsuarioNi())){
				validate.addError(new ErrorVO(track, "El usuario: '"+bloqueoAct.getBloqueo().getUsername()+"', está bloqueando este CMR."));
			} else {
				if (motRec != null && motRec.trim().startsWith("{")) {
				    try {
				        ObjectMapper mapper = new ObjectMapper();
				        JsonNode node = mapper.readTree(motRec);
				        // "motRec" es la clave que manda el front {"motRec":"..."}
				        if (node.has("motRec")) {
				        	motRec = node.get("motRec").asText(); 
				        }
				    } catch (Exception e) {
				        //logger.error("Error parseando el comentario: {}", e.getMessage());
				        //Si falla el parseo, aplicamos un fallback limpio por si acaso
				    	motRec = "";
				    }
				}
				validate = storageService.rejectCMRFile(context, track, exp, motRec);
			}
		}
		return validate;
	}
	
	@RequestMapping(method = RequestMethod.PUT, path="/rejectFAC/{lockType}/{track}/{exp}")
	public @ResponseBody()
	ValidateDTO<PedidoFilesVO> putRechazoFAC(
			@AuthenticationPrincipal final SegurUser _user,
			@PathVariable(required=true) String lockType,
			@PathVariable(required=true) String track,
			@PathVariable(required=true) String exp,
			@RequestBody String motRec) 
					throws PedidoFilesException, IOException, BloqueoServiceException {
		
		MyServiceContext context = MyControlUtils.getServiceContext(_user);
		
		ValidateDTO<PedidoFilesVO> validate = new ValidateDTO<PedidoFilesVO>();
		String codigo = "VAL_" +track.toString();
		if(track.startsWith("GRU") && exp != null){
			codigo = codigo+"-"+exp;
		}
		if(codigo.length()>32){
			codigo = codigo.substring(0, 32);
		}
		
		BloqueoControlVO bloqueoAct = bloqueoService.comprobarBloqueoUsuarioSession(context, lockType, codigo, _user.getNi());
		if(bloqueoAct.getBloqueo() != null){
			if(!bloqueoAct.getBloqueo().getUsuarioNi().equals(context.getUsuarioNi())){
				validate.addError(new ErrorVO(track, "El usuario: '"+bloqueoAct.getBloqueo().getUsername()+"', está bloqueando esta Factura."));
			} else {
				if (motRec != null && motRec.trim().startsWith("{")) {
				    try {
				        ObjectMapper mapper = new ObjectMapper();
				        JsonNode node = mapper.readTree(motRec);
				        // "motRec" es la clave que manda el front {"motRec":"..."}
				        if (node.has("motRec")) {
				        	motRec = node.get("motRec").asText(); 
				        }
				    } catch (Exception e) {
				        //logger.error("Error parseando el comentario: {}", e.getMessage());
				        //Si falla el parseo, aplicamos un fallback limpio por si acaso
				    	motRec = "";
				    }
				}
				validate = storageService.rejectFACFile(context, track, exp, motRec);
			}
		}
		return validate;
	}
	
	@RequestMapping(method = RequestMethod.GET, path="/getByFilter")
	public @ResponseBody()
	List<PedidoProveedorVO> getPedidosFiltrados(
			@AuthenticationPrincipal final SegurUser user,
			@RequestParam(required=false) String expediente,
			@RequestParam(required=false) String provName,
			@RequestParam(required=false) String trFilterValue,
			@RequestParam(required=false) String refCharge,
			@RequestParam(required=false) String fecDesde,
	        @RequestParam(required=false) String fecHasta)
					throws PedidosValidadorServiceException, PedidoFilesException {
		MyServiceContext context = MyControlUtils.getServiceContext(user);
		return pedidosValidadorService.getPedidosFiltrados(context, expediente, provName, trFilterValue, refCharge, fecDesde, fecHasta);
	}
	
	@ExceptionHandler(PedidosValidadorServiceException.class)
	public ResponseEntity<?> handleStorageFileNotFound(PedidosValidadorServiceException exc) {
		return ResponseEntity.notFound().build();
	}

}
