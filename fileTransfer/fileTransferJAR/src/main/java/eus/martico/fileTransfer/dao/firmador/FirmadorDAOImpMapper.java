package eus.martico.fileTransfer.dao.firmador;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eus.martico.fileTransfer.model.firma.PedidoFirmadorVO;
import eus.martico.fileTransfer.sql.SqlUtils;

/**
 * NEW
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public class FirmadorDAOImpMapper implements RowMapper<PedidoFirmadorVO> {
	public PedidoFirmadorVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		//Solo para los campos que recoge la select
		PedidoFirmadorVO pedidoFirma = new PedidoFirmadorVO();
		pedidoFirma.setF3refPed(SqlUtils.getStringTrim(rs, FirmadorDAOImpCons.BD_FTCMR_REFPED));
		pedidoFirma.setF3expediente(SqlUtils.getStringTrim(rs, FirmadorDAOImpCons.BD_FTCMR_EXPEDI));
		pedidoFirma.setF3refCarga(SqlUtils.getStringTrim(rs, FirmadorDAOImpCons.BD_FTCMR_RCARGA));
		pedidoFirma.setF3matricula(SqlUtils.getStringTrim(rs, FirmadorDAOImpCons.BD_FTCMR_MATRIC));
		pedidoFirma.setF3remolque(SqlUtils.getStringTrim(rs, FirmadorDAOImpCons.BD_FTCMR_REMOLQ));
		pedidoFirma.setF3cliCod(SqlUtils.getStringTrim(rs, FirmadorDAOImpCons.BD_FTCMR_CLICOD));
		pedidoFirma.setF3destCod(SqlUtils.getStringTrim(rs, FirmadorDAOImpCons.BD_FTCMR_DESCOD));
		
		pedidoFirma.setF3rutCMR(SqlUtils.getStringTrim(rs, FirmadorDAOImpCons.BD_FTCMR_RUTCMR));
		pedidoFirma.setF3useCMR(SqlUtils.getStringTrim(rs, FirmadorDAOImpCons.BD_FTCMR_USECMR));
		
		pedidoFirma.setF3fechaCreacionCMR(SqlUtils.getLocalDateTimeFromRs(rs, FirmadorDAOImpCons.BD_FTCMR_FECCM1));
		pedidoFirma.setF3anotaMart(SqlUtils.getStringTrim(rs, FirmadorDAOImpCons.BD_FTCMR_ANOCM1));
		pedidoFirma.setF3fechaFirmaTrans(SqlUtils.getLocalDateTimeFromRs(rs, FirmadorDAOImpCons.BD_FTCMR_FECCM2));
		pedidoFirma.setF3anotaTrans(SqlUtils.getStringTrim(rs, FirmadorDAOImpCons.BD_FTCMR_ANOCM2));
		pedidoFirma.setF3fechaFirmaDest(SqlUtils.getLocalDateTimeFromRs(rs, FirmadorDAOImpCons.BD_FTCMR_FECCM3));
		pedidoFirma.setF3anotaDest(SqlUtils.getStringTrim(rs, FirmadorDAOImpCons.BD_FTCMR_ANOCM3));
		
		pedidoFirma.setF3estado(SqlUtils.getIntegerFromRs(rs, FirmadorDAOImpCons.BD_FTCMR_ESTCMR));
		return pedidoFirma;
	}
}