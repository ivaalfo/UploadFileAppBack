package eus.martico.fileTransfer.control.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eus.martico.fileTransfer.control.bloqueo.BloqueoComponentIService;
import eus.martico.fileTransfer.model.bloqueo.BloqueoVO;
import eus.martico.fileTransfer.model.vo.ValidateDTO;
import eus.martico.fileTransfer.seguridad.user.entity.SegurUser;
import eus.martico.fileTransfer.services.MyServiceContext;
import eus.martico.fileTransfer.services.bloqueo.BloqueoServiceException;

@RestController()
@RequestMapping(value = "/api/v1/admin")
public class AdminControllerV1 {

	@Autowired
	BloqueoComponentIService bloqueoComponent;

	@RequestMapping(method = RequestMethod.DELETE, path="/bloqueo/{_tipo}/{_userNi}/{_codigo}")
	public @ResponseBody()
	ValidateDTO<BloqueoVO> finalizar(@AuthenticationPrincipal final SegurUser _user, 
			@PathVariable(required=true) final String _tipo, 
			@PathVariable(required=true) final String _codigo, 
			@PathVariable(required=true) final long _userNi
			) throws BloqueoServiceException {
		return bloqueoComponent.forzarFinalizar(MyServiceContext.getInstance(), _tipo, _codigo, _userNi);
	}

	@ExceptionHandler({BloqueoServiceException.class})
	public ResponseEntity<String> handleException(BloqueoServiceException ex) {
		if (ex.isViolatesForeignKey()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMsgError());
		} else if (ex.isClaveDuplicada()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMsgError());
		} else {
			return new ResponseEntity<String>(ex.getMsgError(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@ExceptionHandler({ Exception.class})
	public ResponseEntity<String> handleException(Exception ex) {
		return new ResponseEntity<String>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
