package eus.martico.fileTransfer.seguridad.model.login;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class LoginDatosDTO {

	@JsonIgnore
	private String ni;
	private String nif;
	private String username;
	private String nombre;
	private String[] roles;
	//private String[] almacenes;
	//private String[] transportistas;
	private String manFileNameES;
	private String manFileNameEN;
//	private String codNotario;
//	private String nomNotario;
//	private String codNotaria;
	
	public String getNi() {
		return ni;
	}
	public void setNi(String ni) {
		this.ni = ni;
	}
	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
		this.nif = nif;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
//	@JsonProperty("codnotario")
//	public String getCodNotario() {
//		return codNotario;
//	}
//	public void setCodNotario(String codNotario) {
//		this.codNotario = codNotario;
//	}
//	@JsonProperty("nomnotario")
//	public String getNomNotario() {
//		return nomNotario;
//	}
//	public void setNomNotario(String nomNotario) {
//		this.nomNotario = nomNotario;
//	}
//	@JsonProperty("codnotaria")
//	public String getCodNotaria() {
//		return codNotaria;
//	}
//	public void setCodNotaria(String codNotaria) {
//		this.codNotaria = codNotaria;
//	}
	public String[] getRoles() {
		return roles;
	}
	public void setRoles(String[] roles) {
		this.roles = roles;
	}
//	public String[] getAlmacenes() {
//		return almacenes;
//	}
//	public void setAlmacenes(String[] almacenes) {
//		this.almacenes = almacenes;
//	}
//	public String[] getTransportistas() {
//		return transportistas;
//	}
//	public void setTransportistas(String[] transportistas) {
//		this.transportistas = transportistas;
//	}
	public String getManFileNameES() {
		return manFileNameES;
	}
	public void setManFileNameES(String manFileNameES) {
		this.manFileNameES = manFileNameES;
	}
	public String getManFileNameEN() {
		return manFileNameEN;
	}
	public void setManFileNameEN(String manFileNameEN) {
		this.manFileNameEN = manFileNameEN;
	}
}
