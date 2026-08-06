package eus.martico.fileTransfer.model.loginuser.vo;

import java.time.LocalDateTime;

public class LoginUserVO {
	
	private Long ni;
	private String username;
	private LocalDateTime createDate;
	
	public LocalDateTime getCreateDate() {
		return createDate;
	}
	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}
	public Long getNi() {
		return ni;
	}
	public void setNi(Long ni) {
		this.ni = ni;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}
