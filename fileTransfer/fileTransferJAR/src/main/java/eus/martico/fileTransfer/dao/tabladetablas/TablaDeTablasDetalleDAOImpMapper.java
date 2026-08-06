package eus.martico.fileTransfer.dao.tabladetablas;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eus.martico.fileTransfer.model.tabladetablas.vo.TablaDetalleVO;
import eus.martico.fileTransfer.sql.SqlUtils;

public class TablaDeTablasDetalleDAOImpMapper implements RowMapper<TablaDetalleVO> {
	public TablaDetalleVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		TablaDetalleVO tablaDetalle = new TablaDetalleVO();
		mapRow(tablaDetalle, rs, rowNum);
		return tablaDetalle;
	}

	public TablaDetalleVO mapRow(TablaDetalleVO tablaDetalle, ResultSet rs, int rowNum) throws SQLException {
		tablaDetalle.setNi(SqlUtils.getLongFromRs(rs, TablaDeTablasDAOImpCons.BD_TABLADETALLE_NI));
		tablaDetalle.setCodigo(SqlUtils.getStringTrim(rs, TablaDeTablasDAOImpCons.BD_TABLADETALLE_CODIGO));
		tablaDetalle.setClave(SqlUtils.getStringTrim(rs, TablaDeTablasDAOImpCons.BD_TABLADETALLE_CLAVE));
		tablaDetalle.setTipo(SqlUtils.getStringTrim(rs, TablaDeTablasDAOImpCons.BD_TABLADETALLE_TIPO));
		tablaDetalle.setDescEs(SqlUtils.getStringTrim(rs, TablaDeTablasDAOImpCons.BD_DESCRIPCION_ES));
		tablaDetalle.setDescEus(SqlUtils.getStringTrim(rs, TablaDeTablasDAOImpCons.BD_DESCRIPCION_EUS));
		tablaDetalle.setIndice(SqlUtils.getShortFromRs(rs, TablaDeTablasDAOImpCons.BD_TABLADETALLE_INDICE));
		Boolean inactivo = SqlUtils.getBooleanFromRs(rs, TablaDeTablasDAOImpCons.BD_INACTIVO);
		if (inactivo!=null)
			tablaDetalle.setActivo(!inactivo);
		return tablaDetalle;
	}
}