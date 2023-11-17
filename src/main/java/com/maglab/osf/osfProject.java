package com.maglab.osf;

import java.util.List;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maglab.model.DbUtils;
import com.maglab.model.Experiment;

public class osfProject {
	osfUtils osf;
	String osf_name = "";

	public osfProject() {
		this.osf = new osfUtils();
	}

	//used to check project by description field  and  experiment by title
	public String get_userprojects(String userurl, String token, String proposal, String field) {
		String projectId = null;
// String userurl=uurl+"me/";
		System.out.println(userurl);

		try {
			Entry userInfo = osf.get_info(userurl, token);
			String userInfoResponse = (String) userInfo.getValue();
			System.out.println("User Info: " + userInfoResponse);

			JsonElement jsonEl = new JsonParser().parse(userInfoResponse);
			JsonObject user = jsonEl.getAsJsonObject();
			JsonObject data = user.get("data").getAsJsonObject();

			String userid = data.get("id").getAsString();
			System.out.println("User ID:" + userid);
			osf_name = data.get("attributes").getAsJsonObject().get("full_name").getAsString();
			System.out.println(osf_name);

			System.out.println(osfUtils.uurl + userid + "/nodes/");

			Entry nodesInfo = osf.get_info(osfUtils.uurl + userid + "/nodes/", token);
			String nodesResponse = (String) nodesInfo.getValue();
			JsonElement nodesJsonElement = new JsonParser().parse(nodesResponse);
			JsonObject obj = nodesJsonElement.getAsJsonObject();
			JsonArray nodesArray = obj.getAsJsonArray("data");
			JsonElement jnext = obj.get("links").getAsJsonObject().get("next");
			String nextLink = (jnext instanceof JsonNull) ? "" : jnext.getAsString();
			boolean hasMore = !nextLink.isEmpty();
			Boolean donext = false;

			if (nextLink.equals(""))
				donext = false;
			else {
				donext = true;
			}
			System.out.println(nextLink);
			for (JsonElement pa : nodesArray) {
				JsonObject prObj = pa.getAsJsonObject();
				String desc = (String) prObj.get("attributes").getAsJsonObject().get(field).getAsString();
				if (desc.contains(proposal)) {
					projectId = (String) prObj.get("id").getAsString();
// System.out.print(proj_id);
					break;
				}
			}
			if (projectId == null && donext) {
				projectId = get_userprojects_next(nextLink, token, proposal, field);
// proj_id=get_userprojects(next, token, proposal);
			}
		} catch (Exception e) {
// TODO Auto-generated catch block
			e.printStackTrace();
		}
// String
// username=propdata.get("attributes").getAsJsonObject().get("description").getAsString();
		return projectId;
	}

