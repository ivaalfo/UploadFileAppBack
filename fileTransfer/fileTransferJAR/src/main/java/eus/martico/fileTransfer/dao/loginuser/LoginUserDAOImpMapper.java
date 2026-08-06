package eus.martico.fileTransfer.dao.loginuser;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eus.martico.fileTransfer.model.loginuser.vo.LoginUserVO;
import eus.martico.fileTransfer.sql.SqlUtils;

public class LoginUserDAOImpMapper implements RowMapper<LoginUserVO> {
	   public LoginUserVO mapRow(ResultSet rs, int rowNum) throws SQLException {
			LoginUserVO userVO = new LoginUserVO();
			userVO.setNi(SqlUtils.getLongFromRs(rs, LoginUserDAOImpCons.BD_USERNI));
			userVO.setUsername(rs.getString(LoginUserDAOImpCons.BD_USERNAME));
			userVO.setCreateDate(SqlUtils.getLocalDateTimeFromRs(rs, LoginUserDAOImpCons.BD_CREATEDATE));
			return userVO;
	   }
	}