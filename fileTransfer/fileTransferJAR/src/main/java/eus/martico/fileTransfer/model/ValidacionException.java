package eus.martico.fileTransfer.model;

import javax.validation.ValidationException;

import eus.martico.fileTransfer.model.error.vo.ErrorVO;
import eus.martico.fileTransfer.model.vo.EntidadVaciaVO;
import eus.martico.fileTransfer.model.vo.ValidateDTO;

public class ValidacionException extends ValidationException {

	@SuppressWarnings("rawtypes")
	ValidateDTO validacion ; 
	/**
	 * 
	 */
	private static final long serialVersionUID = 2885349925200677857L;

	public static final String VALIDATION_ERR = "VAL0001"; 
	public static final String BAD_REQUEST_EXCEPTION = "BAD_REQUEST";
	
	private String codigoError=null;
	
	public ValidacionException(String _msgError, @SuppressWarnings("rawtypes") ValidateDTO _validacion) {
		this(_msgError);
		this.validacion = _validacion;
	}

	public ValidacionException(String _msgError) {
		super(_msgError);
	}
	public ValidacionException(String _codigoError, String _msgError) {
		super(_msgError);
		codigoError=_codigoError;
		if (this.validacion==null) {
			this.validacion = new ValidateDTO<EntidadVaciaVO>(new EntidadVaciaVO("error"));
		}
		this.validacion.addError(new ErrorVO(_codigoError, _msgError));
	}

	public String getCodigoError() {
		return codigoError;
	}

	public void setCodigoError(String codigoError) {
		this.codigoError = codigoError;
	}

	@SuppressWarnings("rawtypes")
	public ValidateDTO getValidacion() {
		return validacion;
	}

	public void setValidacion(@SuppressWarnings("rawtypes") ValidateDTO validacion) {
		this.validacion = validacion;
	}

}
