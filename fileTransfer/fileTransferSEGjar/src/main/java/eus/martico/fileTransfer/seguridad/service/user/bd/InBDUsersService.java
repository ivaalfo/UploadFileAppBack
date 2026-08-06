package eus.martico.fileTransfer.seguridad.service.user.bd;

import static java.util.Optional.ofNullable;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eus.martico.fileTransfer.dao.DAOException;
import eus.martico.fileTransfer.dao.loginuser.LoginUserIDAO;
import eus.martico.fileTransfer.model.loginuser.vo.LoginUserVO;
import eus.martico.fileTransfer.seguridad.service.user.LoginUserIService;
import eus.martico.fileTransfer.seguridad.service.user.LoginUserServiceException;
import eus.martico.fileTransfer.seguridad.user.entity.SegurUser;

@Service(value="LoginUserIService")
public class InBDUsersService implements LoginUserIService {
	
	protected static final Logger logger = LoggerFactory.getLogger(InBDUsersService.class);

	@Autowired
	LoginUserIDAO loginUserDAO;
	
    //Mantenemos los usuarios en memoria para que funcione todo
    //pero esto habra que quitarlo
    private static Map<String, SegurUser> users = new ConcurrentHashMap<>();

	@Override
	public SegurUser save(SegurUser _user) throws LoginUserServiceException {
		return save(_user, true);
	}
	
	@Override
//	@Transactional(rollbackFor = DAOException.class)
	public SegurUser save(SegurUser _user, final boolean _guardarEnBD) throws LoginUserServiceException {
		//Cambiamos de User a LoginUserVO y lo guardamos
		LoginUserVO loginUser = new LoginUserVO();
		loginUser.setUsername(_user.getUsername());
		loginUser.setCreateDate(LocalDateTime.now());
		try {
			users.put(_user.getId(), _user);
			if (_guardarEnBD) {
				loginUserDAO.alta(loginUser);
			}
			return _user;
		} catch (DAOException ex) {
			if (ex.isClaveDuplicada()) {
				throw new LoginUserServiceException(ex); 
			}
		} finally {
			//SqlUtils.close(conn);
		}
		return null;
	}

	@Override
	public Optional<SegurUser> find(final String _id) {
		LoginUserVO loginUser = null;
		try {
			if (users.get(_id)!=null) {
				/** Podemos buscarlo en BD pero es un gasto de tiempo **/
				loginUser = new LoginUserVO();
			}
			else {
				//Solo lo buscamos en BD si no esta en memoria
				loginUser = loginUserDAO.get(_id);
				SegurUser user = new SegurUser(loginUser.getNi().toString(), loginUser.getUsername());
				//Lo metemos en el Map para que la proxima vez ya este
				users.put(user.getId(), user);
			}
			
		} catch (Exception e) {
			logger.error(this.getClass().getName()+":find", e);
		} finally {
			//SqlUtils.close(conn);
		}

		if (loginUser==null)
			return Optional.empty();
		else
			return ofNullable(users.get(_id));

	}

//	public Optional<SegurUser> findByNif(String _nif) {		
//		LoginUserVO loginUser = null;
//		try {
//			loginUser = loginUserDAO.findByNif(_nif);
//		} catch (DAOException e) {
//			logger.error(this.getClass().getName()+":findByNif", e);
//		} finally {
//			//SqlUtils.close(conn);
//		}
//		if (loginUser==null)
//			return Optional.empty();
//		else {
//			Optional<SegurUser> user = users
//				      .values()
//				      .stream()
//				      .filter(u -> Objects.equals(_nif, u.getNif()))
//				      .findFirst();
//			return user;
//		}
//	}

	@Override
	public void disable(final String _id) {
		users.remove(_id);
	}

	@Override
	public boolean estaLogeado(String _id) {
		if (users.get(_id)==null)
			return false;
		else 
			return true;
	}
}

