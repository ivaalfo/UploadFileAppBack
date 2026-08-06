package eus.martico.fileTransfer.seguridad.password;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eus.martico.fileTransfer.model.usuario.UsuarioVO;
import eus.martico.fileTransfer.services.seguridad.password.PasswordException;
import eus.martico.fileTransfer.services.seguridad.password.PasswordIService;
import eus.martico.fileTransfer.services.seguridad.password.inversa.PasswordInversaServiceImp;

public class SetPassword {
	
	protected static Logger logger = LoggerFactory.getLogger(SetPassword.class);
	
	public static void main(String[] args) {
		String username = args[0];
		String password = args[1];

		PasswordIService passwordService = new PasswordInversaServiceImp();
		
        // Protect user's password. The generated value can be stored in DB.
        String mySecurePassword;
		try {
			String salt = passwordService.getSalt(32);
	        
			mySecurePassword = passwordService.generateSecurePassword(password, salt);
			
			@SuppressWarnings("unused")
			boolean passwordMatch = passwordService.verifyUserPassword(password, mySecurePassword, salt);
			
			@SuppressWarnings("unused")
			String text = passwordService.retrieveSecurePassword(mySecurePassword, salt);
			
			UsuarioVO usu = new UsuarioVO();
			usu.setUsername(username);
			usu.setSecurePassword(mySecurePassword);
			usu.setPassSalt(salt);
			logger.info("username:"+username);
			logger.info("password:"+password);
			logger.info("salt:"+salt);
			logger.info("mySecurePassword:"+mySecurePassword);
		} catch (PasswordException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
	}

}
