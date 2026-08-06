package eus.martico.fileTransfer.dao.tabladetablas;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eus.martico.fileTransfer.model.tabladetablas.vo.TablaDetalleCodTabCab;

public class TablaDeTablasTabCodDAOImpMapper  implements RowMapper<TablaDetalleCodTabCab> {
	   public TablaDetalleCodTabCab mapRow(ResultSet rs, int rowNum) throws SQLException {
		   TablaDetalleCodTabCab tablacabecera = new TablaDetalleCodTabCab();
		   new TablaDeTablasDetalleDAOImpMapper().mapRow(tablacabecera,rs, rowNum);
		   tablacabecera.setTabcab_cod(rs.getString(TablaDeTablasDAOImpCons.BD_TABLACABECERA_CODIGO));
		   return tablacabecera;
	   }
	}