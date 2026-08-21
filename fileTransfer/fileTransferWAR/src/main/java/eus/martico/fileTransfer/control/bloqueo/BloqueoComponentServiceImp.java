package eus.martico.fileTransfer.control.bloqueo;

import java.time.LocalDateTime;

import javax.ejb.EJB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eus.martico.fileTransfer.ejb.timer.bloqueo.BloqueoTimerFileTransferEjb;
import eus.martico.fileTransfer.model.bloqueo.BloqueoControlVO;
import eus.martico.fileTransfer.model.bloqueo.BloqueoRespVO;
import eus.martico.fileTransfer.model.bloqueo.BloqueoVO;
import eus.martico.fileTransfer.model.error.vo.ErrorVO;
import eus.martico.fileTransfer.model.mensaje.vo.MensajeVO;
import eus.martico.fileTransfer.model.vo.ValidateDTO;
import eus.martico.fileTransfer.services.MyServiceContext;
import eus.martico.fileTransfer.services.bloqueo.BloqueoIService;
import eus.martico.fileTransfer.services.bloqueo.BloqueoServiceException;

@Component
public class BloqueoComponentServiceImp implements BloqueoComponentIService {

	protected static final Logger logger = LoggerFactory.getLogger(BloqueoComponentServiceImp.class);

	@Autowired
	private BloqueoIService bloqueoService;

	@EJB(mappedName="ejblocal:ejb/BloqueoTimerFileTransferEjb")
	private BloqueoTimerFileTransferEjb bloqueoTimer;

	@Override
	public ValidateDTO<BloqueoVO> iniciar(MyServiceContext _context, final String _tipo, String _codigo, final long _userNi) 
			throws BloqueoServiceException {
		BloqueoVO bloqueo = null;
		ValidateDTO<BloqueoVO> validate = new ValidateDTO<BloqueoVO>();
		ErrorVO error = null;
		boolean yaExisteBloqueo = false;
		try {
			bloqueo = bloqueoService.iniciar(_context, _tipo, _codigo, _userNi);
			if(bloqueoTimer != null){
				bloqueoTimer.iniciar(bloqueo);
			}
			validate.setDatos(bloqueo);
			MensajeVO mensaje = new MensajeVO();
			mensaje.setCodigo(BloqueoVO.BLOQUEO_OK);
			validate.addMensaje(mensaje);
			logger.info("Bloqueo creado: Tipo: "+bloqueo.getTipoCod());
			logger.info("Bloqueo creado: Usuario: "+bloqueo.getUsername()+" : UserNi: "+bloqueo.getUsuarioNi());
			logger.info("Bloqueo creado: Codigo: "+bloqueo.getCodigo());
		} catch (BloqueoServiceException e) {
			yaExisteBloqueo = e.isClaveDuplicada();
			if(e.isClaveDuplicada()) {
				error = new ErrorVO();
				error.setCodigo(BloqueoVO.BLOQUEO_ERROR_YA_ESTABA_BLOQUEADO);
			} else {
				throw e;
			}
		}
		if(yaExisteBloqueo) {
			bloqueo = bloqueoService.getActivo(_context, _tipo, _codigo);

			//Si los usuarios son iguales SI se bloquea
			if(_userNi == bloqueo.getUsuarioNi()){
				//SI Bloquea - marcamos el error
				error.setDescripcion(bloqueo.getUsername());	//bloqueo.getApellidosNombre());
				validate.addError(error);
			} else {
				//Si los usuarios son distintos 
				//Pero seleccionan el mismo documento (codigo)	
				if(_codigo.equals(bloqueo.getCodigo())){
					//SI Bloquea - marcamos el error
					error.setDescripcion(bloqueo.getUsername());		//bloqueo.getApellidosNombre());
					validate.addError(error);
				} else {
					//Si los usuarios son distintos 
					//Y el documento que seleccionan es distinto
					//NO se bloquea
					MensajeVO mensaje = new MensajeVO();
					mensaje.setCodigo(BloqueoVO.BLOQUEO_OK);
					validate.addMensaje(mensaje);
				}
			}
		} 
		return validate;
	}

