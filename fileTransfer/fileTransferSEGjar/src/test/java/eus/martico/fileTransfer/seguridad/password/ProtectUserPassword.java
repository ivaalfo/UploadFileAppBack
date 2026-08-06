package eus.martico.fileTransfer.seguridad.password;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eus.martico.fileTransfer.services.seguridad.password.PasswordException;
import eus.martico.fileTransfer.services.seguridad.password.PasswordIService;
import eus.martico.fileTransfer.services.seguridad.password.noinversa.PasswordNoInversaServiceImp;

public class ProtectUserPassword {
	
	protected static Logger logger = LoggerFactory.getLogger(ProtectUserPassword.class);
	
    public static void main(String[] args)
    {
        String myPassword = "myPassword12345";

		PasswordIService passwordService = new PasswordNoInversaServiceImp();
        
        // Protect user's password. The generated value can be stored in DB.
        try {
            // Generate Salt. The generated value can be stored in DB. 
            String salt = passwordService.getSalt(30);
			String mySecurePassword = passwordService.generateSecurePassword(myPassword, salt);
			
			// Print out protected password 
			logger.info("My secure password = " + mySecurePassword);
			logger.info("Salt value = " + salt);
		} catch (PasswordException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
    }
}