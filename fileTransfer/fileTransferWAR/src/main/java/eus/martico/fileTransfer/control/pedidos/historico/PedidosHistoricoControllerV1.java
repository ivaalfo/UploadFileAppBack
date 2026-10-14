package eus.martico.fileTransfer.control.pedidos.historico;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eus.martico.fileTransfer.control.MyControlUtils;
import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesException;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoProveedorVO;
import eus.martico.fileTransfer.seguridad.user.entity.SegurUser;
import eus.martico.fileTransfer.services.MyServiceContext;
import eus.martico.fileTransfer.services.pedidos.historico.PedidosHistoricoIService;
import eus.martico.fileTransfer.services.pedidos.historico.PedidosHistoricoServiceException;
import eus.martico.fileTransfer.services.pedidos.historico.PedidosHistoricoServiceImp;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@RestController()
@RequestMapping(value = "/api/v1/pedidosHistorico")
public class PedidosHistoricoControllerV1 extends PedidosHistoricoServiceImp {
	
	@Autowired(required=true)
	private PedidosHistoricoIService pedidosHistoricoService;
	
	
	@RequestMapping(method = RequestMethod.PUT, path="/reactivarPedido/{track}/{exp}")
	public @ResponseBody()
	Integer setReactivatePedido(@AuthenticationPrincipal final SegurUser _user,
			@PathVariable(required=true) String track,
			@PathVariable(required=true) String exp) 
					throws PedidoFilesException {
		MyServiceContext context = MyControlUtils.getServiceContext(_user);
		return pedidosHistoricoService.setReactivarPedido(context, track, exp);
	}
	
	@RequestMapping(method = RequestMethod.GET, path="/getByFilter")
	public @ResponseBody()
	List<PedidoProveedorVO> getPedidosFiltrados(
			@AuthenticationPrincipal final SegurUser user,
			@RequestParam(required=false) String expediente,
	        @RequestParam(required=false) String provName,
	        @RequestParam(required=false) String trFilterValue,
	        @RequestParam(required=false) String facNum,
	        @RequestParam(required=false) String refCharge,
	        @RequestParam(required=false) String fecDesde,
	        @RequestParam(required=false) String fecHasta)
	        		throws PedidosHistoricoServiceException, PedidoFilesException {
		MyServiceContext context = MyControlUtils.getServiceContext(user);
		return pedidosHistoricoService.getPedidosFiltrados(context, expediente, provName, trFilterValue, facNum, refCharge, fecDesde, fecHasta);
	}
	
	@ExceptionHandler(PedidosHistoricoServiceException.class)
	public ResponseEntity<?> handleStorageFileNotFound(PedidosHistoricoServiceException exc) {
		return ResponseEntity.notFound().build();
	}

}
