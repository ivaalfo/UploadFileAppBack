package eus.martico.fileTransfer.model.bloqueo;

public class BloqueoControlVO {

	public BloqueoControlVO(BloqueoVO bloqueo) {
		super();
		this.bloqueo = bloqueo;
	}
//	private boolean error=false;
	private boolean isMismaSession=true;
	private boolean isMismoUsuario=false;
	private BloqueoVO bloqueo;
	
	public BloqueoVO getBloqueo() {
		return bloqueo;
	}
	public boolean isMismoUsuario() {
		return isMismoUsuario;
	}
	public void setMismoUsuario(boolean isMismoUsuario) {
		this.isMismoUsuario = isMismoUsuario;
	}
	public boolean isMismaSession() {
		return isMismaSession;
	}
	public void setMismaSession(boolean isMismaSession) {
		this.isMismaSession = isMismaSession;
	}
	public boolean isMyBloqueo() {
		return this.bloqueo!=null && this.isMismoUsuario && this.isMismaSession;
	}
}
