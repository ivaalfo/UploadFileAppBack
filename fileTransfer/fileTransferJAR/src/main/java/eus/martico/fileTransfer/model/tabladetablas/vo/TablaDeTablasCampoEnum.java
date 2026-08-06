package eus.martico.fileTransfer.model.tabladetablas.vo;

public enum TablaDeTablasCampoEnum {

		DETALLE_NI("detalle_ni"),
		CABECERA_NI("cabecera_ni"),
		CABECERA_CODIGO("cabecera_codigo"),
		DETALLE_CODIGO("detalle_codigo"),
		DESCRIPCION_ES("descripcion_es"),
		DESCRIPCION_EUS("descripcion_eus")
		;
		
		private TablaDeTablasCampoEnum(String _nombre) {
			this.nombre = _nombre;
		}
		private TablaDeTablasCampoEnum(int _icampo) {
			this.icampo = _icampo;
		}

		private String nombre;
		private int icampo;

		public String getNombre() {
	        return this.nombre;
	    }
//		
//		public String setNombre(String _nombre) {
//	        return this.nombre = _nombre;
//	    }
		
		public int getIndCampo() {
			return icampo;
		}

}
