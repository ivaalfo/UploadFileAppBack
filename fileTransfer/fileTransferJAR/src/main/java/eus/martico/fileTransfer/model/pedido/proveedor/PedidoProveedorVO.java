package eus.martico.fileTransfer.model.pedido.proveedor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.model.vo.BaseBDVO;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@SuppressWarnings("serial")
public class PedidoProveedorVO extends BaseBDVO implements Serializable {
    
	private static final String entidadTipo = "PedidoProveedorVO";
	
	public PedidoProveedorVO(){
		super(entidadTipo);
	}
	
	public PedidoProveedorVO(String _entidadTipo){
		super(_entidadTipo);
	}
	
	private Long proveedorKy;
	private String proveedor;
	private String proveedorUser;
	
	private String facNumber;
	private Double facAmount;
	
	private String track;
	private String expediente;
	
	//CHAPUZAS HEREDADAS VARIAS: F_CARGA de tipo CHARACTER en FCTPDNPRV
	private String fechaCarga;
	private String refCarga;
	
	//CHAPUZAS HEREDADAS VARIAS: F_DESCARGA de tipo CHARACTER en FCTPDNPRV
	private String fechaDescarga;
	private String refDescarga;
	
	private String origen;
	private String destino;
	private Long numPalets;
	private String matriculaCamion;
	private String matriculaRemolque;
	
	private String org;
	private Long hasCMR;
	private Long hasFAC;
	private Long hasOTR;
	private Long estado;
	
