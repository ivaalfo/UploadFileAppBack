package eus.martico.fileTransfer.ejb.timer.bloqueo;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eus.martico.fileTransfer.config.FileTransferConfiguration;
import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.model.bloqueo.BloqueoVO;

@Stateless
public class BloqueoTimerFileTransferEjbBean implements BloqueoTimerFileTransferEjb {

	protected static final Logger logger = LoggerFactory.getLogger(BloqueoTimerFileTransferEjbBean.class);
	
//	private static final int 	TIMER_MINUTOS_DELAY = FileTransferConfiguration.getIntProperty(FileTransferConstantes.PROPERTY_KEY_PROGRAMADOS_BLOQUEO_TIMEOUT);
//
	private static final String URL_FINALIZAR_BLOQUEO = "http://"
														+ FileTransferConfiguration.getProperty(FileTransferConstantes.PROPERTY_KEY_URL_LOCAL_HOST)
														+ ":"+ FileTransferConfiguration.getProperty(FileTransferConstantes.PROPERTY_KEY_URL_LOCAL_PORT)
														+ FileTransferConfiguration.getProperty(FileTransferConstantes.PROPERTY_KEY_ADMIN_BLOQUEO_URI); 
	
	private static final String ADMIN_TOKEN = FileTransferConfiguration.getProperty(FileTransferConstantes.PROPERTY_KEY_ROL_ADMIN_TOKEN);
	
//	private BloqueoVO bloqueo;
	
    @Resource
    private TimerService ts;
    
//	@Resource
//	private DataSource ds;
//	
	@Resource 
	private SessionContext ctx;

    /**
     * Default constructor. 
     */
    public BloqueoTimerFileTransferEjbBean() {
    	logger.info("create BloqueoTimerFileTransferEjbBean");
    }
	
