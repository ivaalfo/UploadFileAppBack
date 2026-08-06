package eus.martico.fileTransfer.model.bloqueo;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import eus.martico.fileTransfer.config.FileTransferConfiguration;
import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.model.usuario.UsuarioVO;
import eus.martico.fileTransfer.model.vo.EntityVO;

@SuppressWarnings("serial")
public class BloqueoVO extends EntityVO implements Serializable {
	
	public static final String BLOQUEO_OK = "00";
	public static final String BLOQUEO_ERROR_YA_ESTABA_BLOQUEADO = "01";
	public static final String BLOQUEO_ERROR_FINALIZANDO = "02";
	public static final String BLOQUEO_ERROR_USUARIO_NO_PROPIETARIO = "03";
	public static final String BLOQUEO_ERROR_SESSION_NO_PROPIETARIA = "04";
	public static final String BLOQUEO_ERROR_NO_HAY_BLOQUEO_ACTIVO = "05";
	
	public static final byte BLOQUEO_ACCION_GUARDAR = 1;
	
	private static final Integer TIMER_MINUTOS_DELAY = FileTransferConfiguration.getIntProperty(FileTransferConstantes.PROPERTY_KEY_PROGRAMADOS_BLOQUEO_TIMEOUT);

	@JsonIgnore
	private Long ni;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATETIME_FORMAT)
	private LocalDateTime inicio;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATETIME_FORMAT)
	private LocalDateTime fin;
//	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private UsuarioVO usuario = new UsuarioVO();
	@JsonIgnore
	private String sessionId;
	@JsonIgnore
	private Long tipoNi;
	private String tipoCod;
	private String codigo;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long estado;
	private int timeoutMilliseconds = (TIMER_MINUTOS_DELAY == null ? 3 : TIMER_MINUTOS_DELAY.intValue())*60*1000;

	private Byte accion;
		
	public BloqueoVO() {
		super("Bloqueo");
	}

	public Long getNi() {
		return ni;
	}
	public void setNi(Long ni) {
		this.ni = ni;
	}
	public LocalDateTime getInicio() {
		return inicio;
	}
	public void setInicio(LocalDateTime inicio) {
		this.inicio = inicio;
	}
	public LocalDateTime getFin() {
		return fin;
	}
	public void setFin(LocalDateTime fin) {
		this.fin = fin;
	}
	public Long getTipoNi() {
		return tipoNi;
	}
	public void setTipoNi(Long tipoNi) {
		this.tipoNi = tipoNi;
	}
	public String getTipoCod() {
		return tipoCod;
	}
	public void setTipoCod(String tipoCod) {
		this.tipoCod = tipoCod;
	}
	public Long getEstado() {
		return estado;
	}
	public void setEstado(Long estado) {
		this.estado = estado;
	}
	public String getUsername() {
		return usuario.getUsername();
	}
	@JsonIgnore
	public Long getUsuarioNi() {
		return this.usuario.getNi();
	}
	public void setUsuarioNi(Long _ni) {
		this.usuario.setNi(_ni);
	}
	public void setUsername(String username) {
		usuario.setUsername(username);
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public int getTimeoutMilliseconds() {
		return timeoutMilliseconds;
	}
	public void setTimeoutMilliseconds(int timeoutMilliseconds) {
		this.timeoutMilliseconds = timeoutMilliseconds;
	}
	public Byte getAccion() {
		return accion;
	}
	public void setAccion(Byte accion) {
		this.accion = accion;
	}

}
