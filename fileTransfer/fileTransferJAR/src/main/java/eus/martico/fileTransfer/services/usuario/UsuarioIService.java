package eus.martico.fileTransfer.services.usuario;

import java.util.List;

import eus.martico.fileTransfer.model.usuario.UsuarioVO;
import eus.martico.fileTransfer.model.vo.ValidateDTO;
import eus.martico.fileTransfer.services.MyServiceContext;
import eus.martico.fileTransfer.services.seguridad.password.PasswordException;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public interface UsuarioIService {

	ValidateDTO<UsuarioVO> alta(MyServiceContext _context, UsuarioVO _usuario) throws UsuarioServiceException, PasswordException;
	ValidateDTO<UsuarioVO> modificar(MyServiceContext _context, UsuarioVO _usuario) throws UsuarioServiceException, PasswordException;

	UsuarioVO get(MyServiceContext _context, long _ni) throws UsuarioServiceException;
	UsuarioVO findById(String _id) throws UsuarioServiceException;

	int baja(MyServiceContext _context, long _ni) throws UsuarioServiceException;
	int reactivar(MyServiceContext _context, long _ni) throws UsuarioServiceException;

	List<UsuarioVO> getAllMantenimiento(MyServiceContext _context) throws UsuarioServiceException;

//	Integer setPassword(MyServiceContext _context, final String _username, final String _password) 
//			throws UsuarioServiceException, PasswordException;

	/**
	 * verifica la password del usuario con el username como username
	 * @param _context
	 * @param _Usuario
	 * @return
	 * @throws UsuarioServiceException
	 */
	Boolean verifyPassword(final UsuarioVO _usuario, final String _passwordToVerify) throws UsuarioServiceException, PasswordException;

}
