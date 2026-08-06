package eus.martico.fileTransfer.test.login;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.http.HttpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eus.martico.fileTransfer.seguridad.model.login.LoginDTO;
import eus.martico.fileTransfer.seguridad.model.login.LoginDatosDTO;
import eus.martico.fileTransfer.seguridad.model.login.LoginErrorDTO;
import eus.martico.fileTransfer.seguridad.model.login.LoginTokenDTO;
import lombok.NonNull;

public class TestLoginTokenControl {

	public static void main(String[] args) throws JsonProcessingException {

		
		//JSONObject jsonObject = new JSONObject(login);
		ObjectMapper mapper = new ObjectMapper();
		String jsonWriter;
		try {
			jsonWriter = mapper.writerWithDefaultPrettyPrinter()
					 .writeValueAsString(getLoginToken("1"));
			System.out.println(jsonWriter);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//System.out.println(jsonObject.toString());
		
	}

    @SuppressWarnings("unused")
	@ResponseBody 
    static ResponseEntity<?> getLoginToken(String _id) throws IOException, ServletException, HttpException {

    	LoginErrorDTO loginError = null;
		/** cargo datos usuario en sesion **/
    	@NonNull
    	//LoginSessionIService session = new UserSessionBDService();

        String sessionToken = _id;
        if (sessionToken==null) {
        	loginError = new LoginErrorDTO();
        	loginError.setDescripcion("Acceso erroneo");
			LoginDTO loginDTO = new LoginDTO();
			loginDTO.setLoginError(loginError);
			return new ResponseEntity<LoginDTO>(loginDTO, HttpStatus.BAD_REQUEST);
        }
        else {
			//Optional<LoginSessionVO> optSession = Optional.of(null);
			if (true) {
	        	loginError = new LoginErrorDTO();
	        	loginError.setDescripcion("Sesion erronea");
				LoginDTO loginDTO = new LoginDTO();
				loginDTO.setLoginError(loginError);
				return new ResponseEntity<LoginDTO>(loginDTO, HttpStatus.BAD_REQUEST);
			}
			else {
				//LoginSessionVO loginSession = optSession.get();
				return new ResponseEntity<LoginDTO>(generateTokens(), HttpStatus.OK);				
			}
        }
    }

	private static LoginDTO generateTokens() throws JsonProcessingException {
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
		return login;
		
	}

}
