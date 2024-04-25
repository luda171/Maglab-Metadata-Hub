package com.maglab.rest;

import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractMap;
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

import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.beans.factory.annotation.Value;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.ws.rs.Consumes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maglab.instruments.Instrument;
import com.maglab.instruments.InstrumentService;
import com.maglab.model.DbUtils;
import com.maglab.model.Experiment;
import com.maglab.osf.osfProject;
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
	//@SpringBean
	//private InstrumentService iservice;
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

	@GET
	@Path("probs/")
	@Produces("application/json")
	public Response listProbs() {
		//InstrumentService service =new InstrumentService();
		//List instruments=null;
		System.out.println("in probs");

		DbUtils utils = new DbUtils();
		List <Instrument> instruments=utils.getProbs();
		//List instruments=new ArrayList(iservice.getInstruments());
		GenericEntity<List<Instrument>> myEntity = new GenericEntity<List<Instrument>>(instruments) {
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
	 * OSF synchronization methods
	 */
	
	@GET
	@Path("callback")

	public Response initProjectNow(@QueryParam("code") String inoauthcode, @QueryParam("state") String state) {
		//Date now = new Date();
		//SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		//String  currentDate = sqlDateFormat.format(now);
		osfUtils osfu = new osfUtils();
		System.out.println("OAuth Code:" + inoauthcode);
		System.out.println("State:" + state);
		
		String[] parts = state.split("\\|");
		String expid = parts[0]; //expid
		String station = parts[1]; //location
		System.out.println(expid);
	
		Entry tokenEntry = osfu.do_token(inoauthcode, "");
		String result = (String) tokenEntry.getValue();
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonElement jsonElement = new JsonParser().parse(result);

		JsonObject root = jsonElement.getAsJsonObject();
		String accessToken  = root.get("access_token").getAsString();
		String expire = root.get("expires_in").getAsString();
		String refreshToken = root.get("refresh_token").getAsString();
		System.out.println("Access Token:"+accessToken );
		System.out.println("Refresh Token:"+refreshToken);
		//osfUtils osf = new osfUtils();
		osfProject osf = new osfProject();
		String user = osf.create_initial_project_experiment_wiki(accessToken , expid, expire,refreshToken,station);

		// String json = "{user:\"" + user + "\"}";
		DbUtils utils = new DbUtils();

		SimpleEntry entry = utils.select_osftokeninfo(expid, "exp",station);
		if (entry != null) {
			String expnode = (String) entry.getValue();
			String url = "https://osf.io/" + expnode + "/";
			System.out.println(url);
			
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
	public Response checkAddons( @QueryParam("expid") String expId, @QueryParam("addonid") String addid,@QueryParam("station") String station) {
		DbUtils utils = new DbUtils();
		//String addonurl= "https://api.osf.io/v2/addons/";
		osfUtils osfu = new osfUtils();	
		
		this.doCheck(expId,  station);
		
		SimpleEntry entry = utils.select_osftokeninfo(expId, "exp",station);
		String expnode = (String) entry.getValue();
		String token = (String) entry.getKey();
		System.out.println("expnode" + expnode);
		System.out.println("token"+token);
		
		String userUrl = osfu.uurl+"me/";
		Entry userInfoEntry = osfu.get_info(userUrl, token);
		String userInfoResponse = (String) userInfoEntry.getValue();
		System.out.println("User Info:" + userInfoResponse);
		
		//String nodeanurl="https://api.test.osf.io/v2/nodes/"+expnode+"/addons/";
		String nodeAddonsUrl = "https://api.osf.io/v2/nodes/"+expnode+"/addons/";
		//https://api.osf.io/v2/addons/
		System.out.println ("Node Addons URL: " +nodeAddonsUrl);
		//String nodeaddonurl = "https://api.osf.io/v2/nodes/"+expnode+"/addons/"+addid+"/folders";
		Entry nodeAddonsEntry = osfu.get_info(nodeAddonsUrl, token);
		Integer status = (Integer) nodeAddonsEntry.getKey();
		String result = (String) nodeAddonsEntry.getValue();
		System.out.println(result);
		
		if (status == null)
			status = 403;
		if (result == null)
			result = "Hub problem";
       		
		ResponseBuilder rr = Response.status(status).entity(result);
		return rr.build();
	}
			
	
	@Path("updatewiki")
	@PUT
	// @Consumes(MediaType.APPLICATION_JSON)
	/*
	 * https://magx.lanl.gov/rest/updatewiki?name=p004_113021.md&expid=P19635-E002-PF
	 */
	public Response updateOSFWiki(String msg,
			                @QueryParam("name") String name,
			                @QueryParam("expid") String expid,
			                @QueryParam("station") String station) {
		DbUtils utils = new DbUtils();
		osfUtils osfu = new osfUtils();
		SimpleEntry entry = utils.select_osftokeninfo(expid, "wiki",station);
		String wikinode = (String) entry.getValue();
		String token = (String) entry.getKey();
		System.out.println("wikinode" + wikinode);
		SimpleEntry expentry = utils.select_osftokeninfo(expid, "exp",station);
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
	 public static String[] parseResponse(String response) {
	        String[] parts = response.split(":");
	       // if (parts.length == 3 && parts[0].equals("error")) {
	        	 if (parts[0].equals("error")) {
	            String fcode = parts[1];
	            String fresult = parts[2];
	            return new String[] { fcode, fresult };
	        } else {
	            return null;
	        }
	    }
	 
	@Path("updatefile")
	@PUT
	// @Consumes(MediaType.APPLICATION_JSON)
	/*
	 * https://magx.lanl.gov/rest/updatefile?name=p004_113021.tdms&expid=P19635-E002-PF
	 */
	public Response uploadOSFFIle(InputStream in, 
			                     @QueryParam("name") String name,
			                     @QueryParam("expid") String expid,
			                     @QueryParam("folder") String folderpath,
			                     @QueryParam("addon") String provider,
			                     @QueryParam("station") String station,
			                     @QueryParam("component") String component) {
		DbUtils utils = new DbUtils();
		
		String fpath=""; 
		
		SimpleEntry entry = utils.select_osftokeninfo(expid, "exp",station);
		String expnode = (String) entry.getValue();
		String token = (String) entry.getKey();
		//Map m = utils.select_osfinfo(expid,station);
		//String userid = (String) m.get("osf_name");
		osfUtils osfu = new osfUtils();
	
		//String url_list_contributers="https://api.test.osf.io/v2/nodes/"+expnode+"/contributors/";	
		
		
		//get userid
			/*
		String userUrl = osfu.uurl+"me/";
		Entry userInfoEntry = osfu.get_info(userUrl, token);
		String userInfoResponse = (String) userInfoEntry.getValue();
		JsonElement jsonEl = new JsonParser().parse(userInfoResponse);
		JsonObject user = jsonEl.getAsJsonObject();
		JsonObject data = user.get("data").getAsJsonObject();

		String userid = data.get("id").getAsString();
		System.out.println("User ID:" + userid);
		
		*/
		//check contributors of exp node
	    List users_ids = osfu.get_contributers(expnode, token);
	        
		
		
		if (component!=null) {
		osfProject op= new osfProject();		
		String compnode = op.get_or_create_component(token, expid, expnode,component);
		if (compnode!=null) {expnode=compnode;}
		
		}
		
		 // Determine the provider if it's not specified
		if (provider==null) {
		  provider="osfstorage";
		}
		// Get folder information if folderpath is provided
		String prp="";
		//get folder id 
		if (folderpath!=null){
		//String putfolderurl = "https://files.osf.io/v1/resources/" + expnode + "/providers/osfstorage/?kind=folder&name=" +folderpath;
			prp=osfu.check_folders(expnode, token,folderpath, provider);
			if (prp.startsWith("error:")) {
			String[] parts=parseResponse(prp);
			if (parts != null) {				
	            int fcode = Integer.parseInt(parts[0]);
	            String fresult = parts[1];
	            
	            ResponseBuilder responseBuilder = Response.status(fcode).entity(fresult);
	            return responseBuilder.build();
			}
			}
		}
		String puturl = "https://files.osf.io/v1/resources/" + expnode + "/providers/"+prp+"?kind=file&name="	+ name;
		System.out.println(puturl);
		ResponseBuilder responseBuilder;
		Entry entryResult = (SimpleEntry) osfu.do_put_file(puturl, in, token);
		if (entryResult != null) {
			String result = (String) entryResult.getValue();
			Integer code = (Integer) entryResult.getKey();
			System.out.println(result);
			System.out.println("osf status: " + code);
			responseBuilder = Response.status(code).entity(result);
		} else {
			responseBuilder = Response.status(201);// new resource created
		}
		
		if (component!=null) {
			String url_contributers="https://api.test.osf.io/v2/nodes/"+expnode+"/contributors/";	
			if ( users_ids.size()>1) {
			for (int kk=0;kk<users_ids.size();kk++) {
				String id = (String) users_ids.get(kk);				
			    String body = "{\"data\": {\"type\": \"contributors\",\"attributes\": {},\"relationships\": {\"user\": {\"data\": { \"type\": \"users\"," +
				         " \"id\": \""+ id +"\"}}}}}";
			    SimpleEntry en = osfu.do_put(url_contributers,  body,  token);
			   
			    if (en != null) {
					String result = (String) en.getValue();
					Integer code = (Integer) en.getKey();
					System.out.println(result);
					System.out.println("osf status to add contributers: " + code);
			    }
			}
				
			}
		}
		return responseBuilder.build();
	}

	@Path("auth")
	@GET
	/*  this method initiates a osf form that asks the user to grant authorization for application to sync data. 
	 *  https://magx.lanl.gov/rest/auth?expid=P19635-E002-PF&station=Cell_4
	 */
	public Response initOSFLoginForm(@QueryParam("expid") String expid, @QueryParam("station") String station) {

		DbUtils utils = new DbUtils();
		//this we indicate that process of auth started
		utils.insert_auth(expid, "authorizing", station);
        String id = expid+"%7C"+station;
		URI externalUri = null;
		try {
			externalUri = new URI(
					"https://accounts.osf.io/oauth2/authorize?response_type=code&" + "client_id=" + osfUtils.clientID
							+ "&redirect_uri=" + osfUtils.callbackurl + "&scope=osf.full_write&state=" + id +"&access_type=offline");
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
	public Response doLogoff(@QueryParam("expid") String expid, @QueryParam("name") String name,@QueryParam("station") String station) {
		DbUtils utils = new DbUtils();
		osfUtils osf = new osfUtils();
		Integer status = 204;
		String result = "";
		//SimpleEntry entry = utils.select_osftokeninfo(expid, "user");
		Map mosf=utils.select_osfinfo(expid,station);
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
				utils.update_token_status(token, expid, dt,"R",station);
				
              Entry ent = osf.do_token(refresh_token, "revoke");
				
				status = (Integer) ent.getKey();
				System.out.println("refresh_revoked:" + status);
				result = (String) ent.getValue();
				System.out.println(result);
				
								
			
				
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
	public Response doCheck(@QueryParam("expid") String expid, @QueryParam("station") String station) {
		DbUtils utils = new DbUtils();
		osfUtils osf = new osfUtils();
		String userdefault = "unauthorized";
		String nodeurl = osf.uurl + "me/";
		SimpleEntry entry = utils.select_osftokeninfo(expid, "user",station);
		Map mosf=utils.select_osfinfo(expid,station);
		
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
					    utils.update_token(ast, expid, dt,expire,station);
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