	@Override
	public ValidateDTO<BloqueoVO> finalizar(MyServiceContext _context, final String _tipo, final String _codigo, final long _userNi) 
			throws BloqueoServiceException {
		//Comprobamos que el bloqueo es del usuario que lo solicita
		String codigoError = null;
		ValidateDTO<BloqueoVO> validate = new ValidateDTO<BloqueoVO>();
		BloqueoControlVO controlBloqueo = comprobarBloqueoUsuarioSession(_context, _tipo, _codigo, _userNi);
		if(controlBloqueo.isMyBloqueo() /* || _forzarFinalizar */) {
			//Es el mismo usuario o lo pide el ADMIN
			Integer x = bloqueoService.finalizar(_context, _tipo, _codigo, _userNi);
			if((x != null) && (x > 0)) {
				MensajeVO mensaje = new MensajeVO();
				mensaje.setCodigo(BloqueoVO.BLOQUEO_OK);
				validate.addMensaje(mensaje);
				logger.info("Bloqueo finalizado: Tipo: "+_tipo);
				logger.info("Bloqueo finalizado: Usuario: "+_context.getUsername()+" : UserNi: "+_userNi);
				logger.info("Bloqueo finalizado: Codigo: "+_codigo);
			} else {
				//puede ser que justo lo haya finalizado manualmente el usuario ???
				codigoError = BloqueoVO.BLOQUEO_ERROR_FINALIZANDO;
			}			
			if(bloqueoTimer != null){
				bloqueoTimer.cancelar(controlBloqueo.getBloqueo());
			}
		} else if(controlBloqueo.getBloqueo() == null){
			codigoError = BloqueoVO.BLOQUEO_ERROR_NO_HAY_BLOQUEO_ACTIVO;
		} else {
			if(controlBloqueo.isMismoUsuario()){
				codigoError = BloqueoVO.BLOQUEO_ERROR_SESSION_NO_PROPIETARIA;
			} else {
				codigoError = BloqueoVO.BLOQUEO_ERROR_USUARIO_NO_PROPIETARIO;
			}
		}

		if(codigoError != null){
			ErrorVO error = new ErrorVO();
			error.setCodigo(codigoError);
			validate.addError(error);
		}
		if(controlBloqueo.getBloqueo() != null){
			if(_userNi != controlBloqueo.getBloqueo().getUsuarioNi()){
				//Si los usuarios son distintos NO se bloquean
				//Por tanto, NO hay codigo de error al finalizar la operativa
				validate = new ValidateDTO<BloqueoVO>();
				ErrorVO error = new ErrorVO();
				validate.addError(error);
				MensajeVO mensaje = new MensajeVO();
				mensaje.setCodigo(BloqueoVO.BLOQUEO_OK);
				validate.addMensaje(mensaje);
			}
		}
		return validate;
	}
	
	@Override
	public ValidateDTO<BloqueoVO> forzarFinalizar(MyServiceContext _context, final String _tipo, final String _codigo, final long _userNi) 
			throws BloqueoServiceException {
		ValidateDTO<BloqueoVO> validate = new ValidateDTO<BloqueoVO>();
		BloqueoVO bloqueoAct = bloqueoService.getActivo(_context, _tipo, _codigo);
		Integer x = bloqueoService.finalizar(_context, _tipo, _codigo, _userNi);
		if((x != null) && (x > 0)){
			MensajeVO mensaje = new MensajeVO();
			mensaje.setCodigo(BloqueoVO.BLOQUEO_OK);
			validate.addMensaje(mensaje);
			if(bloqueoTimer != null){
				bloqueoTimer.cancelar(bloqueoAct);
			}
		} else {
			validate.addError(new ErrorVO(BloqueoVO.BLOQUEO_ERROR_NO_HAY_BLOQUEO_ACTIVO, "No se ha finalizado bloqueo"));			
		}
		return validate;
	}

	/*
	 * Comprobamos que el bloqueo es del usuario que lo solicita
	 */
	@Override
	public BloqueoVO comprobarBloqueoUsuario(MyServiceContext _context, final String _tipo, final String _codigo, final long _userNi) throws BloqueoServiceException {		
		BloqueoVO bloqueoAct = bloqueoService.getActivo(_context, _tipo, _codigo);
		if((bloqueoAct != null) && (bloqueoAct.getUsuarioNi() == _userNi)) {
			return bloqueoAct;
		} else {
			return null;
		}
	}
	
