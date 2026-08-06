package eus.martico.fileTransfer.seguridad.service.session;

import static java.util.Optional.ofNullable;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eus.martico.fileTransfer.dao.DAOException;
import eus.martico.fileTransfer.dao.loginsession.LoginSessionIDAO;
import eus.martico.fileTransfer.model.session.vo.LoginSessionVO;

/**
 * User security operations like login and logout, and CRUD operations on {@link User}.
 * 
 * @author 
 *
 */
@Service(value="LoginSessionIService")
public class UserSessionBDService implements LoginSessionIService {
	protected static final Logger logger = LoggerFactory.getLogger(UserSessionBDService.class);

	@Autowired
	LoginSessionIDAO loginSessionIDAO;
	
	@Override
	public LoginSessionVO save(LoginSessionVO userSession) throws DAOException{
		//Connection conn = null;
		//LoginSessionIDAO sessionDAO;
		try {
			/* El datasource de Session es el por defecto */
//			DataSource ds = (DataSource)ApplicationContextProvider.getBean(FileTransferConstantes.DFC_DEFAULT_DATASOURCE_ID);
//			conn = ds.getConnection();
			loginSessionIDAO.save(userSession);
			return userSession;
		} catch (DAOException ex) {
			if (ex.isClaveDuplicada()) {
				throw ex; 
			}
		}
		
		return null;
		
  }
	
  @Override
  public Optional<LoginSessionVO> find(String _id){
		//Connection conn = null;
		@SuppressWarnings("unused")
		LoginSessionIDAO loginSessionDAO;
		LoginSessionVO loginSession=null;
		try {
			/* El datasource de Session es el por defecto */
//			DataSource ds = (DataSource)ApplicationContextProvider.getBean(FileTransferConstantes.DFC_DEFAULT_DATASOURCE_ID);			
//			conn = ds.getConnection();
			loginSession = loginSessionIDAO.get(_id);
		} catch (DAOException e) {
			logger.error(this.getClass().getName()+":find", e);
		} finally {
			//SqlUtils.close(conn);
		}
		return ofNullable(loginSession);
  }
  
  @Override
  public Optional<LoginSessionVO> findBySessionToken(String sessionToken){
		//Connection conn = null;
	  LoginSessionVO loginSession=null;
		try {
			/* El datasource de Session es el por defecto */
//			DataSource ds = (DataSource)ApplicationContextProvider.getBean(FileTransferConstantes.DFC_DEFAULT_DATASOURCE_ID);			
//			conn = ds.getConnection();
			loginSession = loginSessionIDAO.get(sessionToken);;
		} catch (DAOException e) {
			logger.error(this.getClass().getName()+":findBySessionToken", e);
		} finally {
			//SqlUtils.close(conn);
		}
		return ofNullable(loginSession);
  }
  
}
