package eus.martico.fileTransfer.utils;

import java.text.Normalizer;

import org.springframework.util.StringUtils;


/**
 * 
 * @author Martico S.L.
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public class MyUtils {

	
	public static String quitarCaracterEspeciales(String _original) {
		if (!StringUtils.isEmpty(_original)) {
			String cadenaNormalize = Normalizer.normalize(_original, Normalizer.Form.NFD);   
			return cadenaNormalize.replaceAll("[^\\p{ASCII}]", "");
		} else {
			return _original;
		}
	}
	
}
