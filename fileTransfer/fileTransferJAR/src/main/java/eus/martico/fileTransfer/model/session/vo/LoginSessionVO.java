package eus.martico.fileTransfer.model.session.vo;

import java.time.LocalDateTime;

public class LoginSessionVO {
	private String accessToken;
	private String sessionToken;
	private String userId;
	private String IP;
	private LocalDateTime createDate;
	private LocalDateTime lastAccess;
	
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getSessionToken() {
		return sessionToken;
	}
	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}
	public LocalDateTime getCreateDateTime() {
		return createDate;
	}
	public void setCreateDateTime(LocalDateTime createDate) {
		this.createDate = createDate;
	}
	public LocalDateTime getLastAccess() {
		return lastAccess;
	}
	public void setLastAccess(LocalDateTime lastAccess) {
		this.lastAccess = lastAccess;
	}
	public String getIP() {
		return IP;
	}
	public void setIP(String iP) {
		IP = iP;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

}
