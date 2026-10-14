package eus.martico.fileTransfer.dao.usuario;

import java.util.List;

import eus.martico.fileTransfer.model.usuario.UsuarioVO;
import eus.martico.fileTransfer.services.MyServiceContext;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public interface UsuarioIDAO {

	Integer altaR(final UsuarioVO _usuario) throws UsuarioDAOException;
	Integer altaX(final UsuarioVO _usuario) throws UsuarioDAOException;

	UsuarioVO modificarR(final UsuarioVO _Usuario) throws UsuarioDAOException;
	UsuarioVO modificarX(final UsuarioVO _Usuario) throws UsuarioDAOException;

	UsuarioVO get(final long _usuarioNi) throws UsuarioDAOException;
	UsuarioVO findById(final String _id) throws UsuarioDAOException;

	List<UsuarioVO> getAllMantenimiento(MyServiceContext _context) throws UsuarioDAOException;

	//No se borra, se marca como baja (Sin Acceso)
	int baja(MyServiceContext _context, long _userQueSeDaDeBajaNi) throws UsuarioDAOException;
	//Se reactiva un usuario dado de baja (Externo)
	int reactivar(MyServiceContext _context, long _userQueSeReactivaNi) throws UsuarioDAOException;

	//OLD : TO_USE?
//	int setPassword(final String _username, final String _password, final String _salt) throws UsuarioDAOException;

	boolean validateCodPro(String _clicodpro);
	
	long selectW2optrky(String _opprov) throws UsuarioDAOException;
	long selectMaxW2usuky() throws UsuarioDAOException;
	
	String getProvByCLICOD(String _provCod) throws UsuarioDAOException;
	List<String> getCLICODbyProv(String _provUsername);
	
	String getSqlPedidos(boolean isExterno);
	String getSqlProveedores(boolean isNameSearch);
	
//	//XXX descartado firmador
//	String getSqlPedidosFirma(boolean isExterno);

}
