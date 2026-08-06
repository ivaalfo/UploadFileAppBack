package eus.martico.fileTransfer.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @author Martico S.L.
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public class CryptographyGenerator {

    public String generateMD2(String input, int _lon) throws NoSuchAlgorithmException {
//    	return generateString(input, "MD2", 32);
        return generateString(input, "MD2", _lon);
    }

    public String generateMD5(String input, int _lon) throws NoSuchAlgorithmException {
//    	return generateString(input, "MD5", 32);
        return generateString(input, "MD5", _lon);
    }

    public String generateSHA1(String input, int _lon) throws NoSuchAlgorithmException {
//    	return generateString(input, "SHA-1", 40);
        return generateString(input, "SHA-1", _lon);
    }

    public String generateSHA256(String input, int _lon) throws NoSuchAlgorithmException {
//    	return generateString(input, "SHA-256", 64);
        return generateString(input, "SHA-256", _lon);
    }

    public String generateSHA512(String input, int _lon) throws NoSuchAlgorithmException {
//        return generateString(input, "SHA-512", 128);
        return generateString(input, "SHA-512", _lon);
    }

    public String generateSHA384(String input, int _lon) throws NoSuchAlgorithmException {
//        return generateString(input, "SHA-384", 96);
        return generateString(input, "SHA-384", _lon);
    }

    private static String generateString(String input, String algorithm, int minLength) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        byte[] bytes = messageDigest.digest(input.getBytes());
        BigInteger integer = new BigInteger(1, bytes);
        String result = integer.toString(16);
        while (result.length() < minLength) {
            result = "0" + result;
        }
        return result;
    }

}
