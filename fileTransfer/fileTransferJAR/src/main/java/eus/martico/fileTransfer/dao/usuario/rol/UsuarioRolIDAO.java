package eus.martico.fileTransfer.dao.usuario.rol;

import eus.martico.fileTransfer.model.usuario.rol.UsuarioRolVO;

public interface UsuarioRolIDAO {

	UsuarioRolVO alta(final UsuarioRolVO _usuarioRol) throws UsuarioRolDAOException;
	
	Integer eliminar(final Long ni) throws UsuarioRolDAOException;
	
}
