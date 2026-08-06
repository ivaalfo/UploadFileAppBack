package eus.martico.fileTransfer.login.control;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eus.martico.fileTransfer.config.FileTransferConfiguration;
import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.login.config.LoginConstantes;
import eus.martico.fileTransfer.model.session.vo.LoginSessionVO;

public class UtilController {
	
	protected static final Logger logger = LoggerFactory.getLogger(UtilController.class);
	
	private static PropertiesConfiguration applicationProperties = null;
	
	static {
		try {
			URL urlProperties = UtilController.class.getClassLoader().getResource(LoginConstantes.DFC_LOGIN_FICHERO_PROPERTIES);
			applicationProperties = new PropertiesConfiguration(urlProperties);									
		} catch (ConfigurationException e) {
			logger.error(e.getMessage(), e);
		}
	}
	

	public static String getRedirectUrl(final HttpServletRequest _request, final LoginSessionVO _loginSession) {

		String appHomeRedirectUri;
		if(FileTransferConfiguration.getBooProperty(FileTransferConstantes.PROPERTY_KEY_LOGIN_MODO_TEST, false))
			appHomeRedirectUri = applicationProperties.getString("appHomeRedirectUriTest");
		else
			appHomeRedirectUri = applicationProperties.getString("appHomeRedirectUri");
		
		String server = _request.getServerName();
		int port = _request.getLocalPort();
		String appHomeRedirectUrl = appHomeRedirectUri.replaceAll("<server>", server);
		if(port == 80) {
			appHomeRedirectUrl = appHomeRedirectUrl.replaceAll("<:port>", "");
		} else {
			appHomeRedirectUrl = appHomeRedirectUrl.replaceAll("<:port>", ":"+port);
		}
		appHomeRedirectUrl = appHomeRedirectUrl + "?id="+_loginSession.getSessionToken();
		
		return appHomeRedirectUrl;
	}

	public static PropertiesConfiguration getApplicationProperties() {
		return applicationProperties;
	}
	public static String getPropertyString(String _key) {
		return applicationProperties.getString(_key);
	}

}
