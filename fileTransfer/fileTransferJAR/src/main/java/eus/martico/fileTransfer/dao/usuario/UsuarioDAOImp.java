package eus.martico.fileTransfer.dao.usuario;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.dao.BaseGenDAO;
import eus.martico.fileTransfer.dao.DAOException;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoProveedorDAOImpCons;
import eus.martico.fileTransfer.model.usuario.UsuarioVO;
import eus.martico.fileTransfer.model.usuario.rol.TipoRolEnum;
import eus.martico.fileTransfer.services.MyServiceContext;
import eus.martico.fileTransfer.sql.SqlUtils;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@Service(value = "UsuarioIDAO")
public class UsuarioDAOImp extends BaseGenDAO<UsuarioVO> implements UsuarioIDAO {
	
	protected static final Logger logger = LoggerFactory.getLogger(UsuarioDAOImp.class);
	
	private static StringBuilder sqlInsertUserLdusuar;
	private static StringBuilder sqlInsertUserLdusuax;
	
	private static StringBuilder sqlSelectUserByNi;
	private static StringBuilder sqlSelectUserByName;
	private static StringBuilder sqlSelectUserCliCod;
	
	private static StringBuilder sqlSelectAllMttoUsers4exter;
	private static StringBuilder sqlSelectAllMttoUsers4admin;
	
	private static StringBuilder sqlSelectRolesByName;
	private static StringBuilder sqlSelectW2optrkyByOpprov;
	
//	private static StringBuilder sqlUpdatePassword;				//OLD : TO_USE?
	private static StringBuilder sqlUpdateUserLdusuar;
	private static StringBuilder sqlUpdateUserLdusuarWithPassword;
	private static StringBuilder sqlUpdateUserLdusuax;
	
	private static StringBuilder sqlBajaLogicaUserLdusuar;
	
