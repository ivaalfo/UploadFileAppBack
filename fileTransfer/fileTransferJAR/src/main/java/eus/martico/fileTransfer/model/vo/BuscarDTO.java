package eus.martico.fileTransfer.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 
 * @author Martico S.L.
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public class BuscarDTO<T extends EntityVO> {

	private Boolean found;
	private String foundDescripcion;
	private T datos;
	
	public BuscarDTO() {
		super();
	}
	
	public BuscarDTO(T datos) {
		super();
		this.datos = datos;
	}
	
	public boolean isFound() {
		if(found == null){
			//Si found no tiene valor devolvemos si hay datos
			return (datos!=null);
		} else {
			return found.booleanValue();
		}
	}
	
	public void setFound(boolean found) {
		this.found = found;
	}
	
	public String getEntidadTipo() {
		if(this.datos == null){
			return this.getClass().getName();
		} else {
			return this.datos.getEntidadTipo();
		}
	}

	public T getDatos() {
		return datos;
	}
	
	public void setDatos(T datos) {
		this.datos = datos;
	}
	
	@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
	public String getFoundDescripcion() {
		return foundDescripcion;
	}
	
	public void setFoundDescripcion(String foundDescripcion) {
		this.foundDescripcion = foundDescripcion;
	}

}
