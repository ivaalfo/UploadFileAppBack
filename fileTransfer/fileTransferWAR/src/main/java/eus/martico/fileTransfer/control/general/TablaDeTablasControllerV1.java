package eus.martico.fileTransfer.control.general;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eus.martico.fileTransfer.control.MyControlUtils;
import eus.martico.fileTransfer.model.tabladetablas.vo.TablaDetalleCodTabCab;
import eus.martico.fileTransfer.model.tabladetablas.vo.TablaDetalleVO;
import eus.martico.fileTransfer.seguridad.user.entity.SegurUser;
import eus.martico.fileTransfer.services.tabladetablas.TablaDeTablasIService;
import eus.martico.fileTransfer.services.tabladetablas.TablaDeTablasServiceException;

@RestController()
@RequestMapping(value = "/api/v1/maestras")
public class TablaDeTablasControllerV1 {

	protected static final Logger logger = LoggerFactory.getLogger(TablaDeTablasControllerV1.class);

	@Autowired()
	private TablaDeTablasIService tablaDeTablasService;

	@GetMapping("")
	public @ResponseBody()
	Map<String, List<TablaDetalleCodTabCab>> getListaCompleta(@AuthenticationPrincipal final SegurUser _user ) throws TablaDeTablasServiceException {
		return tablaDeTablasService.getAll(MyControlUtils.getServiceContext(_user));
	}

	@RequestMapping(method = RequestMethod.GET, path="/{codigo_cabecera}")
	public @ResponseBody()
	List<TablaDetalleVO> getByCodigoCabecera(@AuthenticationPrincipal final SegurUser _user, 
			@PathVariable(required=true) String codigo_cabecera ) throws TablaDeTablasServiceException {
		int x = -1;
		if (codigo_cabecera!=null) {
			x = codigo_cabecera.indexOf('$');
			if (x > 0 && x == (codigo_cabecera.length()-1)) {
				// Si es el ultimo caracter, lo quitamos y punto
				codigo_cabecera = codigo_cabecera.substring(0, x);
			}
		}
		if (x > 0 && x < codigo_cabecera.length()) {
			// Viene un tipo dentro de cabecera
			String cabecera = codigo_cabecera.substring(0, x);
			String tipo=null;
			try {
				tipo = codigo_cabecera.substring(x+1);
			} catch (Exception e) {
				throw new TablaDeTablasServiceException("01", e.getMessage());
			}
			return tablaDeTablasService.getByCabeceraTipo(MyControlUtils.getServiceContext(_user), cabecera, tipo);
		} else {
			return tablaDeTablasService.getByCodigoCabecera(codigo_cabecera);
		}
	}

	@GetMapping("/{codigo_cabecera}/{codigo_detalle}")
	public @ResponseBody()
	TablaDetalleVO getDetalle(@AuthenticationPrincipal final SegurUser _user,
			@PathVariable(required=true) String codigo_cabecera,
			@PathVariable(required=true) String codigo_detalle) throws TablaDeTablasServiceException {

		return tablaDeTablasService.getDetalle(MyControlUtils.getServiceContext(_user), codigo_cabecera, codigo_detalle);
	}

	@ExceptionHandler({ TablaDeTablasServiceException.class})
	public ResponseEntity<String> handleException(TablaDeTablasServiceException ex) {
		return new ResponseEntity<String>(ex.getMsgError(), HttpStatus.INTERNAL_SERVER_ERROR); //BAD REQUEST
	}

}
