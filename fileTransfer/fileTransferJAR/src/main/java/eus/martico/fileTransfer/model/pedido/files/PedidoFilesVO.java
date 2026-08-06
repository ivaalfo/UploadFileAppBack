package eus.martico.fileTransfer.model.pedido.files;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.model.vo.EntityVO;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@SuppressWarnings("serial")
public class PedidoFilesVO extends EntityVO {

	private String refPed;
	private String expediente;
	
	private String rutaCMR;
	private String usuSubidaCMR;
	private Integer estadoCMR;
	private String usuOkCMR;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATETIME_FORMAT)
	private LocalDateTime fechaSubidaCMR;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATETIME_FORMAT)
	private LocalDateTime fechaEstadoCMR;
	
	private String rutaFAC;
	private String usuSubidaFAC;
	private Integer estadoFAC;
	private String usuOkFAC;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATETIME_FORMAT)
	private LocalDateTime fechaSubidaFAC;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATETIME_FORMAT)
	private LocalDateTime fechaEstadoFAC;
	
	private Integer estadoFacturacion;
	
	private String rutaOTR;
	private String usuSubidaOTR;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATETIME_FORMAT)
	private LocalDateTime fechaSubidaOTR;
	
	private String motivoRechazo;
	private Integer anotaciones;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATETIME_FORMAT)
	private LocalDateTime fechaEntReal;
		
	
	public String getRefPed() {
		return refPed;
	}
	public void setRefPed(String refPed) {
		this.refPed = refPed;
	}
	public String getExpediente() {
		return expediente;
	}
	public void setExpediente(String expediente) {
		this.expediente = expediente;
	}
	public String getRutaCMR() {
		return rutaCMR;
	}
	public void setRutaCMR(String rutaCMR) {
		this.rutaCMR = rutaCMR;
	}
	public String getUsuSubidaCMR() {
		return usuSubidaCMR;
	}
	public void setUsuSubidaCMR(String usuSubidaCMR) {
		this.usuSubidaCMR = usuSubidaCMR;
	}
	public Integer getEstadoCMR() {
		return estadoCMR;
	}
	public void setEstadoCMR(Integer estadoCMR) {
		this.estadoCMR = estadoCMR;
	}
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATETIME_FORMAT)
	public LocalDateTime getFechaSubidaCMR() {
		return fechaSubidaCMR;
	}
	public void setFechaSubidaCMR(LocalDateTime fechaSubidaCMR) {
		this.fechaSubidaCMR = fechaSubidaCMR;
	}
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATETIME_FORMAT)
	public LocalDateTime getFechaEstadoCMR() {
		return fechaEstadoCMR;
	}
	public void setFechaEstadoCMR(LocalDateTime fechaEstadoCMR) {
		this.fechaEstadoCMR = fechaEstadoCMR;
	}
	public String getUsuOkCMR() {
		return usuOkCMR;
	}
	public void setUsuOkCMR(String usuOkCMR) {
		this.usuOkCMR = usuOkCMR;
	}
	public String getRutaFAC() {
		return rutaFAC;
	}
	public void setRutaFAC(String rutaFAC) {
		this.rutaFAC = rutaFAC;
	}
	public String getUsuSubidaFAC() {
		return usuSubidaFAC;
	}
	public void setUsuSubidaFAC(String usuSubidaFAC) {
		this.usuSubidaFAC = usuSubidaFAC;
	}
	public Integer getEstadoFAC() {
		return estadoFAC;
	}
	public void setEstadoFAC(Integer estadoFAC) {
		this.estadoFAC = estadoFAC;
	}
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATETIME_FORMAT)
	public LocalDateTime getFechaSubidaFAC() {
		return fechaSubidaFAC;
	}
	public void setFechaSubidaFAC(LocalDateTime fechaSubidaFAC) {
		this.fechaSubidaFAC = fechaSubidaFAC;
	}
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATETIME_FORMAT)
	public LocalDateTime getFechaEstadoFAC() {
		return fechaEstadoFAC;
	}
	public void setFechaEstadoFAC(LocalDateTime fechaEstadoFAC) {
		this.fechaEstadoFAC = fechaEstadoFAC;
	}
	public String getUsuOkFAC() {
		return usuOkFAC;
	}
	public void setUsuOkFAC(String usuOkFAC) {
		this.usuOkFAC = usuOkFAC;
	}
	public Integer getEstadoFacturacion() {
		return estadoFacturacion;
	}
	public void setEstadoFacturacion(Integer estadoFacturacion) {
		this.estadoFacturacion = estadoFacturacion;
	}
	public String getRutaOTR() {
		return rutaOTR;
	}
	public void setRutaOTR(String rutaOTR) {
		this.rutaOTR = rutaOTR;
	}
	public String getUsuSubidaOTR() {
		return usuSubidaOTR;
	}
	public void setUsuSubidaOTR(String usuSubidaOTR) {
		this.usuSubidaOTR = usuSubidaOTR;
	}
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATETIME_FORMAT)
	public LocalDateTime getFechaSubidaOTR() {
		return fechaSubidaOTR;
	}
	public void setFechaSubidaOTR(LocalDateTime fechaSubidaOTR) {
		this.fechaSubidaOTR = fechaSubidaOTR;
	}
	public String getMotivoRechazo() {
		return motivoRechazo;
	}
	public void setMotivoRechazo(String motivoRechazo) {
		this.motivoRechazo = motivoRechazo;
	}
	public Integer getAnotaciones() {
		return anotaciones;
	}
	public void setAnotaciones(Integer anotaciones) {
		this.anotaciones = anotaciones;
	}
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATETIME_FORMAT)
	public LocalDateTime getFechaEntReal() {
		return fechaEntReal;
	}
	public void setFechaEntReal(LocalDateTime fechaEntReal) {
		this.fechaEntReal = fechaEntReal;
	}
	
}
