package eus.martico.fileTransfer.dao.tabladetablas;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eus.martico.fileTransfer.model.tabladetablas.vo.TablaCabeceraVO;
import eus.martico.fileTransfer.sql.SqlUtils;

public class TablaDeTablasCabeceraDAOImpMapper implements RowMapper<TablaCabeceraVO> {
	public TablaCabeceraVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		TablaCabeceraVO tablaCabecera = new TablaCabeceraVO();
		mapRow(tablaCabecera, rs, rowNum);
		return tablaCabecera;
	}

	public TablaCabeceraVO mapRow(TablaCabeceraVO tablaCabecera, ResultSet rs, int rowNum) throws SQLException {
		tablaCabecera.setNi(SqlUtils.getLongFromRs(rs, TablaDeTablasDAOImpCons.BD_TABLACABECERA_NI));
		tablaCabecera.setCodigo(SqlUtils.getStringTrim(rs, TablaDeTablasDAOImpCons.BD_TABLACABECERA_CODIGO));
		tablaCabecera.setDescEs(SqlUtils.getStringTrim(rs, TablaDeTablasDAOImpCons.BD_TABLACABECERA_DESC_ES));
		tablaCabecera.setDescEus(SqlUtils.getStringTrim(rs, TablaDeTablasDAOImpCons.BD_TABLACABECERA_DESC_EUS));
		return tablaCabecera;
	}
}