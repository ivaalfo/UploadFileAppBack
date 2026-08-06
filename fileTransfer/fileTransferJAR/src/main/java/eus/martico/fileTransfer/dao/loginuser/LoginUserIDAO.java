package eus.martico.fileTransfer.dao.loginuser;

import eus.martico.fileTransfer.dao.DAOException;
import eus.martico.fileTransfer.model.loginuser.vo.LoginUserVO;

public interface LoginUserIDAO {
	
	LoginUserVO alta(LoginUserVO _userVo) throws DAOException;
	
	LoginUserVO get(String _userId) throws DAOException;
	
}
