package eus.martico.fileTransfer.model.tabladetablas.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

public class TablaDetalleVO implements Cloneable  {
	
	private static final Logger logger = LoggerFactory.getLogger(TablaDetalleVO.class);
			
	@JsonIgnore
	private Long 	ni;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Short 	indice;
	private String 	codigo;
//	@JsonIgnore
	private String 	clave;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String 	tipo;
	private String 	descEs;
	private String 	descEus;
	private boolean activo=true;
	
	public Long getNi() {
		return ni;
	}
	public void setNi(Long ni) {
		this.ni = ni;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getDescEs() {
		return descEs;
	}
	public void setDescEs(String desc_es) {
		this.descEs = desc_es;
	}
	public String getDescEus() {
		return descEus;
	}
	public void setDescEus(String desc_eus) {
		this.descEus = desc_eus;
	}
	public boolean isActivo() {
		return activo;
	}
	public void setActivo(boolean activo) {
		this.activo = activo;
	}
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		this.clave = clave;
	}
	public Short getIndice() {
		return indice;
	}
	public void setIndice(Short indice) {
		this.indice = indice;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public TablaDetalleVO copia() {
		TablaDetalleVO clone = new TablaDetalleVO();
		clone.setActivo(activo);
		clone.setClave(clave);
		clone.setCodigo(codigo);
		clone.setDescEs(descEs);
		clone.setDescEus(descEus);
		clone.setIndice(indice);
		clone.setNi(ni);
		clone.setTipo(tipo);
        return clone;
	}
	
	@Override
	public TablaDetalleVO clone() {
		try {
			return (TablaDetalleVO)super.clone();
		} catch (CloneNotSupportedException e) {
			logger.error("super.clone CloneNotSupportedException "+e.getMessage());
			return this.copia();
		}
	}
}
