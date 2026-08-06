package eus.martico.fileTransfer.model;

@SuppressWarnings("serial")
public class MyException extends Exception implements IMyException  {

	private String codigoError=null;
	private String msgError=null;
	private String campo=null;
	private boolean claveDuplicada;
	private boolean violatesForeignKey;
	private boolean dataTruncation;

	public MyException(String _msgError) {
		super(_msgError);
		this.msgError = _msgError;
	}
	public MyException(Exception _ex) {
		super(_ex);
		if (_ex instanceof IMyException) {
			IMyException iex = (IMyException)_ex;
			this.setCampo(iex.getCampo());
			this.setCodigoError(iex.getCodigoError());
			this.setMsgError(iex.getMsgError());
			this.setClaveDuplicada(iex.isClaveDuplicada());
			this.setViolatesForeignKey(iex.isViolatesForeignKey());
			this.setDataTruncation(iex.isDataTruncation());
		}		
	}
	public MyException(String _codigoError, String _msgError) {
		super();
		codigoError=_codigoError;
		msgError = _msgError;
	}
	@Override
	public String getCodigoError() {
		return codigoError;
	}

	void setCodigoError(String codigoError) {
		this.codigoError = codigoError;
	}
	@Override
	public String getMsgError() {
		return msgError;
	}

	void setMsgError(String msgError) {
		this.msgError = msgError;
	}
	@Override
	public String getCampo() {
		return campo;
	}

	void setCampo(String campo) {
		this.campo = campo;
	}
	@Override
	public boolean isClaveDuplicada() {
		return claveDuplicada;
	}
	void setClaveDuplicada(boolean claveDuplicada) {
		this.claveDuplicada = claveDuplicada;
	}
	@Override
	public boolean isViolatesForeignKey() {
		return violatesForeignKey;
	}
	void setViolatesForeignKey(boolean violatesForeignKey) {
		this.violatesForeignKey = violatesForeignKey;
	}
	@Override
	public boolean isDataTruncation() {
		return dataTruncation;
	}
	void setDataTruncation(boolean dataTruncation) {
		this.dataTruncation = dataTruncation;
	}

}
