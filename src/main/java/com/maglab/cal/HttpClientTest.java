package com.maglab.cal;
import java.io.IOException;
import javax.net.ssl.*;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NoHttpResponseException;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import java.security.cert.X509Certificate;
public class HttpClientTest  {
	 Integer readTimeout=29000; //10sec  The timeout says how long to wait for the other end to send a SYN-ACK in response to the initial SYN packet(s).
	 private static final int TOTAL_MAX_CONNECTIONS = 4000;
	 Integer connectTimeout = readTimeout; //30sec tcp connection handshake
	 int maxConnectionsPerHost=400;
	 int retryCount=2;
	 HttpClient httpClient;
	 
	 public HttpClientTest(Integer readTimeout, Integer connectTimeout) {
		 readTimeout = readTimeout;
		 connectTimeout = connectTimeout;
	 }
	 
	 public HttpClient initclient(String hproxy,int port) {
		 try {
			trustAllSslCertificates();
		} catch (KeyManagementException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 
		 // Register a custom protocol for HTTPS to bypass SSL
		    Protocol easyHttps = new Protocol("https", (ProtocolSocketFactory) new EasySSLSocketFactory(), 443);
		    Protocol.registerProtocol("https", easyHttps);
	 
	 MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
     connectionManager.getParams().setConnectionTimeout(connectTimeout);
	 connectionManager.getParams().setSoTimeout(readTimeout);
	 connectionManager.getParams().setDefaultMaxConnectionsPerHost(maxConnectionsPerHost);
	 
	 
      httpClient = new HttpClient(connectionManager);
      httpClient.getParams().setParameter("http.useragent", "Maglab Crawl-lanl;Browser");
    //  httpClient.getParams().
    //  setParameter(HttpMethodParams.RETRY_HANDLER, 
      //             new DefaultHttpMethodRetryHandler( 3, false ));
      
      httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new HttpMethodRetryHandler() {
    	     @Override
    	     public boolean retryMethod(final HttpMethod method, final IOException e,
    	             final int executionCount) {
    	         if (executionCount >= retryCount) {
    	             // Do not retry if over max retry count
    	             return false;
    	         }
    	         if (e instanceof NoHttpResponseException) {
    	             // Retry if the server dropped connection on us
    	             return true;
    	         }
    	         if (e instanceof SocketException) {
    	             // Retry if the server reset connection on us
    	             return true;
    	         }
    	         if (e instanceof SocketTimeoutException) {
    	             // Retry if the server reset connection on us
    	             return true;
    	         }
    	        // if (instanceOf(exception, SocketTimeoutException.class)) {
    	             // Retry if the read timed out
    	          //   return true;
    	        // }
    	         if (!method.isRequestSent()) {
    	             // Retry if the request has not been sent fully or
    	             // if it's OK to retry methods that have been sent
    	             return true;
    	         }
    	         // otherwise do not retry
    	         return false;
    	     }
    	 });
     if (hproxy!=null){
     httpClient.getHostConfiguration().setProxy(hproxy,port);
     }
  
      return httpClient;
	}
	 
	 
	 /**
	     * Trust all SSL certificates by configuring a custom TrustManager.
	     */
	    private void trustAllSslCertificates() throws NoSuchAlgorithmException, KeyManagementException {
	        TrustManager[] trustAllCerts = new TrustManager[]{
	            new X509TrustManager() {
	                @Override
	                public void checkClientTrusted(X509Certificate[] chain, String authType) {
	                }

	                @Override
	                public void checkServerTrusted(X509Certificate[] chain, String authType) {
	                }

	                @Override
	                public X509Certificate[] getAcceptedIssuers() {
	                    return null;
	                }
	            }
	        };

	        SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

	        // Disable hostname verification
	        HostnameVerifier allHostsValid = (hostname, session) -> true;
	        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	    }
	 
	 public HttpClient getHttpClient(){
		 return httpClient;
	 }
}