	public String get_userprojects_next(String nodeurl, String token, String proposal, String field) {
		String proj_id = null;
		Entry ep = osf.get_info(nodeurl, token);
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
    public  String get_or_create_component(String token, String pid,String  expnode,String compname) {
    	DbUtils dbu = new DbUtils();
		List<Experiment> experiments = dbu.getbyPid(pid);
		//String prop = pid;
		Experiment e = null;
        String component_id;
		if (experiments.size() > 0) {
			e = (Experiment) experiments.get(0);
			//prop = e.getProposal_Number();
		}
		
		//check if component exists
		component_id = get_userprojects(osf.uurl + "me/", token, pid+"-"+compname, "title");
		//create new component
		if (component_id==null) {
    	String expJson = osf.compose_json_exp_component(e,compname);
		String expUrl = osf.apiurl + expnode + "/children/";

		Entry expEntry = osf.do_post(expUrl, expJson, token);
		Integer expStatus = (Integer) expEntry.getKey();
		System.out.println("Component status:" + expStatus);
		String expResponse = (String) expEntry.getValue();

		System.out.println("New component:" + expResponse);
		JsonElement expJsonElement = new JsonParser().parse(expResponse);
		JsonObject expJsonObject = expJsonElement.getAsJsonObject();
		component_id = expJsonObject.get("data").getAsJsonObject().get("id").getAsString();
		System.out.println("Component ID:" + component_id);
		}
    	return component_id;
    }
	public String create_initial_project_experiment_wiki(String token, String pid, String expire, String rtoken,
			String station) {

		DbUtils dbu = new DbUtils();
		List<Experiment> experiments = dbu.getbyPid(pid);
		String prop = pid;
		Experiment e = null;

		if (experiments.size() > 0) {
			e = (Experiment) experiments.get(0);
			prop = e.getProposal_Number();
		}

		String exp_id = get_userprojects(osf.uurl + "me/", token, pid, "title");
		if (exp_id != null) {
			System.out.println("experiment exists:" + exp_id);
		}
		String pr_id = get_userprojects(osf.uurl + "me/", token, prop, "description");

		if (pr_id == null && exp_id == null) {

			String projectJson = osf.compose_json_project(e);
			System.out.println("Project JSON:" + projectJson);

			Entry projectEntry = osf.do_post(osf.apiurl, projectJson, token);
			Integer status = (Integer) projectEntry.getKey();
			System.out.println("status" + status);
			String response = (String) projectEntry.getValue();
			JsonElement jsonEl2 = new JsonParser().parse(response);
			JsonObject obj = jsonEl2.getAsJsonObject();
			pr_id = obj.get("data").getAsJsonObject().get("id").getAsString();
			System.out.println("New project created:" + response);
// need to parse id
		} else {
			System.out.println("Existed project id:" + pr_id);
		}

		String wiki_id = "";

		if (exp_id == null) {
			String expJson = osf.compose_json_exp(e);
			String expUrl = osf.apiurl + pr_id + "/children/";

			Entry expEntry = osf.do_post(expUrl, expJson, token);
			Integer expStatus = (Integer) expEntry.getKey();
			System.out.println("Experiment status:" + expStatus);
			String expResponse = (String) expEntry.getValue();

			System.out.println("New experiment:" + expResponse);

			JsonElement expJsonElement = new JsonParser().parse(expResponse);
			JsonObject expJsonObject = expJsonElement.getAsJsonObject();
			exp_id = expJsonObject.get("data").getAsJsonObject().get("id").getAsString();
			System.out.println("Exp ID:" + exp_id);

			String wikiurl = osf.apiurl + exp_id + "/wikis/";
			String wikiJson = osf.compose_json_wiki(e);
			System.out.print(wikiJson);

			Entry wikiEntry = osf.do_post(wikiurl, wikiJson, token);
			Integer wikiStatus = (Integer) wikiEntry.getKey();
			System.out.println("Wiki status:" + wikiStatus);
			String wikiResponse = (String) wikiEntry.getValue();

			System.out.println(wikiResponse);

			try {
				JsonElement wikiJsonElement = new JsonParser().parse(wikiResponse);
				JsonObject wikiJsonObject = wikiJsonElement.getAsJsonObject();
				wiki_id = wikiJsonObject.get("data").getAsJsonObject().get("id").getAsString();

				System.out.println("Wiki ID:" + wiki_id);
			} catch (Exception ee) {
// TODO Auto-generated catch block
				ee.printStackTrace();
			}

		}

// get all /nodes/{node_id}/wikis/
// lookup wikis
		if (wiki_id.equals("")) {
			try {
				String wikiurl = osf.apiurl + exp_id + "/wikis/";
// https://api.osf.io/v2/nodes/krcew/wikis/

// System.out.println(wikiurl);

				Entry er = osf.get_info(wikiurl, token);
				String r = (String) er.getValue();
				System.out.println("wikis:" + r);

				JsonElement jsonEl = new JsonParser().parse(r);

				JsonObject user = jsonEl.getAsJsonObject();

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

		System.out.println("inserted magproj:" + pid);
		System.out.println("inserted project:" + pr_id);
		System.out.println("inserted experiment:" + exp_id);
		System.out.println("inserted wiki:" + wiki_id);
		dbu.insert_token(token, osf_name, expire, pid, pr_id, exp_id, wiki_id, rtoken, station);
		return osf_name;
	}
}