	/*
	 * Comprobamos que el bloqueo es del usuario y de la sesion que lo solicita
	 */
	@Override
	public BloqueoControlVO comprobarBloqueoUsuarioSession(MyServiceContext _context, final String _tipo, final String _codigo, final long _userNi) throws BloqueoServiceException {		
		BloqueoVO bloqueoAct = bloqueoService.getActivo(_context, _tipo, _codigo);
		BloqueoControlVO bloqueo = new BloqueoControlVO(bloqueoAct);
		if(bloqueoAct != null){
			if(bloqueoAct.getUsuarioNi() == _userNi){
				bloqueo.setMismoUsuario(true);
				//Es el mismo usuario, comprobamos la session
				//Existe un bloqueo activo del mismo usuario 
				if(bloqueoAct.getSessionId() != null){
					//El bloqueo tiene sesion
					if(_context.getSessionId() == null){
						//Esta peticion no tiene sesion 
						bloqueo.setMismaSession(false);
					} else {
						// esta peticion tiene sesion, las sesiones pueden ser diferentes en cada peticion lo damos por buena
						if(_context.getSessionId().equals(bloqueoAct.getSessionId())){
							//Es la misma sesion que solicita el bloqueo, lo damos por bueno y no hacemos nada
						} else {
							//Es otra sesion del mimso usuario
							bloqueo.setMismaSession(false);
						}
					}
				} else {
					//El bloqueo no tiene sesion
					if(_context.getSessionId() == null){
						//Esta peticion no tiene sesion
						//Es el mismo usuario el que solicita el bloqueo, lo damos por bueno y no hacemos nada
					} else {
						//Esta peticion tiene sesion
						//Como el bloqueo no tiene sesion, le negamos el nuevo bloqueo
						bloqueo.setMismaSession(false);
					}
				}
			}
		}
		return bloqueo;
	}
	
	@Override
	public ValidateDTO<BloqueoVO> reiniciar(MyServiceContext _context, final String _tipo, final String _codigo, final long _userNi) throws BloqueoServiceException {		
		ValidateDTO<BloqueoVO> validate = null;
		String codigoError = null;
		BloqueoControlVO controlBloqueo = comprobarBloqueoUsuarioSession(_context, _tipo, _codigo, _userNi);
		if(controlBloqueo.isMyBloqueo()){
			if(bloqueoTimer != null){
				bloqueoTimer.cancelar(controlBloqueo.getBloqueo());
				bloqueoTimer.iniciar(controlBloqueo.getBloqueo());
				controlBloqueo.getBloqueo().setInicio(LocalDateTime.now());
				validate = new ValidateDTO<BloqueoVO>(controlBloqueo.getBloqueo());
				MensajeVO mensaje = new MensajeVO();
				mensaje.setCodigo(BloqueoVO.BLOQUEO_OK);
				validate.addMensaje(mensaje);
				return validate;
			}
		} else if(controlBloqueo.getBloqueo() == null){
			codigoError = BloqueoVO.BLOQUEO_ERROR_NO_HAY_BLOQUEO_ACTIVO;
		} else { 
			if(controlBloqueo.isMismoUsuario()){
				codigoError = BloqueoVO.BLOQUEO_ERROR_SESSION_NO_PROPIETARIA;
			} else {
				codigoError = BloqueoVO.BLOQUEO_ERROR_USUARIO_NO_PROPIETARIO;
			}
		}
		if(codigoError != null) {
			ErrorVO error = new ErrorVO();
			validate = new ValidateDTO<BloqueoVO>();
			//marcamos el error
			error.setCodigo(codigoError);
			if(controlBloqueo.getBloqueo() != null){
				error.setDescripcion(controlBloqueo.getBloqueo().getUsername());		//controlBloqueo.getBloqueo().getApellidosNombre());
			}
			validate.addError(error);
		}
		return validate;
	}

	@Override
	@Transactional(rollbackFor = BloqueoServiceException.class)
	public int marcarAccion(MyServiceContext _context, BloqueoVO _bloqueo, byte _accion) throws BloqueoServiceException {
		return bloqueoService.marcarAccion(_context, _bloqueo, _accion);
	}

	@Override
	public ValidateDTO<BloqueoRespVO> getSavedLastUser(MyServiceContext _context, final String _tipo, final String _codigo)
			throws BloqueoServiceException {
		BloqueoVO bloqueo = bloqueoService.getSavedLastUser(_context, _tipo, _codigo);
		if(bloqueo == null){
			return new ValidateDTO<BloqueoRespVO>();
		} else {
			return new ValidateDTO<BloqueoRespVO>(new BloqueoRespVO(bloqueo));
		}
	}

}
