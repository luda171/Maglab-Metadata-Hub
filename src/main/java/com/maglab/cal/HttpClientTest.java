package com.maglab.cal;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;


public class HttpClientTest  {
	 Integer readTimeout=29000; //10sec  The timeout says how long to wait for the other end to send a SYN-ACK in response to the initial SYN packet(s).
	 private static final int TOTAL_MAX_CONNECTIONS = 4000;
	 Integer connectTimeout = readTimeout; //30sec tcp connection handshake
	 int maxConnectionsPerHost=400;
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
     if (hproxy!=null){
     httpClient.getHostConfiguration().setProxy(hproxy,port);
     }
    
      return httpClient;
	}
	 
	 public HttpClient getHttpClient(){
		 return httpClient;
	 }
}
