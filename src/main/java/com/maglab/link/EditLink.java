package com.maglab.link;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.link.Link;
import com.maglab.model.Experiment;
//not used currently
public class EditLink extends Link<Void> {
	 private ListItem<Experiment> item;

	    public EditLink(String id, ListItem<Experiment> item) {
	        super(id);
	        this.item = item;
	    }

	    @Override
	    public void onClick() {
	        int userId = item.getModelObject().getId();
	        PageParameters parameters = new PageParameters();
	        parameters.add("id", userId);
	        setResponsePage(EditPage.class, parameters);
	    }
	}


