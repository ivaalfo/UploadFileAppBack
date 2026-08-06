package eus.martico.fileTransfer.dao.tabladetablas;

import eus.martico.fileTransfer.dao.BaseDAOCons;

public class TablaDeTablasDAOImpCons extends BaseDAOCons {

	public static final String BD_TABLA_TABLACABECERA   = "ENGNTTBCAB";
	public static final String BD_TABLA_TABLADETALLE    = "ENGNTTBDET";
	public static final String BD_VISTA_TABLADETALLE 	= "ENGNVTBDET";
	public static final String BD_SEQ_TABLADETALLE 		= "ENGNQTBDET";
	
	public static String BD_TABLACABECERA_NI 		= "TBCABNI";
	public static String BD_TABLACABECERA_CODIGO	= "TBCABCOD";
	public static String BD_TABLACABECERA_DESC_ES	= "TBCABDESES";
	public static String BD_TABLACABECERA_DESC_EUS	= "TBCABDESEU";

	public static String BD_TABLADETALLE_NI	 		= "TBDETNI";
	public static String BD_TABLADETALLE_CODIGO		= "TBDETCOD";
	public static String BD_TABLADETALLE_CLAVE		= "TBDETCLV";
	public static String BD_TABLADETALLE_TIPO		= "TBDETTIP";
	public static String BD_TABLADETALLE_INDICE		= "TBDETIND";
	public static String BD_DESCRIPCION_ES			= "TBDETDESES";
	public static String BD_DESCRIPCION_EUS			= "TBDETDESEU";
	public static String BD_INACTIVO				= "TBDETINAC";
	
	public static String BD_INACTIVO_VALOR_ACTIVO		= "0";
	
}
