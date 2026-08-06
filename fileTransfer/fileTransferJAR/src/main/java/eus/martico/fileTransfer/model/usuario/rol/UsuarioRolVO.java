package eus.martico.fileTransfer.model.usuario.rol;

import eus.martico.fileTransfer.model.rol.RolVO;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public class UsuarioRolVO {

	public UsuarioRolVO() {
	}

	private Long usuarioNi;
	private RolVO rol = new RolVO();
	
	public Long getUsuarioNi() {
		return usuarioNi;
	}
	public void setUsuarioNi(Long usuarioNi) {
		this.usuarioNi = usuarioNi;
	}
	public Long getRolNi() {
		return rol.getNi();
	}

	public void setRolNi(Long ni) {
		rol.setNi(ni);
	}

	public String getRolCodigo() {
		return rol.getCodigo();
	}

	public void setRolCodigo(String codigo) {
		rol.setCodigo(codigo);
	}
	
}
