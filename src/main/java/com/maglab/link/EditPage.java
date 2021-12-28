package com.maglab.link;

import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

public class EditPage extends WebPage {
	// private int userId;

	    public EditPage(PageParameters parameters) {
	        super(parameters);
	        List<StringValue> nameSearch = parameters.getValues("pid");
			StringValue nameValue = nameSearch.get(0);
			String pid = nameValue.toString();
			 List<StringValue> nameS = parameters.getValues("start");
				StringValue sv = nameS.get(0);
				String start = sv.toString();
	       
	        add(new EditForm("editForm", pid,start));
	    }

}
