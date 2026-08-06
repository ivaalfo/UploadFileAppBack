package eus.martico.fileTransfer.model.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author Martico S.L.
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public class EntityVO implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	
	@JsonIgnore
	private String entidadTipo;
	
	@JsonIgnore
	private Long ni;
	
	public EntityVO() {
		super();
	}

	public EntityVO(String _entidadTipo) {
		super();
		this.entidadTipo = _entidadTipo;
	}

	@JsonIgnore
	public String getEntidadTipo() {
		return entidadTipo;
	}

	public void setEntidadTipo(String entidadTipo) {
		this.entidadTipo = entidadTipo;
	}
	
	@JsonIgnore
	public Long getNi() {
		return ni;
	}

	@JsonProperty
	public void setNi(Long _ni) {
		this.ni = _ni;
	}

}
