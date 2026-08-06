//package eus.martico.fileTransfer.dao.configuracion;
//
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.Arrays;
//import java.util.List;
//
//import org.springframework.dao.DataAccessException;
//import org.springframework.jdbc.core.PreparedStatementSetter;
//import org.springframework.stereotype.Service;
//
//import eus.martico.fileTransfer.dao.BaseGenDAO;
//import eus.martico.fileTransfer.dao.DAOException;
//import eus.martico.fileTransfer.model.configuracion.ConfiguracionVO;
//import eus.martico.fileTransfer.sql.SqlUtils;
//
//@Service
//public class ConfiguracionDAOImp extends BaseGenDAO<ConfiguracionVO> implements ConfiguracionIDAO {
//
//	private static StringBuilder sqlSelectByCodigo;
//	private static StringBuilder sqlInsert;
//	private static StringBuilder sqlUpdate;
//
//	static {
//		List<String> camposInsert = Arrays.asList(
//				ConfiguracionDAOImpCons.BD_CODIGO,
//				ConfiguracionDAOImpCons.BD_VALOR
//			);
//		
//		sqlSelectByCodigo= SqlUtils.generarSelectById(ConfiguracionDAOImpCons.BD_TABLA_CONFIGURACION
//										, ConfiguracionDAOImpCons.BD_CODIGO);
//		sqlInsert = SqlUtils.generarSqlInsert(ConfiguracionDAOImpCons.BD_TABLA_CONFIGURACION
//													, camposInsert);
//		sqlUpdate = SqlUtils.generarSqlUpdate(ConfiguracionDAOImpCons.BD_TABLA_CONFIGURACION
//												, Arrays.asList(ConfiguracionDAOImpCons.BD_VALOR), 
//												ConfiguracionDAOImpCons.BD_CODIGO);
//		
//	}
//	
//	@Override
//	public ConfiguracionVO getValor(final String _codigo) throws ConfiguracionDAOException {
//		return this.findByIdBase(sqlSelectByCodigo.toString(), new ConfiguracionDAOImpMapper(), _codigo);
//	}
//
//	@Override
//	public int setValor(final String _codigo, final String _valor) throws ConfiguracionDAOException {
//		if (getValor(_codigo)==null) {
//			return alta( _codigo,  _valor);
//		} else {
//			return modificar(_codigo, _valor);
//		}
//	}
//
//	@Override
//	public int alta(String _codigo, String _valor) throws ConfiguracionDAOException {
//		try {
//			return this.jdbcTemplate.update(
//					connection -> {
//		        		int i=1;
//			            PreparedStatement ps = connection.prepareStatement(sqlInsert.toString(), 
//			            	Statement.RETURN_GENERATED_KEYS);
//			            ps.setString(i++, _codigo);
//			            ps.setString(i++, _valor);
//						return ps;
//		            });
//		} catch (DataAccessException e) {
//			throw new ConfiguracionDAOException(DAOException.SQLERRCODE_INSERT, e.getMessage());
//		}
//	}
//
//	@Override
//	public int modificar(String _codigo, String _valor) throws ConfiguracionDAOException {
//		try {
//			return this.jdbcTemplate.update(sqlUpdate.toString(), 
//				new PreparedStatementSetter(){  
//					public void setValues(PreparedStatement ps)  
//					    throws SQLException{
//						int i=1;
//			            ps.setString(i++, _valor);
//			            ps.setString(i++, _codigo);
//					}  
//				});
//		} catch (DataAccessException e) {
//			throw new ConfiguracionDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());
//		}
//	}
//
//}
