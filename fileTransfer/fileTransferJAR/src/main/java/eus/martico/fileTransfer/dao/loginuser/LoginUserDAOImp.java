package eus.martico.fileTransfer.dao.loginuser;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import eus.martico.fileTransfer.dao.BaseGenDAO;
import eus.martico.fileTransfer.dao.DAOException;
import eus.martico.fileTransfer.model.loginuser.vo.LoginUserVO;
import eus.martico.fileTransfer.sql.SqlUtils;

@Service(value = "LoginUserIDAO")
public class LoginUserDAOImp extends BaseGenDAO<LoginUserVO> implements LoginUserIDAO {
	
	private static StringBuilder sqlInsert;
	private static StringBuilder sqlSelect;
	
	static {
		List<String> camposInsert = Arrays.asList(
				LoginUserDAOImpCons.BD_USERNAME,
				LoginUserDAOImpCons.BD_CREATEDATE
				);
		sqlInsert = SqlUtils.generarSqlInsert(LoginUserDAOImpCons.BD_TABLA_USERSESSION, camposInsert);

		sqlSelect = SqlUtils.generarSelectById(LoginUserDAOImpCons.BD_TABLA_USERSESSION, LoginUserDAOImpCons.BD_USERNAME);
		
	}

	private static final String SQLINSERT = sqlInsert.toString();
	private static final String SQLSELECT = sqlSelect.toString();
	
	@Override
	public LoginUserVO alta(final LoginUserVO _userVo) throws LoginUserDAOException {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
			int x = this.jdbcTemplate.update(
			        connection -> {
			        		int i=1;
				            PreparedStatement ps = connection.prepareStatement(SQLINSERT, Statement.RETURN_GENERATED_KEYS);
							ps.setString(i++, _userVo.getUsername());
							SqlUtils.setLocalDateTimeToPs(ps, i++, _userVo.getCreateDate());
							return ps;
			            }
			    		, keyHolder);
			
			if(x > 0) {
				if(keyHolder.getKey() == null){
					throw new LoginUserDAOException(DAOException.SQLERRCODE_NEXTNI,	"Error generando la sequencia ");
				}
				_userVo.setNi(keyHolder.getKey().longValue());
			}
			return _userVo;
		} catch (DataAccessException e) {
			throw new LoginUserDAOException(DAOException.SQLERRCODE_INSERT, e.getMessage());
		}
	}

	public LoginUserVO get(String _userId) {
		return this.findByIdBase(SQLSELECT, new LoginUserDAOImpMapper(), _userId);
	}

}
