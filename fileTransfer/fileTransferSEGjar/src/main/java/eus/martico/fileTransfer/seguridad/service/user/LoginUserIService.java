package eus.martico.fileTransfer.seguridad.service.user;

import eus.martico.fileTransfer.seguridad.user.entity.SegurUser;

import java.util.Optional;

/**
 * User security operations like login and logout, and CRUD operations on {@link User}.
 * 
 * @author 
 *
 */
public interface LoginUserIService {

  SegurUser save(SegurUser user) throws LoginUserServiceException;
  SegurUser save(SegurUser _user, final boolean _guardarEnBD) throws LoginUserServiceException;
  
  Optional<SegurUser> find(String id);

  boolean estaLogeado(String _id);
  
  void disable(String id);
}
