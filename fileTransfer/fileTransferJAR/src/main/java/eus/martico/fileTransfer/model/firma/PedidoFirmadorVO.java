package eus.martico.fileTransfer.model.firma;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.model.vo.EntityVO;

/**
 * NEW
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@SuppressWarnings("serial")
public class PedidoFirmadorVO extends EntityVO implements Serializable {

	private static final String entidadTipo = "PedidoFirmadorVO";
	
	public PedidoFirmadorVO(){
		super(entidadTipo);
	}
	
	public PedidoFirmadorVO(String _entidadTipo){
		super(_entidadTipo);
	}
	
	private String f3refPed;
	private String f3expediente;
	private String f3refCarga;
	private String f3matricula;
	private String f3remolque;
	
	private String f3cliCod;
	private String provUser;
	private String f3destCod;
	private String destUser;
	
	private String f3rutCMR;
	private String f3useCMR;
	private Integer f3estado;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATETIME_FORMAT)
	private LocalDateTime f3fechaCreacionCMR;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATETIME_FORMAT)
	private LocalDateTime f3fechaFirmaTrans;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATETIME_FORMAT)
	private LocalDateTime f3fechaFirmaDest;

	private String f3anotaMart;
	private String f3anotaTrans;
	private String f3anotaDest;
	
	
	
	/********** GETTERS Y SETTERS **********/

	public String getF3refPed() {
		return f3refPed;
	}

	public void setF3refPed(String f3refPed) {
		this.f3refPed = f3refPed;
	}

	public String getF3expediente() {
		return f3expediente;
	}

	public void setF3expediente(String f3expediente) {
		this.f3expediente = f3expediente;
	}

	public String getF3refCarga() {
		return f3refCarga;
	}

	public void setF3refCarga(String f3refCarga) {
		this.f3refCarga = f3refCarga;
	}

	public String getF3matricula() {
		return f3matricula;
	}

	public void setF3matricula(String f3matricula) {
		this.f3matricula = f3matricula;
	}

	public String getF3remolque() {
		return f3remolque;
	}

	public void setF3remolque(String f3remolque) {
		this.f3remolque = f3remolque;
	}

	public String getF3cliCod() {
		return f3cliCod;
	}

	public void setF3cliCod(String f3cliCod) {
		this.f3cliCod = f3cliCod;
	}

	public String getProvUser() {
		return provUser;
	}

	public void setProvUser(String provUser) {
		this.provUser = provUser;
	}

	public String getF3destCod() {
		return f3destCod;
	}

	public void setF3destCod(String f3desCod) {
		this.f3destCod = f3desCod;
	}

	public String getDestUser() {
		return destUser;
	}

	public void setDestUser(String destUser) {
		this.destUser = destUser;
	}

	public String getF3rutCMR() {
		return f3rutCMR;
	}

	public void setF3rutCMR(String f3rutCMR) {
		this.f3rutCMR = f3rutCMR;
	}

	public String getF3useCMR() {
		return f3useCMR;
	}

	public void setF3useCMR(String f3useCMR) {
		this.f3useCMR = f3useCMR;
	}

	public Integer getF3estado() {
		return f3estado;
	}

	public void setF3estado(Integer f3estado) {
		this.f3estado = f3estado;
	}
	
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATETIME_FORMAT)
	public LocalDateTime getF3fechaCreacionCMR() {
		return f3fechaCreacionCMR;
	}

	public void setF3fechaCreacionCMR(LocalDateTime f3fechaCreacionCMR) {
		this.f3fechaCreacionCMR = f3fechaCreacionCMR;
	}

	@JsonFormat(pattern=FileTransferConstantes.JSON_DATETIME_FORMAT)
	public LocalDateTime getF3fechaFirmaTrans() {
		return f3fechaFirmaTrans;
	}

	public void setF3fechaFirmaTrans(LocalDateTime f3fechaFirmaTrans) {
		this.f3fechaFirmaTrans = f3fechaFirmaTrans;
	}

	@JsonFormat(pattern=FileTransferConstantes.JSON_DATETIME_FORMAT)
	public LocalDateTime getF3fechaFirmaDest() {
		return f3fechaFirmaDest;
	}

	public void setF3fechaFirmaDest(LocalDateTime f3fechaFirmaDest) {
		this.f3fechaFirmaDest = f3fechaFirmaDest;
	}

	public String getF3anotaMart() {
		return f3anotaMart;
	}

	public void setF3anotaMart(String f3anotaMart) {
		this.f3anotaMart = f3anotaMart;
	}

	public String getF3anotaTrans() {
		return f3anotaTrans;
	}

	public void setF3anotaTrans(String f3anotaTrans) {
		this.f3anotaTrans = f3anotaTrans;
	}

	public String getF3anotaDest() {
		return f3anotaDest;
	}

	public void setF3anotaDest(String f3anotaDest) {
		this.f3anotaDest = f3anotaDest;
	}
	
}
