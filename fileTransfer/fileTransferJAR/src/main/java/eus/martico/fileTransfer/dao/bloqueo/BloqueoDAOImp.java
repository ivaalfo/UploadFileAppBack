package eus.martico.fileTransfer.dao.bloqueo;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import eus.martico.fileTransfer.dao.BaseGenDAO;
import eus.martico.fileTransfer.dao.DAOException;
import eus.martico.fileTransfer.model.bloqueo.BloqueoVO;
import eus.martico.fileTransfer.sql.SqlUtils;

@Service
public class BloqueoDAOImp extends BaseGenDAO<BloqueoVO> implements BloqueoIDAO {

	private static StringBuilder sqlSelectId;
	private static StringBuilder sqlSelectTipoNi;
	private static StringBuilder sqlSelectNi;
	private static StringBuilder sqlInsertConCodigo;
	private static StringBuilder sqlInsertSinCodigo;
	private static StringBuilder sqlUpdateFinBloqueo = new StringBuilder();
	private static StringBuilder sqlUpdateFinBloqueoTipo = new StringBuilder();
	private static StringBuilder sqlUpdateFinBloqueoTipoCodigo = new StringBuilder();
//	private static StringBuilder sqlUpdateFinBloqueoTipoUsuario = new StringBuilder();
	private static StringBuilder sqlUpdateFinBloqueoAll = new StringBuilder();
	
	private static StringBuilder sqlUpdateAccion;
	private static StringBuilder sqlSelectSavedLastUser;

	static {
		List<String> camposInsert = new ArrayList<String>(Arrays.asList(
															BloqueoDAOImpCons.BD_USUARIO_NI,
															BloqueoDAOImpCons.BD_SESSION_ID,
															BloqueoDAOImpCons.BD_TIPO_NI
															));
				
		sqlSelectId = SqlUtils.generarSelectById(BloqueoDAOImpCons.BD_VISTA_BLOQUEO, BloqueoDAOImpCons.BD_VISTA_TIPO, BloqueoDAOImpCons.BD_ESTADO, BloqueoDAOImpCons.BD_VISTA_CODIGO);
		
		sqlSelectTipoNi = SqlUtils.generarSelectById(BloqueoDAOImpCons.BD_VISTA_BLOQUEO, BloqueoDAOImpCons.BD_VISTA_TIPO_NI, BloqueoDAOImpCons.BD_ESTADO, BloqueoDAOImpCons.BD_VISTA_CODIGO);
		sqlSelectNi = SqlUtils.generarSelectById(BloqueoDAOImpCons.BD_VISTA_BLOQUEO, BloqueoDAOImpCons.BD_BLOQNI);
		
		sqlInsertSinCodigo = SqlUtils.generarSqlInsert(BloqueoDAOImpCons.BD_TABLA_BLOQUEO, camposInsert);
		camposInsert.add(BloqueoDAOImpCons.BD_CODIGO);
		sqlInsertConCodigo = SqlUtils.generarSqlInsert(BloqueoDAOImpCons.BD_TABLA_BLOQUEO, camposInsert);
		
		
		sqlUpdateFinBloqueoAll.append("UPDATE ");
		sqlUpdateFinBloqueoAll.append(BloqueoDAOImpCons.BD_TABLA_BLOQUEO);
		sqlUpdateFinBloqueoAll.append(" SET ");
		// Quitamos el estado = 0
		sqlUpdateFinBloqueoAll.append(BloqueoDAOImpCons.BD_ESTADO);
		sqlUpdateFinBloqueoAll.append(" = ");
		sqlUpdateFinBloqueoAll.append(BloqueoDAOImpCons.BD_BLOQNI);
		sqlUpdateFinBloqueoAll.append(", ");
		// Ponemos el timestamp de fin de bloqueo
		sqlUpdateFinBloqueoAll.append(BloqueoDAOImpCons.BD_FECHAHORA_FIN);
		sqlUpdateFinBloqueoAll.append(" = CURRENT_TIMESTAMP");		
		sqlUpdateFinBloqueoAll.append(" WHERE ");
		sqlUpdateFinBloqueoAll.append(BloqueoDAOImpCons.BD_ESTADO);
		sqlUpdateFinBloqueoAll.append(" = ");
		sqlUpdateFinBloqueoAll.append(BloqueoDAOImpCons.BD_ESTADO_BLOQUEADO);

		// Hasta aqui son iguales
		sqlUpdateFinBloqueoTipo.append(sqlUpdateFinBloqueoAll);
		sqlUpdateFinBloqueoTipo.append(" AND ");
		sqlUpdateFinBloqueoTipo.append(BloqueoDAOImpCons.BD_TIPO_NI);
		sqlUpdateFinBloqueoTipo.append(" = ?");
		
		sqlUpdateFinBloqueoTipoCodigo.append(sqlUpdateFinBloqueoTipo);
		sqlUpdateFinBloqueoTipoCodigo.append(" AND ");
		sqlUpdateFinBloqueoTipoCodigo.append(BloqueoDAOImpCons.BD_CODIGO);
		sqlUpdateFinBloqueoTipoCodigo.append(" = ?");

//		sqlUpdateFinBloqueoTipoUsuario.append(sqlUpdateFinBloqueoTipo);
//		sqlUpdateFinBloqueoTipoUsuario.append(" AND ");
//		sqlUpdateFinBloqueoTipoUsuario.append(BloqueoDAOImpCons.BD_USUARIO_NI);
//		sqlUpdateFinBloqueoTipoUsuario.append(" = ?");
		
		// Hasta aqui son iguales
		sqlUpdateFinBloqueo.append(sqlUpdateFinBloqueoTipoCodigo);
		sqlUpdateFinBloqueo.append(" AND ");
		sqlUpdateFinBloqueo.append(BloqueoDAOImpCons.BD_USUARIO_NI);
		sqlUpdateFinBloqueo.append(" = ?");
		
		sqlUpdateAccion= SqlUtils.generarSqlUpdate(BloqueoDAOImpCons.BD_TABLA_BLOQUEO, 
													Arrays.asList(BloqueoDAOImpCons.BD_ACCION),
													BloqueoDAOImpCons.BD_BLOQNI);
		sqlSelectSavedLastUser = SqlUtils.generarSelectById(BloqueoDAOImpCons.BD_VISTA_BLOQUEO, BloqueoDAOImpCons.BD_VISTA_TIPO, 
																								BloqueoDAOImpCons.BD_VISTA_CODIGO, 
																								BloqueoDAOImpCons.BD_ACCION);
		// El ultimo
		sqlSelectSavedLastUser.append(" ORDER BY ");
		sqlSelectSavedLastUser.append(BloqueoDAOImpCons.BD_FECHAHORA_INICIO);
		sqlSelectSavedLastUser.append(" DESC");
	}
	
