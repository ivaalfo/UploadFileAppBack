package eus.martico.fileTransfer.model.pedido.proveedor;

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
public class PedidoProveedorDAOImpMapper implements RowMapper<PedidoProveedorVO> {
	public PedidoProveedorVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		//Solo para los campos que recoge la select (Mantenimiento vista)
		PedidoProveedorVO PedidoProv = new PedidoProveedorVO();
		PedidoProv.setProveedorKy(SqlUtils.getLongFromRs(rs, PedidoProveedorDAOImpCons.BD_FCTPDNPRV_KYPROVEEDOR));
		PedidoProv.setProveedor(SqlUtils.getStringTrim(rs, PedidoProveedorDAOImpCons.BD_FCTPDNPRV_PROVEEDOR));
		PedidoProv.setTrack(SqlUtils.getStringTrim(rs, PedidoProveedorDAOImpCons.BD_FCTPDNPRV_TR));
		PedidoProv.setExpediente(SqlUtils.getStringTrim(rs, PedidoProveedorDAOImpCons.BD_FCTPDNPRV_EXPEDIENTE));
		PedidoProv.setFechaCarga(SqlUtils.getStringTrim(rs, PedidoProveedorDAOImpCons.BD_FCTPDNPRV_FCARGA));
		PedidoProv.setRefCarga(SqlUtils.getStringTrim(rs, PedidoProveedorDAOImpCons.BD_FCTPDNPRV_RCARGA));
		PedidoProv.setFechaDescarga(SqlUtils.getStringTrim(rs, PedidoProveedorDAOImpCons.BD_FCTPDNPRV_FDESCARGA));
		PedidoProv.setRefDescarga(SqlUtils.getStringTrim(rs, PedidoProveedorDAOImpCons.BD_FCTPDNPRV_RDESCARGA));
		PedidoProv.setOrigen(SqlUtils.getStringTrim(rs, PedidoProveedorDAOImpCons.BD_FCTPDNPRV_ORIGEN));
		PedidoProv.setDestino(SqlUtils.getStringTrim(rs, PedidoProveedorDAOImpCons.BD_FCTPDNPRV_DESTINO));
		PedidoProv.setNumPalets(SqlUtils.getLongFromRs(rs, PedidoProveedorDAOImpCons.BD_FCTPDNPRV_PALETS));
		PedidoProv.setMatriculaCamion(SqlUtils.getStringTrim(rs, PedidoProveedorDAOImpCons.BD_FCTPDNPRV_MATRICULA));
		PedidoProv.setMatriculaRemolque(SqlUtils.getStringTrim(rs, PedidoProveedorDAOImpCons.BD_FCTPDNPRV_REMOLQUE));
		
		PedidoProv.setNi(SqlUtils.getLongFromRs(rs, PedidoProveedorDAOImpCons.BD_FCTPDNPRV_KYPROVEEDOR));
		PedidoProv.setOrg(SqlUtils.getStringTrim(rs, PedidoProveedorDAOImpCons.BD_FCTPDNPRV_ORG));
		PedidoProv.setHasCMR(SqlUtils.getLongFromRs(rs, PedidoProveedorDAOImpCons.BD_FCTPDNPRV_ECMR));
		PedidoProv.setHasFAC(SqlUtils.getLongFromRs(rs, PedidoProveedorDAOImpCons.BD_FCTPDNPRV_EFAC));
		PedidoProv.setHasOTR(SqlUtils.getLongFromRs(rs, PedidoProveedorDAOImpCons.BD_FCTPDNPRV_EOTR));
		PedidoProv.setEstado(SqlUtils.getLongFromRs(rs, PedidoProveedorDAOImpCons.BD_FCTPDNPRV_ESTADO));
		
		PedidoProv.setAnotaciones(SqlUtils.getLongFromRs(rs, PedidoProveedorDAOImpCons.BD_FCTPDNPRV_ANOCMR));
		PedidoProv.setFechaEntReal(SqlUtils.getLocalDateFromRs(rs, PedidoProveedorDAOImpCons.BD_FCTPDNPRV_FECREAL));
			
		return PedidoProv;
	}
}
