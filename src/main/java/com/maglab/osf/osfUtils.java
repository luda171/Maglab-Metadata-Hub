package com.maglab.osf;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.RequestLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maglab.PropConfig;
import com.maglab.model.DbUtils;
import com.maglab.model.Experiment;

public class osfUtils {

	static PropConfig pconf = PropConfig.getInstance();

	static public String apiurl = pconf.all().get("osf.api");
	// "https://api.osf.io/v2/nodes/";
	static public String uurl = pconf.all().get("osf.userurl");
	// "https://api.osf.io/v2/users/";
	static public String clientID = pconf.all().get("maglabfairdata.clientID");

	static public String clientSecret = pconf.all().get("maglabfairdata.clientSecret");

	static public String callbackurl = pconf.all().get("osf.callback");

	static public String tokenurl = pconf.all().get("osf.tokenurl");
	static public String proxyhost = pconf.all().get("proxyhost");
	static public String revokeurl = "https://accounts.osf.io/oauth2/revoke";

	static HttpClient httpClient;
	static RequestConfig config;

	static {

		HttpHost proxy = new HttpHost(proxyhost, 8080, "http");

		/*
		 * DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy) {
		 * 
		 * @Override public HttpRoute determineRoute(final HttpHost host, final
		 * HttpRequest request, final HttpContext context) throws HttpException { String
		 * hostname = host.getHostName(); if (hostname.equals("127.0.0.1") ||
		 * hostname.equalsIgnoreCase("localhost") ||
		 * hostname.toLowerCase().contains("osf.io") ||
		 * hostname.toLowerCase().contains("lanl.gov")) { // Return direct route return
		 * new HttpRoute(host); } return super.determineRoute(host, request, context); }
		 * };
		 */
		httpClient = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).evictExpiredConnections()
				// .setRoutePlanner(routePlanner)
				 //.setConnectTimeout(30000)
			     //   .setSocketTimeout(5000)
				.build();

