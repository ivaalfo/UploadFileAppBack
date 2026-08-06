//package eus.martico.fileTransfer.model.persona;
//
//import eus.martico.fileTransfer.config.FileTransferConstantes;
//import eus.martico.fileTransfer.model.vo.IEnumVO;
//
//public enum PersonaVOEnum implements IEnumVO {
//	
//	NIF("nif"),
//	NOMBRE("nombre"),
//	APELLIDO1("ape1"),
//	APELLIDO2("ape2"),
//	TELEFONO("telefono"),
//	EMAIL("email");
//	
//	private final String  nombre;
//
//	private PersonaVOEnum(String nombre) {
//		this.nombre = nombre;
//	}
//
//	@Override
//	public String getNombre() {
//		return nombre;
//	}
//	@Override
//	public String getContextNombre() {
//		return FileTransferConstantes.PERSONA_PREFIJO+nombre;
//	}
//	
//}
