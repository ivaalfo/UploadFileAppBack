package eus.martico.fileTransfer.dao.tabladetablas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.dao.BaseGenDAO;
import eus.martico.fileTransfer.dao.DAOException;
import eus.martico.fileTransfer.dao.DAOFiltro;
import eus.martico.fileTransfer.model.tabladetablas.vo.TablaCabeceraVO;
import eus.martico.fileTransfer.model.tabladetablas.vo.TablaDeTablasFiltroVO;
import eus.martico.fileTransfer.model.tabladetablas.vo.TablaDetalleCodTabCab;
import eus.martico.fileTransfer.model.tabladetablas.vo.TablaDetalleVO;
import eus.martico.fileTransfer.sql.SqlUtils;


@Service(value = "TablaDeTablasIDAO")
public class TablaDeTablasDAOImp extends BaseGenDAO<TablaDetalleVO> implements TablaDeTablasIDAO {

	protected static final Logger logger = LoggerFactory.getLogger(TablaDeTablasDAOImp.class);
	
	private static StringBuilder sqlInsert;
	private static StringBuilder sqlSelectCabeceraByCodigo;
	private static StringBuilder sqlSelectDetalleBase;
	private static StringBuilder sqlSelectDetalleCount;
	private static StringBuilder sqlSelectDetalleByNi;
	private static StringBuilder sqlSelectDetalleByCabecera;
	private static StringBuilder sqlSelectDetalleByCabeceraFT;
	private static StringBuilder sqlSelectDetalleByCabeceraAll;
	private static StringBuilder sqlSelectDetalleByCabeceraYTipo;
	private static StringBuilder sqlSelectDetalleByClave;
	private static StringBuilder sqlSelectDetalleActivoByCodigo;
	private static StringBuilder sqlSelectDetalleByCodigo;
	private static StringBuilder sqlSelectDetalleAll;
	private static StringBuilder sqlNextSequence;
	
	private static StringBuilder sqlActivo;
	private static StringBuilder sqlAndActivo;
	private static StringBuilder sqlNiFT;
	private static StringBuilder sqlAndNiFT;
	private static StringBuilder sqlOrderByDescripcion;
	private static StringBuilder sqlOrderByIV;
	
