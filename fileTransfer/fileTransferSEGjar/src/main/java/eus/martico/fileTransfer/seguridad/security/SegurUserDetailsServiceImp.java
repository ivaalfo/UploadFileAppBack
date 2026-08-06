package eus.martico.fileTransfer.seguridad.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import eus.martico.fileTransfer.config.FileTransferConfiguration;
import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.model.usuario.UsuarioVO;
import eus.martico.fileTransfer.seguridad.user.entity.SegurUser;
import eus.martico.fileTransfer.seguridad.user.entity.SegurUser.SegurUserBuilder;
//import eus.martico.fileTransfer.services.seguridad.password.PasswordException;
//import eus.martico.fileTransfer.services.seguridad.password.PasswordIService;
import eus.martico.fileTransfer.services.usuario.UsuarioIService;
import eus.martico.fileTransfer.services.usuario.UsuarioServiceException;

@Service
public class SegurUserDetailsServiceImp implements UserDetailsService {
	
	protected static final Logger logger = LoggerFactory.getLogger(SegurUserDetailsServiceImp.class);
	
	@Autowired
	private UsuarioIService usuarioService;

//	@Autowired
//	private PasswordIService passowrdService;		//OLD : DELETE?

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

	UsuarioVO usuario = null;
	String text = null;

	try {
		if (usuario==null)
			usuario = usuarioService.findById(username);
			logger.info("loadUserByUsername :: Username: "+username);
		if (usuario!=null) {
//			if (usuario.getSecurePassword()!=null){		//OLD : TO_USE?
			if (usuario.getW2contras()!=null){
				if (FileTransferConfiguration.getBooProperty(FileTransferConstantes.PROPERTY_KEY_LOGIN_MODO_MOCK, false)) {
//					if (usuario.getVisiblePassword()==null)		//OLD : TO_USE?
//						usuario.setVisiblePassword(FileTransferConfiguration.getProperty(FileTransferConstantes.PROPERTY_KEY_LOGIN_MODO_MOCK_PASSWORD));
//					logger.info("Paso por SegurUserDetailsServiceImp 2.2.1 "+username+" "+usuario.getVisiblePassword());
//					text = usuario.getVisiblePassword();	//OLD : TO_USE?
					text = usuario.getW2contras();
				} else {
//					text = passowrdService.retrieveSecurePassword(usuario.getSecurePassword(), usuario.getPassSalt());	//OLD : TO_USE?
					text = usuario.getW2contras();
					logger.info("loadUserByUsername :: Clinom: "+usuario.getClinom());
					logger.info("loadUserByUsername :: Rol: "+usuario.getRol());
				} 
			}		
		}
	} catch (UsuarioServiceException e) {
		throw new UsernameNotFoundException("loadUserByUsername :: User not found.", e);
	}
	
	SegurUserBuilder builder = null;
    if (usuario != null) {
      builder = SegurUser.builder().username(username);
      builder.password(text==null?"":text);
      builder.ni(usuario.getW2usuky());
      builder.nif(usuario.getClicif());
      builder.roles(usuario.getRoles());
    } else {
    	throw new UsernameNotFoundException("loadUserByUsername :: User not found.");
    }
    
    logger.info("loadUserByUsername :: End");
    return builder.build();
  }
	  
}