	private static final String SELECT_BASE = "SELECT distinct * FROM ";
	
	
	private static StringBuilder sqlSelectRoles = 
			new StringBuilder(
					"SELECT DISTINCT UR.W3USUKY, USU.W2USUARIO, UR.W3TIPO AS ROLNI, ROL.TBDETCOD AS ROLCOD "
					+ " FROM MARTICOF.LDUSUAX UR "
					+ " JOIN MARTICOF.LDUSUAR USU ON USU.W2USUKY = UR.W3USUKY "
					+ " JOIN MARTICOF.ENGNVTTROL ROL ON ROL.TBDETNI = UR.W3TIPO ");
	
	
	private static StringBuilder sqlSelectUser = 
			new StringBuilder(
					"SELECT u.W2USUKY, c.CLINOM, c.CLIDIR, c.CLICIF, c.CLICOD, u.W2USUARIO, u.W2CONTRAS, u.W2EMAIL, x.W3TIPO, x.W3USUKY, u.W2USCR, u.W2USCA, u.W2FECR, u.W2FECA "
					+ " FROM MARTICOF.LDUSUAR u "
					+ " LEFT JOIN MARTICOF.LDOPTRD o ON u.W2OPEKY = o.OPTRKY "
					+ " LEFT JOIN MARTICOF.CLIPRO c ON o.OPPROV = c.CLICOD "
					+ " LEFT JOIN MARTICOF.LDUSUAX x ON x.W3USUKY = u.W2USUKY ");
	
	
	private static StringBuilder sqlSelectAllMtto = 
			new StringBuilder(
					"SELECT u.W2USUKY, "
							+ " case when x.w3tipo in (264, 265, 287) then 'MARTICO, S.L.' else c.CLINOM end as CLINOM, " 
							+ " case when x.w3tipo in (264, 265, 287) then t.tbdetdeses else c.CLIDIR end as CLIDIR, "
							+ " case when c.CLICIF <> '' then c.CLICIF else trim(substr(c.NADA96, 3, 20)) end as CLICIF, "
							+ " c.CLICOD, u.W2USUARIO, u.W2CONTRAS, u.W2EMAIL, x.W3TIPO, x.W3USUKY, u.W2USCR, u.W2USCA, u.W2FECR, u.W2FECA, o.OPPROV "  
					+ " FROM MARTICOF.LDUSUAR u  "
					+ " LEFT JOIN MARTICOF.LDOPTRD o ON u.W2OPEKY = o.OPTRKY "  
					+ " LEFT JOIN MARTICOF.CLIPRO c ON o.OPPROV = c.CLICOD "
					+ " LEFT JOIN MARTICOF.LDUSUAX x ON x.W3USUKY = u.W2USUKY " 
					+ " LEFT JOIN MARTICOF.ENGNTTBDET t on t.tbdetni = x.w3tipo ");
	
	
	private static StringBuilder sqlSelectCoalesceOptrky = new StringBuilder(
			"SELECT COALESCE(OPTRKY, '') FROM " + UsuarioDAOImpCons.BD_TABLA_LDOPTRD);
	
	
	private static StringBuilder sqlSelectMAXusuky = new StringBuilder(
			"SELECT MAX(W2USUKY) FROM " + UsuarioDAOImpCons.BD_TABLA_LDUSUAR);
	
	
	private static final String SUBQUERY_USER_PROV = 
		    " FROM MARTICOF.LDUSUAR u "
		    + " LEFT JOIN MARTICOF.LDOPTRD o ON u.W2OPEKY = o.OPTRKY "
		    + " LEFT JOIN MARTICOF.CLIPRO c ON o.OPPROV = c.CLICOD "
		    + " INNER JOIN MARTICOF.LDUSUAX x ON x.W3USUKY = u.W2USUKY ";

	
	static {
		List<String> camposValidateCliCod = Arrays.asList(
			UsuarioDAOImpCons.BD_CLIPRO_CLICOD
		);
		
		List<String> camposUpdatePassword = Arrays.asList(
			UsuarioDAOImpCons.BD_LDUSUAR_W2CONTRAS
		);
		
		List<String> camposInsertLdusuar = new ArrayList<String>(Arrays.asList(
			UsuarioDAOImpCons.BD_LDUSUAR_W2USUKY,
			UsuarioDAOImpCons.BD_LDUSUAR_W2USUARIO,
			UsuarioDAOImpCons.BD_LDUSUAR_W2EMAIL,
			UsuarioDAOImpCons.BD_LDUSUAR_W2NUEM,
			UsuarioDAOImpCons.BD_LDUSUAR_W2ALMKY,
			UsuarioDAOImpCons.BD_LDUSUAR_W2OPEKY,
			UsuarioDAOImpCons.BD_LDUSUAR_W2USCR,
			UsuarioDAOImpCons.BD_LDUSUAR_W2USCA,
			UsuarioDAOImpCons.BD_LDUSUAR_W2FECR,
			UsuarioDAOImpCons.BD_LDUSUAR_W2FECA
		));
		camposInsertLdusuar.addAll(camposUpdatePassword);
		
		List<String> camposInsertLdusuax = new ArrayList<String>(Arrays.asList(
			UsuarioDAOImpCons.BD_LDUSUAX_W3USUKY,
			UsuarioDAOImpCons.BD_LDUSUAX_W3TIPO,
			UsuarioDAOImpCons.BD_LDUSUAX_W3CMP1,
			UsuarioDAOImpCons.BD_LDUSUAX_W3CMP2,
			UsuarioDAOImpCons.BD_LDUSUAX_W3CMP3
		));
		
		List<String> camposUpdateLdusuar = new ArrayList<String>(Arrays.asList(
			UsuarioDAOImpCons.BD_LDUSUAR_W2EMAIL,
			UsuarioDAOImpCons.BD_LDUSUAR_W2USCA,
			UsuarioDAOImpCons.BD_LDUSUAR_W2FECA	
		));
		
		List<String> camposUpdateWithPassword = new ArrayList<String>(camposUpdateLdusuar);
		camposUpdateWithPassword.addAll(camposUpdatePassword);
		
		List<String> camposUpdateLdusuax = new ArrayList<String>(Arrays.asList(
			UsuarioDAOImpCons.BD_LDUSUAX_W3TIPO
		));
		
		List<String> camposUpdateLogicalDelete = Arrays.asList(
			UsuarioDAOImpCons.BD_LDUSUAR_W2USCA,
			UsuarioDAOImpCons.BD_LDUSUAR_W2FECA
		);
		
		
		sqlSelectUserCliCod = SqlUtils.generarSelectById(UsuarioDAOImpCons.BD_TABLA_CLIPRO, camposValidateCliCod, UsuarioDAOImpCons.BD_CLIPRO_CLICOD);
		
		sqlSelectRolesByName = new StringBuilder(sqlSelectRoles);
		sqlSelectRolesByName.append(" WHERE ");
		sqlSelectRolesByName.append(UsuarioDAOImpCons.BD_LDUSUAR_W2USUARIO);
		sqlSelectRolesByName.append(" = ? ");
		
		sqlSelectUserByNi = new StringBuilder(sqlSelectUser);
		sqlSelectUserByNi.append(" WHERE ");
		sqlSelectUserByNi.append(UsuarioDAOImpCons.BD_LDUSUAR_W2USUKY);
		sqlSelectUserByNi.append(" = ? ");
		
		sqlSelectUserByName = new StringBuilder(sqlSelectUser);
		sqlSelectUserByName.append(" WHERE ");
		sqlSelectUserByName.append(UsuarioDAOImpCons.BD_LDUSUAR_W2USUARIO);
		sqlSelectUserByName.append(" = ? ");
		
		sqlSelectAllMttoUsers4admin = new StringBuilder(sqlSelectAllMtto);
		sqlSelectAllMttoUsers4admin.append(" WHERE (x."+UsuarioDAOImpCons.BD_LDUSUAX_W3TIPO);
		sqlSelectAllMttoUsers4admin.append(" IN ("+FileTransferConstantes.ROL_NUMBER_ADMIN);
		sqlSelectAllMttoUsers4admin.append(", "+FileTransferConstantes.ROL_NUMBER_INTERNO);
		sqlSelectAllMttoUsers4admin.append(", "+FileTransferConstantes.ROL_NUMBER_CONSULTA);
		sqlSelectAllMttoUsers4admin.append(", "+FileTransferConstantes.ROL_NUMBER_EXTERNO);
		sqlSelectAllMttoUsers4admin.append(", "+FileTransferConstantes.ROL_NUMBER_NO_ACCESS+") )");
		sqlSelectAllMttoUsers4admin.append(" OR o."+UsuarioDAOImpCons.BD_LDOPTRD_OPPROV+" <> '' ");
		sqlSelectAllMttoUsers4admin.append(" ORDER BY CASE x."+UsuarioDAOImpCons.BD_LDUSUAX_W3TIPO);
		sqlSelectAllMttoUsers4admin.append(" WHEN "+FileTransferConstantes.ROL_NUMBER_ADMIN+" THEN 1 ");
		sqlSelectAllMttoUsers4admin.append(" WHEN "+FileTransferConstantes.ROL_NUMBER_INTERNO+" THEN 2 ");
		sqlSelectAllMttoUsers4admin.append(" WHEN "+FileTransferConstantes.ROL_NUMBER_CONSULTA+" THEN 3 ");
		sqlSelectAllMttoUsers4admin.append(" WHEN "+FileTransferConstantes.ROL_NUMBER_NO_ACCESS+" THEN 4 ");
		sqlSelectAllMttoUsers4admin.append(" WHEN "+FileTransferConstantes.ROL_NUMBER_EXTERNO+" THEN 5 ");
		sqlSelectAllMttoUsers4admin.append(" ELSE 6 END, c."+UsuarioDAOImpCons.BD_CLIPRO_CLINOM);
		
		sqlSelectAllMttoUsers4exter = new StringBuilder(sqlSelectAllMtto);
		sqlSelectAllMttoUsers4exter.append(" WHERE ");
		sqlSelectAllMttoUsers4exter.append(UsuarioDAOImpCons.BD_LDUSUAX_W3TIPO);
		sqlSelectAllMttoUsers4exter.append(" = "+FileTransferConstantes.ROL_NUMBER_EXTERNO+" ");
		sqlSelectAllMttoUsers4exter.append(" AND ");
		sqlSelectAllMttoUsers4exter.append(UsuarioDAOImpCons.BD_LDUSUAR_W2USUARIO);
		sqlSelectAllMttoUsers4exter.append(" = ? ");
		sqlSelectAllMttoUsers4exter.append(" AND o."+UsuarioDAOImpCons.BD_LDOPTRD_OPPROV+" <> '' ");
		sqlSelectAllMttoUsers4exter.append(" ORDER BY c."+UsuarioDAOImpCons.BD_CLIPRO_CLINOM);
		
		sqlSelectW2optrkyByOpprov = new StringBuilder(sqlSelectCoalesceOptrky);
		sqlSelectW2optrkyByOpprov.append(" WHERE ");
		sqlSelectW2optrkyByOpprov.append(UsuarioDAOImpCons.BD_LDOPTRD_OPPROV);
		sqlSelectW2optrkyByOpprov.append(" = ? ");
		
		sqlInsertUserLdusuar = SqlUtils.generarSqlInsert(UsuarioDAOImpCons.BD_TABLA_LDUSUAR, camposInsertLdusuar);
		sqlInsertUserLdusuax = SqlUtils.generarSqlInsert(UsuarioDAOImpCons.BD_TABLA_LDUSUAX, camposInsertLdusuax);
		
		sqlUpdateUserLdusuar = SqlUtils.generarSqlUpdate(UsuarioDAOImpCons.BD_TABLA_LDUSUAR, camposUpdateLdusuar, UsuarioDAOImpCons.BD_LDUSUAR_W2USUKY);
		sqlUpdateUserLdusuarWithPassword = SqlUtils.generarSqlUpdate(UsuarioDAOImpCons.BD_TABLA_LDUSUAR, camposUpdateWithPassword, UsuarioDAOImpCons.BD_LDUSUAR_W2USUKY);
		sqlUpdateUserLdusuax = SqlUtils.generarSqlUpdate(UsuarioDAOImpCons.BD_TABLA_LDUSUAX, camposUpdateLdusuax, UsuarioDAOImpCons.BD_LDUSUAX_W3USUKY);
		
		sqlBajaLogicaUserLdusuar = SqlUtils.generarSqlUpdate(UsuarioDAOImpCons.BD_TABLA_LDUSUAR, camposUpdateLogicalDelete, UsuarioDAOImpCons.BD_LDUSUAR_W2USUKY);
		
	}
	
	
	@Override
	@Transactional
	public Integer altaR(final UsuarioVO _usuario) throws UsuarioDAOException {
		logger.info("altaR: ");
		logger.info("W2USUKY: {}", _usuario.getW2usuky());
		logger.info("W2USUARIO: {}", _usuario.getW2usuario());
		logger.info("W2EMAIL: {}", _usuario.getW2email());
		logger.info("W2NUEM: {}", _usuario.getW2nuem().toString());
		logger.info("W2ALMKY: {}", _usuario.getW2almky().toString());
		logger.info("CLICOD: {}", _usuario.getClicodpro());
		logger.info("W2OPEKY: {}", _usuario.getW2opeky().toString());
		logger.info("W2USCR: {}", _usuario.getW2uscr());
		logger.info("W2USCA: {}", _usuario.getW2usca());
		logger.info("W2FECR: {}", _usuario.getW2fecr());
		logger.info("W2FECA: {}", _usuario.getW2feca());
		try {
			int x = this.jdbcTemplate.update(sqlInsertUserLdusuar.toString(),
					new PreparedStatementSetter(){
					    public void setValues(PreparedStatement ps)
					            throws SQLException {
					        int i=1;
					        ps.setLong(i++, _usuario.getW2usuky());
					        ps.setString(i++, _usuario.getW2usuario());
							ps.setString(i++, _usuario.getW2email());
							ps.setLong(i++, _usuario.getW2nuem());
							ps.setLong(i++, _usuario.getW2almky());
							ps.setLong(i++, _usuario.getW2opeky());
							ps.setString(i++, _usuario.getW2uscr());
							ps.setString(i++, _usuario.getW2usca());
							SqlUtils.setLocalDateToPs(ps, i++, _usuario.getW2fecr());
							SqlUtils.setLocalDateTimeToPs(ps, i++, _usuario.getW2feca());
							ps.setString(i++, _usuario.getW2contras());
					    }
				    });
			return new Integer(x);
		} catch (DataAccessException e){
			logger.error("altaR", e);
			throw new UsuarioDAOException(DAOException.SQLERRCODE_INSERT, e.getMessage());
		}
	}
	
