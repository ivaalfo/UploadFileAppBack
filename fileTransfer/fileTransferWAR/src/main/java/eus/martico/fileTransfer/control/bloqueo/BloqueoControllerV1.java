package eus.martico.fileTransfer.control.bloqueo;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.control.MyControlUtils;
import eus.martico.fileTransfer.model.ValidacionException;
import eus.martico.fileTransfer.model.bloqueo.BloqueoVO;
import eus.martico.fileTransfer.model.vo.ValidateDTO;
import eus.martico.fileTransfer.seguridad.user.entity.SegurUser;
import eus.martico.fileTransfer.services.bloqueo.BloqueoServiceException;

@RestController()
@RequestMapping(value = "/api/v1/bloqueo")
public class BloqueoControllerV1 {

	@Autowired
	BloqueoComponentIService bloqueoComponent;

	@RequestMapping(method = RequestMethod.PUT, path="/{_tipo}")
	public @ResponseBody()
	ValidateDTO<BloqueoVO> iniciar(HttpServletRequest request,
			@AuthenticationPrincipal final SegurUser _user, 
			@PathVariable(required=true) final String _tipo,
			@RequestParam(required=false) Map<String,String> _params
			) throws BloqueoServiceException, ValidacionException {
		String clave = valorParametroClave(_params);
		String codigo = clave;
		if(_params.toString().contains("?")){
			String track = valorParametroTR(_params);
			String exp = valorParametroEXP(_params);
			if(track != null){
				String prefix = "";
				if(_tipo.equalsIgnoreCase(FileTransferConstantes.BLOQ_VALIDADOR)){
					prefix = "VAL_";
				}
//				if(_tipo.equalsIgnoreCase(FileTransferConstantes.BLOQ_FIRMADOR)){
//					prefix = "FIR_";
//				}
				codigo = prefix + track.toString();
				
				if(track.startsWith("GRU") && exp != null){
					codigo = codigo+"-"+exp;
				}
			}
		}
		return bloqueoComponent.iniciar(MyControlUtils.getServiceContext(_user, request), _tipo, codigo, _user.getNi());
	}
	
	@RequestMapping(method = RequestMethod.POST, path="/activo/{_tipo}")
	public @ResponseBody()
	ValidateDTO<BloqueoVO> activo(HttpServletRequest request,
			@AuthenticationPrincipal final SegurUser _user, 
			@PathVariable(required=true) final String _tipo,
			@RequestParam(required=false) Map<String,String> _params
			) throws BloqueoServiceException, ValidacionException {
		String clave = valorParametroClave(_params);
		String codigo = clave;
		if(_params.toString().contains("?")){
			String track = valorParametroTR(_params);
			String exp = valorParametroEXP(_params);
			if(track != null){
				String prefix = "";
				if(_tipo.equalsIgnoreCase(FileTransferConstantes.BLOQ_VALIDADOR)){
					prefix = "VAL_";
				}
//				if(_tipo.equalsIgnoreCase(FileTransferConstantes.BLOQ_FIRMADOR)){
//					prefix = "FIR_";
//				}
				codigo = prefix + track.toString();
				if(track.startsWith("GRU") && exp != null){
					codigo = codigo+"-"+exp;
				}
			}
		}
		return bloqueoComponent.reiniciar(MyControlUtils.getServiceContext(_user, request), _tipo, codigo, _user.getNi());
	}
	
	@RequestMapping(method = RequestMethod.DELETE, path="/{_tipo}")
	public @ResponseBody()
	ValidateDTO<BloqueoVO> finalizar(HttpServletRequest request,
			@AuthenticationPrincipal final SegurUser _user, 
			@PathVariable(required=true) final String _tipo,
			@RequestParam(required=false) Map<String,String> _params
			) throws BloqueoServiceException, ValidacionException {
		String clave = valorParametroClave(_params);
		String codigo = clave;
		if(_params.toString().contains("?")){
			String track = valorParametroTR(_params);
			String exp = valorParametroEXP(_params);
			if(track != null){
				String prefix = "";
				if(_tipo.equalsIgnoreCase(FileTransferConstantes.BLOQ_VALIDADOR)){
					prefix = "VAL_";
				}
//				if(_tipo.equalsIgnoreCase(FileTransferConstantes.BLOQ_FIRMADOR)){
//					prefix = "FIR_";
//				}
				codigo = prefix + track.toString();
				if(track.startsWith("GRU") && exp != null){
					codigo = codigo+"-"+exp;
				}
			}
		}
		return bloqueoComponent.finalizar(MyControlUtils.getServiceContext(_user, request), _tipo, codigo, _user.getNi());
	}

	private String valorParametroClave(Map<String,String> _params) throws ValidacionException {
		String clave = null;
		if(!_params.isEmpty()){
			Set<String> keys = _params.keySet();
			for(String key: keys) {
				if(key.equals("clave")){
					clave = _params.get(key);
					if(clave.contains("?")){
						clave = clave.substring(0, clave.indexOf("?"));
					}
				} else {
					// han enviado otro parametro => BAD_REQUEST
					throw new ValidacionException(ValidacionException.BAD_REQUEST_EXCEPTION, "Solo se permite el parametro clave");
				}
			}
		}
		return clave;
	}

	private String valorParametroTR(Map<String,String> _params) throws ValidacionException {
		String track = null;
		if(!_params.isEmpty()){
			track = _params.toString();
			if(track.contains("?")){
				track = track.substring(track.indexOf("?"));
				track = track.substring(track.indexOf("=")+1);
				track = track.replaceAll("}", "");
			}
		}
		return track;
	}
	
	private String valorParametroEXP(Map<String,String> _params) throws ValidacionException {
		String exp = null;
		if(!_params.isEmpty()){
			exp = _params.toString();
			if(exp.contains("?")){
				exp = exp.substring(exp.indexOf("=")+1, exp.indexOf("?"));
			}
		}
		return exp;
	}

	@ExceptionHandler({BloqueoServiceException.class})
	public ResponseEntity<String> handleException(BloqueoServiceException ex) {
		if(ex.isViolatesForeignKey()){
			return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMsgError());
		} else if(ex.isClaveDuplicada()){
			return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMsgError());
		} else {
			return new ResponseEntity<String>(ex.getMsgError(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ExceptionHandler({ ValidacionException.class})
	public ResponseEntity<String> handleException(ValidacionException ex) {
		if(ex.getCodigoError().equals(ValidacionException.BAD_REQUEST_EXCEPTION)){
			return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
		} else {
			return new ResponseEntity<String>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ExceptionHandler({ Exception.class})
	public ResponseEntity<String> handleException(Exception ex) {
		return new ResponseEntity<String>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