	@Override
	public boolean iniciar(final BloqueoVO _bloqueo) {
    	logger.debug("==> iniciar timer...");
    	if (ctx!=null)
    		logger.debug("==> SessionContext ");
        final TimerConfig tc = new TimerConfig();
        tc.setPersistent(false);
        tc.setInfo(_bloqueo);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MILLISECOND, _bloqueo.getTimeoutMilliseconds()); // que empiece dentro de dos minutos.
        @SuppressWarnings("unused")
		Timer tm = ts.createIntervalTimer(cal.getTime(), _bloqueo.getTimeoutMilliseconds(), tc);
		return false;
	}

    @Timeout
    public void programmaticTimeout(Timer timer) {
    	logger.debug("==> timeout timer...");
		// finalizar el bloqueo
		try {
			if(timer.getInfo() != null){
				httpAsyncFinalizarBloqueo((BloqueoVO)timer.getInfo());
			} else {
				timer.cancel();
			}
		} catch (IOException e) {
			logger.debug("error terminar timer");
			logger.error("programmaticTimeout",e);
		} catch (InterruptedException e) {
			logger.debug("error terminar timer");
			logger.error("programmaticTimeout",e);
		} catch (ExecutionException e) {
			logger.debug("error terminar timer");
			logger.error("programmaticTimeout",e);
		}
    }

	@Override
	public boolean reiniciar() {
		logger.debug("==> reiniciar timer...");
		return false;
	}

	@Override
	public boolean cancelar(BloqueoVO _bloqueo) {
		logger.debug("==> terminar timer...");
		if(ts!=null && _bloqueo!=null){
			for(Timer t: ts.getAllTimers()){
				if(t.getInfo() != null){
					try {
						BloqueoVO bloqueo = (BloqueoVO) t.getInfo();
						logger.debug("timer "+t.getInfo());
						if(_bloqueo != null 
								&& bloqueo.getTipoCod().equals(_bloqueo.getTipoCod())
								&& bloqueo.getCodigo().equals(_bloqueo.getCodigo())
								&& bloqueo.getUsuarioNi().equals(_bloqueo.getUsuarioNi())) {
							logger.debug("timer cancelado "+bloqueo.getTipoCod()+bloqueo.getCodigo()+bloqueo.getUsuarioNi());
							t.cancel();
						}
					} catch (NoSuchObjectLocalException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (EJBException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return true;
	}

	@Override
	public boolean cancelAll() {
		logger.debug("==> terminar all timer...");
		if(ts != null){
			try {
				for(Timer t: ts.getAllTimers()){
					try {
						if(t.getInfo() != null){
							logger.debug("timer a cancelar "+t.getInfo());
						}
						t.cancel();
					} catch (NoSuchObjectLocalException | IllegalStateException e) {
						logger.error("Timer ", e);
					}
				}
			} catch (IllegalStateException | EJBException e) {
				logger.warn("getAllTimers ", e.getMessage());
			}
		}
		return true;
	}

	@PreDestroy
	public void preDestroy() {
		logger.debug("==> preDestroy timer...");
		cancelAll();
	}
	
//	private void httpFinalizarBloqueo(BloqueoVO _bloqueo) throws ClientProtocolException, IOException {
//		CloseableHttpClient httpClient = HttpClients.createDefault();
//	    try {
//	        HttpDelete request = new HttpDelete("http://localhost:9080/fileTransferWAR/api/v1/admin/bloqueo/"+_bloqueo.getTipoCod()+"/"+_bloqueo.getUsuarioNi());
//	        RequestConfig config = RequestConfig
//	        	      .custom()
//	        	      .build();
//	        request.setConfig(config);
//	        
//	        // add request headers
//	        request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
//	        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWyiwuVrJS8nUMCvF09lfSUcpMLFGyMjQ1NzMztTA2tdBRSq0oAAlYmBpZQgQSc3ITk1PzUoH6opWMTJVigZpSgGaYAnUnlpZk5BdllmRCZEODXYOAoo4uvp5-QBpmC1BHaXFqUV5ibipQX25iUUlmcr5SLQDZEmSUjQAAAA.98wL6NjMi6pm9ymeyJGDoUkbKxD1eKzgZgCUwLYHdY8");
//	
//	        CloseableHttpResponse response = httpClient.execute(request);
//	
//	        try {
//	            // Get HttpResponse Status
//	            logger.debug(response.getProtocolVersion().getProtocol());              // HTTP/1.1
//	            logger.debug(response.getStatusLine().getStatusCode()+"");   // 200
//	            logger.debug(response.getStatusLine().getReasonPhrase()); // OK
//	            logger.debug(response.getStatusLine().toString());        // HTTP/1.1 200 OK
//	
//	            HttpEntity entity = response.getEntity();
//	            if (entity != null) {
//	                // return it as a String
//	                String result = EntityUtils.toString(entity);
//	                logger.debug(result);
//	            }
//	        } finally {
//	            response.close();
//	        }
//	    } finally {
//	        httpClient.close();
//	    }		
//
//	}
	
	private void httpAsyncFinalizarBloqueo(BloqueoVO _bloqueo) 
			throws ClientProtocolException, IOException, InterruptedException, ExecutionException {
		CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault();
	    try {
	    	StringBuilder url = new StringBuilder(URL_FINALIZAR_BLOQUEO);
	    	url.append("/");
	    	url.append(_bloqueo.getTipoCod());
	    	url.append("/");
	    	url.append(_bloqueo.getUsuarioNi());
	    	if (_bloqueo.getCodigo()!=null) {
		    	url.append("/");
		    	url.append(_bloqueo.getCodigo());
	    	}
	        HttpDelete request = new HttpDelete(url.toString());
	        RequestConfig config = RequestConfig
	        	      .custom()
	        	      .build();
	        request.setConfig(config);
	        
	        // add request headers
	        request.addHeader(HttpHeaders.CONTENT_TYPE, FileTransferConstantes.CONTENT_TYPE_APPLICATION_JSON);
	        request.addHeader(HttpHeaders.AUTHORIZATION, FileTransferConstantes.SECURITY_BEARER+" "+ADMIN_TOKEN);
	
	        Future<HttpResponse> future;
	        HttpResponse response;
        	httpClient.start();
	        future = httpClient.execute(request, null);
	        response = future.get();
	        
            // Get HttpResponse Status
//            logger.debug(response.getProtocolVersion().getProtocol());   // HTTP/1.1
//            logger.debug(response.getStatusLine().getStatusCode()+"");   // 200
//            logger.debug(response.getStatusLine().getReasonPhrase()); // OK
            logger.debug(response.getStatusLine().toString());        // HTTP/1.1 200 OK

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // return it as a String
                String result = EntityUtils.toString(entity);
                logger.debug(result);
            }
	    } finally {
        	if (httpClient!=null)
        		httpClient.close();
	    }		

	}

}