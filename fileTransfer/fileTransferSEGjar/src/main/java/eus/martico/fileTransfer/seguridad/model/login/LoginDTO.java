package eus.martico.fileTransfer.seguridad.model.login;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginDTO {

	private LoginDatosDTO loginDatos;
	private LoginTokenDTO loginToken;
	private LoginErrorDTO loginError;
	
	@JsonProperty("error")
	public boolean isError() {
		if (this.loginError!=null && 
				((this.loginError.getCodigo()!=null && this.loginError.getCodigo().length() > 0) 
						|| !StringUtils.isEmpty(this.loginError.getDescripcion()) )
				)
			return true;
		else
			return false;
	}
	
	@JsonProperty("datosLogin")
	public LoginDatosDTO getLoginDatos() {
		return loginDatos;
	}
	public void setLoginDatos(LoginDatosDTO loginDatos) {
		this.loginDatos = loginDatos;
	}
	@JsonProperty("datosToken")
	public LoginTokenDTO getLoginToken() {
		return loginToken;
	}
	public void setLoginToken(LoginTokenDTO loginToken) {
		this.loginToken = loginToken;
	}
	@JsonProperty("datosError")
	public LoginErrorDTO getLoginError() {
		return loginError;
	}
	public void setLoginError(LoginErrorDTO loginError) {
		this.loginError = loginError;
	}
}
