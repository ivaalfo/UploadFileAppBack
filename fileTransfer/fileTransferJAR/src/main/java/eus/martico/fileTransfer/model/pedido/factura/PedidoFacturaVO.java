package eus.martico.fileTransfer.model.pedido.factura;

import java.time.LocalDate;

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
public class PedidoFacturaVO extends EntityVO {

	private static final String entidadTipo = "PedidoFacturaVO";

	public PedidoFacturaVO(){
		super(entidadTipo);
	}

	public PedidoFacturaVO(String _entidadTipo){
		super(_entidadTipo);
	}
	
	private String facTR;
	private String facEXP;
	
	private String facNumOriginal;
	private Long ejercicio;
	
	private Long numAsiento;
	private String facNumExcell;
	private Long procesado;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonFormat(pattern=FileTransferConstantes.PROCEDURE_DATE_FORMAT)
	private LocalDate fechaCreacion;

	
	/********** GETTERS Y SETTERS **********/
	public String getFacTR() {
		return facTR;
	}

	public void setFacTR(String facTR) {
		this.facTR = facTR;
	}

	public String getFacEXP() {
		return facEXP;
	}

	public void setFacEXP(String facEXP) {
		this.facEXP = facEXP;
	}

	public String getFacNumOriginal() {
		return facNumOriginal;
	}

	public void setFacNumOriginal(String facNumOriginal) {
		this.facNumOriginal = facNumOriginal;
	}

	public Long getEjercicio() {
		return ejercicio;
	}

	public void setEjercicio(Long ejercicio) {
		this.ejercicio = ejercicio;
	}

	public Long getNumAsiento() {
		return numAsiento;
	}

	public void setNumAsiento(Long numAsiento) {
		this.numAsiento = numAsiento;
	}

	public String getFacNumExcell() {
		return facNumExcell;
	}

	public void setFacNumExcell(String facNumExcell) {
		this.facNumExcell = facNumExcell;
	}

	public Long getProcesado() {
		return procesado;
	}

	public void setProcesado(Long procesado) {
		this.procesado = procesado;
	}
	
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATE_FORMAT)
	public LocalDate getFechaCreacion() {
		return fechaCreacion;
	}
	public void setFechaCreacion(LocalDate fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	
}
