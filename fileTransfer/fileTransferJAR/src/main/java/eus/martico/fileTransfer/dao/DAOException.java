package eus.martico.fileTransfer.dao;

import java.sql.SQLException;

import eus.martico.fileTransfer.config.FileTransferConfiguration;
import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.model.IMyException;
import eus.martico.fileTransfer.model.error.vo.ErrorVO;
import eus.martico.fileTransfer.model.vo.ValidateDTO;
import eus.martico.fileTransfer.utils.MyUtils;

/**
 * 
 * @author Martico S.L.
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public class DAOException extends SQLException implements IMyException {
	
	private static final long serialVersionUID = 7689919380968929453L;

	private static final String[] SQLERRMSG_CLAVEDUPLICADA_A = FileTransferConfiguration.getStringArray(FileTransferConstantes.PROPERTY_KEY_CLAVE_DUPLICADA_STRINGS);
	public static final String SQLERRCODE_CLAVEDUPLICADA = (SQLERRMSG_CLAVEDUPLICADA_A == null ? null : SQLERRMSG_CLAVEDUPLICADA_A[0]);
	
	private static final String[] SQLERRMSG_DATA_TRUNCATION_A = FileTransferConfiguration.getStringArray(FileTransferConstantes.PROPERTY_KEY_DATA_TRUNCATION_STRINGS);
	public static final String SQLERRMSG_DATA_TRUNCATION = (SQLERRMSG_DATA_TRUNCATION_A == null ? null : SQLERRMSG_DATA_TRUNCATION_A[0]);
	
	private static final String[] SQLERRMSG_VIOLATES_FOREIGN_KEY_A = FileTransferConfiguration.getStringArray(FileTransferConstantes.PROPERTY_KEY_VIOLATES_FOREIGN_KEY);
	public static final String SQLERRCODE_VIOLATES_FOREIGN_KEY = (SQLERRMSG_VIOLATES_FOREIGN_KEY_A == null ? null : SQLERRMSG_VIOLATES_FOREIGN_KEY_A[0]); 

	public static final String SQLERRCODE_GET_DAO = "SQL00000";
	public static final String SQLERRCODE_GETNOTARIO_DAO = "SQL00001";
	
	public static final String SQLERRCODE_NEXTNI = "SQL00010";
	public static final String SQLERRCODE_DELETE = "SQL00011";
	public static final String SQLERRCODE_QUERY = "SQL00012";
	public static final String SQLERRCODE_UPDATE = "SQL00013";
	public static final String SQLERRCODE_INSERT = "SQL00014";
	
	private String codigoError = null;
	private String msgError = null;
	private String campo;
	
//	public DAOException(String _codigoError) {
//		super();
//		codigoError=_codigoError;
//	}
	
	public DAOException(String _msgError) {
		super();
		msgError = _msgError;
	}
	
	public DAOException(String _codigoError, String _msgError) {
		super();
		codigoError = _codigoError;
		msgError = _msgError;
		if (this.isClaveDuplicada()) {
			codigoError = SQLERRCODE_CLAVEDUPLICADA;
		} else if (this.isViolatesForeignKey()) {
			codigoError = SQLERRCODE_VIOLATES_FOREIGN_KEY;
		}
	}
	
	public DAOException(Exception _ex) {
		super(_ex);
		this.msgError = _ex.getMessage();		
	}
	
	public DAOException(DAOException _ex) {
		super(_ex);
		this.codigoError = _ex.getCodigoError();
		this.msgError = _ex.getMsgError();		
	}
	
	@Override
	public String getCodigoError() {
		return codigoError;
	}
	
	@Override
	public String getMsgError() {
		return msgError;
	}

	public void setMsgError(String msgError) {
		this.msgError = msgError;
	}

	public void setCodigoError(String codigoError) {
		this.codigoError = codigoError;
	}
	
	@Override
	public String getCampo() {
		return campo;
	}
	
	public void setCampo(String campo) {
		this.campo = campo;
	}
	
	public ValidateDTO<ErrorVO> getValidate() {
		ErrorVO error = new ErrorVO(this.getCodigoError(), this.getMsgError());
		ValidateDTO<ErrorVO> valida = new ValidateDTO<ErrorVO>();
		valida.addError(error);
		return valida;
	}

	@Override
	public boolean isClaveDuplicada() {
		if (getCodigoError() != null && getCodigoError().contains(DAOException.SQLERRCODE_CLAVEDUPLICADA) ){
			return true;
		} else if (getMsgError() != null) {
			if (msgErrorContains(MyUtils.quitarCaracterEspeciales(getMsgError()), SQLERRMSG_CLAVEDUPLICADA_A)){
				return true;
			 } else {
				return false;
			 }
		} else {
			return false;
		}
	}
	
	@Override
	public boolean isViolatesForeignKey() {
		if (getCodigoError() != null && getCodigoError().contains(DAOException.SQLERRCODE_VIOLATES_FOREIGN_KEY) ){
			return true;
		} else if (getMsgError() != null) {
			if (msgErrorContains(MyUtils.quitarCaracterEspeciales(getMsgError()), SQLERRMSG_VIOLATES_FOREIGN_KEY_A)){
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public boolean isDataTruncation() {
		if (getCodigoError() != null && getCodigoError().contains(DAOException.SQLERRMSG_DATA_TRUNCATION) ){
			return true;
		} else if (getMsgError() != null) {
			if (msgErrorContains(MyUtils.quitarCaracterEspeciales(getMsgError()), SQLERRMSG_DATA_TRUNCATION_A)){
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private boolean msgErrorContains(final String _eMsgError, final String[] _contenidos) {
		if (_eMsgError != null) {
			for (String c: _contenidos) {
				if (_eMsgError.contains(c)) {
					return true;
				}
			}
		}
		return false;
	}
}
