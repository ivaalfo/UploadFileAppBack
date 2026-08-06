package eus.martico.fileTransfer.model;

@SuppressWarnings("serial")
public class LoginException extends Exception {

	public static final String LOGIN_ERROR_CERTIFICADO = "01";
	public static final String LOGIN_ERROR_USERNAME = "02";
	public static final String LOGIN_ERROR_PASSWORD = "03";
	public static final String LOGIN_ERROR_NIF = "04";
	public static final String LOGIN_NOTFOUND_NIF = "05";
	public static final String LOGIN_ERROR_COD_NOTARIA = "06";
	public static final String LOGIN_NOTFOUND_NOTARIA = "07";
	public static final String LOGIN_ERROR_POLITICA = "08";
	public static final String LOGIN_REFRESH_ERROR = "09";
	
	public static final String LOGIN_EXCEPTION_91 = "91";
	public static final String LOGIN_EXCEPTION_92 = "92";
	public static final String LOGIN_EXCEPTION_93 = "93";
	public static final String LOGIN_EXCEPTION_94 = "94";
	
	public static final String LOGIN_ERROR_USERROL = "95";
	
	private String codigo;
	
	public LoginException(String _message) {
		super(_message);
	}
	public LoginException(String _codigo, Throwable cause) {
		super(cause);
		this.codigo = _codigo;
	}
	public LoginException(String _codigo, String _message) {
		super(_message);
		this.codigo = _codigo;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
}
