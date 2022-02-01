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
				.build();

		config = RequestConfig.custom().setProxy(proxy).build();

	}

	static String sproxy = pconf.all().get("osf.proxy");
	boolean proxy = Boolean.parseBoolean(sproxy);
	// temporary
	static String f_proxy = pconf.all().get("osf.proxy.file");
	boolean fproxy = Boolean.parseBoolean(f_proxy);
	// boolean proxy = false;
	String osf_name = "";

	public Entry get_info(String geturl, String token) {
		HttpGet get = new HttpGet(geturl);
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

	public String get_userprojects_next(String nodeurl, String token, String proposal, String field) {
		String proj_id = null;
		Entry ep = get_info(nodeurl, token);
		String p = (String) ep.getValue();
		JsonElement jsonEl2 = new JsonParser().parse(p);
		JsonObject obj = jsonEl2.getAsJsonObject();
		JsonArray jarray = obj.getAsJsonArray("data");

		JsonElement jnext = obj.get("links").getAsJsonObject().get("next");
		String next = (jnext instanceof JsonNull) ? "" : jnext.getAsString();

		Boolean donext = false;
		// if (next.equals("null")) donext=false;
		if (next.equals(next))
			donext = false;
		else {
			donext = true;
		}
		System.out.println(next);
		for (JsonElement pa : jarray) {
			JsonObject prObj = pa.getAsJsonObject();
			// description
			String desc = (String) prObj.get("attributes").getAsJsonObject().get(field).getAsString();
			if (desc.contains(proposal)) {
				proj_id = (String) prObj.get("id").getAsString();
				// System.out.print(proj_id);
				break;
			}
		}
		if (proj_id == null && donext) {
			proj_id = get_userprojects_next(next, token, proposal, field);
			// proj_id=get_userprojects(next, token, proposal);
		}
		return proj_id;
	}

	public String get_userprojects(String userurl, String token, String proposal, String field) {
		String proj_id = null;
		// String userurl=uurl+"me/";
		System.out.println(userurl);
		try {
			Entry er = get_info(userurl, token);
			String r = (String) er.getValue();
			System.out.println("r" + r);
			JsonElement jsonEl = new JsonParser().parse(r);

			JsonObject user = jsonEl.getAsJsonObject();

			JsonObject data = user.get("data").getAsJsonObject();

			String userid = data.get("id").getAsString();
			System.out.println("userid" + userid);
			osf_name = data.get("attributes").getAsJsonObject().get("full_name").getAsString();
			System.out.println(osf_name);
			System.out.println(uurl + userid + "/nodes/");
			Entry ep = get_info(uurl + userid + "/nodes/", token);
			String p = (String) ep.getValue();
			JsonElement jsonEl2 = new JsonParser().parse(p);
			JsonObject obj = jsonEl2.getAsJsonObject();
			JsonArray jarray = obj.getAsJsonArray("data");
			JsonElement jnext = obj.get("links").getAsJsonObject().get("next");
			String next = (jnext instanceof JsonNull) ? "" : jnext.getAsString();

			Boolean donext = false;
			// if (next.equals("null")) donext=false;
			if (next.equals(""))
				donext = false;
			else {
				donext = true;
			}
			System.out.println(next);
			for (JsonElement pa : jarray) {
				JsonObject prObj = pa.getAsJsonObject();
				String desc = (String) prObj.get("attributes").getAsJsonObject().get(field).getAsString();
				if (desc.contains(proposal)) {
					proj_id = (String) prObj.get("id").getAsString();
					// System.out.print(proj_id);
					break;
				}
			}
			if (proj_id == null && donext) {
				proj_id = get_userprojects_next(next, token, proposal, field);
				// proj_id=get_userprojects(next, token, proposal);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// String
		// username=propdata.get("attributes").getAsJsonObject().get("description").getAsString();
		return proj_id;
	}

	public String create_initial_project_experiment_wiki(String token, String pid, String expire, String rtoken) {

		DbUtils dbu = new DbUtils();
		List exp = dbu.getbyPid(pid);
		String prop = pid;
		Experiment e = null;
		if (exp.size() > 0) {
			e = (Experiment) exp.get(0);
			prop = e.getProposal_Number();
		}

		String exp_id = get_userprojects(uurl + "me/", token, pid, "title");
		if (exp_id != null) {
			System.out.println("experiment exists:" + exp_id);
		}
		String pr_id = get_userprojects(uurl + "me/", token, prop, "description");
		if (pr_id == null && exp_id == null) {

			String bod = compose_json_project(e);
			System.out.println("rr:" + bod);
			// String r2 = do_post(apiurl, bod, token);
			Entry e2 = do_post(apiurl, bod, token);
			Integer status = (Integer) e2.getKey();
			System.out.println("status" + status);
			String r2 = (String) e2.getValue();
			JsonElement jsonEl2 = new JsonParser().parse(r2);
			JsonObject obj = jsonEl2.getAsJsonObject();
			pr_id = obj.get("data").getAsJsonObject().get("id").getAsString();
			System.out.println("new project creation:" + r2);
			// need to parse id
		} else {
			System.out.println("existed project id:" + pr_id);
		}
		String wiki_id = "";

		if (exp_id == null) {
			String ex = compose_json_exp(e);
			String eurl = apiurl + pr_id + "/children/";
			// String r3 = do_post(eurl, ex, token);
			Entry e3 = do_post(eurl, ex, token);
			Integer status3 = (Integer) e3.getKey();
			System.out.println("status3" + status3);
			String r3 = (String) e3.getValue();

			System.out.println("new experiment" + r3);

			JsonElement jsonEl2 = new JsonParser().parse(r3);
			JsonObject obj = jsonEl2.getAsJsonObject();
			exp_id = obj.get("data").getAsJsonObject().get("id").getAsString();
			System.out.println("r3" + exp_id);
			String wikiurl = apiurl + exp_id + "/wikis/";
			String wiki = compose_json_wiki(e);
			System.out.print(wiki);
			// String r4 = do_post(wikiurl, wiki, token);
			Entry e4 = do_post(wikiurl, wiki, token);
			Integer status4 = (Integer) e4.getKey();
			System.out.println("status4" + status4);
			String r4 = (String) e4.getValue();

			System.out.println(r4);

			try {
				JsonElement jsonEl3 = new JsonParser().parse(r4);
				JsonObject obj3 = jsonEl3.getAsJsonObject();
				wiki_id = obj3.get("data").getAsJsonObject().get("id").getAsString();

				System.out.println("wikiid" + wiki_id);
			} catch (Exception ee) {
				// TODO Auto-generated catch block
				ee.printStackTrace();
			}

		}

		// get all /nodes/{node_id}/wikis/
		// lookup wikis
		if (wiki_id.equals("")) {
			try {
				String wikiurl = apiurl + exp_id + "/wikis/";
				// https://api.osf.io/v2/nodes/krcew/wikis/

				// System.out.println(wikiurl);

				Entry er = get_info(wikiurl, token);
				String r = (String) er.getValue();
				System.out.println("wikis:" + r);

				JsonElement jsonEl = new JsonParser().parse(r);

				JsonObject user = jsonEl.getAsJsonObject();
				// JsonObject data = user.get("data").getAsJsonObject();
				JsonArray data = user.getAsJsonArray("data");
				for (JsonElement pa : data) {
					JsonObject dat = pa.getAsJsonObject();
					wiki_id = dat.get("id").getAsString();
					break;
				}
			} catch (Exception ee) {
				// TODO Auto-generated catch block
				ee.printStackTrace();
			}
		}
		/*
		 * String wiki = compose_json_wiki(e); System.out.print(wiki); String r4 =
		 * do_post(wikiurl, wiki, token); System.out.println(r4);
		 * 
		 * try { JsonElement jsonEl3 = new JsonParser().parse(r4); JsonObject obj3 =
		 * jsonEl3.getAsJsonObject(); wiki_id =
		 * obj3.get("data").getAsJsonObject().get("id").getAsString();
		 * 
		 * System.out.println("wikiid" + wiki_id); }catch (Exception ee) { // TODO
		 * Auto-generated catch block ee.printStackTrace(); }
		 */
		// dbu.insert_token(response.toString(), state);
		System.out.println("inserted magproj:" + pid);
		System.out.println("inserted proj:" + pr_id);
		System.out.println("inserted exp:" + exp_id);
		System.out.println("inserted wiki:" + wiki_id);
		dbu.insert_token(token, osf_name, expire, pid, pr_id, exp_id, wiki_id, rtoken);
		return osf_name;
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
			System.out.println(wikitext);
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
			System.out.println("wikicontent" + wikicontent);

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
		System.out.println(mjson);
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
