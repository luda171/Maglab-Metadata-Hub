package com.maglab.cal;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NoHttpResponseException;
import org.apache.commons.httpclient.params.HttpMethodParams;


public class HttpClientTest  {
	 Integer readTimeout=29000; //10sec  The timeout says how long to wait for the other end to send a SYN-ACK in response to the initial SYN packet(s).
	 private static final int TOTAL_MAX_CONNECTIONS = 4000;
	 Integer connectTimeout = readTimeout; //30sec tcp connection handshake
	 int maxConnectionsPerHost=400;
	 int retryCount=3;
	 HttpClient httpClient;
	 
	 public HttpClientTest(Integer readTimeout, Integer connectTimeout) {
		 readTimeout = readTimeout;
		 connectTimeout = connectTimeout;
	 }
	 
	 public HttpClient initclient(String hproxy,int port) {
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
	 
	 public HttpClient getHttpClient(){
		 return httpClient;
	 }
}
