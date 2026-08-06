package eus.martico.fileTransfer.dao.usuario;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eus.martico.fileTransfer.model.usuario.UsuarioVO;
import eus.martico.fileTransfer.sql.SqlUtils;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public class UsuarioUpfDAOImpMapper implements RowMapper<UsuarioVO> {
	public UsuarioVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		//Solo para los campos que recoge la select (Mantenimiento vista)
		UsuarioVO Usuario = new UsuarioVO();
		Usuario.setW2usuky(SqlUtils.getLongFromRs(rs, UsuarioDAOImpCons.BD_LDUSUAR_W2USUKY));
		Usuario.setW2usuario(SqlUtils.getStringTrim(rs, UsuarioDAOImpCons.BD_LDUSUAR_W2USUARIO));
		Usuario.setW2email(SqlUtils.getStringTrim(rs, UsuarioDAOImpCons.BD_LDUSUAR_W2EMAIL));
		Usuario.setW2contras(SqlUtils.getStringTrim(rs, UsuarioDAOImpCons.BD_LDUSUAR_W2CONTRAS));
		//Usuario.setW2opeky(SqlUtils.getLongFromRs(rs, UsuarioDAOImpCons.BD_LDUSUAR_W2OPEKY));
		//Usuario.setW2nuem(SqlUtils.getLongFromRs(rs, UsuarioDAOImpCons.BD_LDUSUAR_W2NUEM));
		//Usuario.setW2almky(SqlUtils.getLongFromRs(rs, UsuarioDAOImpCons.BD_LDUSUAR_W2ALMKY));
		Usuario.setW2uscr(SqlUtils.getStringTrim(rs, UsuarioDAOImpCons.BD_LDUSUAR_W2USCR));
		Usuario.setW2usca(SqlUtils.getStringTrim(rs, UsuarioDAOImpCons.BD_LDUSUAR_W2USCA));
		Usuario.setW2fecr(SqlUtils.getLocalDateFromRs(rs, UsuarioDAOImpCons.BD_LDUSUAR_W2FECR));
		Usuario.setW2feca(SqlUtils.getLocalDateTimeFromRs(rs, UsuarioDAOImpCons.BD_LDUSUAR_W2FECA));
		
		Usuario.setW3tipo(SqlUtils.getLongFromRs(rs, UsuarioDAOImpCons.BD_LDUSUAX_W3TIPO));
		Usuario.setW3usuky(SqlUtils.getLongFromRs(rs, UsuarioDAOImpCons.BD_LDUSUAX_W3USUKY));
		//Usuario.setW3cmp1(SqlUtils.getStringTrim(rs, UsuarioDAOImpCons.BD_LDUSUAX_W3CMP1));
		//Usuario.setW3cmp2(SqlUtils.getStringTrim(rs, UsuarioDAOImpCons.BD_LDUSUAX_W3CMP2));
		//Usuario.setW3cmp3(SqlUtils.getStringTrim(rs, UsuarioDAOImpCons.BD_LDUSUAX_W3CMP3));
		
		Usuario.setClicodpro(SqlUtils.getStringTrim(rs, UsuarioDAOImpCons.BD_CLIPRO_CLICOD));
		Usuario.setClinom(SqlUtils.getStringTrim(rs, UsuarioDAOImpCons.BD_CLIPRO_CLINOM));
		Usuario.setClidir(SqlUtils.getStringTrim(rs, UsuarioDAOImpCons.BD_CLIPRO_CLIDIR));
		Usuario.setClicif(SqlUtils.getStringTrim(rs, UsuarioDAOImpCons.BD_CLIPRO_CLICIF));
		
		Usuario.setNi(SqlUtils.getLongFromRs(rs, UsuarioDAOImpCons.BD_LDUSUAR_W2USUKY));
		Usuario.setUsername(SqlUtils.getStringTrim(rs, UsuarioDAOImpCons.BD_LDUSUAR_W2USUARIO));
		
		return Usuario;
   }
}
	
	
	