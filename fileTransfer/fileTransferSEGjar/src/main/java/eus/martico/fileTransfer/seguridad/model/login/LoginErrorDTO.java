package eus.martico.fileTransfer.seguridad.model.login;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LoginErrorDTO {
	
	private String codigo="";
	private String descripcion="";
	
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		if (descripcion!=null)
			this.descripcion = descripcion.trim();
		else
			this.descripcion = descripcion;
	}

}
