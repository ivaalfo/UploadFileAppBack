package eus.martico.fileTransfer.dao.loginsession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import eus.martico.fileTransfer.dao.BaseGenDAO;
import eus.martico.fileTransfer.dao.DAOException;
import eus.martico.fileTransfer.model.session.vo.LoginSessionVO;
import eus.martico.fileTransfer.sql.SqlUtils;

@Service(value = "LoginSessionIDAO")
public class LoginSessionDAO_Imp extends BaseGenDAO<LoginSessionVO> implements LoginSessionIDAO {

	private static final Logger logger = LoggerFactory.getLogger(LoginSessionDAO_Imp.class);

	private static StringBuilder sqlInsert = new StringBuilder();
	private static StringBuilder sqlSelect = new StringBuilder();
	private static StringBuilder sqlUpdate = new StringBuilder();
	private static StringBuilder sqlUpdateLastAccess = new StringBuilder();

	private static String TABLE_SESSION = "ENSEGTSESION";
	private static String BD_SESSIONID = "SESSIONID";
	private static String BD_ACCESSTOKEN = "ACCESSTOKEN";
	private static String BD_CREATEDATE = "CREATEDATE";
	private static String BD_LASTACCESS = "LASTACCESS";
	private static String BD_IP = "IP";
	private static String BD_USERID = "USERID";

	static {
		sqlInsert.append("INSERT INTO ");
		sqlInsert.append(TABLE_SESSION);
		sqlInsert.append(" (");
		sqlInsert.append(BD_SESSIONID);
		sqlInsert.append(", ");
		sqlInsert.append(BD_ACCESSTOKEN);
		sqlInsert.append(", ");
		sqlInsert.append(BD_CREATEDATE);
		sqlInsert.append(", ");
		sqlInsert.append(BD_LASTACCESS);
		sqlInsert.append(", ");
		sqlInsert.append(BD_IP);
		sqlInsert.append(", ");
		sqlInsert.append(BD_USERID);
		sqlInsert.append(")");
		sqlInsert.append(" VALUES (");
		sqlInsert.append(SqlUtils.sqlInterrogaciones(sqlInsert.toString()));
		sqlInsert.append(")");

		sqlSelect.append("SELECT * FROM ");
		sqlSelect.append(TABLE_SESSION);
		sqlSelect.append(" WHERE ");
		sqlSelect.append(BD_SESSIONID);
		sqlSelect.append(" = ?");

		sqlUpdate.append("UPDATE ");
		sqlUpdate.append(TABLE_SESSION);
		sqlUpdate.append(" SET ");
		sqlUpdate.append(BD_ACCESSTOKEN);
		sqlUpdate.append(" = ?");
		sqlUpdate.append(", ");
		sqlUpdate.append(BD_LASTACCESS);
		sqlUpdate.append(" = ?");
		sqlUpdate.append(", ");
		sqlUpdate.append(BD_IP);
		sqlUpdate.append(" = ?");
		sqlUpdate.append(", ");
		sqlUpdate.append(BD_USERID);
		sqlUpdate.append(" = ?");
		sqlUpdate.append(" WHERE ");
		sqlUpdate.append(BD_SESSIONID);
		sqlUpdate.append(" = ?");

		sqlUpdateLastAccess.append("UPDATE ");
		sqlUpdateLastAccess.append(TABLE_SESSION);
		sqlUpdateLastAccess.append(" SET ");
		sqlUpdateLastAccess.append(BD_LASTACCESS);
		sqlUpdateLastAccess.append(" = ?");
		sqlUpdateLastAccess.append(" WHERE ");
		sqlUpdateLastAccess.append(BD_SESSIONID);
		sqlUpdateLastAccess.append(" = ?");

	}

	public LoginSessionVO save(LoginSessionVO sessionVo) throws DAOException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = this.jdbcTemplate.getDataSource().getConnection();
			ps = conn.prepareStatement(sqlInsert.toString());
			int i = 1;
			ps.setString(i++, sessionVo.getSessionToken());
			ps.setString(i++, sessionVo.getAccessToken());
			ps.setTimestamp(i++, java.sql.Timestamp.valueOf(sessionVo.getCreateDateTime()));
			ps.setTimestamp(i++, java.sql.Timestamp.valueOf(sessionVo.getLastAccess()));
			ps.setString(i++, sessionVo.getIP());
			ps.setString(i++, sessionVo.getUserId());
			@SuppressWarnings("unused")
			int x = ps.executeUpdate();
			return sessionVo;
		} catch (SQLException e) {
			DAOException de = new DAOException(e);
			if (de.isClaveDuplicada()) {
				throw new DAOException(DAOException.SQLERRCODE_CLAVEDUPLICADA, e.getMessage());
			}
			throw new DAOException(DAOException.SQLERRCODE_INSERT, e.getMessage());
		} finally {
			SqlUtils.close(ps);
			SqlUtils.close(conn);
		}
	}

	public LoginSessionVO get(String sessionToken) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		LoginSessionVO sessionVO = null;
		try {
			Timestamp dateTime = null;
			conn = this.jdbcTemplate.getDataSource().getConnection();
			ps = conn.prepareStatement(sqlSelect.toString());
			int i = 1;
			ps.setString(i++, sessionToken);
			rs = ps.executeQuery();
			if (rs.next()) {
				sessionVO = new LoginSessionVO();
				sessionVO.setSessionToken(rs.getString(BD_SESSIONID));
				sessionVO.setAccessToken(rs.getString(BD_ACCESSTOKEN));
				dateTime = rs.getTimestamp(BD_CREATEDATE);
				if (dateTime != null)
					sessionVO.setCreateDateTime(dateTime.toLocalDateTime());
				dateTime = rs.getTimestamp(BD_LASTACCESS);
				if (dateTime != null)
					sessionVO.setLastAccess(dateTime.toLocalDateTime());
				sessionVO.setUserId(rs.getString(BD_USERID));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SqlUtils.close(rs);
			SqlUtils.close(ps);
			SqlUtils.close(conn);
		}
		return sessionVO;
	}

	@Override
	public Integer updateLastAccess(String _sessionToken, LocalDateTime _lastAccess) throws DAOException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = this.jdbcTemplate.getDataSource().getConnection();
			ps = conn.prepareStatement(sqlUpdateLastAccess.toString());
			int i = 1;
			ps.setTimestamp(i++, java.sql.Timestamp.valueOf(_lastAccess));
			ps.setString(i++, _sessionToken);
			int x = ps.executeUpdate();
			return x;
		} catch (SQLException e) {
			logger.error("getErrorCode=", e);
		} finally {
			SqlUtils.close(ps);
			SqlUtils.close(conn);
		}
		return null;
	}

	@Override
	public Integer update(LoginSessionVO sessionVo) throws DAOException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = this.jdbcTemplate.getDataSource().getConnection();
			ps = conn.prepareStatement(sqlUpdate.toString());
			int i = 1;
			ps.setString(i++, sessionVo.getAccessToken());
			ps.setTimestamp(i++, java.sql.Timestamp.valueOf(sessionVo.getLastAccess()));
			ps.setString(i++, sessionVo.getIP());
			ps.setString(i++, sessionVo.getUserId());
			ps.setString(i++, sessionVo.getSessionToken());
			int x = ps.executeUpdate();
			return x;
		} catch (SQLException e) {
			logger.error("getErrorCode=", e);
		} finally {
			SqlUtils.close(ps);
			SqlUtils.close(conn);
		}
		return null;
	}

}
