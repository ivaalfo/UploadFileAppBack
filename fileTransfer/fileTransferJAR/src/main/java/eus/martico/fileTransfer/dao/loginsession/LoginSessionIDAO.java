package eus.martico.fileTransfer.dao.loginsession;

import java.time.LocalDateTime;

import eus.martico.fileTransfer.dao.DAOException;
import eus.martico.fileTransfer.model.session.vo.LoginSessionVO;

public interface LoginSessionIDAO {
	
	LoginSessionVO save(LoginSessionVO sessionVo) throws DAOException;
	
	LoginSessionVO get(String sessionToken) throws DAOException;
	
	Integer update(LoginSessionVO sessionVo) throws DAOException;	
	
	Integer updateLastAccess(String _sessionToken, LocalDateTime _lastAccess) throws DAOException;

}
