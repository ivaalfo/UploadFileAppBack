package eus.martico.fileTransfer.model.persona;

import java.io.Serializable;

import eus.martico.fileTransfer.model.vo.EntityVO;

/**
 * 
 * @author Martico S.L.
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@SuppressWarnings("serial")
public class PersonaVO extends EntityVO implements Serializable{
	
	private Long personaNi;
	private String nif;
	private String apellido1;
	private String apellido2;
	private String nombre;
	private String telefono;
	private String email;


	public PersonaVO() {
		super();
	}
	
	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
		this.nif = nif;
	}
	public String getApellido1() {
		return apellido1;
	}
	public void setApellido1(String ape1) {
		this.apellido1 = ape1;
	}
	public String getApellido2() {
		return apellido2;
	}
	public void setApellido2(String ape2) {
		this.apellido2 = ape2;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public Long getPersonaNi() {
		return personaNi;
	}
	public void setPersonaNi(Long persona_ni) {
		this.personaNi = persona_ni;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

}