	@Override
	@Transactional
	public Integer altaX(final UsuarioVO _usuario) throws UsuarioDAOException {
		logger.info("altaX: ");
		logger.info("W3USUKY: {}", _usuario.getW3usuky().toString());
		logger.info("W3TIPO: {}", _usuario.getW3tipo().toString());
		logger.info("W3CMP1: {}", _usuario.getW3cmp1());
		logger.info("W3CMP2: {}", _usuario.getW3cmp2());
		logger.info("W3CMP3: {}", _usuario.getW3cmp3());
		try {
			int x = this.jdbcTemplate.update(sqlInsertUserLdusuax.toString(),
					new PreparedStatementSetter(){
					    public void setValues(PreparedStatement ps)
					    		throws SQLException {
					        int i=1;
					        ps.setLong(i++, _usuario.getW3usuky());
					        ps.setLong(i++, _usuario.getW3tipo());
					        ps.setString(i++, _usuario.getW3cmp1());
							ps.setString(i++, _usuario.getW3cmp2());
							ps.setString(i++, _usuario.getW3cmp3());
					    }
				    });
			return new Integer(x);
		} catch (DataAccessException e){
			logger.error("altaX", e);
			throw new UsuarioDAOException(DAOException.SQLERRCODE_INSERT, e.getMessage());
		}
	}
	
