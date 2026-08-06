package eus.martico.fileTransfer.services.usuario.rol;

import eus.martico.fileTransfer.model.rol.RolVO;
import eus.martico.fileTransfer.model.usuario.rol.UsuarioRolVO;

public interface UsuarioRolIService {

	  UsuarioRolVO alta(UsuarioRolVO _usuarioRol) throws UsuarioRolServiceException;
	  
	  Integer eliminar(long ni) throws UsuarioRolServiceException;
	  
	  RolVO getRolByCodigo(String _codigoRol) throws UsuarioRolServiceException;
}
