package eus.martico.fileTransfer.model.usuario;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.model.persona.PersonaVO;
import eus.martico.fileTransfer.model.vo.BaseBDVO;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@SuppressWarnings("serial")
public class UsuarioVO extends BaseBDVO implements Serializable {
    
	private static final String entidadTipo = "Usuario";
	
    public UsuarioVO() {
		super(entidadTipo);
		this.persona = new PersonaVO();
    }
    public UsuarioVO(PersonaVO persona) {
		super(entidadTipo);
		this.persona = persona;
	}
    
    private String username;
    private PersonaVO persona;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String visiblePassword;
    
    @JsonIgnore 
	private String securePassword;
    
	@JsonIgnore 
	private String passSalt;
	
//	OLD : POR SI EN ALGUN MOMENTO UN USER TIENE Q TENER ASIGNADO X PROVEEDORES O ALGO ASI
//	@JsonInclude(JsonInclude.Include.NON_NULL)
//	private String[] almacenes;
//	private String[] transportistas;
	
	private Long w2usuky;
	private String w2usuario;
	private String w2email;
	private String w2contras;
	private Long w2nuem;
	private Long w2almky;
	private Long w2opeky;
	private String w2uscr;
	private String w2usca;
	
//	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATE_FORMAT)
	private LocalDate w2fecr;
	
//	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATETIME_FORMAT)
	private LocalDateTime w2feca;
	
	private String clinom;
	private String clidir;
	private String clicif;
	private String clicodpro;
	
	private Long w3usuky;
	private Long w3tipo;
	private String w3cmp1;
	private String w3cmp2;	
	private String w3cmp3;
	
	private String[] roles;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String rol;	
	
	
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
	public String getNif() {
		return this.persona.getNif();
	}
	public void setNif(String nif) {
		this.persona.setNif(nif);
	}

	@JsonIgnore
	public String getPassSalt() {
		return passSalt;
	}
	public void setPassSalt(String passSalt) {
		this.passSalt = passSalt;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String[] getRoles() {
		return roles;
	}
	public void setRoles(String[] roles) {
		this.roles = roles;
	}
	@JsonIgnore
    public String getSecurePassword() {
		return securePassword;
	}
	public void setSecurePassword(String securePassword) {
		this.securePassword = securePassword;
	}
	@JsonIgnore
	public String getVisiblePassword() {
		return visiblePassword;
	}
	public void setVisiblePassword(String visiblePassword) {
		this.visiblePassword = visiblePassword;
	}
	
	//OLD_PARAMS
//	@JsonInclude(JsonInclude.Include.NON_NULL)
//	public String[] getAlmacenes() {
//		return almacenes;
//	}
//	public void setAlmacenes(String[] almacenes) {
//		this.almacenes = almacenes;
//	}
//	@JsonInclude(JsonInclude.Include.NON_NULL)
//	public String[] getTransportistas() {
//		return transportistas;
//	}
//	public void setTransportistas(String[] transportistas) {
//		this.transportistas = transportistas;
//	}

	public String getRol() {
		return this.rol;
	}
	public void setRol(String _rol) {
		this.rol = _rol;
	}
	public Long getW2usuky() {
		return w2usuky;
	}
	public void setW2usuky(Long w2usuky) {
		this.w2usuky = w2usuky;
	}
	public String getW2usuario() {
		return w2usuario;
	}
	public void setW2usuario(String w2usuario) {
		this.w2usuario = w2usuario;
	}
	public String getW2email() {
		return w2email;
	}
	public void setW2email(String w2email) {
		this.w2email = w2email;
	}
	public String getW2contras() {
		return w2contras;
	}
	public void setW2contras(String w2contras) {
		this.w2contras = w2contras;
	}
	public Long getW2nuem() {
		return w2nuem;
	}
	public void setW2nuem(Long w2nuem) {
		this.w2nuem = w2nuem;
	}
	public Long getW2almky() {
		return w2almky;
	}
	public void setW2almky(Long w2almky) {
		this.w2almky = w2almky;
	}
	public Long getW2opeky() {
		return w2opeky;
	}
	public void setW2opeky(Long w2opeky) {
		this.w2opeky = w2opeky;
	}
	public String getW2uscr() {
		return w2uscr;
	}
	public void setW2uscr(String w2uscr) {
		this.w2uscr = w2uscr;
	}
	public String getW2usca() {
		return w2usca;
	}
	public void setW2usca(String w2usca) {
		this.w2usca = w2usca;
	}
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATE_FORMAT)
	public LocalDate getW2fecr() {
		return w2fecr;
	}
	public void setW2fecr(LocalDate w2fecr) {
		this.w2fecr = w2fecr;
	}
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATETIME_FORMAT)
	public LocalDateTime getW2feca() {
		return w2feca;
	}
	public void setW2feca(LocalDateTime w2feca) {
		this.w2feca = w2feca;
	}
	public String getClinom() {
		return clinom;
	}
	public void setClinom(String clinom) {
		this.clinom = clinom;
	}
	public String getClidir() {
		return clidir;
	}
	public void setClidir(String clidir) {
		this.clidir = clidir;
	}
	public String getClicif() {
		return clicif;
	}
	public void setClicif(String clicif) {
		this.clicif = clicif;
	}
	public String getClicodpro() {
		return clicodpro;
	}
	public void setClicodpro(String clicodpro) {
		this.clicodpro = clicodpro;
	}
	public Long getW3usuky() {
		return w3usuky;
	}
	public void setW3usuky(Long w3usuky) {
		this.w3usuky = w3usuky;
	}
	public Long getW3tipo() {
		return w3tipo;
	}
	public void setW3tipo(Long w3tipo) {
		this.w3tipo = w3tipo;
	}
	public String getW3cmp1() {
		return w3cmp1;
	}
	public void setW3cmp1(String w3cmp1) {
		this.w3cmp1 = w3cmp1;
	}
	public String getW3cmp2() {
		return w3cmp2;
	}
	public void setW3cmp2(String w3cmp2) {
		this.w3cmp2 = w3cmp2;
	}
	public String getW3cmp3() {
		return w3cmp3;
	}
	public void setW3cmp3(String w3cmp3) {
		this.w3cmp3 = w3cmp3;
	}
	
	
}
