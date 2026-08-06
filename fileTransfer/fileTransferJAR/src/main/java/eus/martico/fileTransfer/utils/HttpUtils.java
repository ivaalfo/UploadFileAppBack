package eus.martico.fileTransfer.utils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.core.HttpHeaders;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * 
 * @author Martico S.L.
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public class HttpUtils {

	private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);
			
	/**
	 * 
	 * @param _protocolo
	 * @param _host
	 * @param _port
	 * @param _uri
	 * @param _metodo
	 * @param _params
	 * @param _token
	 * @param _timeout en milisegundos
	 * @return
	 * @throws Exception
	 */
	public static String consumirServicioRESTAsyncAuthorBearer(
			String _protocolo, String _host, Integer _port, String _uri, String _metodo, String _params, String _token, int _timeout) throws Exception {
		String result = "";
		HttpHost target = new HttpHost(_host, _port, _protocolo);
		HttpUriRequest objetoHttp = null;	//objetoHttp queremos que nos funcione como HttpGet, HttpPost, HttpPut...
		StringBuilder uri = new StringBuilder(_uri);
		if (!StringUtils.isEmpty(_params)) {
			uri.append("?");
			uri.append(_params);
		}
		switch(_metodo) {
		  case "GET":
			  objetoHttp = new HttpGet(uri.toString());
			  break;
		  case "POST":
			  objetoHttp = new HttpPost(uri.toString());
			  break;
		  case "PUT":
			  objetoHttp = new HttpPut(uri.toString());
			  break;
		}
		
		objetoHttp.setHeader("Content-type", "application/json");
		objetoHttp.addHeader(HttpHeaders.AUTHORIZATION,"Bearer "+_token);

		// NO esperamos respuesta
		RequestConfig.Builder requestConfigB = RequestConfig.custom();
        if (_timeout > 0) {
        	requestConfigB.setConnectionRequestTimeout(_timeout);
        	requestConfigB.setSocketTimeout(_timeout).build();        
        }
        RequestConfig requestConfig = requestConfigB.build();
        
        Future<HttpResponse> future;
        HttpResponse response;
		
		try (CloseableHttpAsyncClient httpClient = HttpAsyncClients.custom()
        		.setDefaultRequestConfig(requestConfig)
                .build();
				) {
		    httpClient.start();
	        future = httpClient.execute(target, objetoHttp, null);
	        response = future.get();
	        
			HttpEntity httpEntity = response.getEntity();
			result = EntityUtils.toString(httpEntity);
			if (response.getStatusLine()!=null && response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK) {
				logger.warn("StatusCode="+response.getStatusLine().getStatusCode());
			}
			if (logger.isDebugEnabled()) {
				if (result==null)
					logger.debug("Result=null");
				else
					logger.debug("Result="+result.trim());
			}
		} catch (Exception e) {
			boolean launche = true;
			if (e instanceof SocketTimeoutException || 
				e.getCause() instanceof SocketTimeoutException) {
				// No hacemos nada, nosotros hemos puesto el timeout
				// no esperamos respuesta
				if (_timeout > 0) {
					launche = false;
				}
			}
			if (launche)
				throw e;
		}
		return result;
	}

	public static void consumirServicioRESTAsync(
			String _protocolo, String _host, Integer _port, String _uri, String _metodo, String _params, String _token) throws Exception {
		HttpHost target = new HttpHost(_host, _port, _protocolo);
		StringBuilder uri = new StringBuilder(_uri);
		if (!StringUtils.isEmpty(_params)) {
			uri.append("?");
			uri.append(_params);
		}
		logger.trace("consumirServicioRESTAsync "+uri.toString());
		HttpUriRequest objetoHttp = null;	//objetoHttp queremos que nos funcione como HttpGet, HttpPost, HttpPut...
		switch(_metodo) {
		  case "GET":
			  objetoHttp = new HttpGet(target.toURI()+uri.toString());
			  break;
		  case "POST":
			  objetoHttp = new HttpPost(target.toURI()+uri.toString());
			  break;
		  case "PUT":
			  objetoHttp = new HttpPut(target.toURI()+uri.toString());
			  break;
		}
		objetoHttp.setHeader("Content-type", "application/json");
		objetoHttp.addHeader(HttpHeaders.AUTHORIZATION,"Bearer "+_token);
		
		logger.trace("consumirServicioRESTAsync 1");
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        try {
            httpclient.start();
            //HttpGet request = new HttpGet(target.toURI()+uri.toString());
            Future<HttpResponse> future = httpclient.execute(objetoHttp, null);
            HttpResponse response = future.get();
            logger.trace("consumirServicioRESTAsync Response: " + response.getStatusLine());
        } finally {
            httpclient.close();
        }
        logger.trace("consumirServicioRESTAsync Done");
    }

	public static String consumirServicioRESTAsyncDigestAuth(
			  final String  _protocolo
			, final String  _host
			, final Integer _port
			, final String  _uri
			, final String  _metodo
			, final String  _params
			, final int     _timeout
			, final String  _username
			, final String  _password
			, final String  _DIGESTAUTH_REALM
			, final String  _DIGESTAUTH_NONCE
			)  
		throws ClientProtocolException, IOException, InterruptedException, ExecutionException {

		String result=null;
		
	    HttpHost target = new HttpHost(_host, _port, _protocolo);
	    CredentialsProvider credsProvider = new BasicCredentialsProvider();
	    credsProvider.setCredentials(
	            new AuthScope(target.getHostName(), target.getPort()),
	            new UsernamePasswordCredentials(_username, _password));
	    // Ponemos un timeout minimo 5 sec para no esperar la respuesta
		RequestConfig.Builder requestConfigB = RequestConfig.custom();
        if (_timeout > 0) {
        	requestConfigB.setConnectionRequestTimeout(_timeout);
        	requestConfigB.setSocketTimeout(_timeout).build();        
        }
        RequestConfig requestConfig = requestConfigB.build();
	    
	    try (CloseableHttpAsyncClient httpClient = HttpAsyncClients.custom()
	    		.setDefaultRequestConfig(requestConfig)
	            .setDefaultCredentialsProvider(credsProvider)
	            .build())
	    	{
	        // Create AuthCache instance
	        AuthCache authCache = new BasicAuthCache();
	        // Generate DIGEST scheme object, initialize it and add it to the local
	        // auth cache
	        DigestScheme digestAuth = new DigestScheme();
	        // Suppose we already know the realm name
	        digestAuth.overrideParamter("realm", _DIGESTAUTH_REALM);
	//        // Suppose we already know the expected nonce value
	        digestAuth.overrideParamter("nonce", _DIGESTAUTH_NONCE);
	        authCache.put(target, digestAuth);
	
	        HttpPost httpreq = new HttpPost(_uri);
	        
	        if (logger.isDebugEnabled()) {
	        	logger.debug("----------------------------------------");
	        	logger.debug("Executing request " + httpreq.getRequestLine() + " to target " + target);
	        }
	
	        Future<HttpResponse> future;
	        HttpResponse response;
	
	    	httpClient.start();
	        future = httpClient.execute(target, httpreq, null);
	        response = future.get();
	        
	        if (logger.isDebugEnabled()) {
	            try { 
	    			HttpEntity httpEntity = response.getEntity();
	    			result = EntityUtils.toString(httpEntity);
	                logger.debug(result);
	                logger.debug(""+response.getStatusLine().getStatusCode());   // 200
	                logger.debug(response.getStatusLine().getReasonPhrase()); // OK
	                logger.debug(response.getStatusLine().toString());        // HTTP/1.1 200 OK
	            } catch (HttpHostConnectException e) {
	            	logger.debug("Error "+e.getMessage());
	            }
	        }
	    } catch(Exception e) {
	    	logger.error("consumirServicioRESTAsyncDigestAuth SocketTimeoutException "+e.getMessage());
			boolean launche = true;
			if (e instanceof SocketTimeoutException || 
				e.getCause() instanceof SocketTimeoutException) {
				// No hacemos nada, nosotros hemos puesto el timeout
				// no esperamos respuesta
				if (_timeout > 0) {
					launche = false;
				}
			}
			if (launche)
				throw e;
	    } 
	    return result;
	    
	}

}
