package eus.martico.fileTransfer.model.pedido.factura;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eus.martico.fileTransfer.sql.SqlUtils;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public class PedidoFacturaDAOImpMapper implements RowMapper<PedidoFacturaVO> {
	public PedidoFacturaVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		PedidoFacturaVO PedidoFac = new PedidoFacturaVO();
		
		PedidoFac.setFacTR(SqlUtils.getStringTrim(rs, PedidoFacturaDAOImpCons.BD_FTRCONTA_TR));
		PedidoFac.setFacEXP(SqlUtils.getStringTrim(rs, PedidoFacturaDAOImpCons.BD_FTRCONTA_EXPE));
		
		PedidoFac.setFacNumOriginal(SqlUtils.getStringTrim(rs, PedidoFacturaDAOImpCons.BD_FTRCONTA_FACTUR));
		PedidoFac.setEjercicio(SqlUtils.getLongFromRs(rs, PedidoFacturaDAOImpCons.BD_FTRCONTA_EJER));
		PedidoFac.setNumAsiento(SqlUtils.getLongFromRs(rs, PedidoFacturaDAOImpCons.BD_FTRCONTA_NUMAS));
		
		PedidoFac.setFacNumExcell(SqlUtils.getStringTrim(rs, PedidoFacturaDAOImpCons.BD_FTRCONTA_FACTEX));
		PedidoFac.setProcesado(SqlUtils.getLongFromRs(rs, PedidoFacturaDAOImpCons.BD_FTRCONTA_PROCE));
		
		PedidoFac.setFechaCreacion(SqlUtils.getLocalDateFromRs(rs, PedidoFacturaDAOImpCons.BD_FTRCONTA_FECCRE));
			
		return PedidoFac;
	}
}
