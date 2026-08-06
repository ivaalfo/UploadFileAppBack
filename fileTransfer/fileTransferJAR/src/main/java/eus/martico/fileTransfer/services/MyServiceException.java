package eus.martico.fileTransfer.services;

import eus.martico.fileTransfer.model.MyException;
import eus.martico.fileTransfer.model.error.vo.ErrorVO;
import eus.martico.fileTransfer.model.vo.ValidateDTO;

@SuppressWarnings("serial")
public class MyServiceException extends MyException {

	public MyServiceException(String _msgError) {
		super(_msgError);
	}
	public MyServiceException(Exception _ex) {
		super(_ex);
	}
	public MyServiceException(String _codigoError, String _msgError) {
		super(_codigoError, _msgError);
	}

	public ValidateDTO<ErrorVO> getValidate() {
		ValidateDTO<ErrorVO> valida = new ValidateDTO<ErrorVO>();
		ErrorVO error = null;
		if (this.getCodigoError()!=null) {
			if (this.getMsgError()!=null) {
				error = new ErrorVO(this.getCodigoError(), this.getMsgError());
			} else {
				error = new ErrorVO(this.getCodigoError(), null);
			}
		} else if (this.getMsgError()!=null) {
			error = new ErrorVO(null, this.getMsgError());			
		}
		if (error!=null)
			valida.addError(error);
		return valida;
	}

}