	private String motivoRechazo;
	private Long anotaciones;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATE_FORMAT)
	private LocalDate fechaEntReal;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonFormat(pattern=FileTransferConstantes.PROCEDURE_DATE_FORMAT)
	private LocalDate fechaSubidaCMR;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonFormat(pattern=FileTransferConstantes.PROCEDURE_DATE_FORMAT)
	private LocalDate fechaSubidaFAC;
	
	//Para los grupajes
	private boolean isGrupaje;
	private String grupTR;
	private String grupNum;
	
	//Campo para ordenación interna en BACKend (NO se serializa hacia el FRONT)
	private transient LocalDateTime fechaSubidaCMRcompleta;
	
	
	/********** GETTERS Y SETTERS **********/
	@Override
	public Long getNi() {
		return super.getNi();
	}
	@Override
	public void setNi(Long ni) {
		super.setNi(ni);
	}
	
	public Long getUsuarioNi() {
		return super.getNi();
	}
	public void setUsuarioNi(Long ni) {
		super.setNi(ni);
	}

	public Long getProveedorKy() {
		return proveedorKy;
	}
	public void setProveedorKy(Long proveedorKy) {
		this.proveedorKy = proveedorKy;
	}
	
	public String getProveedor() {
		return proveedor;
	}
	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}
	
	public String getProveedorUser() {
		return proveedorUser;
	}
	public void setProveedorUser(String proveedorUser) {
		this.proveedorUser = proveedorUser;
	}
	
	public String getTrack() {
		return track;
	}
	public void setTrack(String track) {
		this.track = track;
	}

	public String getFacNumber() {
		return facNumber;
	}
	public void setFacNumber(String facNumber) {
		this.facNumber = facNumber;
	}
	public Double getFacAmount() {
		return facAmount;
	}
	public void setFacAmount(Double facAmount) {
		this.facAmount = facAmount;
	}
	
	public String getExpediente() {
		return expediente;
	}
	public void setExpediente(String expediente) {
		this.expediente = expediente;
	}
	
	//CHAPUZAS HEREDADAS VARIAS: F_CARGA tipo CHARACTER en FCTPDNPRV
	public String getFechaCarga() {
		return fechaCarga;
	}
	public void setFechaCarga(String fechaCarga) {
		this.fechaCarga = fechaCarga;
	}
	
	public String getRefCarga() {
		return refCarga;
	}
	public void setRefCarga(String refCarga) {
		this.refCarga = refCarga;
	}
	
	//CHAPUZAS HEREDADAS VARIAS: F_DESCARGA tipo CHARACTER en FCTPDNPRV
	public String getFechaDescarga() {
		return fechaDescarga;
	}
	public void setFechaDescarga(String fechaDescarga) {
		this.fechaDescarga = fechaDescarga;
	}
	
	public String getRefDescarga() {
		return refDescarga;
	}
	public void setRefDescarga(String refDescarga) {
		this.refDescarga = refDescarga;
	}
	
	public String getOrigen() {
		return origen;
	}
	public void setOrigen(String origen) {
		this.origen = origen;
	}
	
	public String getDestino() {
		return destino;
	}
	public void setDestino(String destino) {
		this.destino = destino;
	}
	
	public Long getNumPalets() {
		return numPalets;
	}
	public void setNumPalets(Long numPalets) {
		this.numPalets = numPalets;
	}
	
	public String getMatriculaCamion() {
		return matriculaCamion;
	}
	public void setMatriculaCamion(String matriculaCamion) {
		this.matriculaCamion = matriculaCamion;
	}
	
	public String getMatriculaRemolque() {
		return matriculaRemolque;
	}
	public void setMatriculaRemolque(String matriculaRemolque) {
		this.matriculaRemolque = matriculaRemolque;
	}

	public String getOrg() {
		return org;
	}
	public void setOrg(String org) {
		this.org = org;
	}

	public Long getHasCMR() {
		return hasCMR;
	}
	public void setHasCMR(Long hasCMR) {
		this.hasCMR = hasCMR;
	}

	public Long getHasFAC() {
		return hasFAC;
	}
	public void setHasFAC(Long hasFAC) {
		this.hasFAC = hasFAC;
	}

	public Long getHasOTR() {
		return hasOTR;
	}
	public void setHasOTR(Long hasOTR) {
		this.hasOTR = hasOTR;
	}

	public Long getEstado() {
		return estado;
	}
	public void setEstado(Long estado) {
		this.estado = estado;
	}

	public String getMotivoRechazo() {
		return motivoRechazo;
	}
	public void setMotivoRechazo(String motivoRechazo) {
		this.motivoRechazo = motivoRechazo;
	}

	public Long getAnotaciones() {
		return anotaciones;
	}
	public void setAnotaciones(Long anotaciones) {
		this.anotaciones = anotaciones;
	}
	
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATE_FORMAT)
	public LocalDate getFechaEntReal() {
		return fechaEntReal;
	}
	public void setFechaEntReal(LocalDate fechaEntReal) {
		this.fechaEntReal = fechaEntReal;
	}
	
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATE_FORMAT)
	public LocalDate getFechaSubidaCMR() {
		return fechaSubidaCMR;
	}
	public void setFechaSubidaCMR(LocalDate fechaSubidaCMR) {
		this.fechaSubidaCMR = fechaSubidaCMR;
	}

	@JsonFormat(pattern=FileTransferConstantes.JSON_DATE_FORMAT)
	public LocalDate getFechaSubidaFAC() {
		return fechaSubidaFAC;
	}
	public void setFechaSubidaFAC(LocalDate fechaSubidaFAC) {
		this.fechaSubidaFAC = fechaSubidaFAC;
	}

	public boolean isGrupaje() {
		return isGrupaje;
	}

	public void setGrupaje(boolean isGrupaje) {
		this.isGrupaje = isGrupaje;
	}

	public String getGrupTR() {
		return grupTR;
	}

	public void setGrupTR(String grupTR) {
		this.grupTR = grupTR;
	}

	public String getGrupNum() {
		return grupNum;
	}

	public void setGrupNum(String grupNum) {
		this.grupNum = grupNum;
	}

	public LocalDateTime getFechaSubidaCMRcompleta() { 
		return fechaSubidaCMRcompleta;
	}

	public void setFechaSubidaCMRcompleta(LocalDateTime f) { 
		this.fechaSubidaCMRcompleta = f; 
	}
}
