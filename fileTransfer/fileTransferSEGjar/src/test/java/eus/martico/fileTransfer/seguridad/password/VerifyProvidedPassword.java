 package eus.martico.fileTransfer.seguridad.password;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eus.martico.fileTransfer.services.seguridad.password.PasswordException;
import eus.martico.fileTransfer.services.seguridad.password.PasswordIService;
import eus.martico.fileTransfer.services.seguridad.password.noinversa.PasswordNoInversaServiceImp;

public class VerifyProvidedPassword {
	
	protected static Logger logger = LoggerFactory.getLogger(VerifyProvidedPassword.class);
	
    public static void main(String[] args)
    {
        // User provided password to validate
        String providedPassword = "myPassword123";
        
        // Encrypted and Base64 encoded password read from database
        String securePassword = "HhaNvzTsVYwS/x/zbYXlLOE3ETMXQgllqrDaJY9PD/U=";
        
        // Salt value stored in database 
        String salt = "EqdmPh53c9x33EygXpTpcoJvc4VXLK";
        
        PasswordIService passwordService = new PasswordNoInversaServiceImp();
        
        try {
			boolean passwordMatch = passwordService.verifyUserPassword(providedPassword, securePassword, salt);
			
			if(passwordMatch) {
				logger.info("Provided user password " + providedPassword + " is correct.");
			} else {
				logger.info("Provided password is incorrect");
			}
		} catch (PasswordException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

    }
}