	static {
		List<String> camposInsert = new ArrayList<String>(Arrays.asList(
				TablaDeTablasDAOImpCons.BD_TABLACABECERA_NI,
				TablaDeTablasDAOImpCons.BD_TABLADETALLE_CODIGO,
				TablaDeTablasDAOImpCons.BD_TABLADETALLE_CLAVE,
				TablaDeTablasDAOImpCons.BD_DESCRIPCION_ES,
				TablaDeTablasDAOImpCons.BD_DESCRIPCION_EUS,
				TablaDeTablasDAOImpCons.BD_TABLADETALLE_TIPO
//				TablaDeTablasDAOImpCons.BD_TABLADETALLE_INDICE, //Nullable
		));

		sqlInsert = SqlUtils.generarSqlInsert(TablaDeTablasDAOImpCons.BD_TABLA_TABLADETALLE, camposInsert);
		
		sqlSelectCabeceraByCodigo = new StringBuilder("SELECT cab.* FROM ");
		sqlSelectCabeceraByCodigo.append(TablaDeTablasDAOImpCons.BD_TABLA_TABLACABECERA);
		sqlSelectCabeceraByCodigo.append(" cab");
		sqlSelectCabeceraByCodigo.append(" WHERE cab.");
		sqlSelectCabeceraByCodigo.append(TablaDeTablasDAOImpCons.BD_TABLACABECERA_CODIGO);
		sqlSelectCabeceraByCodigo.append(" = ?");
		
		sqlSelectDetalleBase = new StringBuilder("SELECT det.* FROM ");
		sqlSelectDetalleBase.append(TablaDeTablasDAOImpCons.BD_VISTA_TABLADETALLE);
		sqlSelectDetalleBase.append(" det");

		sqlSelectDetalleCount = new StringBuilder("SELECT COUNT(*) FROM ");
		sqlSelectDetalleCount.append(TablaDeTablasDAOImpCons.BD_TABLA_TABLADETALLE);

		sqlSelectDetalleByNi = new StringBuilder(sqlSelectDetalleBase);
		sqlSelectDetalleByNi.append(" WHERE ");
		sqlSelectDetalleByNi.append(TablaDeTablasDAOImpCons.BD_TABLADETALLE_NI);
		sqlSelectDetalleByNi.append(" = ?");

		sqlSelectDetalleByCabecera = new StringBuilder(sqlSelectDetalleBase);
		sqlSelectDetalleByCabecera.append(" WHERE det.");
		sqlSelectDetalleByCabecera.append(TablaDeTablasDAOImpCons.BD_TABLACABECERA_NI);
		sqlSelectDetalleByCabecera.append(" = (");
		sqlSelectDetalleByCabecera.append(" SELECT cab.");
		sqlSelectDetalleByCabecera.append(TablaDeTablasDAOImpCons.BD_TABLACABECERA_NI);
		sqlSelectDetalleByCabecera.append(" FROM ");
		sqlSelectDetalleByCabecera.append(TablaDeTablasDAOImpCons.BD_TABLA_TABLACABECERA);
		sqlSelectDetalleByCabecera.append(" cab");
		sqlSelectDetalleByCabecera.append(" WHERE cab.");
		sqlSelectDetalleByCabecera.append(TablaDeTablasDAOImpCons.BD_TABLACABECERA_CODIGO);
		sqlSelectDetalleByCabecera.append(" = ?");
		sqlSelectDetalleByCabecera.append(")");
		
		//Hasta aqui son iguales
		sqlSelectDetalleByCodigo = new StringBuilder(sqlSelectDetalleByCabecera);
		sqlSelectDetalleByCabeceraYTipo = new StringBuilder(sqlSelectDetalleByCabecera);
		sqlSelectDetalleByCabeceraAll = new StringBuilder(sqlSelectDetalleByCabecera);
		sqlSelectDetalleByClave = new StringBuilder(sqlSelectDetalleByCabecera);
		sqlSelectDetalleByCabeceraFT = new StringBuilder(sqlSelectDetalleByCabecera);

		sqlActivo = new StringBuilder(TablaDeTablasDAOImpCons.BD_INACTIVO);
		sqlActivo.append(" = ");
		sqlActivo.append(TablaDeTablasDAOImpCons.BD_INACTIVO_VALOR_ACTIVO);		
		sqlAndActivo = new StringBuilder(" AND ").append(sqlActivo);
		
		sqlNiFT = new StringBuilder(TablaDeTablasDAOImpCons.BD_TABLADETALLE_NI);
		sqlNiFT.append(" IN (");
		sqlNiFT.append(FileTransferConstantes.ROL_NUMBER_ADMIN +", ");
		sqlNiFT.append(FileTransferConstantes.ROL_NUMBER_INTERNO +", ");
		sqlNiFT.append(FileTransferConstantes.ROL_NUMBER_CONSULTA +", ");
		sqlNiFT.append(FileTransferConstantes.ROL_NUMBER_EXTERNO +", ");
		sqlNiFT.append(FileTransferConstantes.ROL_NUMBER_NO_ACCESS);
		sqlNiFT.append(")");
		sqlAndNiFT = new StringBuilder(" AND ").append(sqlNiFT);

		sqlOrderByDescripcion = new StringBuilder(" ORDER BY det.");
		sqlOrderByDescripcion.append(TablaDeTablasDAOImpCons.BD_DESCRIPCION_ES);
		
		sqlOrderByIV = new StringBuilder(" ORDER BY CASE det.");
		sqlOrderByIV.append(TablaDeTablasDAOImpCons.BD_TABLADETALLE_NI);
		sqlOrderByIV.append(" WHEN "+FileTransferConstantes.ROL_NUMBER_ADMIN+" THEN 1 ");
		sqlOrderByIV.append(" WHEN "+FileTransferConstantes.ROL_NUMBER_INTERNO+" THEN 2 ");
		sqlOrderByIV.append(" WHEN "+FileTransferConstantes.ROL_NUMBER_CONSULTA+" THEN 3 ");
		sqlOrderByIV.append(" WHEN "+FileTransferConstantes.ROL_NUMBER_EXTERNO+" THEN 4 ");
		sqlOrderByIV.append(" WHEN "+FileTransferConstantes.ROL_NUMBER_NO_ACCESS+" THEN 5 ");
		sqlOrderByIV.append(" ELSE 6 END");
		
		sqlSelectDetalleByCabecera.append(sqlAndActivo);
		sqlSelectDetalleByCabecera.append(sqlOrderByDescripcion);
		
		sqlSelectDetalleByCabeceraFT.append(sqlAndActivo);
		sqlSelectDetalleByCabeceraFT.append(sqlAndNiFT);
		sqlSelectDetalleByCabeceraFT.append(sqlOrderByIV);
		
		sqlSelectDetalleByCabeceraAll.append(sqlOrderByDescripcion);

		sqlSelectDetalleByCodigo.append(" AND ");
		sqlSelectDetalleByCodigo.append(TablaDeTablasDAOImpCons.BD_TABLADETALLE_CODIGO);
		sqlSelectDetalleByCodigo.append(" = ?");

		sqlSelectDetalleActivoByCodigo = new StringBuilder(sqlSelectDetalleByCodigo);	
		sqlSelectDetalleActivoByCodigo.append(sqlAndActivo);

		sqlSelectDetalleByCabeceraYTipo.append(" AND ");
		sqlSelectDetalleByCabeceraYTipo.append(TablaDeTablasDAOImpCons.BD_TABLADETALLE_TIPO);
		sqlSelectDetalleByCabeceraYTipo.append(" = ?");
		sqlSelectDetalleByCabeceraYTipo.append(sqlAndActivo);
		sqlSelectDetalleByCabeceraYTipo.append(sqlOrderByDescripcion);

		sqlSelectDetalleAll = new StringBuilder(sqlSelectDetalleBase);
        sqlSelectDetalleAll.append(" WHERE det.");
        sqlSelectDetalleAll.append(sqlActivo);
        
		sqlNextSequence = new StringBuilder("SELECT nextval('");
		sqlNextSequence.append(TablaDeTablasDAOImpCons.BD_SEQ_TABLADETALLE);
		sqlNextSequence.append("')");
		
		sqlSelectDetalleByClave.append(" AND ");
		sqlSelectDetalleByClave.append(TablaDeTablasDAOImpCons.BD_TABLADETALLE_CLAVE);
		sqlSelectDetalleByClave.append(" = ?");
		sqlSelectDetalleByClave.append(sqlAndActivo);

	}

	
	@Transactional
	public TablaDetalleVO insertDetalle(final TablaCabeceraVO _cabecera, final TablaDetalleVO _detalle) throws TablaDeTablasDAOException {
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
			int x = this.jdbcTemplate.update(
		        new PreparedStatementCreator() {
					
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						int i=1;
					    PreparedStatement ps = connection.prepareStatement(sqlInsert.toString(), 
					    	Statement.RETURN_GENERATED_KEYS);
					    ps.setLong(i++, _cabecera.getNi());
					    ps.setString(i++, _detalle.getCodigo());
					    ps.setString(i++, _detalle.getClave());
					    ps.setString(i++, _detalle.getDescEs());
					    ps.setString(i++, _detalle.getDescEus());
					    ps.setString(i++, _detalle.getTipo());
						return ps;
					}
				}
	    		, keyHolder);
			
