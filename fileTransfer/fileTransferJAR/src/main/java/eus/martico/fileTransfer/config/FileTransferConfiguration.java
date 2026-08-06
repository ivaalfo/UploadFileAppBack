package eus.martico.fileTransfer.config;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class FileTransferConfiguration {

	protected static final Logger logger = LoggerFactory.getLogger(FileTransferConfiguration.class);
	
	public static final DateTimeFormatter DATE_TIME_FORMATTER1 = DateTimeFormatter.ofPattern(FileTransferConstantes.JSON_DATETIME_FORMAT1);
	public static final DateTimeFormatter DATE_FORMATTER1 = DateTimeFormatter.ofPattern(FileTransferConstantes.JSON_DATE_FORMAT1);

	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(FileTransferConstantes.JSON_DATETIME_FORMAT);
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(FileTransferConstantes.JSON_DATE_FORMAT);

	public static final DateTimeFormatter DATE_PROCEDURE_FORMAT = DateTimeFormatter.ofPattern(FileTransferConstantes.PROCEDURE_DATE_FORMAT);
//	public static final DateTimeFormatter DATETIME_FORMATTER_PORTIC = DateTimeFormatter.ofPattern(fileTransferConstantes.JSON_DATETIME_FORMAT_PORTIC);
	
	public static final LocalDateTime FECHA_HACE_SEIS_MESES = LocalDateTime.now().minusMonths(6);
	
	private static PropertiesConfiguration applicationProperties = null;
	
	static {
		try {
			URL urlProperties = FileTransferConfiguration.class.getClassLoader().getResource(FileTransferConstantes.FILETRANSFER_FICHERO_PROPERTIES);
			applicationProperties = new PropertiesConfiguration(urlProperties);			
		} catch (ConfigurationException e) {
			logger.error("Error inicializando fileTransferConfiguration: applicationProperties", e);
		}		
	}

	public static String getProperty(String _propertyName) {
		try {
			return applicationProperties.getString(_propertyName);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
	public static String[] getStringArray(String _propertyName) {
		try {
			return applicationProperties.getStringArray(_propertyName);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
//	public static boolean getBooProperty(String _propertyName) {
//		try {
//			return applicationProperties.getBoolean(_propertyName);
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			return false;
//		}
//	}
	public static boolean getBooProperty(final String _propertyName, final boolean _default) {
		try {
			return applicationProperties.getBoolean(_propertyName);
		} catch (Exception e) {
			//logger.error(e.getMessage());
			return _default;
		}
	}
//	public static boolean getBooPropertyDefaultTrue(String _propertyName) {
//		try {
//			return applicationProperties.getBoolean(_propertyName);
//		} catch (Exception e) {
//			return true;
//		}
//	}

	public static Integer getIntProperty(String _propertyName) {
		try {
			return applicationProperties.getInt(_propertyName);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
	
	public static Long getLongProperty(String _propertyName) {
		try {
			return applicationProperties.getLong(_propertyName);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

}
