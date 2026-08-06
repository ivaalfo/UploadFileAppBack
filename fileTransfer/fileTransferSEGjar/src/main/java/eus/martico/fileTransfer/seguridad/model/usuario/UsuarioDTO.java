package eus.martico.fileTransfer.seguridad.model.usuario;

import java.util.List;

public final class UsuarioDTO {
	
	public UsuarioDTO() {
		super();
	}

	private String sub;

	private String domain;

	private String acr;

	private List<String> amr;

	private String name;

	private String dni;

	private String givenName;

	private String familyName;

	private String surname1;

	private String surname2;

	private String country;

	private String birthdate;

	private String email;

	private String issuer;

	private String personStatus;

	private String organization;

	private String cif;

	private String notBefore;

	private String notAfter;

	private String policyIdentifier;

	private Object externalInfo;

	private String serialNumber;

//	private List<String> keyUsage;
	private String keyUsage;

	private String subject;

	private String tsl;

	private String registerType;

	private String feaOwner;

	private String pais;

	private String firmaCualificada;

	private String tipoAfirma;

	private String legalPersonSemanticsIdentifier;

	private String naturalPersonSemanticsIdentifier;

	private String sign_identitie;
	
	private String authnFlow;
	
//	private String codNotaria;
	
	private String jsonUserInfoResponsePrettyPrint;
	
	public String getSub() {
		return sub;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getAcr() {
		return acr;
	}

	public void setAcr(String acr) {
		this.acr = acr;
	}

	public List<String> getAmr() {
		return amr;
	}

	public void setAmr(List<String> amr) {
		this.amr = amr;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getSurname1() {
		return surname1;
	}

	public void setSurname1(String surname1) {
		this.surname1 = surname1;
	}

	public String getSurname2() {
		return surname2;
	}

	public void setSurname2(String surname2) {
		this.surname2 = surname2;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getPersonStatus() {
		return personStatus;
	}

	public void setPersonStatus(String personStatus) {
		this.personStatus = personStatus;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public String getNotBefore() {
		return notBefore;
	}

	public void setNotBefore(String notBefore) {
		this.notBefore = notBefore;
	}

	public String getNotAfter() {
		return notAfter;
	}

	public void setNotAfter(String notAfter) {
		this.notAfter = notAfter;
	}

	public String getPolicyIdentifier() {
		return policyIdentifier;
	}

	public void setPolicyIdentifier(String policyIdentifier) {
		this.policyIdentifier = policyIdentifier;
	}

	public Object getExternalInfo() {
		return externalInfo;
	}

	public void setExternalInfo(Object externalInfo) {
		this.externalInfo = externalInfo;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

//	public List<String> getKeyUsage() {
//		return keyUsage;
//	}
//
//	public void setKeyUsage(List<String> keyUsage) {
//		this.keyUsage = keyUsage;
//	}
	
	public String getKeyUsage() {
		return keyUsage;
	}

	public void setKeyUsage(String keyUsage) {
		this.keyUsage = keyUsage;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTsl() {
		return tsl;
	}

	public void setTsl(String tsl) {
		this.tsl = tsl;
	}

	public String getRegisterType() {
		return registerType;
	}

	public void setRegisterType(String registerType) {
		this.registerType = registerType;
	}

	public String getFeaOwner() {
		return feaOwner;
	}

	public void setFeaOwner(String feaOwner) {
		this.feaOwner = feaOwner;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public String getFirmaCualificada() {
		return firmaCualificada;
	}

	public void setFirmaCualificada(String firmaCualificada) {
		this.firmaCualificada = firmaCualificada;
	}

	public String getTipoAfirma() {
		return tipoAfirma;
	}

	public void setTipoAfirma(String tipoAfirma) {
		this.tipoAfirma = tipoAfirma;
	}

	public String getLegalPersonSemanticsIdentifier() {
		return legalPersonSemanticsIdentifier;
	}

	public void setLegalPersonSemanticsIdentifier(String legalPersonSemanticsIdentifier) {
		this.legalPersonSemanticsIdentifier = legalPersonSemanticsIdentifier;
	}

	public String getNaturalPersonSemanticsIdentifier() {
		return naturalPersonSemanticsIdentifier;
	}

	public void setNaturalPersonSemanticsIdentifier(String naturalPersonSemanticsIdentifier) {
		this.naturalPersonSemanticsIdentifier = naturalPersonSemanticsIdentifier;
	}

	public String getSign_identitie() {
		return sign_identitie;
	}

	public void setSign_identitie(String sign_identitie) {
		this.sign_identitie = sign_identitie;
	}

	public String getAuthnFlow() {
		return authnFlow;
	}

	public void setAuthnFlow(String authnFlow) {
		this.authnFlow = authnFlow;
	}

	public String getJsonUserInfoResponsePrettyPrint() {
		return jsonUserInfoResponsePrettyPrint;
	}

	public void setJsonUserInfoResponsePrettyPrint(String jsonUserInfoResponsePrettyPrint) {
		this.jsonUserInfoResponsePrettyPrint = jsonUserInfoResponsePrettyPrint;
	}

//	public String getCodNotaria() {
//		return codNotaria;
//	}
//
//	public void setCodNotaria(String codNotaria) {
//		this.codNotaria = codNotaria;
//	}
	
}
