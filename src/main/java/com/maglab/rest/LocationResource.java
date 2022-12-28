package com.maglab.rest;

import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.springframework.beans.factory.annotation.Value;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.ws.rs.Consumes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maglab.model.DbUtils;
import com.maglab.model.Experiment;
import com.maglab.osf.osfUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@Path("")

public class LocationResource {

	final static String authorizeUrl = "https://accounts.osf.io/oauth2/authorize";
	static String tokenurl = "https://accounts.osf.io/oauth2/token";

	@GET
	@Path("all")
	@Produces("application/json")
	/*
	 * Gives list of all experiments from start to end in json format
	 * https://magx.lanl.gov/rest/all?start=20210608&end=20210906
	 */
	public Response listALL(@QueryParam("start") String start, @QueryParam("end") String end) {
		// ExperimentService serv = new ExperimentService();
		DbUtils utils = new DbUtils();
		System.out.println("was here");
		SimpleDateFormat dfs = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd");
		String dstart = null;
		if (start != null) {
			try {
				Date d = null;
				d = dfs.parse(start);
				dstart = sqldf.format(d);
				System.out.println(dstart);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		String dend = null;
		if (end != null) {
			try {
				Date d = null;
				d = dfs.parse(end);
				dend = sqldf.format(d);
				System.out.println(dend);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		List experiments = null;
		if (start == null) {
			experiments = utils.getALL();
		} else {
			experiments = utils.getRange(dstart, dend);
		}
		GenericEntity<List<Experiment>> myEntity = new GenericEntity<List<Experiment>>(experiments) {
		};
		return Response.status(200).entity(myEntity).build();

	}

	@GET
	@Path("now/{id:.*}")
	@Produces("application/json")
	/*
	 * Gives current  experiment for the Cell  in json format
	 * https://myresearch.institute/rest/now/Cell_4
	 */
	public Response listbyNow(@PathParam("id") String cellid) {
		Date now = new Date();
		SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd");
		DbUtils utils = new DbUtils();
		System.out.println("was here1");

		String dr = sqldf.format(now);
		List experiments = utils.getcurrrent(cellid, dr);
		GenericEntity<List<Experiment>> myEntity = new GenericEntity<List<Experiment>>(experiments) {
		};
		return Response.status(200).entity(myEntity).build();

	}

	/*
	 * Gives current  experiment for the Cell with specified start date in json format
	 * https://myresearch.institute/rest/20210608/Cell_4
	 */

	@Path("{date}/{id:.*}")
	@GET
	@Produces("application/json")
	public Response listbyCell(@PathParam("id") String cellid, @PathParam("date") String reqDate) {
		SimpleDateFormat dfs = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd");
		DbUtils utils = new DbUtils();
		System.out.println("was here2");
		Date d = null;
		try {
			d = dfs.parse(reqDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String dr = sqldf.format(d);
		System.out.println(dr);
		List experiments = utils.getcurrrent(cellid, dr);
		GenericEntity<List<Experiment>> myEntity = new GenericEntity<List<Experiment>>(experiments) {
		};
		return Response.status(200).entity(myEntity).build();

	}

	
	/*
	 * osf synchronization methods
	 */
	
	@GET
	@Path("callback")

	public Response listtestbyNow(@QueryParam("code") String inoauthcode, @QueryParam("state") String state) {
		Date now = new Date();
		SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd");

		osfUtils osfu = new osfUtils();
		System.out.println("was here1 in redirect" + inoauthcode);
		System.out.println("was here1 in redirect" + state);
		String dr = sqldf.format(now);

		Entry en = osfu.do_token(inoauthcode, "");
		String result = (String) en.getValue();
		// String result=osfu.do_post(tokenurl,POST_PARAMS,"");
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonElement jsonElement = new JsonParser().parse(result);

		JsonObject root = jsonElement.getAsJsonObject();
		String ast = root.get("access_token").getAsString();
		String expire = root.get("expires_in").getAsString();
		String rt = root.get("refresh_token").getAsString();
		System.out.println("ast"+ast);
		System.out.println("rt"+rt);
		osfUtils osf = new osfUtils();
		String user = osf.create_initial_project_experiment_wiki(ast, state, expire,rt);

		// String json = "{user:\"" + user + "\"}";
		DbUtils utils = new DbUtils();

		SimpleEntry entry = utils.select_osftokeninfo(state, "exp");
		if (entry != null) {
			String expnode = (String) entry.getValue();
			String url = "https://osf.io/" + expnode + "/";
			System.out.println(url);
			// ResponseBuilder r = Response.ok(json);
			// r.status(200);
			URI externalUri = null;
			try {
				externalUri = new URI(url);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// return r.build();
			return Response.seeOther(externalUri).build();
		} else {
			ResponseBuilder r = Response.ok("osf  authorization failed");
			r.status(403);
			return r.build();
		}
	}
	

//	 https://api.test.osf.io/v2/users/{user_id}/addons/
	
	@Path("submit")
	@POST
	// @Consumes(MediaType.APPLICATION_JSON)
	//this method was for testing
	public Response doPost(String msg) {
		UUID uuid = UUID.randomUUID();
		String n = uuid.toString();
		File targetFile = new File("./test/N" + n + ".json");
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(targetFile);

			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.print(msg);
			printWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResponseBuilder r = Response.status(201);
		return r.build();
	}
   
	@GET 
	@Path("addons")
	public Response checkAddons( @QueryParam("expid") String expid, @QueryParam("addonid") String addid,@QueryParam("station") String station) {
		DbUtils utils = new DbUtils();
		//String addonurl= "https://api.osf.io/v2/addons/";
		osfUtils osfu = new osfUtils();	
		this.docheck(expid,  station);
		SimpleEntry entry = utils.select_osftokeninfo(expid, "exp");
		String expnode = (String) entry.getValue();
		String token = (String) entry.getKey();
		System.out.println("expnode" + expnode);
		System.out.println("token"+token);
		String userurl=osfu.uurl+"me/";
		Entry er = osfu.get_info(userurl, token);
		String r = (String) er.getValue();
		System.out.println("r" + r);
		//JsonElement jsonEl = new JsonParser().parse(r);

		//JsonObject user = jsonEl.getAsJsonObject();

		//JsonObject data = user.get("data").getAsJsonObject();

		//String userid = data.get("id").getAsString();
		//System.out.println("userid" + userid);
		//String ua ="https://api.osf.io/v2/users/"+userid + "/addons/";
		//Entry ee=osfu.get_info(ua, token);
		//Integer status0 = (Integer) ee.getKey();
		//String resulte = (String) ee.getValue();
		//System.out.println(resulte);
		                       //http://api.osf.io/v2/nodes/gaz5n/addons/box/
		

		//String nodeanurl="https://api.test.osf.io/v2/nodes/"+expnode+"/addons/";
		String nodeanurl="https://api.osf.io/v2/nodes/"+expnode+"/addons/";
		//https://api.osf.io/v2/addons/
		System.out.println (nodeanurl);
		//String nodeaddonurl = "https://api.osf.io/v2/nodes/"+expnode+"/addons/"+addid+"/folders";
		Entry e=osfu.get_info(nodeanurl, token);
		Integer status = (Integer) e.getKey();
		String result = (String) e.getValue();
		System.out.println(result);
		if (status == null)
			status = 403;
		if (result == null)
			result = "hub problem";
       
		
		ResponseBuilder rr = Response.status(status).entity(result);
		return rr.build();
	}
			
	
	@Path("updatewiki")
	@PUT
	// @Consumes(MediaType.APPLICATION_JSON)
	/*
	 * https://magx.lanl.gov/rest/updatewiki?name=p004_113021.md&expid=P19635-E002-PF
	 */
	public Response doOSFPUT(String msg, @QueryParam("name") String name, @QueryParam("expid") String expid) {
		DbUtils utils = new DbUtils();
		osfUtils osfu = new osfUtils();
		SimpleEntry entry = utils.select_osftokeninfo(expid, "wiki");
		String wikinode = (String) entry.getValue();
		String token = (String) entry.getKey();
		System.out.println("wikinode" + wikinode);
		SimpleEntry expentry = utils.select_osftokeninfo(expid, "exp");
		String expnode = (String) entry.getValue();
		System.out.println(expnode);
		// String wtext=osfu.get_wiki_text(expid, token);
		String content = osfu.get_wiki_content(expnode, token, wikinode);
		String cwiki = content + msg;
		System.out.println(cwiki);
		String wikiurl = "https://api.osf.io/v2/wikis/" + wikinode + "/versions/";
		String wj = osfu.append_json_wiki(wikinode, cwiki);
		// String result = osfu.do_post(wikiurl, wj, token);
		Entry e = osfu.do_post(wikiurl, wj, token);
		Integer status = (Integer) e.getKey();
		System.out.println("wiki post status" + status);
		String result = (String) e.getValue();
		// String result = osfu.do_put_wiki(puturl, cwiki, token);
		System.out.println(result);
		if (status == null)
			status = 403;
		if (result == null)
			result = "hub problem";
		/*
		 * UUID uuid = UUID.randomUUID(); String n=uuid.toString(); File targetFile =
		 * new File("./test/N"+n+".md"); FileWriter fileWriter; try { fileWriter = new
		 * FileWriter(targetFile);
		 * 
		 * PrintWriter printWriter = new PrintWriter(fileWriter);
		 * printWriter.print(msg); printWriter.close();} catch (IOException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		ResponseBuilder r = Response.status(status).entity(result);
		return r.build();
	}

	@Path("updatefile")
	@PUT
	// @Consumes(MediaType.APPLICATION_JSON)
	/*
	 * https://magx.lanl.gov/rest/updatefile?name=p004_113021.tdms&expid=P19635-E002-PF
	 */
	public Response doOSFPUTFIle(InputStream in, @QueryParam("name") String name, @QueryParam("expid") String expid, @QueryParam("folder") String folderpath) {
		DbUtils utils = new DbUtils();
		String fpath=""; 
		SimpleEntry entry = utils.select_osftokeninfo(expid, "exp");
		String expnode = (String) entry.getValue();
		String token = (String) entry.getKey();
		osfUtils osfu = new osfUtils();
		if (folderpath!=null){
		String putfolderurl = "https://files.osf.io/v1/resources/" + expnode + "/providers/osfstorage/?kind=folder&name=" +folderpath;
		 
		Entry f = osfu.do_put_folder(putfolderurl,  token);
		if (f != null) {
			String fresult = (String) f.getValue();
			Integer fcode = (Integer) f.getKey();
			System.out.println("folder:"+fresult);
			System.out.println("osf folder status: " + fcode);
			JsonElement jsonEl = new JsonParser().parse(fresult);
			JsonObject user = jsonEl.getAsJsonObject();

			JsonObject data = user.get("data").getAsJsonObject();

			fpath = data.get("path").getAsString();
			System.out.println("folder path:"+fpath);
		}
		}
		String puturl = "https://files.osf.io/v1/resources/" + expnode + "/providers/osfstorage/"+fpath+"?kind=file&name="
				+ name;
		
		ResponseBuilder r;
		Entry en = (SimpleEntry) osfu.do_put_file(puturl, in, token);
		if (en != null) {
			String result = (String) en.getValue();
			Integer code = (Integer) en.getKey();
			System.out.println(result);
			System.out.println("osf status: " + code);
			r = Response.status(code).entity(result);
		} else {
			r = Response.status(201);// new resource created
		}
		return r.build();
	}

	@Path("auth")
	@GET
	/*  this method initiates a osf form that asks the user to grant authorization for application to sync data. 
	 *  https://magx.lanl.gov/rest/auth?expid=P19635-E002-PF&station=Cell_4
	 */
	public Response doredir(@QueryParam("expid") String expid, @QueryParam("station") String station) {

		DbUtils utils = new DbUtils();
		//this we indicate that process of auth started
		utils.insert_auth(expid, "authorizing", station);

		URI externalUri = null;
		try {
			externalUri = new URI(
					"https://accounts.osf.io/oauth2/authorize?response_type=code&" + "client_id=" + osfUtils.clientID
							+ "&redirect_uri=" + osfUtils.callbackurl + "&scope=osf.full_write&state=" + expid +"&access_type=offline");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.seeOther(externalUri).build();

	}

	@Path("logoff")
	@GET
	/*
	 *  http://<hostname>/rest/logoff?expid=P19635-E002-PF
	 */
	public Response dologoff(@QueryParam("expid") String expid, @QueryParam("name") String name) {
		DbUtils utils = new DbUtils();
		osfUtils osf = new osfUtils();
		Integer status = 204;
		String result = "";
		//SimpleEntry entry = utils.select_osftokeninfo(expid, "user");
		Map mosf=utils.select_osfinfo(expid);
		if (mosf.containsKey("access_token")) {
		//if (entry != null) {
			//String token = (String) entry.getValue();
			String token =(String) mosf.get("access_token");
			String refresh_token=(String) mosf.get("refresh_token");
			System.out.println("token" + token);
			if (token != null) {
				Entry en = osf.do_token(token, "revoke");
				
				status = (Integer) en.getKey();
				System.out.println("revoked:" + status);
				result = (String) en.getValue();
				System.out.println(result);
				String dt =(String) mosf.get("dtgranted");
				// status R -revoke
				
              Entry ent = osf.do_token(refresh_token, "revoke");
				
				status = (Integer) ent.getKey();
				System.out.println("refresh_revoked:" + status);
				result = (String) ent.getValue();
				System.out.println(result);
				String dt2 =(String) mosf.get("dtgranted");
				
				
				utils.update_token_status(token, expid, dt,"R");
				
				
			}
		//}
		}
		// ResponseBuilder r = Response.ok("osf token revoked");
		// status 400 -token expired;
		// 204 -token revoked

		String user = "unauthorized";
		String json = "{\"user\":\"" + user + "\",\"expid\":\"" + expid + "\"}";

		ResponseBuilder r = Response.ok(json);

		r.status(200);
		return r.build();

	}

	@Path("status")
	@GET
	/*
	 *  https://magx.lanl.gov/rest/status?expid=P19635-E002-PF&station=Cell_4
	 */
	public Response docheck(@QueryParam("expid") String expid, @QueryParam("station") String station) {
		DbUtils utils = new DbUtils();
		osfUtils osf = new osfUtils();
		String userdefault = "unauthorized";
		String nodeurl = osf.uurl + "me/";
		SimpleEntry entry = utils.select_osftokeninfo(expid, "user");
		Map mosf=utils.select_osfinfo(expid);
		
		if (entry != null) {
			String token = (String) entry.getValue();
			//String token = mosf.get("acess_token");
			System.out.println(token);

			String user = (String) entry.getKey();

			System.out.println("method:status user" + user);
			if (user == null) {
				user = userdefault;
			} else if (token == null) {
			} else {
               //check osf that token is not expired
				Entry ep = osf.get_info(nodeurl, token);
				int code = (Integer) ep.getKey();
				System.out.print("method:checkcode:" + code);
				Object String;
				
				
				//user name will be forwarded to aqn panel
				//if (code == 200) {
					if (code != 200) {
					//token expired
					//get refresh token from db
					
					String rftoken=null;
					if  (mosf.containsKey("refresh_token"))
					 rftoken=(String) mosf.get("refresh_token");
					System.out.println("refresh"+rftoken);
					//ask osf to refresh token
					Entry  e = osf.refresh_token(rftoken);
					String result = (String) e.getValue();
					int hcode= (Integer) e.getKey();
					//token refreshed
					  if (hcode==200) {
					   Gson gson = new GsonBuilder().setPrettyPrinting().create();
					   JsonElement jsonElement = new JsonParser().parse(result);

					    JsonObject root = jsonElement.getAsJsonObject();
					    String ast = root.get("access_token").getAsString();
					    String expire = root.get("expires_in").getAsString();
					    //String rt = root.get("refresh_token").getAsString();
					    System.out.println("new ast:"+ast);
					    //System.out.println(rt);
					    String dt=(String) mosf.get("dtgranted");
					    utils.update_token(ast, expid, dt,expire);
					   }else {
					    //old behavier tell that token expired
						   //renewal of token  failed
					   System.out.print("checkresponse:" + (String) ep.getValue());
					    user = userdefault; }
					
				}
			}
			String json = "{\"user\":\"" + user + "\",\"expid\":\"" + expid + "\"}";

			ResponseBuilder r = Response.ok(json);

			r.status(200);
			return r.build();
		} else {
			String json = "{\"user\":\"" + userdefault + "\",\"expid\":\"" + expid + "\"}";

			ResponseBuilder r = Response.ok(json);
			r.status(200);
			return r.build();
		}
	}

}
