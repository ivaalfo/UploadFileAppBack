package eus.martico.fileTransfer.services;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eus.martico.fileTransfer.config.FileTransferConstantes;

public class MyServiceUtil {

	protected static final Logger logger = LoggerFactory.getLogger(MyServiceUtil.class);
	
	public static DataSource getDataSource() {
		return getDataSource("");
	}
	public static DataSource getDataSource(String _dataID) {
		DataSource ds = null;  
		try {
			Context context = new InitialContext();
			ds = (DataSource)context.lookup(FileTransferConstantes.JNDI_INITIAL_CONTEXT_KEY+FileTransferConstantes.DFC_DEFAULT_DATASOURCE_ID+_dataID);
		} catch (NamingException e) {
			logger.error("MyServiceUtil: getDataSource", e);
		}
		return ds;
	}


}
