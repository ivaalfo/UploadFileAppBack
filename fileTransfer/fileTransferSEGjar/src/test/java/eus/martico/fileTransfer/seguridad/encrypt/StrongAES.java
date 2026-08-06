package eus.martico.fileTransfer.seguridad.encrypt;

import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
public class StrongAES 
{
    public void run() 
    {
        try 
        {
            String text = "Valor nuevo para probar 1234567890";
            String key = "Bar12345Bar12345"; // 128 bit key
            // Create key and cipher
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(text.getBytes());
            System.err.println(new String(encrypted));
            String returnValue = Base64.getEncoder().encodeToString(encrypted);
            // Valor para guardar en base de datos
            System.err.println(returnValue);
            // Camino inverso
            byte[] encryptedNew = Base64.getDecoder().decode(returnValue);
            System.err.println(encryptedNew);
            // decrypt the text
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            String decrypted = new String(cipher.doFinal(encryptedNew));
            System.err.println(decrypted);
        }
        catch(Exception e) 
        {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) 
    {
        StrongAES app = new StrongAES();
        app.run();
    }
}