package eus.martico.fileTransfer.model;

public interface IMyException {

	String getCodigoError();
//	void setCodigoError(String codigoError);
	String getMsgError();
//	void setMsgError(String msgError);
	String getCampo();
//	void setCampo(String campo);

	boolean isClaveDuplicada();
	boolean isViolatesForeignKey();
	boolean isDataTruncation();

}
