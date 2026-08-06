package eus.martico.fileTransfer.model.error.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import eus.martico.fileTransfer.model.vo.EntityVO;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorVO extends EntityVO {

	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	
	private Long ni;
	private String entidad;
	private String codigo;
	private Long tipoNi;
	private String campo;
	private String descCampo;
	private String descripcion;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Long referenciaNi;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Long referenciaCod;
	
	public ErrorVO() {
		super("error");
	}

	public ErrorVO(String _codigo, String _descripcion) {
		this();
		this.codigo = _codigo;
		this.descripcion = _descripcion;
	}
	
	public ErrorVO(String codigo, String campo, String descripcionError) {
		this();
		this.codigo = codigo;
		this.campo = campo;
		this.descripcion = descripcionError;
	}
	public ErrorVO(String codigo, String campo, String _descCampo, String descripcionError) {
		this();
		this.codigo = codigo;
		this.campo = campo;
		this.descCampo = _descCampo;
		this.descripcion = descripcionError;
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

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public String getDescCampo() {
		return descCampo;
	}

	public void setDescCampo(String descCampo) {
		this.descCampo = descCampo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Error [codigo=");
		builder.append(codigo);
		builder.append(", campo=");
		builder.append(campo);
		builder.append(", descripcion=");
		builder.append(descripcion);
		builder.append(", referenciaCod=");
		builder.append(referenciaCod);
		builder.append("]");
		return builder.toString();
	}
	
}
