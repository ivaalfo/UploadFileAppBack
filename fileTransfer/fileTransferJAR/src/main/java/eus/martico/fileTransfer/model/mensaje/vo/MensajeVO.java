package eus.martico.fileTransfer.model.mensaje.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import eus.martico.fileTransfer.model.vo.EntityVO;

/**
 * 
 * @author Martico S.L.
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MensajeVO extends EntityVO {

	private static final long serialVersionUID = 1L;
	
	private Long ni;
	private String entidad;
	private String codigo;
	private Long tipoNi;
	private String campo;
	private String descripcion;
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Long referenciaNi;
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Long referenciaCod;
	
	public MensajeVO() {
		super("mensaje");
	}

	public MensajeVO(String _codigo) {
		this();
		this.codigo = _codigo;
	}
	
	public MensajeVO(String _codigo, String _descripcion) {
		this();
		this.codigo = _codigo;
		this.descripcion = _descripcion;
	}
	
	public MensajeVO(String codigo, String campo, String descripcion) {
		this();
		this.codigo = codigo;
		this.campo = campo;
		this.descripcion = descripcion;
	}

	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
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

	public Long getTipoNi() {
		return tipoNi;
	}

	public void setTipoNi(Long tipoNi) {
		this.tipoNi = tipoNi;
	}

	public String getCampo() {
		return campo;
	}

	public void setCampo(String campo) {
		this.campo = campo;
	}
	
	@JsonIgnore
	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public Long getReferenciaNi() {
		return referenciaNi;
	}

	public void setReferenciaNi(Long referenciaNi) {
		this.referenciaNi = referenciaNi;
	}

	public Long getReferenciaCod() {
		return referenciaCod;
	}

	public void setReferenciaCod(Long referenciaCod) {
		this.referenciaCod = referenciaCod;
	}

}
