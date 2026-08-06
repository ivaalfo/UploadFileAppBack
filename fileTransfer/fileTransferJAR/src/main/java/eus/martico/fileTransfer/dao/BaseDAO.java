package eus.martico.fileTransfer.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import eus.martico.fileTransfer.model.usuario.UsuarioVO;
import eus.martico.fileTransfer.model.vo.BaseBDVO;
import eus.martico.fileTransfer.sql.SqlUtils;


public abstract class BaseDAO<T> {

	protected JdbcTemplate jdbcTemplate;
	protected DataSource dataSource;

	protected BaseDAO() {
		super();
	}

	@javax.annotation.Resource()
	public void setDataSource(DataSource _marticoDS) {
		//this.dataSource = _marticoDS;
		this.jdbcTemplate = new JdbcTemplate(_marticoDS);
	}

	protected T findBase(String sqlSelect, RowMapper<T> rowMapper, final long _ni) throws DataAccessException {
		List<T> datos = this.jdbcTemplate.query(sqlSelect, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				int i = 1;
				ps.setLong(i++, _ni);
			}
		}, rowMapper);
		return (datos.isEmpty() ? null : datos.get(0));
	}
	
	protected T findBaseByString(String sqlSelect, RowMapper<T> rowMapper, final String _ni) throws DataAccessException {
		List<T> datos = this.jdbcTemplate.query(sqlSelect, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				int i = 1;
				ps.setString(i++, _ni);
			}
		}, rowMapper);
		return (datos.isEmpty() ? null : datos.get(0));
	}
	
	protected T findByIdBase(String sqlSelect, RowMapper<T> rowMapper, final String id) throws DataAccessException {
		List<T> datos = this.jdbcTemplate.query(sqlSelect, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				int i = 1;
				ps.setString(i++, id);
			}
		}, rowMapper);
		return (datos.isEmpty() ? null : datos.get(0));
	}
	
	protected List<T> findByNiListaBase(String sqlSelect, RowMapper<T> rowMapper, final long _ni) throws DataAccessException {
		List<T> datos = this.jdbcTemplate.query(sqlSelect, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				int i = 1;
				ps.setLong(i++, _ni);
			}
		}, rowMapper);
		return datos;
	}
	
	protected List<T> findByIdListaBase(String sqlSelect, RowMapper<T> rowMapper, final String _id) throws DataAccessException {
		List<T> datos = this.jdbcTemplate.query(sqlSelect, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				int i = 1;
				ps.setString(i++, _id);
			}
		}, rowMapper);
		return datos;
	}

	protected Integer eliminarByNiBase(String sqlDelete, final long _ni) throws DataAccessException {
		int x = this.jdbcTemplate.update(sqlDelete.toString(), new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				int i = 1;
				ps.setLong(i++, _ni);
			}
		});
		return x;
	}
//
//	public Long countBaseFiltro(String sqlSelectFiltro, DAOFiltro daoFiltro) {
//		List<Long> lNumregs = this.jdbcTemplate.query(sqlSelectFiltro,
//				(PreparedStatementSetter) ps -> daoFiltro.setFiltro(ps), (RowMapper<Long>) (rs, rowNum) -> {
//					Long numregs = rs.getLong(1);
//					return numregs;
//				}
//		);
//		if (lNumregs != null && !lNumregs.isEmpty())
//			return lNumregs.get(0);
//		else
//			return null;
//	}

	public List<T> getListadoFiltroPaginacion(String sqlPaginacion, final DAOFiltro daoFiltro, RowMapper<T> rowMapper) {
		List<T> lista = this.jdbcTemplate.query(sqlPaginacion, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				daoFiltro.setFiltro(ps);
			}
		}, rowMapper);
		return lista;
	}
 
	protected int bajaLogicaBaseR(String sqlUpdate, final UsuarioVO userVO) {
		int x = this.jdbcTemplate.update(sqlUpdate, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				int i = 1;
				ps.setString(i++, userVO.getW2usca());
				SqlUtils.setLocalDateTimeToPs(ps, i++, userVO.getW2feca());
				ps.setLong(i++, userVO.getW2usuky());
			}
		});
		return x;
	}
	
	protected int bajaLogicaBaseX(String sqlUpdate, final UsuarioVO userVO) {
		int x = this.jdbcTemplate.update(sqlUpdate, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				int i = 1;
				ps.setLong(i++, userVO.getW3tipo());
				ps.setLong(i++, userVO.getW3usuky());
			}
		});
		return x;
	}
	
	protected int reactivarBase(String _sqlUpdate, final long _ni, final BaseBDVO _baseBd) {
		int x = this.jdbcTemplate.update(_sqlUpdate, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				int i = 1;
				SqlUtils.setLocalDateTimeToPs(ps, i++, null);
				ps.setLong(i++, _baseBd.getUserBaja());
				ps.setLong(i++, _ni);
			}
		});
		return x;
	}
	
	protected String getCampoString(String sqlSelect, long _ni) throws DataAccessException {
		return this.jdbcTemplate.queryForObject(sqlSelect, new Object[]{_ni}, String.class);
	}
	
	protected String getCampoString(String sqlSelect, String _ni) throws DataAccessException {
		return this.jdbcTemplate.queryForObject(sqlSelect, new Object[]{_ni}, String.class);
	}
	
	protected Integer getCampoInteger(String sqlSelect, long _ni) throws DataAccessException {
		return this.jdbcTemplate.queryForObject(sqlSelect, new Object[]{_ni}, Integer.class);
	}
	
	protected Integer getCampoInteger(String sqlSelect, String _id) throws DataAccessException {
		return this.jdbcTemplate.queryForObject(sqlSelect, new Object[]{_id}, Integer.class);
	}
	
	protected Integer getCampoInteger(String sqlSelect) throws DataAccessException {
		return this.jdbcTemplate.queryForObject(sqlSelect, Integer.class);
	}
	
}
