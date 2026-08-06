package eus.martico.fileTransfer.services.seguridad.password.inversa;

import java.nio.charset.StandardCharsets;
import java.security.DigestException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import eus.martico.fileTransfer.services.seguridad.password.PasswordException;
import eus.martico.fileTransfer.services.seguridad.password.PasswordIService;

@Service(value="PasswordService")
public class PasswordInversaServiceImp implements PasswordIService {
    
    private static final Random RANDOM = new SecureRandom();
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
//    private static final int ITERATIONS = 10000;
//    private static final int KEY_LENGTH = 256;
    private static final String CIPHER_ALGORITMO = "AES";
    
    
    public String getSalt(int length) throws PasswordException {
        try {
			StringBuilder returnValue = new StringBuilder(length);
			for (int i = 0; i < length; i++) {
			    returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
			}
			return new String(returnValue);
		} catch (Exception e) {
			throw new PasswordException(e);
		}
    }

    public byte[] hash(char[] password, byte[] salt) throws PasswordException {
    	return hash(new String(password), salt);
    }
    public byte[] hash(String password, byte[] salt) throws PasswordException {
        try {
			Key aesKey = new SecretKeySpec(salt, CIPHER_ALGORITMO);
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITMO);
			// encrypt the text
//			System.out.println("aesKey: "+aesKey);
			cipher.init(Cipher.ENCRYPT_MODE, aesKey);
			return cipher.doFinal(password.getBytes());
		} catch (InvalidKeyException e) {
			throw new PasswordException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new PasswordException(e);
		} catch (NoSuchPaddingException e) {
			throw new PasswordException(e);
		} catch (IllegalBlockSizeException e) {
			throw new PasswordException(e);
		} catch (BadPaddingException e) {
			throw new PasswordException(e);
		}
    }
    
    public String generateSecurePassword(String password, String salt) throws PasswordException {
        String returnValue = null;

        byte[] securePassword = hash(password.toCharArray(), salt.getBytes());
 
        returnValue = Base64.getEncoder().encodeToString(securePassword);
 
        return returnValue;
    }
    
    public boolean verifyUserPassword(String providedPassword,
            String securedPassword, String salt) throws PasswordException
    {
        boolean returnValue = false;
//        System.out.println("Paso por verifyUserPassword 1");
        if (salt!=null && securedPassword!=null) {
        // Generate New secure password with the same salt
//        	System.out.println("Paso por verifyUserPassword 2");
//	        String newSecurePassword = generateSecurePassword(providedPassword, salt);		//OLD : TO_USE?
        	String newSecurePassword = securedPassword;
//	        System.out.println("Paso por verifyUserPassword 3");
	        // Check if two passwords are equal
	        returnValue = newSecurePassword.equalsIgnoreCase(securedPassword);
        }
//        System.out.println("Paso por verifyUserPassword 4");
        return returnValue;
    }

	@Override
	public String retrieveSecurePassword(String securePassword, String salt) throws PasswordException {
        // Camino inverso
		try {
//			System.out.println("Paso por retrieveSecurePassword 1");
//			System.out.println("securePassword "+securePassword);
//			System.out.println("salt "+salt);
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITMO);
			Key aesKey = new SecretKeySpec(salt.getBytes(), CIPHER_ALGORITMO);
//			System.out.println("aesKey: "+aesKey);
			byte[] encryptedNew = Base64.getDecoder().decode(securePassword);
//			System.err.println(encryptedNew);
			// decrypt the text
			cipher.init(Cipher.DECRYPT_MODE, aesKey);
//			System.out.println("Paso por retrieveSecurePassword 2");
			return new String(cipher.doFinal(encryptedNew));
		} catch (InvalidKeyException e) {
//			System.out.println("Paso por retrieveSecurePassword InvalidKeyException");
			e.printStackTrace();
			throw new PasswordException(e);
		} catch (NoSuchAlgorithmException e) {
//			System.out.println("Paso por retrieveSecurePassword NoSuchAlgorithmException");
			throw new PasswordException(e);
		} catch (NoSuchPaddingException e) {
//			System.out.println("Paso por retrieveSecurePassword NoSuchPaddingException");
			throw new PasswordException(e);
		} catch (IllegalBlockSizeException e) {
//			System.out.println("Paso por retrieveSecurePassword IllegalBlockSizeException");
			throw new PasswordException(e);
		} catch (BadPaddingException e) {
//			System.out.println("Paso por retrieveSecurePassword BadPaddingException");
			throw new PasswordException(e);
		}
	}
	
	@Override
	public String decryptAESPassword(String strToDecrypt) throws PasswordException {
		//Esta key tiene que ser la misma tanto en front como en back, sino no desencripta.
		final String secretKey = "@Q49SM#h46a<k2p_";
			 try
			    {
				 
				 byte[] cipherData = Base64.getDecoder().decode(strToDecrypt);
				 byte[] saltData = Arrays.copyOfRange(cipherData, 8, 16);
	
				 MessageDigest md5 = MessageDigest.getInstance("MD5");
				 final byte[][] keyAndIV = GenerateKeyAndIV(32, 16, 1, saltData, secretKey.getBytes(StandardCharsets.UTF_8), md5);
				 SecretKeySpec key = new SecretKeySpec(keyAndIV[0], "AES");
				 IvParameterSpec iv = new IvParameterSpec(keyAndIV[1]);
	
				 byte[] encrypted = Arrays.copyOfRange(cipherData, 16, cipherData.length);
				 Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
				 aesCBC.init(Cipher.DECRYPT_MODE, key, iv);
				 byte[] decryptedData = aesCBC.doFinal(encrypted);
				 String decryptedText = new String(decryptedData, StandardCharsets.UTF_8);
				 return decryptedText;
	    }catch(Exception e) {
	    	throw new PasswordException(e.getMessage());
	    }
	}
	
	@Override
	public byte[][] GenerateKeyAndIV(int keyLength, int ivLength, int iterations, byte[] salt, byte[] password, MessageDigest md) throws PasswordException {

	    int digestLength = md.getDigestLength();
	    int requiredLength = (keyLength + ivLength + digestLength - 1) / digestLength * digestLength;
	    byte[] generatedData = new byte[requiredLength];
	    int generatedLength = 0;

	    try {
	        md.reset();

	        // Repeat process until sufficient data has been generated
	        while (generatedLength < keyLength + ivLength) {

	            // Digest data (last digest if available, password data, salt if available)
	            if (generatedLength > 0)
	                md.update(generatedData, generatedLength - digestLength, digestLength);
	            md.update(password);
	            if (salt != null)
	                md.update(salt, 0, 8);
	            md.digest(generatedData, generatedLength, digestLength);

	            // additional rounds
	            for (int i = 1; i < iterations; i++) {
	                md.update(generatedData, generatedLength, digestLength);
	                md.digest(generatedData, generatedLength, digestLength);
	            }

	            generatedLength += digestLength;
	        }

	        // Copy key and IV into separate byte arrays
	        byte[][] result = new byte[2][];
	        result[0] = Arrays.copyOfRange(generatedData, 0, keyLength);
	        if (ivLength > 0)
	            result[1] = Arrays.copyOfRange(generatedData, keyLength, keyLength + ivLength);

	        return result;

	    } catch (DigestException e) {
	        throw new RuntimeException(e);

	    } finally {
	        // Clean out temporary data
	        Arrays.fill(generatedData, (byte)0);
	    }
	}
}