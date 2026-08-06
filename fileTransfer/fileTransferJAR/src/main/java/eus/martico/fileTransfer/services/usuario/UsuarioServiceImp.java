package eus.martico.fileTransfer.services.usuario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.dao.usuario.UsuarioDAOException;
import eus.martico.fileTransfer.dao.usuario.UsuarioIDAO;
import eus.martico.fileTransfer.model.error.vo.ErrorVO;
import eus.martico.fileTransfer.model.usuario.UsuarioVO;
import eus.martico.fileTransfer.model.usuario.rol.TipoRolEnum;
import eus.martico.fileTransfer.model.vo.ValidateDTO;
import eus.martico.fileTransfer.services.MyServiceContext;
import eus.martico.fileTransfer.services.seguridad.password.PasswordException;
import eus.martico.fileTransfer.services.seguridad.password.PasswordIService;
import eus.martico.fileTransfer.services.usuario.UsuarioIService;
import eus.martico.fileTransfer.services.usuario.rol.UsuarioRolIService;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@Service(value="UsuarioIService")
public class UsuarioServiceImp implements UsuarioIService {

	protected static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImp.class);

	@Autowired
	UsuarioIDAO usuarioDAO;

	@Autowired
	PasswordIService passwordService;

	@Autowired
	UsuarioRolIService usuarioRolService;
	
	
	@Override
	@Transactional(rollbackFor = {UsuarioServiceException.class})
	public ValidateDTO<UsuarioVO> alta(final MyServiceContext _context, UsuarioVO _usuario)
			throws UsuarioServiceException {
//		String decryptedPassword = null;
		logger.info("{}: Alta usuario: {}",_context.getUsername(), _usuario.getW2usuario());

		if(!StringUtils.isEmpty(_usuario.getRol())){
			//Lo metemos en el array de roles
			_usuario.setRoles(new String[]{_usuario.getRol()});
			if(_usuario.getW3tipo() == null || _usuario.getW3tipo() == 0 ){
				_usuario.setW3tipo(getTiposRol(_usuario.getRol()));
			}
		}
		String ctxtRol = _context.getUserRoles()[0];
		ValidateDTO<UsuarioVO> validate = validateUser(_usuario, true, ctxtRol);
		if(!validate.isError()){
			_usuario.setUserAlta(_context.getUsuarioNi());
			_usuario.setW2uscr(_context.getUsername());
			_usuario.setW2usca(_context.getUsername());
			_usuario.setW2fecr(LocalDate.now());
			_usuario.setW2feca(LocalDateTime.now());
			_usuario.setW2nuem(Long.valueOf(1));			//VALORES FIJOS
			_usuario.setW2almky(Long.valueOf(0));			//VALORES FIJOS

			_usuario.setW3cmp1("");			//VALORES FIJOS
			_usuario.setW3cmp2("");			//VALORES FIJOS
			_usuario.setW3cmp3("");			//VALORES FIJOS

			if(_usuario.getFechaAlta() == null){
				_usuario.setFechaAlta(LocalDateTime.now());
			}

			//XXX FT : QUITADA LA ENCRIPTACION DE PASSWORD :: PONER?
			try {
				//Si FTADM creando (FTADM o FTINTER o FTCONSUL) no es necesario el COD_PROV
				if(ctxtRol.equals(FileTransferConstantes.PROPERTY_KEY_ROL_ADMIN) && 
						(_usuario.getRol().equals(FileTransferConstantes.PROPERTY_KEY_ROL_ADMIN) 
								|| _usuario.getRol().equals(FileTransferConstantes.PROPERTY_KEY_ROL_INTERNO)
								|| _usuario.getRol().equals(FileTransferConstantes.PROPERTY_KEY_ROL_CONSULTA))){
					_usuario.setW2opeky(Long.valueOf(0));
				} else {
					//SUBSELECT W2OPEKY
					long w2opeky = usuarioDAO.selectW2optrky(_usuario.getClicodpro());
					_usuario.setW2opeky(w2opeky);
				}

				//SUBSELECT MAX_W2USUKY
				long maxW2usuky = usuarioDAO.selectMaxW2usuky();
				_usuario.setW2usuky(maxW2usuky + 1);
				_usuario.setW3usuky(maxW2usuky + 1);
				
				//chapuzas varias: W2USUARIO precision 40; W2USCR, W2USCA, precision 10
				final String w2uscr10;
				final String w2usca10;
				if(_usuario.getW2uscr().length()>10){
					w2uscr10 = _usuario.getW2uscr().substring(0, 10);
					_usuario.setW2uscr(w2uscr10);
				}
				if(_usuario.getW2usca().length()>10){
					w2usca10 = _usuario.getW2usca().substring(0, 10);
					_usuario.setW2usca(w2usca10);
				}

				//INSERT INTO LDUSUAR
				usuarioDAO.altaR(_usuario);

				//INSERT INTO LDUSUAX
				usuarioDAO.altaX(_usuario);
				validate.setDatos(_usuario);

			} catch (UsuarioDAOException e) {
				if (e.isClaveDuplicada()) {
					validate.addError(new ErrorVO("11", "El Usuario ya existe"));
					logger.error("Alta usuario: "+e.getMessage());
				} else if(e.getCodigoError().equals("SQL00012")) {
					validate.addError(new ErrorVO("12", "El código de proveedor no se corresponde con ningún proveedor válido."));
					logger.error("Alta usuario: "+e.getMessage());
				} else {
					validate.addError(new ErrorVO("13", "Error al dar de alta el Usuario"));
					logger.error("Alta usuario: "+e.getMessage());
				}
				return validate;
			}
		}
		logger.info("Alta usuario FIN");
		return validate;
	}

	@Override
	@Transactional(rollbackFor = {UsuarioServiceException.class})	//, PasswordException.class} )
	public ValidateDTO<UsuarioVO> modificar(MyServiceContext _context, UsuarioVO _usuario)
			throws UsuarioServiceException, PasswordException {
		//		String decryptedPassword = null;
		logger.info("{}: Modificar usuario: {}" ,_context.getUsername(), _usuario.getW2usuario());
		if(!StringUtils.isEmpty(_usuario.getRol())){
			//Lo metemos en el array de roles
			_usuario.setRoles(new String[]{_usuario.getRol()});
			_usuario.setW3tipo(getTiposRol(_usuario.getRol()));
		}
		String ctxtRol = _context.getUserRoles()[0];
		ValidateDTO<UsuarioVO> validate = validateUser(_usuario, false, ctxtRol);
		
		if(!validate.isError()){
			_usuario.setW2usca(_context.getUsername());
			_usuario.setW2feca(LocalDateTime.now());
			_usuario.setW3usuky(_usuario.getW2usuky());

			//QUITADA LA ENCRIPTACION DE PASSWORD

			UsuarioVO userUpdate;
			try {
				//chapuzas varias: W2USUARIO precision 40; W2USCR, W2USCA, precision 10
				final String w2uscr10;
				final String w2usca10;
				if(_usuario.getW2uscr().length()>10){
					w2uscr10 = _usuario.getW2uscr().substring(0, 10);
					_usuario.setW2uscr(w2uscr10);
				}
				if(_usuario.getW2usca().length()>10){
					w2usca10 = _usuario.getW2usca().substring(0, 10);
					_usuario.setW2usca(w2usca10);
				}
				
				//UPDATE LDUSUAR
				userUpdate = usuarioDAO.modificarR(_usuario);

				//UPDATE LDUSUAX
				userUpdate = usuarioDAO.modificarX(_usuario);
				validate.setDatos(userUpdate);

			} catch (UsuarioDAOException e) {
				validate.addError(new ErrorVO("21", "Error al modificar el Usuario"));
				logger.error("Modificar usuario: "+e.getMessage());
				return validate;
			}
		}
		logger.info("Modificar usuario FIN");
		return validate;
	}

	@Override
	public UsuarioVO get(MyServiceContext _context, long _ni) throws UsuarioServiceException {
		try {
			return usuarioDAO.get(_ni);
		} catch (UsuarioDAOException e) {
			logger.error(e.getMessage());
			throw new UsuarioServiceException(e.getMessage());
		}
	}

	@Override
	public UsuarioVO findById(String _id) throws UsuarioServiceException {
		try {
			return usuarioDAO.findById(_id);
		} catch (UsuarioDAOException e) {
			logger.error(e.getMessage());
			throw new UsuarioServiceException(e.getMessage());
		}
	}

	@Override
	public List<UsuarioVO> getAllMantenimiento(final MyServiceContext _context) throws UsuarioServiceException {
		try {
			return usuarioDAO.getAllMantenimiento(_context);
		} catch (UsuarioDAOException e) {
			logger.error(e.getMessage());
			throw new UsuarioServiceException(e.getMessage());
		}
	}

	@Override
	public int baja(final MyServiceContext _context, long _ni) throws UsuarioServiceException {
		try {
			return usuarioDAO.baja(_context, _ni);
		} catch (UsuarioDAOException e) {
			logger.error(e.getMessage());
			throw new UsuarioServiceException(e.getMessage());
		}
	}

	@Override
	public int reactivar(final MyServiceContext _context, long _ni) throws UsuarioServiceException {
		try {
			return usuarioDAO.reactivar(_context, _ni);
		} catch (UsuarioDAOException e) {
			logger.error(e.getMessage());
			throw new UsuarioServiceException(e.getMessage());
		}
	}

	//OLD : TO_USE?
