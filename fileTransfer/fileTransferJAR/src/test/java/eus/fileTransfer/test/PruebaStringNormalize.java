package eus.fileTransfer.test;

import java.text.Normalizer;

public class PruebaStringNormalize {

	public static void main(String[] args) {

		String original = "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝßàáâãäåæçèéêëìíîïðñòóôõöøùúûüýÿó";
		String cadenaNormalize = Normalizer.normalize(original, Normalizer.Form.NFD);   
		String cadenaSinAcentos = cadenaNormalize.replaceAll("[^\\p{ASCII}]", "");
		System.out.println("Resultado: " + cadenaSinAcentos);
		System.exit(0);
	}

}
