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

import org.apache.logging.log4j.Logger;
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
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

@Path("")

public class LocationResource {
	 //Logger logger = LoggerFactory.getLogger( LocationResource.class);
	//private static final org.jboss.logging.Logger logger = LogManager.getLogger( LocationResource.class);
	private static final long serialVersionUID = -1900139055484193969L;
	//private static org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.Logger.getLogger(LocationResource.class);
	Logger logger = LogManager.getLogger(getClass());
	final static String authorizeUrl = "https://accounts.osf.io/oauth2/authorize";
	static String tokenurl = "https://accounts.osf.io/oauth2/token";
	
	//PropertyConfigurator.configure("path/to/log4j.properties");
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
		logger.debug("in probs");
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
		logger.debug("state:"+state);
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
		logger.debug("Access Token:"+accessToken);
		logger.debug("Access Token:"+refreshToken);
		//osfUtils osf = new osfUtils();
		osfProject osf = new osfProject();
		String user = osf.create_initial_project_experiment_wiki(accessToken , expid, expire,refreshToken,station);
		logger.debug("User:"+user);
		// String json = "{user:\"" + user + "\"}";
		DbUtils utils = new DbUtils();

		SimpleEntry entry = utils.select_osftokeninfo(expid, "exp",station);
		if (entry != null) {
			String expnode = (String) entry.getValue();
			String url = "https://osf.io/" + expnode + "/";
			System.out.println(url);
			logger.debug("Experiment node: " + expnode);
			logger.debug("osf  authorization");
			logger.debug(inoauthcode +" "+ state+ "  "+ " " +accessToken+ " "+ refreshToken +" "+url);
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
			logger.debug("osf  authorization failed "+inoauthcode +" "+ state+ "  "+ " " +accessToken+ " "+ refreshToken);
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
	public Response updateOSFWiki(String msg,
	                              @QueryParam("name") String filename,
	                              @QueryParam("expid") String expid,
	                              @QueryParam("station") String station) {
	    // Initialize utility objects
	    DbUtils dbUtils = new DbUtils();
	    osfUtils osfUtils = new osfUtils();
	    doCheck(expid, station);
	    // Retrieve OSF token and node information for the wiki
	    SimpleEntry<String, String> wikiEntry = dbUtils.select_osftokeninfo(expid, "wiki", station);
	    String wikiNode = wikiEntry.getValue();
	    String token = wikiEntry.getKey();
	    logger.debug("Wiki node: " + wikiNode + ", Token: " + token);
	    
	    // Retrieve experiment node information
	    SimpleEntry<String, String> expEntry = dbUtils.select_osftokeninfo(expid, "exp", station);
	    String expNode = expEntry.getValue();
	    logger.debug("Experiment node: " + expNode);

	    // Get existing wiki content
	    String existingContent = osfUtils.get_wiki_content(expNode, token, wikiNode);
	    logger.debug("Existing wiki content retrieved successfully");

	    // Append the new message to the existing content
	    String updatedContent = existingContent + msg;
	    logger.debug("Updated wiki content: " + updatedContent);

	    // Create the URL for posting the new wiki content
	    String wikiUrl = "https://api.osf.io/v2/wikis/" + wikiNode + "/versions/";
	    String wikiJson = osfUtils.append_json_wiki(wikiNode, updatedContent);
	    logger.debug("Wiki URL: " + wikiUrl);

	    // Post the updated content to the OSF wiki
	    SimpleEntry<Integer, String> postResponse = osfUtils.do_post(wikiUrl, wikiJson, token);
	    Integer status = postResponse.getKey();
	    String result = postResponse.getValue();
	    logger.debug("Wiki post status: " + status + ", Result: " + result);

	    // Handle potential null values
	    if (status == null) status = 403;
	    if (result == null) result = "hub problem";
	    logger.debug("Final wiki post status: " + status + ", Result: " + result);

	    // Build and return the response
	    return Response.status(status).entity(result).build();
	}

	@Path("updatewiki0")
	@PUT
	// @Consumes(MediaType.APPLICATION_JSON)
	/*
	 * https://magx.lanl.gov/rest/updatewiki?name=p004_113021.md&expid=P19635-E002-PF
	 */
	public Response updateOSFWiki0(String msg,
			                @QueryParam("name") String filename,
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
		logger.debug("OSFWIKI"+" "+ filename + " "+wikinode+" "+expnode+" "+status+" "+result);
		
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
	 public Response uploadOSFFile(InputStream in, 
	                               @QueryParam("name") String filename,
	                               @QueryParam("expid") String expid,
	                               @QueryParam("folder") String folderpath,
	                               @QueryParam("addon") String provider,
	                               @QueryParam("station") String station,
	                               @QueryParam("component") String component) {

	     // Initialize utility objects
	     DbUtils dbUtils = new DbUtils();
	     osfUtils osfUtils = new osfUtils();
	     doCheck(expid, station);
	     // Get OSF token and node information
	     SimpleEntry<String, String> entry = dbUtils.select_osftokeninfo(expid, "exp", station);
	     String token = entry.getKey();
	     String expNode = entry.getValue();
	     
	     logger.debug("Uploading OSF file: " + filename + ", Folder: " + folderpath + ", Node: " + expNode + ", Token: " + token +
	                  ", Provider: " + provider + ", Station: " + station + ", Component: " + component);
	     
	     // Check contributors of the experiment node
	    // List<String> userIds = osfUtils.get_contributers(expNode, token);
	     
	     // Handle component if specified
	     if (component != null) {
	         osfProject osfProject = new osfProject();
	         String compNode = osfProject.get_or_create_component(token, expid, expNode, component);
	         if (compNode != null) {
	             expNode = compNode;
	         }
	     }
	     
	     // Default provider to "osfstorage" if not specified
	     if (provider == null) {
	         provider = "osfstorage";
	     }
	     
	     // Get folder information if folderpath is provided
	     String folderId = "";
	     if (folderpath != null) {
	         folderId = osfUtils.check_folders(expNode, token, folderpath, provider);
	         if (folderId.startsWith("error:")) {
	             String[] parts = parseResponse(folderId);
	             if (parts != null) {
	                 int statusCode = Integer.parseInt(parts[0]);
	                 String errorMessage = parts[1];
	                 logger.debug("Failed to find folder: " + errorMessage);
	                 return Response.status(statusCode).entity(errorMessage).build();
	             }
	         }
	     }
	     
	     // Build the PUT URL
	     String putUrl = String.format("https://files.osf.io/v1/resources/%s/providers/%s?kind=file&name=%s", expNode, provider, filename);
	     System.out.println(putUrl);
	     
	     // Upload the file
	     SimpleEntry uploadResult = (SimpleEntry) osfUtils.do_put_file(putUrl, in, token);
	     Response.ResponseBuilder responseBuilder;
	     
	     if (uploadResult != null) {
	         int statusCode = (int) uploadResult.getKey();
	         String result = (String) uploadResult.getValue();
	         logger.debug("File uploaded with status: " + statusCode + ", Result: " + result);
	         responseBuilder = Response.status(statusCode).entity(result);
	     } else {
	         logger.debug("File upload entry is null");
	         responseBuilder = Response.status(500); 
	     }
	     // Check contributors of the experiment node
	     List<String> userIds = osfUtils.get_contributers(expNode, token);
	     
	     // Add contributors if component is specified
	     if (component != null && userIds.size() > 1) {
	         String contributorsUrl = String.format("https://api.test.osf.io/v2/nodes/%s/contributors/", expNode);
	         for (String userId : userIds) {
	             String requestBody = String.format("{\"data\": {\"type\": \"contributors\",\"attributes\": {},\"relationships\": {\"user\": {\"data\": { \"type\": \"users\", \"id\": \"%s\"}}}}}", userId);
	             SimpleEntry<Integer, String> contributorResult = osfUtils.do_put(contributorsUrl, requestBody, token);
	             if (contributorResult != null) {
	                 int statusCode = contributorResult.getKey();
	                 String result = contributorResult.getValue();
	                 logger.debug("Added contributor" + userId+ "with status: " + statusCode + ", Result: " + result);
	             }
	         }
	     }
	     
	     return responseBuilder.build();
	 }

	@Path("updatefile0")
	@PUT
	// @Consumes(MediaType.APPLICATION_JSON)
	/*
	 * https://magx.lanl.gov/rest/updatefile?name=p004_113021.tdms&expid=P19635-E002-PF
	 */
	public Response uploadOSFFIle0(InputStream in, 
			                     @QueryParam("name") String filename,
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
		
		osfUtils osfu = new osfUtils();
	
		//String url_list_contributers="https://api.test.osf.io/v2/nodes/"+expnode+"/contributors/";	
		logger.debug("OSFFILE"+" "+ filename + " "+folderpath+" "+expnode+" "+token+" "+provider+" "+station+" "+component);
		
		
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
	            logger.debug("OSFFILE:folderfail "+" "+ filename + " "+folderpath+" "+expnode+" "+token+" "+provider+" "+station+" "+component+fcode+fresult);
	        	 
	            ResponseBuilder responseBuilder = Response.status(fcode).entity(fresult);
	            return responseBuilder.build();
			}
			}
		}
		String puturl = "https://files.osf.io/v1/resources/" + expnode + "/providers/"+prp+"?kind=file&name="	+ filename;
		System.out.println(puturl);
		ResponseBuilder responseBuilder;
		Entry entryResult = (SimpleEntry) osfu.do_put_file(puturl, in, token);
		if (entryResult != null) {
			String result = (String) entryResult.getValue();
			Integer code = (Integer) entryResult.getKey();
			System.out.println(result);
			System.out.println("osf status: " + code);
		    logger.debug("OSFFILE:PUTFILE "+" "+ filename + " "+folderpath+" "+expnode+" "+token+" "+provider+" "+station+" "+component+code+result);
			     
			responseBuilder = Response.status(code).entity(result);
		} else {
			logger.debug("OSFFILE:PUTFILE:noentry"+" "+ filename + " "+folderpath+" "+expnode+" "+token+" "+provider+" "+station+" "+component);
			
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
	public Response doLogoff(@QueryParam("expid") String expid, @QueryParam("name") String name, @QueryParam("station") String station) {
	    //Logger logger = Logger.getLogger(this.getClass().getName());
	    DbUtils utils = new DbUtils();
	    osfUtils osf = new osfUtils();
	    Integer status = 204;
	    String result = "";
	    Map<String, Object> osfInfo = utils.select_osfinfo(expid, station);

	    if (osfInfo.containsKey("access_token")) {
	        String token = (String) osfInfo.get("access_token");
	        String refreshToken = (String) osfInfo.get("refresh_token");

	        logger.debug("token: " + token);

	        if (token != null) {
	            SimpleEntry<Integer, String> tokenRevokeResponse = (SimpleEntry<Integer, String>) osf.do_token(token, "revoke");
	            status = tokenRevokeResponse.getKey();
	            result = tokenRevokeResponse.getValue();

	            logger.debug("revoked status: " + status);
	            logger.debug("result: " + result);

	            String dateGranted = (String) osfInfo.get("dtgranted");
	            utils.update_token_status(token, expid, dateGranted, "R", station);

	            SimpleEntry<Integer, String> refreshTokenRevokeResponse = (SimpleEntry<Integer, String>) osf.do_token(refreshToken, "revoke");
	            status = refreshTokenRevokeResponse.getKey();
	            result = refreshTokenRevokeResponse.getValue();

	            logger.debug("refresh revoked status: " + status);
	            logger.debug("result: " + result);
	            
	            
	        }
	    }

	    String user = "unauthorized";
	    String jsonResponse = String.format("{\"user\":\"%s\",\"expid\":\"%s\"}", user, expid);

	    return Response.ok(jsonResponse).status(200).build();
	}

	
	
	@Path("logoff0")
	@GET
	/*
	 *  http://<hostname>/rest/logoff?expid=P19635-E002-PF
	 */
	public Response doLogoff0(@QueryParam("expid") String expid, @QueryParam("name") String name,@QueryParam("station") String station) {
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
	public Response doCheck(@QueryParam("expid") String expid, @QueryParam("station") String station) {
	    //Logger logger = Logger.getLogger(this.getClass().getName());
	    DbUtils dbUtils = new DbUtils();
	    osfUtils osfUtils = new osfUtils();
	    //OsfUtils osfUtils = new OsfUtils();
	    String userDefault = "unauthorized";
	    String nodeUrl = osfUtils.uurl + "me/";
	    SimpleEntry<String, String> entry = dbUtils.select_osftokeninfo(expid, "user", station);
	    Map<String, Object> osfInfo = dbUtils.select_osfinfo(expid, station);

	    if (entry != null) {
	        String token = entry.getValue();
	        String user = entry.getKey();

	        logger.debug("method:status token: " + token);
	        logger.debug("method:status user: " + user);

	        if (user == null) {
	            user = userDefault;
	        } else if (token != null) {
	            // Check if the token is expired
	            SimpleEntry<Integer, String> ep = (SimpleEntry<Integer, String>) osfUtils.get_info(nodeUrl, token);
	            int code = ep.getKey();

	            logger.debug("method:check code: " + code);

	            if (code != 200) {
	                // Token expired
	                String refreshToken = (String) osfInfo.get("refresh_token");
	                logger.debug("method:refresh token: " + refreshToken);

	                if (refreshToken != null) {
	                    // Ask OSF to refresh the token
	                    SimpleEntry<Integer, String> e = (SimpleEntry<Integer, String>) osfUtils.refresh_token(refreshToken);
	                    int hCode = e.getKey();
	                    String result = e.getValue();

	                    if (hCode == 200) {
	                        // Token refreshed
	                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
	                        //JsonElement jsonElement = JsonParser.parseString(result);
	                        JsonElement jsonElement = new JsonParser().parse(result);
	                        JsonObject root = jsonElement.getAsJsonObject();

	                        String newAccessToken = root.get("access_token").getAsString();
	                        String expiresIn = root.get("expires_in").getAsString();

	                        logger.debug("method:new access_token: " + newAccessToken);

	                        String dateGranted = (String) osfInfo.get("dtgranted");
	                        dbUtils.update_token(newAccessToken, expid, nodeUrl, expiresIn, station);
	                    } else {
	                        // Renewal of token failed
	                        logger.debug("method:check response: " + ep.getValue());
	                        user = userDefault;
	                    }
	                } else {
	                    user = userDefault;
	                }
	            }
	        }

	        String jsonResponse = String.format("{\"user\":\"%s\",\"expid\":\"%s\"}", user, expid);
	        return Response.ok(jsonResponse).status(200).build();
	    } else {
	        String jsonResponse = String.format("{\"user\":\"%s\",\"expid\":\"%s\"}", userDefault, expid);
	        return Response.ok(jsonResponse).status(200).build();
	    }
	}

	
	
	@Path("status0")
	@GET
	/*
	 *  https://magx.lanl.gov/rest/status?expid=P19635-E002-PF&station=Cell_4
	 */
	public Response doCheck0(@QueryParam("expid") String expid, @QueryParam("station") String station) {
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
			 logger.debug("method:status user" + user);
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
