package eus.martico.fileTransfer.services.login;

import org.springframework.stereotype.Service;

import eus.martico.fileTransfer.model.session.vo.LoginSessionVO;

@Service
public class LoginSessionServiceImp implements LoginSessionIService {

	@Override
	public LoginSessionVO save(LoginSessionVO _loginSession) throws LoginServiceException {
		return null;
	}

	@Override
	public Integer update(LoginSessionVO _loginSession) throws LoginServiceException {
		
		return null;
	}

	@Override
	public LoginSessionVO find(long _ni) throws LoginServiceException {
		return null;
	}

	@Override
	public LoginSessionVO findById(String _id) throws LoginServiceException {
		return null;
	}

}
