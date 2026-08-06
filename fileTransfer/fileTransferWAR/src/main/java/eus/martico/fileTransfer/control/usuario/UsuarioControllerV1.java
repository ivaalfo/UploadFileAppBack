package eus.martico.fileTransfer.control.usuario;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eus.martico.fileTransfer.control.MyControlUtils;
import eus.martico.fileTransfer.model.usuario.UsuarioVO;
import eus.martico.fileTransfer.model.vo.BuscarDTO;
import eus.martico.fileTransfer.model.vo.ValidateDTO;
import eus.martico.fileTransfer.seguridad.user.entity.SegurUser;
import eus.martico.fileTransfer.services.MyServiceContext;
import eus.martico.fileTransfer.services.seguridad.password.PasswordException;
import eus.martico.fileTransfer.services.usuario.UsuarioIService;
import eus.martico.fileTransfer.services.usuario.UsuarioServiceException;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@RestController()
@RequestMapping(value = "/api/v1/usuarios")
public class UsuarioControllerV1 {

	protected static final Logger logger = LoggerFactory.getLogger(UsuarioControllerV1.class);

	@Autowired()
	private UsuarioIService UsuarioService;

	@RequestMapping(method = RequestMethod.GET, path="/allmtto")
	public @ResponseBody()
	List<UsuarioVO> getAllMantenimiento(@AuthenticationPrincipal final SegurUser user) throws UsuarioServiceException {
		MyServiceContext context = MyControlUtils.getServiceContext(user);
		return UsuarioService.getAllMantenimiento(context);
	}

	@RequestMapping(method = RequestMethod.GET, path="/{usuarioNi}")
	public @ResponseBody()
	BuscarDTO<UsuarioVO> get(@AuthenticationPrincipal final SegurUser user,
			@PathVariable(required=true) Long usuarioNi) 
					throws UsuarioServiceException {
		MyServiceContext context = MyControlUtils.getServiceContext(user);
		return new BuscarDTO<UsuarioVO>(UsuarioService.get(context, usuarioNi));
	}

	@RequestMapping(method = RequestMethod.PUT, path="/")
	public @ResponseBody()
	ValidateDTO<UsuarioVO> modificar(@AuthenticationPrincipal final SegurUser user, 
			@RequestBody(required=true) UsuarioVO _usuario) 
					throws UsuarioServiceException, PasswordException {
		MyServiceContext context = MyControlUtils.getServiceContext(user);
		return UsuarioService.modificar(context, _usuario);
	}

	@RequestMapping(method = RequestMethod.POST, path="/")
	public @ResponseBody()
	ValidateDTO<UsuarioVO> alta(@AuthenticationPrincipal final SegurUser user, 
			@RequestBody(required=true) UsuarioVO _usuario) 
					throws UsuarioServiceException, PasswordException {
		MyServiceContext context = MyControlUtils.getServiceContext(user);
		return UsuarioService.alta(context, _usuario);
	}	

	@RequestMapping(method = RequestMethod.DELETE, path="/{usuarioNi}")
	public @ResponseBody()
	Integer baja(@AuthenticationPrincipal final SegurUser user,
			@PathVariable(required=true) Long usuarioNi) 
					throws UsuarioServiceException {
		MyServiceContext context = MyControlUtils.getServiceContext(user);
		return UsuarioService.baja(context, usuarioNi.longValue());
	}

	@RequestMapping(method = RequestMethod.PUT, path="/reactivar/{usuarioNi}")
	public @ResponseBody()
	Integer reactivar(@AuthenticationPrincipal final SegurUser user,
			@PathVariable(required=true) Long usuarioNi) 
					throws UsuarioServiceException {
		MyServiceContext context = MyControlUtils.getServiceContext(user);
		return UsuarioService.reactivar(context, usuarioNi.longValue());
	}

//	@RequestMapping(method = RequestMethod.POST, path="/resetpass")				//OLD : DELETE?
//	public @ResponseBody()
//	Integer resetpass(@AuthenticationPrincipal final SegurUser user, @RequestBody UsuarioVO _usuario) 
//			throws UsuarioServiceException, PasswordException {
//		MyServiceContext context = MyControlUtils.getServiceContext(user);
//		return UsuarioService.setPassword(context, _usuario.getUsername(), _usuario.getVisiblePassword());
//	}

	@ExceptionHandler({ UsuarioServiceException.class, PasswordException.class })
	public ResponseEntity<String> handleException(UsuarioServiceException ex) {
		if (ex.isViolatesForeignKey()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMsgError());
		} else if (ex.isClaveDuplicada()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMsgError());
		} else {
			return new ResponseEntity<String>(ex.getMsgError(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
