package eus.martico.fileTransfer.dao.pedido.files;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eus.martico.fileTransfer.model.pedido.files.PedidoFilesVO;
import eus.martico.fileTransfer.sql.SqlUtils;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public class PedidoFilesDAOImpMapper implements RowMapper<PedidoFilesVO> {
	public PedidoFilesVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		//Solo para los campos que recoge la select
		PedidoFilesVO pedidoFiles = new PedidoFilesVO();
		pedidoFiles.setRefPed(SqlUtils.getStringTrim(rs, PedidoFilesDAOImpCons.BD_FTARCHI_REFPED));
		pedidoFiles.setExpediente(SqlUtils.getStringTrim(rs, PedidoFilesDAOImpCons.BD_FTARCHI_EXPEDI));
		
		pedidoFiles.setRutaCMR(SqlUtils.getStringTrim(rs, PedidoFilesDAOImpCons.BD_FTARCHI_RUTCMR));
		pedidoFiles.setUsuSubidaCMR(SqlUtils.getStringTrim(rs, PedidoFilesDAOImpCons.BD_FTARCHI_USECMR));
		pedidoFiles.setEstadoCMR(SqlUtils.getIntegerFromRs(rs, PedidoFilesDAOImpCons.BD_FTARCHI_ESTCMR));
		pedidoFiles.setFechaSubidaCMR(SqlUtils.getLocalDateTimeFromRs(rs, PedidoFilesDAOImpCons.BD_FTARCHI_FECCM1));
		pedidoFiles.setFechaEstadoCMR(SqlUtils.getLocalDateTimeFromRs(rs, PedidoFilesDAOImpCons.BD_FTARCHI_FECCM2));
		pedidoFiles.setUsuOkCMR(SqlUtils.getStringTrim(rs, PedidoFilesDAOImpCons.BD_FTARCHI_USUCMR));

		pedidoFiles.setRutaFAC(SqlUtils.getStringTrim(rs, PedidoFilesDAOImpCons.BD_FTARCHI_RUTFAC));
		pedidoFiles.setUsuSubidaFAC(SqlUtils.getStringTrim(rs, PedidoFilesDAOImpCons.BD_FTARCHI_USEFAC));
		pedidoFiles.setEstadoFAC(SqlUtils.getIntegerFromRs(rs, PedidoFilesDAOImpCons.BD_FTARCHI_ESTFAC));
		pedidoFiles.setFechaSubidaFAC(SqlUtils.getLocalDateTimeFromRs(rs, PedidoFilesDAOImpCons.BD_FTARCHI_FECFA1));
		pedidoFiles.setFechaEstadoFAC(SqlUtils.getLocalDateTimeFromRs(rs, PedidoFilesDAOImpCons.BD_FTARCHI_FECFA2));
		pedidoFiles.setUsuOkFAC(SqlUtils.getStringTrim(rs, PedidoFilesDAOImpCons.BD_FTARCHI_USUFAC));

		pedidoFiles.setRutaOTR(SqlUtils.getStringTrim(rs, PedidoFilesDAOImpCons.BD_FTARCHI_RUTOTR));
		pedidoFiles.setUsuSubidaOTR(SqlUtils.getStringTrim(rs, PedidoFilesDAOImpCons.BD_FTARCHI_USEOTR));
		pedidoFiles.setFechaSubidaOTR(SqlUtils.getLocalDateTimeFromRs(rs, PedidoFilesDAOImpCons.BD_FTARCHI_FECOT1));

		pedidoFiles.setEstadoFacturacion(SqlUtils.getIntegerFromRs(rs, PedidoFilesDAOImpCons.BD_FTARCHI_ESTADO));
		pedidoFiles.setMotivoRechazo(SqlUtils.getStringTrim(rs, PedidoFilesDAOImpCons.BD_FTARCHI_MOTREC));
		pedidoFiles.setAnotaciones(SqlUtils.getIntegerFromRs(rs, PedidoFilesDAOImpCons.BD_FTARCHI_ANOCMR));
		pedidoFiles.setFechaEntReal(SqlUtils.getLocalDateTimeFromRs(rs, PedidoFilesDAOImpCons.BD_FTARCHI_FECENT));
		return pedidoFiles;
	}
}


