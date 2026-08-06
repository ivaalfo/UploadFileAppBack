package eus.martico.fileTransfer.model.vo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import eus.martico.fileTransfer.model.error.vo.ErrorVO;
import eus.martico.fileTransfer.model.mensaje.vo.MensajeVO;

/**
 * 
 * @author Martico S.L.
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public class ValidateDTO<T extends EntityVO> {

	T datos;
	List<ErrorVO> errores; 
	List<MensajeVO> mensajes;
	
	public ValidateDTO() {
		super();
	}
	
	public ValidateDTO(T datos) {
		super();
		this.datos = datos;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public String getEntidadTipo(){
		if(datos != null){
			return datos.getEntidadTipo();
		} else {
			return null;
		}
	}
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public List<ErrorVO> getErrores() {
		return errores;
	}
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public List<MensajeVO> getMensajes() {
		return mensajes;
	}
	
	@JsonInclude(JsonInclude.Include.ALWAYS)
	public boolean isError() {
		return ((this.errores == null) || (this.errores.isEmpty()) ? false : true);
	}

	public boolean addError(ErrorVO _error) {
		if(_error == null){
			return false;
		}
		if(errores == null){
			errores = new ArrayList<ErrorVO>();
		}
		_error.setEntidad(this.getEntidadTipo());
		return errores.add(_error);
	}
	
	public boolean addAllError(List<ErrorVO> _errores) {
		if(_errores == null){
			return false;
		}
		if(errores == null){
			errores = new ArrayList<ErrorVO>();
		}
		return errores.addAll(_errores);
	}
	
	public boolean addMensaje(MensajeVO _mensaje) {
		if(_mensaje == null){
			return false;
		}
		if(mensajes == null){
			mensajes = new ArrayList<MensajeVO>();
		}
		_mensaje.setEntidad(this.getEntidadTipo());
		return mensajes.add(_mensaje);
	}

	@JsonIgnore
	public T getEntidad() {
		return this.datos;
	}
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public T getDatos() {
		return this.datos;
	}
	
	public void setDatos(T datos) {
		this.datos = datos;
	}
}