//	@Override
//	@Transactional(rollbackFor = {UsuarioServiceException.class, PasswordException.class} )
//	public Integer setPassword(final MyServiceContext _context, final String _username, final String _password)
//			throws UsuarioServiceException, PasswordException {
//		String salt = passwordService.getSalt(32);
//		// Protect user's password. The generated value can be stored in DB.
//		String mySecurePassword = passwordService.generateSecurePassword(_password, salt);		
//		try {
//			return usuarioDAO.setPassword( _username, mySecurePassword, salt);
//		} catch (UsuarioDAOException e) {
//			logger.error(e.getMessage());
//			throw new UsuarioServiceException(e.getMessage());
//		}
//	}

//	@Override
//	public Integer setPassword(final String _username, final String _securePassword, final String _salt) 
//			throws UsuarioServiceException, PasswordException {
//		try {
//			return usuarioDAO.setPassword( _username, _securePassword, _salt);
//		} catch (UsuarioDAOException e) {
//			throw new UsuarioServiceException(e);
//		}
//	}
//
//	@Override
//	public Boolean verifyPassword(final String _username, final String _passwordToVerify)
//			throws UsuarioServiceException, PasswordException {
//		UsuarioVO usuario = findById(_username);
//		return verifyPassword(usuario, _passwordToVerify);
//	}

	@Override
	public Boolean verifyPassword(final UsuarioVO _usuario, final String _passwordToVerify) 
			throws UsuarioServiceException, PasswordException{

		_usuario.setSecurePassword(_passwordToVerify);
		_usuario.setPassSalt(_passwordToVerify);
		return passwordService.verifyUserPassword(_passwordToVerify, _usuario.getSecurePassword(), _usuario.getPassSalt());
	}
	
	private ValidateDTO<UsuarioVO> validateUser(UsuarioVO _user, boolean _alta, String _ctxtRol) throws UsuarioServiceException {
		ValidateDTO<UsuarioVO> validate = new ValidateDTO<UsuarioVO>(_user);

		if (StringUtils.isEmpty(_user.getW2usuario())) {
			validate.addError(new ErrorVO("01", "El usuario es obligatorio"));

		} else if (_alta && StringUtils.isEmpty(_user.getW2email())) {
			validate.addError(new ErrorVO("02", "El email es obligatorio"));

		} else if (_alta && StringUtils.isEmpty(_user.getW2contras())) {
			validate.addError(new ErrorVO("03", "La contraseña es obligatoria"));

		} else if(!_ctxtRol.equals(FileTransferConstantes.PROPERTY_KEY_ROL_ADMIN) || 
				(!_user.getRol().equals(FileTransferConstantes.PROPERTY_KEY_ROL_ADMIN) 
						&& !_user.getRol().equals(FileTransferConstantes.PROPERTY_KEY_ROL_INTERNO) 
						&& !_user.getRol().equals(FileTransferConstantes.PROPERTY_KEY_ROL_CONSULTA))){
			//Si FTADM creando (FTADM o FTINTER o FTCONSUL) no es necesario el COD_PROV
			if (_alta && StringUtils.isEmpty(_user.getClicodpro())) {
				validate.addError(new ErrorVO("04", "El cod_pro es obligatorio"));
			}
			if (_alta && !isValidCodPro(_user.getClicodpro())) {
				//Se valida que CLICODPRO, esté contenido en CLIPRO
				validate.addError(new ErrorVO("05", "El cod_pro no es válido"));
			}
		} else if(_user.getRoles() == null || _user.getRoles().length == 0) {
			validate.addError(new ErrorVO("06", "El usuario tiene que tener por lo menos un rol"));

		} else if(!_alta && _user.getW2usuky().equals(null)){
			validate.addError(new ErrorVO("08", "Es necesaria la clave interna del usuario a modificar"));

		}

		return validate;
	}
	
	private boolean isValidCodPro(String _clicodpro) throws UsuarioServiceException {
		//Se ha de validar CLICOD, que este contenido en CLIPRO
		boolean isValidCod = false;
		if(_clicodpro != null){
			isValidCod = usuarioDAO.validateCodPro(_clicodpro);
		}
		return isValidCod;
	}
	
	private Long getTiposRol(String _rolCode) {
		//Convertimos el String w3tipo al objeto Enum
		TipoRolEnum tipo = TipoRolEnum.desdeKey(_rolCode);
	    
	    //Si tipo existe, devuelve su codigoNumerico; si no (default), devuelve 0
	    int w3tipo = (tipo != null) ? tipo.getCodigoNumerico() : 0;
	    
		return Long.valueOf(w3tipo);
	}

}
