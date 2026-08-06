package eus.martico.fileTransfer.seguridad.model.login;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import eus.martico.fileTransfer.config.FileTransferConstantes;

public class LoginTokenDTO {

	LocalDateTime timeStamp = LocalDateTime.now();
	String jwToken;
	String refreshToken;
	String error;
	int    expirationMin;
	
	@JsonProperty("timestamp")
	@JsonFormat(pattern=FileTransferConstantes.JSON_DATETIME_FORMAT)
	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}
	@JsonProperty("JWT")
	public String getJWToken() {
		return jwToken;
	}
	public void setJWToken(String jwToken) {
		this.jwToken = jwToken;
	}
	@JsonProperty("refreshtoken")
	public String getRefreshToken() {
		return this.refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
//	public String getError() {
//		return error;
//	}
//	public void setError(String error) {
//		this.error = error;
//	}
	public int getExpirationMin() {
		return expirationMin;
	}
	public void setExpirationMin(int expirationMin) {
		this.expirationMin = expirationMin;
	}
}