	@Override
	public BloqueoVO iniciar(long _tipoNi, String _codigo, Long _usuarioNi, String _sessionId) throws BloqueoDAOException {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
			int x = this.jdbcTemplate.update(
			        connection -> {
			        		int i = 1;
			        		String sql;
			        		if(_codigo == null){
			        			sql = sqlInsertSinCodigo.toString();
			        		} else {
			        			sql = sqlInsertConCodigo.toString();
			        		}
				            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
							ps.setLong(i++, _usuarioNi);
							ps.setString(i++, _sessionId);
					        ps.setLong(i++, _tipoNi);
					        if(_codigo != null){
					        	ps.setString(i++, _codigo);
					        }
							return ps;
			            }
			    		, keyHolder);
			if(x > 0){
				if(keyHolder.getKey() == null){
					throw new BloqueoDAOException(DAOException.SQLERRCODE_NEXTNI, "Error generando la sequencia ");
				}
				return getBloqueo(keyHolder.getKey().longValue());
			} else {
				return null;
			}
		} catch (DataAccessException e) {
			throw new BloqueoDAOException(DAOException.SQLERRCODE_INSERT, e.getMessage());
		}
	}

	@Override
	public int finalizar(long _tipoNi, String _codigo, Long _usuarioNi) throws BloqueoDAOException {
		try {
			int x = this.jdbcTemplate.update(
			        connection -> {
			        		int i = 1;
				            PreparedStatement ps = connection.prepareStatement(sqlUpdateFinBloqueo.toString());
					        ps.setLong(i++, _tipoNi);
					        if(_codigo == null){
					        	ps.setString(i++, BloqueoDAOImpCons.BD_CODIGO_DEFAULT_VALUE);
					        } else {
					        	ps.setString(i++, _codigo);
					        }
							ps.setLong(i++, _usuarioNi);
							return ps;
			            }
			   );
			return x;
		} catch (DataAccessException e) {
			throw new BloqueoDAOException(DAOException.SQLERRCODE_INSERT, e.getMessage());
		}
	}
	
