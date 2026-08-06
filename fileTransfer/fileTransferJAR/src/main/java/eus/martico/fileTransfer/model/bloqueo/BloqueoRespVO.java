package eus.martico.fileTransfer.model.bloqueo;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.model.vo.EntityVO;

@SuppressWarnings("serial")
public class BloqueoRespVO extends EntityVO implements Serializable {
	
	private BloqueoVO bloqueo=null;
	
	public BloqueoRespVO(BloqueoVO _bloqueo) {
		super();
		this.bloqueo = _bloqueo;
	}
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATETIME_FORMAT)
	public LocalDateTime getInicio() {
		if (this.bloqueo==null)
			return null;
		else
		return this.bloqueo.getInicio();
	}
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATETIME_FORMAT)
	public LocalDateTime getFin() {
		if (this.bloqueo==null)
			return null;
		else
		return this.bloqueo.getFin();
	}
	public String getTipoCod() {
		if (this.bloqueo==null)
			return null;
		else
		return this.bloqueo.getTipoCod();
	}
//	public String getApellidosNombre() {
//		if (this.bloqueo==null)
//			return null;
//		else
//		return this.bloqueo.getApellidosNombre();
//	}
	public String getUsername() {
		if (this.bloqueo==null)
			return null;
		else
		return this.bloqueo.getUsername();
	}
//	public String getNombre() {
//		if (this.bloqueo==null)
//			return null;
//		else
//		return this.bloqueo.getNombre();
//	}
//	public String getApellido1() {
//		if (this.bloqueo==null)
//			return null;
//		else
//		return this.bloqueo.getApellido1();
//	}
//	public String getApellido2() {
//		if (this.bloqueo==null)
//			return null;
//		else
//		return this.bloqueo.getApellido2();
//	}
//	public String getAlmacenCod() {
//		if (this.bloqueo==null)
//			return null;
//		else
//		return this.bloqueo.getCodigo();
//	}

}