			if (x > 0) {
				if (keyHolder.getKey()==null)
					throw new TablaDeTablasDAOException(DAOException.SQLERRCODE_NEXTNI, 
						"Error generando la sequencia ");
				_detalle.setNi(keyHolder.getKey().longValue());
			}
			return _detalle;
		} catch (DataAccessException e) {
			logger.error("Error insertDetalle", e);
			throw new TablaDeTablasDAOException(DAOException.SQLERRCODE_INSERT, e.getMessage());
		}
	}

	
	public TablaDetalleVO find(final Long _ni) throws TablaDeTablasDAOException {
		try {
			List<TablaDetalleVO> datos = this.jdbcTemplate.query(sqlSelectDetalleByNi.toString(), 
			new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps)  
					    throws SQLException{  
						int i=1;
						ps.setLong(i++, _ni);
					} 
			}, new TablaDeTablasDetalleDAOImpMapper());
			if (datos.isEmpty())
				return null;
			else
				return datos.get(0);
		} catch (DataAccessException e) {
			throw new TablaDeTablasDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}

	/**
	 * Parametro: el codigo de la cabecera de la tabla
	 * Devuelve la lista de elementos de la tabla 
	 */
	public  List<TablaDetalleVO> getByCodigoCabecera(final String _codigoCabecera) throws TablaDeTablasDAOException {
		try {
			List<TablaDetalleVO> datos;
			String sqlSelect = "";			
				if(_codigoCabecera.equals(FileTransferConstantes.TT_AUTOR_ROLES)){
					sqlSelect  = sqlSelectDetalleByCabeceraFT.toString();
				} else {
					sqlSelect = sqlSelectDetalleByCabecera.toString();
				}
				logger.trace(sqlSelect);
				datos = this.jdbcTemplate.query(sqlSelect,
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps)  
						    throws SQLException{  
							int i=1;
							logger.trace(_codigoCabecera);
							ps.setString(i++, _codigoCabecera);
						} 
				}, new TablaDeTablasDetalleDAOImpMapper());
			if (datos.isEmpty()) {
				logger.trace("datos empty");
				return null;
			} else {
				logger.trace("datos len="+datos.size());
				return datos;
			}
		} catch (DataAccessException e) {
			logger.error("error "+e.getMessage());
			throw new TablaDeTablasDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}
	
	public List<TablaDetalleVO> findByFiltro(TablaDeTablasFiltroVO _filtro) throws TablaDeTablasDAOException {
		try {
			final DAOFiltro daoFiltro = new TablaDeTablasDAOImpFiltro(_filtro);
			StringBuilder sqlSelectFiltro = new StringBuilder(sqlSelectDetalleBase);
			//Metemos la where si hay
			if (!daoFiltro.isEmpty()) {
				sqlSelectFiltro.append(daoFiltro.getSQLWhere());
			}
			sqlSelectFiltro.append(daoFiltro.getSQLOrderBy());
			
			logger.trace(sqlSelectFiltro.toString());
			
			List<TablaDetalleVO> lista = this.jdbcTemplate.query(sqlSelectFiltro.toString(), 
					new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps)  
						    throws SQLException{  
						daoFiltro.setFiltro(ps);						} 
					}, new TablaDeTablasDetalleDAOImpMapper());
			return lista;
		} catch (SQLException e) {
			throw new TablaDeTablasDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());	
		} catch (DataAccessException e) {
			throw new TablaDeTablasDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());	
		}
	}

	
	public Long nextNi() throws TablaDeTablasDAOException {
		try {
			Long nextNi = this.jdbcTemplate.queryForObject(sqlNextSequence.toString(), new Object[] {}, Long.class); 
			return nextNi;
		} catch (DataAccessException e) {
			throw new TablaDeTablasDAOException(DAOException.SQLERRCODE_NEXTNI, e.getMessage());
		}
	}

	
	public List<TablaDetalleCodTabCab> getAll() throws TablaDeTablasDAOException {
		try {
			List<TablaDetalleCodTabCab> lista = this.jdbcTemplate.query(sqlSelectDetalleAll.toString(), new TablaDeTablasTabCodDAOImpMapper());
			return lista;
		} catch (DataAccessException e) {
			logger.error(this.getClass().getName()+":get all", e);
			throw new TablaDeTablasDAOException(DAOException.SQLERRCODE_NEXTNI, e.getMessage());
		}
	}

	
	public TablaDetalleVO getDetalle(String _tabla, String _codigoDetalle) throws TablaDeTablasDAOException {
		return getDetalle(_tabla, _codigoDetalle, false);
	}
	
	public TablaDetalleVO getDetalle(final String _tabla, final String _codigoDetalle, boolean _conInactivos) throws TablaDeTablasDAOException {
		String sql;
		if (_conInactivos)
			sql = sqlSelectDetalleByCodigo.toString();
		else
			sql = sqlSelectDetalleActivoByCodigo.toString();
		try {
			List<TablaDetalleVO> datos = this.jdbcTemplate.query(sql, 
			new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps)  
					    throws SQLException{  
						int i=1;
						ps.setString(i++, _tabla);
						ps.setString(i++, _codigoDetalle);
					}  
				
			}, new TablaDeTablasDetalleDAOImpMapper());
			if (datos.isEmpty())
				return null;
			else {
				return datos.get(0);
			}
		} catch (DataAccessException e) {
			logger.error("getDetalle "+e.getMessage());
			throw new TablaDeTablasDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
		
	}
	
	public Long getNi(String _tabla, String _codigoDetalle) throws TablaDeTablasDAOException {
		try {
			TablaDetalleVO datos = this.getDetalle(_tabla, _codigoDetalle); 
			if (datos==null)
				return null;
			else {
				return datos.getNi();
			}
		} catch (DataAccessException e) {
			throw new TablaDeTablasDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}
	
	public Short getIndice(String _tabla, String _codigoDetalle) throws TablaDeTablasDAOException {
		try {
			TablaDetalleVO datos = this.getDetalle(_tabla, _codigoDetalle); 
			if (datos==null)
				return null;
			else {
				return datos.getIndice();
			}
		} catch (DataAccessException e) {
			throw new TablaDeTablasDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}

	
	public TablaDetalleVO getDetalleByClave(final String _tabla, final String _claveDetalle) throws TablaDeTablasDAOException {
		try {
			List<TablaDetalleVO> datos = this.jdbcTemplate.query(sqlSelectDetalleByClave.toString(), 
			new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps)  
					    throws SQLException{  
						int i=1;
						ps.setString(i++, _tabla);
						ps.setString(i++, _claveDetalle);
					}  
				
			}, new TablaDeTablasDetalleDAOImpMapper());
			if (datos.isEmpty())
				return null;
			else {
				return datos.get(0);
			}
		} catch (DataAccessException e) {
			throw new TablaDeTablasDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}

	
	public List<TablaDetalleVO> getByCabeceraTipo(final String _codigoCab, final String _tipo) throws TablaDeTablasDAOException {
		try {
			List<TablaDetalleVO> datos = this.jdbcTemplate.query(sqlSelectDetalleByCabeceraYTipo.toString(), 
			new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps)  
					    throws SQLException{  
						int i=1;
						ps.setString(i++, _codigoCab);
						ps.setString(i++, _tipo);
					} 
			}, new TablaDeTablasDetalleDAOImpMapper());
			if (datos.isEmpty())
				return null;
			else
				return datos;
		} catch (DataAccessException e) {
			throw new TablaDeTablasDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}
	
	
	public TablaCabeceraVO getCabeceraByCodigo(final String _cabeceraCodigo) throws TablaDeTablasDAOException {
		try {
			List<TablaCabeceraVO> datos = this.jdbcTemplate.query(sqlSelectCabeceraByCodigo.toString(), 
			new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps)  
					    throws SQLException{  
						int i=1;
						ps.setString(i++, _cabeceraCodigo);
					}  
				
			}, new TablaDeTablasCabeceraDAOImpMapper());
			if (datos.isEmpty())
				return null;
			else {
				return datos.get(0);
			}
		} catch (DataAccessException e) {
			throw new TablaDeTablasDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}
	
}
