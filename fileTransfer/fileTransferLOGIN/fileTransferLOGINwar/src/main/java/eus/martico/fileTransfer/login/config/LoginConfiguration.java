package eus.martico.fileTransfer.login.config;

import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginConfiguration {
	
	protected static final Logger logger = LoggerFactory.getLogger(LoginConfiguration.class);
	
	private static PropertiesConfiguration applicationProperties = null;
	
	static {
		try {
			URL urlProperties = LoginConfiguration.class.getClassLoader().getResource(LoginConstantes.DFC_LOGIN_FICHERO_PROPERTIES);
			applicationProperties = new PropertiesConfiguration(urlProperties);
		} catch (ConfigurationException e) {
			logger.error("Error inicializando LoginConfiguration", e);
		}		
	}

	public static final String getProperty(String _propertyName) {
		try {
			return applicationProperties.getString(_propertyName);
		} catch (Exception e) {
			return null;
		}
	}
	public static boolean getBooProperty(final String _propertyName, final boolean _default) {
		try {
			return applicationProperties.getBoolean(_propertyName);
		} catch (Exception e) {
			return _default;
		}
	}
	public static Integer getIntegerProperty(String _propertyName) {
		try {
			return applicationProperties.getInt(_propertyName);
		} catch (Exception e) {
			return null;
		}
	}
}
