package eus.martico.fileTransfer.dao.loginsession;

import java.time.LocalDateTime;
import java.util.HashMap;

import eus.martico.fileTransfer.dao.DAOException;
import eus.martico.fileTransfer.model.session.vo.LoginSessionVO;

public class LoginSessionInMemoryDAO_Imp implements LoginSessionIDAO{

	private static HashMap<String, LoginSessionVO> sessions = new HashMap<String, LoginSessionVO>();
	
	public LoginSessionVO save(LoginSessionVO sessionVo) {
		sessions.put(sessionVo.getSessionToken(), sessionVo);
		return null;
	}

	public LoginSessionVO get(String sessionToken) {
		return sessions.get(sessionToken);
	}

	@Override
	public Integer updateLastAccess(String _sessionToken, LocalDateTime _lastAccess) throws DAOException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public Integer update(LoginSessionVO sessionVo) throws DAOException {
		// Auto-generated method stub
		return null;
	}

}
