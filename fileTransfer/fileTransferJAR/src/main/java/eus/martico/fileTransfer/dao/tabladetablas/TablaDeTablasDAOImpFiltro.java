package eus.martico.fileTransfer.dao.tabladetablas;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import eus.martico.fileTransfer.dao.DAOFiltro;
import eus.martico.fileTransfer.model.tabladetablas.vo.TablaDeTablasFiltroVO;
import eus.martico.fileTransfer.model.tabladetablas.vo.TablaDeTablasCampoEnum;

/**
 * 
 * @author Martico S.L.
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public class TablaDeTablasDAOImpFiltro extends DAOFiltro {

	TablaDeTablasFiltroVO filtro;
	
	public TablaDeTablasDAOImpFiltro(TablaDeTablasFiltroVO filtro) throws SQLException {
		super();
		this.filtro = filtro;
		setFiltro(null);
	}

	public String getSQLCampo(String _campo) {
	  TablaDeTablasCampoEnum campo = TablaDeTablasCampoEnum.valueOf(_campo);
	  return getSQLCampo(campo);
	}
	
	private String getSQLCampo(TablaDeTablasCampoEnum _campo) {
		switch(_campo){
			case DETALLE_NI: 
				return TablaDeTablasDAOImpCons.BD_TABLADETALLE_NI;
			case CABECERA_NI: 
				return TablaDeTablasDAOImpCons.BD_TABLACABECERA_NI;
			case CABECERA_CODIGO: 
				return TablaDeTablasDAOImpCons.BD_TABLACABECERA_CODIGO;
			case DETALLE_CODIGO: 
				return TablaDeTablasDAOImpCons.BD_TABLADETALLE_CODIGO;
			case DESCRIPCION_ES: 
				return TablaDeTablasDAOImpCons.BD_DESCRIPCION_ES;
			case DESCRIPCION_EUS:
				return TablaDeTablasDAOImpCons.BD_DESCRIPCION_EUS;
			default:
				return null;
		}
	}

	public String getSQLOrdenDireccion() {
		if(filtro.getOrden() < 0){
			return " DESC";
		} else {
			return " ASC";
		}
	}

	public String getSQLOrderBy() {
		//Metemos el order, tiene que haber uno
		StringBuilder sql = new StringBuilder();
		if(filtro.getCampoOrden() != null){
			sql.append(" ORDER BY ");
			sql.append(getSQLCampo(filtro.getCampoOrden()));
			sql.append(this.getSQLOrdenDireccion());
		}
		return sql.toString();
	}
	
	public void setFiltro(final PreparedStatement _ps) throws SQLException {
		int i=1;
		if(filtro.getCodigoDetalle() != null){
			if(_ps == null){
				this.addCampo(TablaDeTablasDAOImpCons.BD_TABLADETALLE_CODIGO, DAOFiltro.TipoOperadorFiltro.IGUAL);
			} else {
				_ps.setString(i++, filtro.getCodigoDetalle());
			}
		}
		if(filtro.getCodigoCabecera() != null){
			if(_ps == null){
				this.addCampo(TablaDeTablasDAOImpCons.BD_TABLACABECERA_CODIGO, DAOFiltro.TipoOperadorFiltro.IGUAL);
			} else {
				_ps.setString(i++, filtro.getCodigoCabecera());
			}
		}
	}

}
