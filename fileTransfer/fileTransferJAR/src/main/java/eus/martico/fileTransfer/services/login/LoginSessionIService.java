package eus.martico.fileTransfer.services.login;

import eus.martico.fileTransfer.model.session.vo.LoginSessionVO;

public interface LoginSessionIService {

	  LoginSessionVO save(LoginSessionVO _loginSession) throws LoginServiceException;
	  
	  Integer update(LoginSessionVO _loginSession) throws LoginServiceException;

	  LoginSessionVO find(long _ni) throws LoginServiceException;

	  LoginSessionVO findById(String _id) throws LoginServiceException;

}
