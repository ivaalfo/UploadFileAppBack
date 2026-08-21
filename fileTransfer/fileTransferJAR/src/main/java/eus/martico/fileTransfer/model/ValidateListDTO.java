//package eus.martico.fileTransfer.model;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//
//import eus.martico.fileTransfer.model.error.vo.ErrorVO;
//import eus.martico.fileTransfer.model.mensaje.vo.MensajeVO;
//
//OLD DELETE
//public class ValidateListDTO<T extends List<?>> {
//
//	T listaDatos;
//	List<ErrorVO> errores; 
//	List<MensajeVO> mensajes;
//	
//	public ValidateListDTO() {
//		super();
//	}
//	public ValidateListDTO(T _listaDatos) {
//		super();
//		this.listaDatos = _listaDatos;
//	}
//
//	@JsonInclude(JsonInclude.Include.NON_NULL)
//	public List<ErrorVO> getErrores() {
//		return errores;
//	}
//	@JsonInclude(JsonInclude.Include.NON_NULL)
//	public List<MensajeVO> getMensajes() {
//		return mensajes;
//	}
//	
//	@JsonInclude(JsonInclude.Include.ALWAYS)
//	public boolean isError() {
////		if (this.errores!=null && !this.errores.isEmpty()) {
////			return true;
////		} else if (listaDatos!=null) {
////			//
////		}		
//		return ((this.errores==null) || (this.errores.isEmpty())?false:true);
//	}
//
//	public boolean addError(ErrorVO _error) {
//		if (_error==null)
//			return false;
//		if (errores==null)
//			errores = new ArrayList<ErrorVO>();
//		return errores.add(_error);
//	}
//	public boolean addAllError(List<ErrorVO> _errores) {
//		if (_errores==null)
//			return false;
//		if (errores==null)
//			errores = new ArrayList<ErrorVO>();
//		return errores.addAll(_errores);
//	}
//	public boolean addMensaje(MensajeVO _mensaje) {
//		if (_mensaje==null)
//			return false;
//		if (mensajes==null)
//			mensajes = new ArrayList<MensajeVO>();
//		return mensajes.add(_mensaje);
//	}
//
//	@JsonInclude(JsonInclude.Include.NON_NULL)
//	public T getLista() {
//		return this.listaDatos;
//	}
//	public void setListaDatos(T listaDatos) {
//		this.listaDatos = listaDatos;
//	}
//}
