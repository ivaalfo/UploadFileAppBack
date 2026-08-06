package eus.martico.fileTransfer.dao.usuario.rol;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Service;

import eus.martico.fileTransfer.dao.BaseGenDAO;
import eus.martico.fileTransfer.dao.DAOException;
import eus.martico.fileTransfer.model.usuario.rol.UsuarioRolVO;
import eus.martico.fileTransfer.sql.SqlUtils;

@Service(value = "UsuarioRolDAO")
public class UsuarioRolDAOImp extends BaseGenDAO<UsuarioRolVO> implements UsuarioRolIDAO {
	
	protected static final Logger logger = LoggerFactory.getLogger(UsuarioRolDAOImp.class);

	private static StringBuilder sqlInsert;
//	private static StringBuilder sqlSelectByUser;
	private static StringBuilder sqlDeleteByUser;
	
	static {
		List<String> camposInsert = new ArrayList<String>(Arrays.asList(UsuarioRolDAOImpCons.BD_USERNI,
																		UsuarioRolDAOImpCons.BD_ROLNI));
		
		sqlInsert = SqlUtils.generarSqlInsert(UsuarioRolDAOImpCons.BD_TABLA_USUARIO_ROLES, camposInsert);
		
//		sqlSelectByUser = SqlUtils.generarSelectById(UsuarioRolDAOImpCons.BD_TABLA_USUARIO_ROLES, UsuarioRolDAOImpCons.BD_USERNI);
		
		sqlDeleteByUser = SqlUtils.generarSqlDelete(UsuarioRolDAOImpCons.BD_TABLA_USUARIO_ROLES, UsuarioRolDAOImpCons.BD_USERNI);
		
	}

	@Override
	public UsuarioRolVO alta(UsuarioRolVO _usuarioRol) throws UsuarioRolDAOException {
		try {
			this.jdbcTemplate.update(sqlInsert.toString(), 
				new PreparedStatementSetter() {  
				    public void setValues(PreparedStatement ps)  
				            throws SQLException{  
				        int i=1;
				        ps.setLong(i++, _usuarioRol.getUsuarioNi());
						ps.setLong(i++, _usuarioRol.getRolNi());
				    }  
			    });
			return _usuarioRol;			
		} catch (DataAccessException e) {
			logger.error(e.getMessage());
			throw new UsuarioRolDAOException(DAOException.SQLERRCODE_INSERT, e.getMessage());
		}
	}

	@Override
	public Integer eliminar(Long ni) throws UsuarioRolDAOException {
		try {
			int x = this.jdbcTemplate.update(sqlDeleteByUser.toString(), 
				new PreparedStatementSetter() {  
					public void setValues(PreparedStatement ps)  
					    throws SQLException{  
						int i=1;
						ps.setLong(i++, ni);
					}  
				});
			return new Integer(x);
		} catch (DataAccessException e) {
			logger.error(e.getMessage());
			throw new UsuarioRolDAOException(DAOException.SQLERRCODE_DELETE, e.getMessage());
		}
	}

}
