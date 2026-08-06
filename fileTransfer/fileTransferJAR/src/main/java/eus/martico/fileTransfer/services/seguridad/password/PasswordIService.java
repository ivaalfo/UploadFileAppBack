package eus.martico.fileTransfer.services.seguridad.password;

import java.security.MessageDigest;

public interface PasswordIService {

	public static final int DEFAULT_KEY_LONG = 32;
	
    public String getSalt(int length) throws PasswordException;

    public byte[] hash(char[] password, byte[] salt) throws PasswordException ;
    
    public String generateSecurePassword(String password, String salt) throws PasswordException ;
    
    public boolean verifyUserPassword(String providedPassword, String securedPassword, String salt) throws PasswordException ;
    
    public String retrieveSecurePassword(String securePassword, String salt) throws PasswordException ;
    
    public String decryptAESPassword(String encryptedPassword) throws PasswordException;
    
    byte[][] GenerateKeyAndIV(int keyLength, int ivLength, int iterations, byte[] salt, byte[] password, MessageDigest md) throws PasswordException;

}
