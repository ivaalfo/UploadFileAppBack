package eus.martico.fileTransfer.dao.usuario.rol;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eus.martico.fileTransfer.model.usuario.rol.UsuarioRolVO;
import eus.martico.fileTransfer.sql.SqlUtils;

public class UsuarioRolDAOImpMapper implements RowMapper<UsuarioRolVO> {
	   public UsuarioRolVO mapRow(ResultSet rs, int rowNum) throws SQLException {
			UsuarioRolVO usuarioRol = new UsuarioRolVO();				
			usuarioRol.setUsuarioNi(SqlUtils.getLongFromRs(rs, UsuarioRolDAOImpCons.BD_USERNI));
			usuarioRol.setRolNi(SqlUtils.getLongFromRs(rs, UsuarioRolDAOImpCons.BD_ROLNI));
			usuarioRol.setRolCodigo(rs.getString(UsuarioRolDAOImpCons.BD_VISTA_ROLCOD));
		return usuarioRol;
	   }
	}