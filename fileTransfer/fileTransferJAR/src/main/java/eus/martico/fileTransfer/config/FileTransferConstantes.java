package eus.martico.fileTransfer.config;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public class FileTransferConstantes {

	public static final String DEFAULT_ENCODE = "UTF-8";
	public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
	
	public static final String SECURITY_BEARER = "Bearer";
	public static final String SECURITY_BASIC = "Basic";
	public static final String SECURITY_AUTHORIZATION = "Authorization";

	public static final Long  DEFAULT_EMPRESA_NI = 1L;
	public static final Long  DEFAULT_USER_NI = 0L;

	public static final String PROV_BIZKAIA_COD = "48";
	
	public static final String FILETRANSFER_FICHERO_PROPERTIES = "/config/fileTransfer.properties"; 
	
	public static final String PROPERTY_KEY_CLAVE_DUPLICADA_STRINGS = "fileTransfer.clave.duplicada.strings";
	public static final String PROPERTY_KEY_VIOLATES_FOREIGN_KEY = "fileTransfer.violates.foreign.key.strings";
	public static final String PROPERTY_KEY_DATA_TRUNCATION_STRINGS = "fileTransfer.data.truncation.key.strings";
	
	public static final String PROPERTY_KEY_LOGINUSER_DAO_CLASS = "loginUserDAO";
	public static final String PROPERTY_KEY_LOGINSESSION_DAO_CLASS = "loginSessionDAO";
	public static final String PROPERTY_KEY_TABLADETABLAS_DAO_CLASS = "tablaDeTablasDAO";
	
	public static final String JNDI_INITIAL_CONTEXT_KEY = "java:/comp/env/jdbc/";
	
	public static final String PROPERTY_KEY_LOGIN_MODO_TEST = "fileTransfer-login-modo-test";
	public static final String PROPERTY_KEY_LOGIN_MODO_TEST_TOKEN = "fileTransfer-login-modo-test-token";
	
	public static final String PROPERTY_KEY_LOGIN_MODO_MOCK = "fileTransfer-api-modo-mock";
	public static final String PROPERTY_KEY_LOGIN_MODO_MOCK_PASSWORD = "fileTransfer-api-modo-mock.user.password";
	
	public static final String DFC_DEFAULT_DATASOURCE_ID = "marticoDS";

	public static final String PROPERTY_KEY_PERSONA_DAO_CLASS = "personaDAO";
	
//	public static final DateTimeFormatter DATEFORMAT_YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//	public static final DateTimeFormatter DATETIMEFORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm:ss");
	public static final String JSON_DATE_FORMAT ="yyyyMMdd";
	public static final String JSON_DATE_FORMAT1 ="dd/MM/yyyy";
	public static final String JSON_DATETIME_FORMAT ="yyyyMMdd'T'HH:mm:ss";
	public static final String JSON_DATETIME_FORMAT1 ="dd/MM/yyyy HH:mm:ss";
	public static final String JSON_DATETIME_FORMAT2 ="yyyyMMddHHmmss";
//	public static final String JSON_DATETIME_FORMAT_PORTIC ="dd/MM/yyyy HH:mm";
//	public static final String FORMAT_DATETIME_PORTIC = "yyyyMMddHHmm";
//	public static final String FORMAT_DATETIME_PORTIC_EA = "yyyyMMddHHmmss";
	public static final String PROCEDURE_DATE_FORMAT = "yyyy-MM-dd"; 
	public static final String HOME_URI = "/home";
	
	public static final String SIGN_FILE_NAME 			= "sign-file-name";
	public static final String DIR_SIGN_FILE			= "sign-dir";
	public static final String MAN_PROV_FILE_NAME_ES	= "manual-file-name-es";
	public static final String MAN_PROV_FILE_NAME_EN	= "manual-file-name-en";
	public static final String MAN_PROV_LOCATION		= "manual-dir";
	
	public static final String DIR_FAC_FILES_DOCPRO	 	= "fac-docpro-dir";
	public static final String DIR_SALIDAS				= "salidas-dir";
	
	public static final String DIR_BASE 				= "filetransfer.base-dir";
	public static final String DIR_BASE_CMR 			= "filetransfer.subfolder.cmr";
	public static final String DIR_BASE_CMR_VALIDATED	= "filetransfer.subfolder.cmr.validados";
	public static final String DIR_BASE_CMR_REJECTED	= "filetransfer.subfolder.cmr.rechazados";
	public static final String DIR_BASE_FAC 			= "filetransfer.subfolder.fac";
	public static final String DIR_BASE_FAC_REJECTED 	= "filetransfer.subfolder.fac.rechazadas";
	public static final String DIR_BASE_OTR 			= "filetransfer.subfolder.otr";
	

	//Vistas de la tabla de tablas
	public static final String TT_AUTOR_ROLES		= "AUTOR_ROLES";
	public static final String TT_TIPOS_BLOQUEOS	= "TIPO_BLOQUEO";
	
	//Codigos Bloqueos
	public static final String BLOQ_VALIDADOR	= "VALIDADOR";
//	public static final String BLOQ_FIRMADOR	= "FIRMADOR";	//XXX descartado firmador
	
	public static final byte ORDEN_ASCENDENTE = 1;
	public static final byte ORDEN_DESCENDENTE = -1;

	public static final String PERSONA_PREFIJO = "persona.";

	public static final String PROPERTY_KEY_ROL_ADMIN = FileTransferConfiguration.getProperty("rol.clave.admin");			//"FTADM";
	public static final String PROPERTY_KEY_ROL_INTERNO = FileTransferConfiguration.getProperty("rol.clave.interno");		//"FTINTER";
	public static final String PROPERTY_KEY_ROL_CONSULTA = FileTransferConfiguration.getProperty("rol.clave.consulta");		//"FTCONSUL";
	public static final String PROPERTY_KEY_ROL_EXTERNO = FileTransferConfiguration.getProperty("rol.clave.externo");		//"FTEXTER";
	public static final String PROPERTY_KEY_ROL_NO_ACCESS = FileTransferConfiguration.getProperty("rol.clave.sinAcceso");	//"FTNO";
	
	
	public static final int ROL_NUMBER_ADMIN = FileTransferConfiguration.getIntProperty("rol.num.admin");
	public static final int ROL_NUMBER_INTERNO = FileTransferConfiguration.getIntProperty("rol.num.interno");
	public static final int ROL_NUMBER_CONSULTA = FileTransferConfiguration.getIntProperty("rol.num.consulta");
	public static final int ROL_NUMBER_EXTERNO = FileTransferConfiguration.getIntProperty("rol.num.externo");
	public static final int ROL_NUMBER_NO_ACCESS = FileTransferConfiguration.getIntProperty("rol.num.sinAcceso");
	
	public static final String PROPERTY_KEY_URL_LOCAL_HOST = "url.local.host";
	public static final String PROPERTY_KEY_URL_LOCAL_PORT = "url.local.port";
	public static final String PROPERTY_KEY_ADMIN_BLOQUEO_URI = "admin.bloqueo.uri";
	public static final String PROPERTY_KEY_ROL_ADMIN_TOKEN = "rol.admin.token";
	
	public static final String PROPERTY_KEY_PROGRAMADOS_BLOQUEO_TIMEOUT = "validacion.bloqueo.timeout.mins";
	
	public static final String PROPERTY_KEY_LOGIN_HTTP_ERROR_RESPONSE_CODE = "fileTransfer-login-HTTP-ERROR-RESPONSE-CODE";
	public static final String PROPERTY_KEY_LOGIN_HTTP_ERROR_RESPONSE_HEADER = PROPERTY_KEY_LOGIN_HTTP_ERROR_RESPONSE_CODE + ".header";
	public static final String PROPERTY_KEY_LOGIN_HTTP_ERROR_RESPONSE_MODO = PROPERTY_KEY_LOGIN_HTTP_ERROR_RESPONSE_CODE + ".modo";
	
	public static final int ESTADO_0_FICH_NO_SUBIDO = FileTransferConfiguration.getIntProperty("estado.fichero.no.subido");
	public static final int ESTADO_1_FICH_SUBIDO 	= FileTransferConfiguration.getIntProperty("estado.fichero.subido");
	public static final int ESTADO_2_FICH_VALIDADO	= FileTransferConfiguration.getIntProperty("estado.fichero.validado");
	public static final int ESTADO_3_FICH_RECHAZADO	= FileTransferConfiguration.getIntProperty("estado.fichero.rechazado");
	//NO SE USA es un 2
//	public static final int ESTADO_2_FAC_CONTABILIZADA = FileTransferConfiguration.getIntProperty("estado.factura.contabilizada");
	public static final int ESTADO_4_FAC_BLOQUEADA			= FileTransferConfiguration.getIntProperty("estado.factura.bloqueada");
	public static final int ESTADO_5_FAC_SIN_CONTABILIZAR 	= FileTransferConfiguration.getIntProperty("estado.factura.sin.contabilizar");
	public static final int ESTADO_6_FAC_NO_CONTABILIZAR 	= FileTransferConfiguration.getIntProperty("estado.factura.no.contabilizar");
	
	public static final int ESTADO_7_FICH_SIN_DOC 	= FileTransferConfiguration.getIntProperty("estado.grupaje.fichero.no.aplica");
	public static final int ESTADO_8_INIT_GRUP		= FileTransferConfiguration.getIntProperty("estado.grupaje.inicio");
	public static final int ESTADO_9_PEDIDO_NULO 	= FileTransferConfiguration.getIntProperty("estado.pedido.deshabilitado");
	

	public static final int SIN_ANOTACIONES = 0;
	public static final int CON_ANOTACIONES = 1;
	
//	public static final String CODIGO_NULO_VACIO = "--";
//	public static final Character OBSERVACIONES_SEPARADOR = '#';
//	public static final Character OBSERVACIONES_SEPARADOR_AUTO = ' ';
	
	public static final String CERT_ENV			= "filetransfer.certificados.entorno";
	public static final String CERT_PATH 		= "filetransfer.certificados.ruta";
	public static final String CERT_PASS 		= "filetransfer.certificados.password";
	public static final String CERT_TSA_URL 	= "filetransfer.certificados.tsa-url";
	public static final String CERT_TSA_USER 	= "filetransfer.certificados.tsa-user";
	public static final String CERT_TSA_PASS 	= "filetransfer.certificados.tsa-pass";
	
//	XXX descartado firmador
//	public static final int ESTADO_FIRMA_INICIO 		= FileTransferConfiguration.getIntProperty("estado.firmado.inicio");
//	public static final int ESTADO_FIRMA_MARTICO 		= FileTransferConfiguration.getIntProperty("estado.firmado.martico");
//	public static final int ESTADO_FIRMA_TRANSPORTISTA 	= FileTransferConfiguration.getIntProperty("estado.firmado.transpo");
//	public static final int ESTADO_FIRMA_DESTINATARIO 	= FileTransferConfiguration.getIntProperty("estado.firmado.destina");
	
	
	//TODO arreglar JndiLookup
	//java.lang.NoClassDefFoundError: org/apache/logging/log4j/core/lookup/JndiLookup
}
