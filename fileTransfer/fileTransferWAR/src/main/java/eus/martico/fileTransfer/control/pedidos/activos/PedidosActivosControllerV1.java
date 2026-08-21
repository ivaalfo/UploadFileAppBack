package eus.martico.fileTransfer.control.pedidos.activos;

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
import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesException;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoGrupajeFechasVO;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoGrupajeVO;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoProveedorVO;
import eus.martico.fileTransfer.seguridad.user.entity.SegurUser;
import eus.martico.fileTransfer.services.MyServiceContext;
import eus.martico.fileTransfer.services.pedidos.activos.PedidosActivosIService;
import eus.martico.fileTransfer.services.pedidos.activos.PedidosActivosServiceException;
import eus.martico.fileTransfer.services.pedidos.activos.PedidosActivosServiceImp;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@RestController()
@RequestMapping(value = "/api/v1/pedidosActivos")
public class PedidosActivosControllerV1 extends PedidosActivosServiceImp {
	
	@Autowired(required=true)
	private PedidosActivosIService pedidosActivosService;
	
	
	@RequestMapping(method = RequestMethod.GET, path="/getGrupaje/{track}")
	public @ResponseBody() PedidoGrupajeVO getGrupaje(
			@AuthenticationPrincipal final SegurUser _user,
			@PathVariable(required=true) String track) throws PedidoFilesException {
		
		MyServiceContext context = MyControlUtils.getServiceContext(_user);
		return pedidosActivosService.getGrupaje(context, track);
	}
	
	@RequestMapping(method = RequestMethod.PUT, path="/putGrupaje/{grupTR}")
	public @ResponseBody() Integer setGrupaje(
			@AuthenticationPrincipal final SegurUser _user,
			@PathVariable(required=true) String grupTR,
			@RequestBody PedidoGrupajeVO pedidoGrup) throws PedidoFilesException {
		
		MyServiceContext context = MyControlUtils.getServiceContext(_user);
		return pedidosActivosService.setGrupaje(context, grupTR, pedidoGrup.arrayGrulog);
	}
	
	@RequestMapping(method = RequestMethod.PUT, path="/putFecReal/{track}/{exp}/{fechaEntReal}")
	public @ResponseBody() Integer setFecReal(
			@AuthenticationPrincipal final SegurUser _user,
			@PathVariable(required=true) String track,
			@PathVariable(required=true) String exp,
			@PathVariable(required=true) String fechaEntReal) throws PedidoFilesException {

		MyServiceContext context = MyControlUtils.getServiceContext(_user);
		return pedidosActivosService.setFecReal(context, track, exp, fechaEntReal);
	}
	
	@RequestMapping(method = RequestMethod.PUT, path="/putFecRealMultiple")
	public @ResponseBody() Integer seFecRealGrup(
			@AuthenticationPrincipal final SegurUser _user,
			@RequestBody PedidoGrupajeFechasVO request) throws PedidoFilesException {
		
		MyServiceContext context = MyControlUtils.getServiceContext(_user);
		int totalProcesados = 0;
		for(PedidoGrupajeFechasVO.FechaItem item : request.arrayFechas){
			totalProcesados += pedidosActivosService.setFecReal(context, item.track, item.expediente, item.fechaEntReal);
		}
		return totalProcesados;
	}
	
	@RequestMapping(method = RequestMethod.PUT, path="/anularPedido/{track}/{exp}")
	public @ResponseBody() Integer setPedidoNulo(
			@AuthenticationPrincipal final SegurUser _user,
			@PathVariable(required=true) String track,
			@PathVariable(required=true) String exp,
			@RequestBody String motivo) throws PedidoFilesException {
		
		MyServiceContext context = MyControlUtils.getServiceContext(_user);
		if (motivo != null && motivo.trim().startsWith("{")) {
		    try {
		        ObjectMapper mapper = new ObjectMapper();
		        JsonNode node = mapper.readTree(motivo);
		        // "motivo" es la clave que manda el front {"motivo":"..."}
		        if (node.has("motivo")) {
		        	motivo = node.get("motivo").asText(); 
		        }
		    } catch (Exception e) {
		        //logger.error("Error parseando el comentario: {}", e.getMessage());
		        //Si falla el parseo, aplicamos un fallback limpio por si acaso
		    	motivo = "";
		    }
		}
		return pedidosActivosService.setPedidoNulo(context, track, exp, motivo);
	}
	
	@RequestMapping(method = RequestMethod.GET, path="/getByFilter")
	public @ResponseBody()
	List<PedidoProveedorVO> getPedidosFiltrados(
			@AuthenticationPrincipal final SegurUser user,
			@RequestParam(required=false) String expediente,
			@RequestParam(required=false) String provName,
			@RequestParam(required=false) String trFilterValue,
			@RequestParam(required=false) String refCharge)	throws PedidosActivosServiceException, PedidoFilesException {
		
		MyServiceContext context = MyControlUtils.getServiceContext(user);
		return pedidosActivosService.getPedidosFiltrados(context, expediente, provName, trFilterValue, refCharge);
	}

	@ExceptionHandler(PedidosActivosServiceException.class)
	public ResponseEntity<?> handleStorageFileNotFound(PedidosActivosServiceException exc) {
		return ResponseEntity.notFound().build();
	}
	
}