		config = RequestConfig.custom().setProxy(proxy).setConnectTimeout(60000)
		        .setSocketTimeout(60000).build();

	}

	static String sproxy = pconf.all().get("osf.proxy");
	boolean proxy = Boolean.parseBoolean(sproxy);
	// temporary
	static String f_proxy = pconf.all().get("osf.proxy.file");
	boolean fproxy = Boolean.parseBoolean(f_proxy);
	// boolean proxy = false;
	String osf_name = "";
	Logger logger = LogManager.getLogger(getClass());
	
	public String check_providers( String expnode, String token) {
	//String url="https://api.test.osf.io/v2/nodes/"+expnode+"/files/providers/{provider}/"
		String nodeanurl="https://api.osf.io/v2/nodes/"+expnode+"/addons/";
		String p = "osfstorage";
		//https://api.osf.io/v2/addons/
		System.out.println (nodeanurl);
		//String nodeaddonurl = "https://api.osf.io/v2/nodes/"+expnode+"/addons/"+addid+"/folders";
		Entry e=get_info(nodeanurl, token);
		Integer status = (Integer) e.getKey();
		String result = (String) e.getValue();
		
		JsonElement jsonEl2 = new JsonParser().parse(result);
		JsonObject obj = jsonEl2.getAsJsonObject();
		JsonArray jarray = obj.getAsJsonArray("data");
		if (jarray!=null) {
		for (JsonElement pa : jarray) {
			JsonObject pObj = pa.getAsJsonObject();
		JsonElement pname = pObj.get("attributes").getAsJsonObject().get("id");
		String fname = (pname instanceof JsonNull) ? "" : pname.getAsString();
		System.out.println(fname);
		if (!fname.equals(p)){
			p = fname;
		}
		}
		}
		return p;
	}

	
	public String get_folder_with_next(String checkurl,String token,String  folderpath) {
		String fid="";
		Entry fr =  get_info(checkurl, token);
		String result = (String) fr.getValue();
		System.out.println("folderlist:"+result);
		logger.debug("Folder list: " + result);
		Integer frcode = (Integer) fr.getKey();
		System.out.println(frcode);
		System.out.println("osf folder status: " + frcode);
		JsonElement jsonEl2 = new JsonParser().parse(result);
		JsonObject obj = jsonEl2.getAsJsonObject();
		
		JsonArray jarray = obj.getAsJsonArray("data");
		for (JsonElement pa : jarray) {
			JsonObject pObj = pa.getAsJsonObject();
		JsonElement folderk = pObj.get("attributes").getAsJsonObject().get("kind");
		String fkname = (folderk instanceof JsonNull) ? "" : folderk.getAsString();
		JsonElement foldername = pObj.get("attributes").getAsJsonObject().get("name");
		String fname = (foldername instanceof JsonNull) ? "" : foldername.getAsString();
		if (fkname.equals("folder")&& fname.equals(folderpath)) {
			 JsonElement foldpath = pObj.get("attributes").getAsJsonObject().get("path");
			 fid = (foldpath instanceof JsonNull) ? "" : foldpath.getAsString();
			 System.out.println("folder path existed:"+fid);
			 logger.debug("Folderid: " + fid);
			 break;
		}
		}
		JsonElement jnext = obj.get("links").getAsJsonObject().get("next");
		String next = (jnext instanceof JsonNull) ? "" : jnext.getAsString();

		Boolean donext = false;
		
		if (next.equals(""))
			donext = false;
		else {
			donext = true;
		}
		if ( fid.equals("") && donext) {
			fid = get_folder_with_next(next,token,folderpath);
		}
		return fid;
	}
	
	public String check_folders(String expNode, String token, String folderPath, String provider) {
	    // Construct URLs for checking and creating folders
	    String putFolderUrl = String.format("https://files.osf.io/v1/resources/%s/providers/%s/?kind=folder&name=%s", expNode, provider, folderPath);
	    String checkUrl = String.format("https://api.osf.io/v2/nodes/%s/files/%s/?filter[name]=%s&filter[kind]=folder", expNode, provider, folderPath);

	    logger.debug("Folder check URL: " + checkUrl);
	    
	    // Get the folder ID
	    String folderId = get_folder_with_next(checkUrl, token, folderPath);
	   
	    // If the folder does not exist, create it
	    if (folderId.isEmpty()) {
	        logger.debug("Folder does not exist, attempting to create: " + putFolderUrl);

	        Entry<Integer, String> folderCreationResponse = do_put_folder(putFolderUrl, token);

	        if (folderCreationResponse != null) {
	            int responseCode = folderCreationResponse.getKey();
	            String responseMessage = folderCreationResponse.getValue();
	            logger.debug("Folder creation response: " + responseMessage);
	            logger.debug("OSF folder status code: " + responseCode);

	            if (responseCode >= 400) {
	                return String.format("error:%d:%s", responseCode, responseMessage);
	            }

	            //JsonElement jsonElement = JsonParser.parseString(responseMessage);
	            JsonElement jsonElement = new JsonParser().parse(responseMessage);
	            JsonObject data = jsonElement.getAsJsonObject().get("data").getAsJsonObject();
	            
	            if (data != null) {
	                JsonElement pathElement = data.get("attributes").getAsJsonObject().get("path");
	                folderId = (pathElement instanceof JsonNull) ? "" : pathElement.getAsString();
	                logger.debug("Folder path created: " + folderId);
	            }
	        }
	    }

	    String folderPathWithProvider = provider + folderId;
	    logger.debug("Final folder path: " + folderPathWithProvider);
	    
	    return folderPathWithProvider;
	}

    public String check_folders0( String expnode, String token, String folderpath,String provider) {
	String putfolderurl = "https://files.osf.io/v1/resources/" + expnode + "/providers/"+provider+"/?kind=folder&name=" +folderpath;
	String fid="";
	 //get list of folders
	//String checkurl="https://api.osf.io/v2/nodes/"+expnode+"/files/" +provider+"/?filter[kind]=folder";
	//String checkurl="https://api.osf.io/v2/nodes/"+expnode+"/files/" +provider+"/?filter[name]="+folderpath;
	String checkurl="https://api.osf.io/v2/nodes/"+expnode+"/files/" +provider+"/?filter[name]="+folderpath+"&filter[kind]=folder";
	   logger.debug("folder check  URL: " + checkurl);
	fid = get_folder_with_next(checkurl,token,folderpath);
//if folder not exists create it 
	if ( fid.equals("")) {
	Entry f = do_put_folder(putfolderurl,  token);
	
	if (f != null) {
		String fresult = (String) f.getValue();
		Integer fcode = (Integer) f.getKey();
		System.out.println("folder:"+fresult);
		
		System.out.println("osf folder status: " + fcode);
		if (fcode>=400) return "error:"+fcode + ":"+fresult;
		JsonElement jsonEl = new JsonParser().parse(fresult);
		JsonObject user = jsonEl.getAsJsonObject();

		JsonObject data = user.get("data").getAsJsonObject();
        if (data!=null) {
        	JsonElement foldpath = data.get("attributes").getAsJsonObject().get("path");
   		 fid = (foldpath instanceof JsonNull) ? "" : foldpath.getAsString();
		
		System.out.println("folder path created:"+fid);
        }
        
	}
	}
	String fp=provider+fid;
	return fp;
}
	public Entry get_info(String geturl, String token) {
		HttpGet get = new HttpGet(geturl);
		System.out.println("get_info");
		String result = null;
		AbstractMap.SimpleEntry<Integer, String> entry = null;
		if (proxy) {
			get.setConfig(config);
		}
		// get.addHeader("Content-Type", "application/vnd.api+json");
		get.addHeader("Authorization", "Bearer " + token);
		try {
			HttpResponse response = httpClient.execute(get);
			int code = response.getStatusLine().getStatusCode();
			HttpEntity resentity = response.getEntity();
			result = EntityUtils.toString(resentity);
			entry = new AbstractMap.SimpleEntry<>(code, result);
			System.out.println(geturl);
			System.out.println(result);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			get.releaseConnection();
		}
		// return result;
		return entry;
	}


	public Entry refresh_token(String rftoken) {
		HttpPost post = null;
		AbstractMap.SimpleEntry<Integer, String> entry = null;
		post = new HttpPost(tokenurl);
		if (proxy) {
			post.setConfig(config);
		}
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("refresh_token", rftoken));
		nameValuePairs.add(new BasicNameValuePair("client_id", clientID));
		nameValuePairs.add(new BasicNameValuePair("client_secret", clientSecret));

		nameValuePairs.add(new BasicNameValuePair("grant_type", "refresh_token"));
		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			post.addHeader("Content-Type", "application/x-www-form-urlencoded");

			RequestLine rl = post.getRequestLine();
			String s = rl.toString();
			System.out.println(s);

			HttpResponse response = httpClient.execute(post);
			String result = null;
			int status = response.getStatusLine().getStatusCode();
			System.out.println("refreshposttoken:" + status);
			HttpEntity resentity = response.getEntity();
			result = EntityUtils.toString(resentity);
			System.out.println(result);
			entry = new AbstractMap.SimpleEntry<>(status, result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return entry;
	}

	public Entry do_token(String code, String action) {
		HttpPost post = null;
		AbstractMap.SimpleEntry<Integer, String> entry = null;
		if (action.equals("revoke")) {
			post = new HttpPost(revokeurl);

		} else {
			post = new HttpPost(tokenurl);
		}
		String result = null;
		if (proxy) {
			post.setConfig(config);
		}

		// String POST_PARAMS = "code=" + code + "&client_id=" + clientID +
		// "&client_secret=" + clientSecret
		// + "&redirect_uri=" + callbackurl + "&grant_type=authorization_code";
		// System.out.println(POST_PARAMS);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		if (!action.equals("revoke")) {
			nameValuePairs.add(new BasicNameValuePair("code", code));
			nameValuePairs.add(new BasicNameValuePair("client_id", clientID));
			nameValuePairs.add(new BasicNameValuePair("client_secret", clientSecret));

			nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
			nameValuePairs.add(new BasicNameValuePair("redirect_uri", callbackurl));

		} else {
			nameValuePairs.add(new BasicNameValuePair("token", code));
		}
		try {

			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// StringEntity entity = new StringEntity(POST_PARAMS);
			// post.setEntity(entity);
			post.addHeader("Content-Type", "application/x-www-form-urlencoded");

			RequestLine rl = post.getRequestLine();
			String s = rl.toString();
			System.out.println(s);

			HttpResponse response = httpClient.execute(post);

			int status = response.getStatusLine().getStatusCode();
			System.out.println("posttoken:" + status);
			HttpEntity resentity = response.getEntity();
			result = "";
			if (resentity != null) {
				result = EntityUtils.toString(resentity);
				System.out.println(result);
			}
			entry = new AbstractMap.SimpleEntry<>(status, result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		finally {
			post.releaseConnection();
		}
		return entry;
	}
    public List get_contributers(String expnode, String token) {
    	List  users_ids = new ArrayList();
    	String url_list_contributers="https://api.test.osf.io/v2/nodes/"+expnode+"/contributors/";	
    	System.out.println(url_list_contributers);
		//String urlcheck_permissions ="https://api.test.osf.io/v2/nodes/"+expnode+"/contributors/"+userid+"/";
    	try  {
    	Entry perm = get_info(url_list_contributers, token);
		//String pp = (String) perm.getKey();
		//System.out.println("Permissions:"+pp);
		  String pp = (String) perm.getValue();
		  Gson gson = new Gson();
	        JsonObject jsonObject = gson.fromJson(pp, JsonObject.class);
	        JsonArray dataArray = jsonObject.getAsJsonArray("data");

	        for (JsonElement element : dataArray) {
	            JsonObject userObject = element.getAsJsonObject();

	            // Get the "users" section for each user
	            JsonObject users = userObject.getAsJsonObject("relationships").getAsJsonObject("users");

	            // Get the "related" section within "users"
	            JsonObject related = users.getAsJsonObject("links").getAsJsonObject("related");

	            // Get the "href" value
	            String href = related.get("href").getAsString();

	            // Extract user IDs from the "href"
	            String[] parts = href.split("/");
	            String userId = parts[parts.length - 1]; // Extracting the user ID
	            System.out.println("Project User ID: " + userId);
	            users_ids.add( userId);
	        }
    	}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	        return users_ids;
    }
	public AbstractMap.SimpleEntry do_post(String posturl, String body, String token) {
		HttpPost post = new HttpPost(posturl);
		String result = null;
		AbstractMap.SimpleEntry<Integer, String> entry = null;

		if (proxy) {
			post.setConfig(config);
		}
		post.addHeader("Content-Type", "application/vnd.api+json");
		post.addHeader("Authorization", "Bearer " + token);
		StringEntity entity;
		try {
			entity = new StringEntity(body);
			post.setEntity(entity);
			HttpResponse response = httpClient.execute(post);

			int code = response.getStatusLine().getStatusCode();
			HttpEntity resentity = response.getEntity();
			result = EntityUtils.toString(resentity);
			// System.out.println(result);
			entry = new AbstractMap.SimpleEntry<>(code, result);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		// return result;
		return entry;

	}
	public AbstractMap.SimpleEntry do_put(String posturl, String body, String token) {
		HttpPut post = new HttpPut(posturl);
		String result = null;
		AbstractMap.SimpleEntry<Integer, String> entry = null;

		if (proxy) {
			post.setConfig(config);
		}
		post.addHeader("Content-Type", "application/vnd.api+json");
		post.addHeader("Authorization", "Bearer " + token);
		StringEntity entity;
		try {
			entity = new StringEntity(body);
			post.setEntity(entity);
			HttpResponse response = httpClient.execute(post);

			int code = response.getStatusLine().getStatusCode();
			HttpEntity resentity = response.getEntity();
			result = EntityUtils.toString(resentity);
			// System.out.println(result);
			entry = new AbstractMap.SimpleEntry<>(code, result);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		// return result;
		return entry;

	}
//not used
	public String do_put_wiki(String posturl, String body, String token) {
		HttpPut post = new HttpPut(posturl);
		String result = null;
		// if (proxy) {
		post.setConfig(config);
		// }
		// post.addHeader("Content-Type", "application/vnd.api+json");
		post.addHeader("Authorization", "Bearer " + token);
		// post.addHeader("Content-Type", "text/plain ; charset=UTF-8");
		post.addHeader("Content-Type", "text/markdown; charset=UTF-8");
		StringEntity entity;
		try {
			entity = new StringEntity(body);
			post.setEntity(entity);
			HttpResponse response = httpClient.execute(post);

			int code = response.getStatusLine().getStatusCode();
			HttpEntity resentity = response.getEntity();
			result = EntityUtils.toString(resentity);
			System.out.println(result);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return result;

	}

	public Entry do_put_file(String posturl, InputStream in, String token) {
		HttpPut post = new HttpPut(posturl);
		AbstractMap.SimpleEntry<Integer, String> entry = null;

		String result = null;
		if (fproxy) {
			post.setConfig(config);
		}
		post.addHeader("Content-Type", "application/octet-stream");
		post.addHeader("Authorization", "Bearer " + token);

		// post.addHeader("Content-Type", "text/plain ; charset=UTF-8");
		// post.addHeader("Content-Type","text/markdown; charset=UTF-8");
		InputStreamEntity entity;

		try {
			// entity = new StringEntity(body);
			entity = new InputStreamEntity(in);
			post.setEntity(entity);
			HttpResponse response = httpClient.execute(post);

			int code = response.getStatusLine().getStatusCode();
			HttpEntity resentity = response.getEntity();
			result = EntityUtils.toString(resentity);
			logger.debug("OSF do put file: " + result);
			System.out.println(result);
			entry = new AbstractMap.SimpleEntry<>(code, result);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return entry;

	}

	public Entry do_put_folder(String posturl, String token) {
		HttpPut post = new HttpPut(posturl);
		AbstractMap.SimpleEntry<Integer, String> entry = null;

		String result = null;
		if (fproxy) {
			post.setConfig(config);
		}
		post.addHeader("Content-Type", "application/octet-stream");
		post.addHeader("Authorization", "Bearer " + token);

		// post.addHeader("Content-Type", "text/plain ; charset=UTF-8");
		// post.addHeader("Content-Type","text/markdown; charset=UTF-8");
		InputStreamEntity entity;

		try {
			// entity = new StringEntity(body);
		//	entity = new InputStreamEntity(in);
		//	post.setEntity(entity);
			HttpResponse response = httpClient.execute(post);

			int code = response.getStatusLine().getStatusCode();
			HttpEntity resentity = response.getEntity();
			result = EntityUtils.toString(resentity);
			System.out.println(result);
			logger.debug("OSF do put folder: " + result);
			entry = new AbstractMap.SimpleEntry<>(code, result);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return entry;

	}
	
	public String compose_json_project(Experiment e) {
		String summary = "Proposal ID:" + e.getProposal_Number() + ",PI:" + e.getPi();
		System.out.println(summary);
		// String title = e.getTitle();
		String ptitle = e.getProposal_Title();
		// if (ptitle!=null )title = ptitle;
		// System.out.println("title" + title);

		String json = "{\"data\":{\"type\":\"nodes\",\"attributes\":{\"title\":\"" + ptitle + "\","
				+ "\"description\":\"" + summary
				+ "\",\"public\":false,\"category\":\"project\",\"tags\":[\"maglab\",\"" + e.getProposal_Number()
				+ "\"]}}}";

		return json;
	}

	public String compose_json_exp(Experiment e) {

		String title = "Experiment " + e.getPID();
		String summary = "-";

		String json = "{\"data\":{\"type\":\"nodes\",\"attributes\":{\"title\":\"" + title + "\","
				+ "\"description\":\"" + summary + "\",\"public\":false,\"category\":\"project\"}}}";

		return json;
	}
	public String compose_json_exp_component(Experiment e,String component) {

		String title = "Component " + e.getPID() + "-"+component;
		String summary = "-";

		String json = "{\"data\":{\"type\":\"nodes\",\"attributes\":{\"title\":\"" + title + "\","
				+ "\"description\":\"" + summary + "\",\"public\":false,\"category\":\"project\"}}}";

		return json;
	}
	// not used
	public String get_wiki_text(String exp_id, String token) {
		String wikitext = null;
		try {
			String wikiurl = apiurl + exp_id + "/wikis/";
			// https://api.osf.io/v2/nodes/krcew/wikis/

			// System.out.println(wikiurl);

			Entry er = get_info(wikiurl, token);
			String r = (String) er.getValue();
			System.out.println("r" + r);

			JsonElement jsonEl = new JsonParser().parse(r);

			JsonObject user = jsonEl.getAsJsonObject();
			JsonObject data = user.get("data").getAsJsonObject();
			// JsonArray data = user.getAsJsonArray("data");
			// String wiki_id = data.get("id").getAsString();
			wikitext = (String) data.get("attributes").getAsJsonObject().get("content").getAsString();
			//System.out.println(wikitext);
		} catch (Exception ee) {
			// TODO Auto-generated catch block
			ee.printStackTrace();
		}
		return wikitext;
	}

	public String get_wiki_content(String exp_id, String token, String wiki_id) {
		String wikicontent = null;
		try {
			// String wikiurl = apiurl + exp_id + "/wikis/"+wiki_id+"/content";
			String wikiurl = "https://api.osf.io/v2/wikis/" + wiki_id + "/content/";
			Entry ewiki = get_info(wikiurl, token);
			wikicontent = (String) ewiki.getValue();
			//System.out.println("wikicontent" + wikicontent);

		} catch (Exception ee) {
			// TODO Auto-generated catch block
			ee.printStackTrace();
		}
		return wikicontent;
	}

	public String compose_json_wiki(Experiment e) {

		String etitle = e.getTitle();
		String ptitle = e.getProposal_Title();
		String facility = e.getFacility();
		String pi = e.getPi();
		String ms = e.getMagnet_System();
		String pn = e.getProposal_Number();
		int id = e.getId();
		// this can be wrong
		String d = e.getDTSTART();
		String end = e.getDTEND();

		String json = "{\"data\":{\"type\":\"wikis\",\"attributes\":{\"name\":\"home\",\"content\":" + "\"Facility: "
				+ facility + "  \\nExperiment Title:" + etitle + " \\nMagnet System:" + ms + " \\nProposal Title:"
				+ ptitle + "\\nProposal Number:" + pn + "  \\nPI:" + pi
				+ " \\n\\nhttps://users.magnet.fsu.edu:443/Experiments/Display.aspx?ExperimentID=" + id + "\\n\\n" + d
				+ " -- " + end + "\"}}}";

		return json;
	}

	public String append_json_wiki(String wikiID, String content) {

		data wiki = new data();
		attributes att = new attributes();
		att.setName("home");
		att.setContent(content);
		wiki.setId(wikiID);
		wiki.setType("wiki-versions");
		wiki.setAttributes(att);
		Gson gson = new Gson();
		JsonElement jsonElement = gson.toJsonTree(wiki);
		String s = gson.toJson(jsonElement);
		System.out.println(s);
		// String mjson=
		// "{\"data\":{\"id\":\""+wikiID+"\",\"type\":\"wiki-versions\",\"attributes\":{\"name\":\"home\",\"content\":\""+
		// content +"\"}}}";
		String mjson = "{\"data\":" + s + "}";
		//System.out.println(mjson);
		return mjson;
	}

	public class data {
		String id;
		String type;
		attributes attributes;

		public void setId(String id) {
			this.id = id;
		}

		public void setType(String type) {
			this.type = type;

		}

		public void setAttributes(attributes att) {
			this.attributes = att;

		}
	}

	public class attributes {
		String name;
		String content;

		public String getName() {
			return name;
		}

		public String geContent() {
			return content;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setContent(String content) {
			this.content = content;
		}
	}
}
