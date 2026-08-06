package eus.martico.fileTransfer.dao.bloqueo;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eus.martico.fileTransfer.dao.bloqueo.BloqueoDAOImpCons;
import eus.martico.fileTransfer.model.bloqueo.BloqueoVO;
import eus.martico.fileTransfer.sql.SqlUtils;

public class BloqueoDAOImpMapper implements RowMapper<BloqueoVO> {

	@Override
	public BloqueoVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		BloqueoVO bloqueo = new BloqueoVO();
		
		bloqueo.setNi(SqlUtils.getLongFromRs(rs, BloqueoDAOImpCons.BD_BLOQNI));
		bloqueo.setInicio(SqlUtils.getLocalDateTimeFromRs(rs, BloqueoDAOImpCons.BD_FECHAHORA_INICIO));
		bloqueo.setFin(SqlUtils.getLocalDateTimeFromRs(rs, BloqueoDAOImpCons.BD_FECHAHORA_FIN));
		
		bloqueo.setTipoNi(SqlUtils.getLongFromRs(rs, BloqueoDAOImpCons.BD_VISTA_TIPO_NI));
		bloqueo.setTipoCod(SqlUtils.getStringTrim(rs, BloqueoDAOImpCons.BD_VISTA_TIPO));
		bloqueo.setCodigo(SqlUtils.getStringTrim(rs, BloqueoDAOImpCons.BD_VISTA_CODIGO));
		
		bloqueo.setEstado(SqlUtils.getLongFromRs(rs, BloqueoDAOImpCons.BD_ESTADO));
		
		bloqueo.setUsuarioNi(SqlUtils.getLongFromRs(rs, BloqueoDAOImpCons.BD_USUARIO_NI));
		bloqueo.setSessionId(SqlUtils.getStringTrim(rs, BloqueoDAOImpCons.BD_SESSION_ID));
		
		bloqueo.setUsername(SqlUtils.getStringTrim(rs, BloqueoDAOImpCons.BD_VISTA_USERNAME));
		bloqueo.setAccion(SqlUtils.getByteFromRs(rs, BloqueoDAOImpCons.BD_ACCION));
		
		return bloqueo;
	}

}
