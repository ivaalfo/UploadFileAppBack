package eus.martico.fileTransfer.seguridad.service.session;

import java.util.Optional;

import eus.martico.fileTransfer.dao.DAOException;
import eus.martico.fileTransfer.model.session.vo.LoginSessionVO;

/**
 * User security operations like login and logout, and CRUD operations on {@link User}.
 * 
 * @author 
 *
 */
public interface LoginSessionIService {

	LoginSessionVO save(LoginSessionVO loginSession) throws DAOException;

	Optional<LoginSessionVO> find(String id);

	Optional<LoginSessionVO> findBySessionToken(String _sessionToken);
}
