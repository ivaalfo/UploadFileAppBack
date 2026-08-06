package eus.martico.fileTransfer.test.login;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eus.martico.fileTransfer.seguridad.model.login.LoginDTO;
import eus.martico.fileTransfer.seguridad.model.login.LoginDatosDTO;
import eus.martico.fileTransfer.seguridad.model.login.LoginErrorDTO;
import eus.martico.fileTransfer.seguridad.model.login.LoginTokenDTO;

public class TestLoginDTO {

	public static void main(String[] args) throws JsonProcessingException {
		LoginDTO login = new LoginDTO();
		LoginErrorDTO loginError = new LoginErrorDTO();
		LoginDatosDTO loginDatos = new LoginDatosDTO();
		LoginTokenDTO loginToken = new LoginTokenDTO();
		
//		loginDatos.setCodNotaria("codNotaria");
//		loginDatos.setCodNotario("codNotario");
//		loginDatos.setNomNotario("Nombre Notario");
		loginDatos.setNi("NI");
		loginDatos.setNif("NIF");
		loginDatos.setNombre("Nombre Completo");
		loginDatos.setUsername("Username");
		loginDatos.setUsername("Username");
		login.setLoginDatos(loginDatos);
		
		loginToken.setJWToken("JWTokenasdjhaksdhkasdhkahskdhaksd");
		loginToken.setRefreshToken("refreshTokenaksdjlskdjlasjdlajlds");
		login.setLoginToken(loginToken);
		
		loginError.setCodigo("2030");
		loginError.setDescripcion("descripcion del error");
		login.setLoginError(loginError);
		
		//JSONObject jsonObject = new JSONObject(login);
		ObjectMapper mapper = new ObjectMapper();
		String jsonWriter = mapper.writerWithDefaultPrettyPrinter()
				 .writeValueAsString(login);
		
		System.out.println(jsonWriter);
		     
		//System.out.println(jsonObject.toString());
		
	}

}