	@Override
	@Transactional
	public UsuarioVO modificarR(final UsuarioVO _usuario) throws UsuarioDAOException {
		logger.info("modificarR: {}", _usuario.getW2usuario());
		//Si llega _usuario.getW2contras() modificamos la W2contras
		boolean updatePass = !StringUtils.isEmpty(_usuario.getW2contras());
		logger.info("update_PASS: {}", updatePass);
		logger.info("W2EMAIL: {}", _usuario.getW2email());
		logger.info("W2USCA: {}", _usuario.getW2usca());
		logger.info("W2FECA: {}", _usuario.getW2feca());
		logger.info("W2USUKY: {}", _usuario.getW2usuky());
		String sql;
		if(updatePass){
			sql = sqlUpdateUserLdusuarWithPassword.toString();
		} else {
			sql = sqlUpdateUserLdusuar.toString();
		}
		try {
			this.jdbcTemplate.update(sql.toString(),
				new PreparedStatementSetter(){
					public void setValues(PreparedStatement ps)
					    throws SQLException {
						int i=1;
						ps.setString(i++, _usuario.getW2email());
						ps.setString(i++, _usuario.getW2usca());
						SqlUtils.setLocalDateTimeToPs(ps, i++, _usuario.getW2feca());
						if (updatePass) {
							ps.setString(i++, _usuario.getW2contras());
						}
						ps.setLong(i++, _usuario.getW2usuky());
					}
				});
			return _usuario;
		} catch (DataAccessException e) {
			logger.error("modificarR", e.getMessage());
			throw new UsuarioDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}
	
	@Override
	@Transactional
	public UsuarioVO modificarX(final UsuarioVO _usuario) throws UsuarioDAOException {
		logger.info("modificarX: {}", _usuario.getW2usuario());
		logger.info("W3TIPO: {}", _usuario.getW3tipo().toString());
		logger.info("W3USUKY: {}", _usuario.getW3usuky().toString());
		try {
			this.jdbcTemplate.update(sqlUpdateUserLdusuax.toString(),
				new PreparedStatementSetter(){
					public void setValues(PreparedStatement ps)
					    throws SQLException {
						int i=1;
						ps.setLong(i++, _usuario.getW3tipo());
						ps.setLong(i++, _usuario.getW3usuky());
					}
				});
			return _usuario;
		} catch (DataAccessException e) {
			logger.error("modificarX", e.getMessage());
			throw new UsuarioDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}
	
	@Override
	public UsuarioVO get(final long _ni) throws UsuarioDAOException {
		logger.info("get : ni : {}", _ni);
		try {
			UsuarioVO usuario = this.findBase(sqlSelectUserByNi.toString(), new UsuarioUpfDAOImpMapper(), _ni);
			
			//Añadimos los roles de cada usuario
			if(usuario != null){
				List<String> rolesUpF = getRolesFT(usuario.getW2usuario());
				if(rolesUpF != null){
					for(int i=0; i < rolesUpF.size(); i++){
						List<String> rolesName = getRolNames(rolesUpF.get(i));
						if(rolesName != null){
							usuario.setRol(rolesUpF.get(i));
							usuario.setRoles(rolesName.stream().toArray(String[]::new));
						}
					}
				}						
			}
			return usuario;
		} catch (DataAccessException e) {
			logger.error(e.getMessage());
			throw new UsuarioDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}

	
	@Override
	public UsuarioVO findById(String _id) throws UsuarioDAOException {
		logger.info("findById :: username: {}", _id);
		try {
			UsuarioVO usuario = this.findByIdBase(sqlSelectUserByName.toString(), new UsuarioUpfDAOImpMapper(), _id);
			
			//Añadimos los roles de cada usuario
			if(usuario != null){
				List<String> rolesUpF = getRolesFT(usuario.getW2usuario());
				if(rolesUpF != null){
					for(int i=0; i < rolesUpF.size(); i++){
						usuario.setRol(rolesUpF.get(i));
						usuario.setRoles(rolesUpF.stream().toArray(String[]::new));
					}
				}						
			}
			return usuario;
		} catch (DataAccessException e) {
			logger.error(e.getMessage());
			throw new UsuarioDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}
	
	
	@Override
	public List<UsuarioVO> getAllMantenimiento(MyServiceContext _context) throws UsuarioDAOException {
		logger.info("{} :: getAllMantenimiento", _context.getUsername());
		try {
			List<UsuarioVO> lista = null;
			String[] userRol = _context.getUserRoles();
			for(String role: userRol){
				if(role.equals(FileTransferConstantes.PROPERTY_KEY_ROL_EXTERNO)){
					lista = this.jdbcTemplate.query(sqlSelectAllMttoUsers4exter.toString(),
							new UsuarioUpfDAOImpMapper(), _context.getUsername());
				} else if(role.equals(FileTransferConstantes.PROPERTY_KEY_ROL_INTERNO) 
						|| role.equals(FileTransferConstantes.PROPERTY_KEY_ROL_CONSULTA)
						|| role.equals(FileTransferConstantes.PROPERTY_KEY_ROL_ADMIN)){
					lista = this.jdbcTemplate.query(sqlSelectAllMttoUsers4admin.toString(),
							new UsuarioUpfDAOImpMapper());
				}
			}
			
			if(lista!=null && !lista.isEmpty()){
				for(UsuarioVO usuario : lista){
					//Añadimos los roles de cada usuario
					if(usuario != null){
						List<String> rolesUpF = getRolesFT(usuario.getW2usuario());
						if(rolesUpF != null){
							for(int i=0; i < rolesUpF.size(); i++){
								List<String> rolesName = getRolNames(rolesUpF.get(i));
								if(rolesName != null){
									usuario.setRoles(rolesName.stream().toArray((String[]::new)));
									usuario.setRol(rolesUpF.get(i));
								}
							}
						}					
					}
				}
			}
			return lista;
		} catch (DataAccessException e) {
			logger.error(e.getMessage());
			throw new UsuarioDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());	
		}
	}
	
	
	@Override
	@Transactional
	public int baja(MyServiceContext _context, final long _userQueSeDaDeBajaNi) throws UsuarioDAOException {
		logger.info("{} : baja : ni : {}", _context.getUsername(), _userQueSeDaDeBajaNi);
		try {
			int z = 0;
			UsuarioVO userQueSeDaDeBaja = new UsuarioVO();
			userQueSeDaDeBaja.setW2usca(_context.getUsername());
			userQueSeDaDeBaja.setW2feca(LocalDateTime.now());
			userQueSeDaDeBaja.setW2usuky(_userQueSeDaDeBajaNi);
			int x = this.bajaLogicaBaseR(sqlBajaLogicaUserLdusuar.toString(), userQueSeDaDeBaja);
			
			//SIN_ACCESO
			userQueSeDaDeBaja.setW3tipo(Long.valueOf(FileTransferConstantes.ROL_NUMBER_NO_ACCESS));
			userQueSeDaDeBaja.setW3usuky(_userQueSeDaDeBajaNi);
			int y = this.bajaLogicaBaseX(sqlUpdateUserLdusuax.toString(), userQueSeDaDeBaja);
			
			if(x == 1 && y == 1){
				z = 1;
			}
			return z;
		} catch (DataAccessException e) {
			logger.error(e.getMessage());
			throw new UsuarioDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}
	
	@Override
	@Transactional
	public int reactivar(MyServiceContext _context, final long _userQueSeReactivaNi) throws UsuarioDAOException {
		logger.info("{} : reactivar : ni : {}", _context.getUsername(), _userQueSeReactivaNi);
		try {
			int z = 0;
			UsuarioVO userQueSeDaDeBaja = new UsuarioVO();
			userQueSeDaDeBaja.setW2usca(_context.getUsername());
			userQueSeDaDeBaja.setW2feca(LocalDateTime.now());
			userQueSeDaDeBaja.setW2usuky(_userQueSeReactivaNi);
			int x = this.bajaLogicaBaseR(sqlBajaLogicaUserLdusuar.toString(), userQueSeDaDeBaja);
			
			//EXTERNO
			userQueSeDaDeBaja.setW3tipo(Long.valueOf(FileTransferConstantes.ROL_NUMBER_EXTERNO));
			userQueSeDaDeBaja.setW3usuky(_userQueSeReactivaNi);
			int y = this.bajaLogicaBaseX(sqlUpdateUserLdusuax.toString(), userQueSeDaDeBaja);
			
			if(x == 1 && y == 1){
				z = 1;
			}
			return z;
		} catch (DataAccessException e) {
			logger.error(e.getMessage());
			throw new UsuarioDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}
	
	//OLD : TO_USE?
//	@Override
//	@Transactional
//	public int setPassword(final String _username, final String _password, final String _salt) throws UsuarioDAOException {
//		logger.info("setPassword : username : {}", _username);
//		try {
//			int x = this.jdbcTemplate.update(sqlUpdatePassword.toString(), 
//				new PreparedStatementSetter(){  
//					public void setValues(PreparedStatement ps)
//					    throws SQLException{  
//						int i=1;
//						ps.setString(i++, _password);
//						ps.setString(i++, _salt);
//						ps.setString(i++, _username);
//					}  
//				});
//			return x;
//		} catch (DataAccessException e) {
//			logger.error(e.getMessage());
//			throw new UsuarioDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());
//		}
//	}

	
	@Override
	public boolean validateCodPro(String _clicodpro) {
		logger.info("validateCodPro : {}", _clicodpro);
		List<String> datos = this.jdbcTemplate.query(sqlSelectUserCliCod.toString(), 
				(PreparedStatementSetter) ps -> {
					int i = 1;
					ps.setString(i++, _clicodpro);
				}, new RowMapper<String>() {
					public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						return SqlUtils.getStringTrim(rs, UsuarioDAOImpCons.BD_CLIPRO_CLICOD);
					}
				}
		);
		return datos.isEmpty() ? false : true;
	}
	
	
	@Override
	public long selectW2optrky(String _opprov) throws UsuarioDAOException {
		logger.info("selectW2optrky: {}", _opprov);
		try {
			int x = this.getCampoInteger(sqlSelectW2optrkyByOpprov.toString(), _opprov);
			return x;
		} catch (DataAccessException e) {
			logger.error("selectW2optrky: {}", e.getMessage());
			throw new UsuarioDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}			
	}
	
	
	@Override
	public long selectMaxW2usuky() throws UsuarioDAOException {
		logger.info("selectMaxW2usuky:");
		try {
			int x = this.getCampoInteger(sqlSelectMAXusuky.toString());
			return x;
		} catch (DataAccessException e) {
			logger.error(e.getMessage());
			throw new UsuarioDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}			
	}
	
	
	@Override
	public String getProvByCLICOD(String _provCod) throws UsuarioDAOException {
		try {
			String sqlSelectProvName = this.getSqlProveedores(true);
			String x = this.getCampoString(sqlSelectProvName, _provCod);
			return x;
		} catch (DataAccessException e) {
			//logger.error("getProvByCLICOD: {}", e.getMessage());
			//throw new UsuarioDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());
			//Devolvemos null para que el metodo siga y la select devuelva una lista vacía
			return null;
		}
	}

	@Override
	public List<String> getCLICODbyProv(String _provUsername) {
		try {
			String sqlSelectProvCod = this.getSqlProveedores(false);
			List<String> x = this.jdbcTemplate.queryForList(sqlSelectProvCod, String.class, _provUsername);
			return x;
		} catch (DataAccessException e) {
			logger.error("getCLICODbyProv: {}", e.getMessage());
			//throw new UsuarioDAOException(DAOException.SQLERRCODE_QUERY, e.getMessage());
			//Devolvemos null para que el metodo siga y la select devuelva una lista vacía
			return new ArrayList<>();
		}
	}
		
	@Override
	public String getSqlPedidos(boolean isExterno) {
		StringBuilder sql = new StringBuilder();
		if(isExterno){
			//Lógica para EXTERNOS:
			sql.append(SELECT_BASE).append(PedidoProveedorDAOImpCons.BD_VISTA_FCTPDNPRV);
			sql.append(" WHERE PROVEEDOR = (SELECT c.clicod ").append(SUBQUERY_USER_PROV);
			sql.append(" WHERE ").append(UsuarioDAOImpCons.BD_LDUSUAR_W2USUKY).append(" = ? )");
		} else {
			//Lógica para INTERNOS/ADMIN: 
			sql.append(SELECT_BASE).append(PedidoProveedorDAOImpCons.BD_VISTA_FCTPDNPRV);
			sql.append(" WHERE 1=1 ");
		}
		
		return sql.toString();
	}

	@Override
	public String getSqlProveedores(boolean isNameSearch) {
		String campoSelect = isNameSearch ? UsuarioDAOImpCons.BD_LDUSUAR_W2USUARIO : UsuarioDAOImpCons.BD_CLIPRO_CLICOD;
		String campoWhere = isNameSearch ? UsuarioDAOImpCons.BD_CLIPRO_CLICOD : "UPPER("+UsuarioDAOImpCons.BD_LDUSUAR_W2USUARIO+")";
		if(isNameSearch){
			return "SELECT " + campoSelect + SUBQUERY_USER_PROV + " WHERE " + campoWhere + " = ?";
		} else {
			return "SELECT " + campoSelect + SUBQUERY_USER_PROV + " WHERE " + campoWhere + " LIKE ?";
		}
	}
	
//	//XXX descartado firmador
//	@Override
//	public String getSqlPedidosFirma(boolean isExterno){
//		StringBuilder sql = new StringBuilder();
//		if(isExterno){
//			//Lógica para EXTERNOS:
//			sql.append(SELECT_BASE).append(FirmadorDAOImpCons.BD_TABLA_FTCMR);
//			sql.append(" WHERE F3CLICOD = (SELECT c.clicod ").append(SUBQUERY_USER_PROV);
//			sql.append(" WHERE ").append(UsuarioDAOImpCons.BD_LDUSUAR_W2USUKY).append(" = ? )");
//		} else {
//			//Lógica para INTERNOS/ADMIN: 
//			sql.append(SELECT_BASE).append(FirmadorDAOImpCons.BD_TABLA_FTCMR);
//			sql.append(" WHERE 1=1 ");
//		}
//		return sql.toString();
//	}
	
	
	private List<String> getRolesFT(String _username) throws UsuarioDAOException {
		List<String> datos = this.jdbcTemplate.query(sqlSelectRolesByName.toString(), 
			(PreparedStatementSetter) ps -> {
				int i = 1;
				ps.setString(i++, _username);
			}, new RowMapper<String>() {
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {
					return SqlUtils.getStringTrim(rs, UsuarioDAOImpCons.BD_VROLES_USER_ROL);
				}
			}
		);
		return datos.isEmpty() ? null : datos;
	}
	
	
	private List<String> getRolNames(String w3tipo) {
		List<String> rolesUpFList = new ArrayList<String>();
		if(w3tipo != null) {
			//Convertimos el String w3tipo al objeto Enum
			TipoRolEnum tipo = TipoRolEnum.desdeKey(w3tipo);
			
			//Asignamos la descripción (o "" si default)
			String rolUpF = (tipo != null) ? tipo.getDescripcion() : "";
			
			rolesUpFList.add(rolUpF);
		}
		return rolesUpFList;
	}
	
}