//	@Override
//	public int finalizar(Long _tipoNi, Long _usuarioNi) throws BloqueoDAOException {
//		try {
//			int x = this.jdbcTemplate.update(
//			        connection -> {
//			        		int i=1;
//				            PreparedStatement ps = connection.prepareStatement(sqlUpdateFinBloqueoTipoUsuario.toString());
//					        ps.setLong(i++, _tipoNi);
//							ps.setLong(i++, _usuarioNi);
//							return ps;
//			            }
//			   );
//			return x;
//		} catch (DataAccessException e) {
//			throw new BloqueoDAOException(DAOException.SQLERRCODE_INSERT, e.getMessage());
//		}
//	}
	
	@Override
	public int finalizar(long _tipoNi) throws BloqueoDAOException {
		try {
			int x = this.jdbcTemplate.update(
			        connection -> {
			        		int i = 1;
				            PreparedStatement ps = connection.prepareStatement(sqlUpdateFinBloqueoTipo.toString());
					        ps.setLong(i++, _tipoNi);
							return ps;
			            }
			   );
			return x;
		} catch (DataAccessException e) {
			throw new BloqueoDAOException(DAOException.SQLERRCODE_INSERT, e.getMessage());
		}
	}
	
	@Override
	public int finalizarAll() throws BloqueoDAOException {
		try {
			int x = this.jdbcTemplate.update(
			        connection -> {
			        		@SuppressWarnings("unused")
							int i = 1;
				            PreparedStatement ps = connection.prepareStatement(sqlUpdateFinBloqueoAll.toString());
							return ps;
			            }
			   );
			return x;
		} catch (DataAccessException e) {
			throw new BloqueoDAOException(DAOException.SQLERRCODE_INSERT, e.getMessage());
		}
	}

	@Override
	public BloqueoVO getActivo(long _tipoNi, String _codigo) throws BloqueoDAOException {
		try {
			List<BloqueoVO> datos = this.jdbcTemplate.query(sqlSelectTipoNi.toString(), 
			new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps)  
					    throws SQLException {
						int i = 1;
						ps.setLong(i++, _tipoNi);
						ps.setLong(i++, BloqueoDAOImpCons.BD_ESTADO_BLOQUEADO);
						if(_codigo == null){
							ps.setString(i++, BloqueoDAOImpCons.BD_CODIGO_DEFAULT_VALUE);
						} else {
							ps.setString(i++, _codigo);
						}
					}  
			}, new BloqueoDAOImpMapper());
			if(datos.isEmpty()){
				return null;
			} else {
				return datos.get(0);
			}
		} catch (DataAccessException e) {
			throw new BloqueoDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}
	
	@Override
	public BloqueoVO getActivo(String _tipo, String _codigo) throws BloqueoDAOException {
		try {
			List<BloqueoVO> datos = this.jdbcTemplate.query(sqlSelectId.toString(), 
			new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps)  
					    throws SQLException {
						int i = 1;
						ps.setString(i++, _tipo);
						ps.setLong(i++, BloqueoDAOImpCons.BD_ESTADO_BLOQUEADO);
						if(_codigo == null){
							ps.setString(i++, BloqueoDAOImpCons.BD_CODIGO_DEFAULT_VALUE);
						} else {
							ps.setString(i++, _codigo);
						}
					}  
			}, new BloqueoDAOImpMapper());
			if(datos.isEmpty()){
				return null;
			} else {
				return datos.get(0);
			}
		} catch (DataAccessException e) {
			throw new BloqueoDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}
	
	@Override
	public BloqueoVO getBloqueo(long _bloqueoNi) throws BloqueoDAOException {
		try {
			List<BloqueoVO> datos = this.jdbcTemplate.query(sqlSelectNi.toString(), 
			new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps)  
					    throws SQLException {
						int i = 1;
						ps.setLong(i++, _bloqueoNi);
					}  
			}, new BloqueoDAOImpMapper());
			if(datos.isEmpty()){
				return null;
			} else {
				return datos.get(0);
			}
		} catch (DataAccessException e) {
			throw new BloqueoDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}

	@Override
	public int marcarAccion(BloqueoVO _bloqueo, byte _accion) throws BloqueoDAOException {
		try {
			int x = this.jdbcTemplate.update(
		        connection -> {
		        		int i = 1;
			            PreparedStatement ps = connection.prepareStatement(sqlUpdateAccion.toString());
			            // Nuevo orden
			            ps.setByte(i++, _accion);
			            // Transporte Ni
			            ps.setLong(i++, _bloqueo.getNi());
						return ps;
		            }
			   );
			return x;
		} catch (DataAccessException e) {
			throw new BloqueoDAOException(DAOException.SQLERRCODE_UPDATE, e.getMessage());
		}
	}

	@Override
	public BloqueoVO getSavedLastUser(String _tipo, String _codigo) throws BloqueoDAOException {
		try {
			List<BloqueoVO> datos = this.jdbcTemplate.query(sqlSelectSavedLastUser.toString(), 
			new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps)  
					    throws SQLException {
						int i = 1;
						ps.setString(i++, _tipo);
						ps.setString(i++, _codigo);
						SqlUtils.setByteToPs(ps, i++, BloqueoVO.BLOQUEO_ACCION_GUARDAR);
//						ps.setString(i++, BloqueoDAOImpCons.BD_CODIGO_DEFAULT_VALUE);
					}  
			}, new BloqueoDAOImpMapper());
			if(datos.isEmpty()){
				return null;
			} else {
				return datos.get(0);
			}
		} catch (DataAccessException e) {
			throw new BloqueoDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}

}
