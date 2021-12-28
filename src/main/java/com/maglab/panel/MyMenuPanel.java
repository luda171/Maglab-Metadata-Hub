package com.maglab.panel;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;


public class MyMenuPanel extends Panel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -160923333676098259L;

	public MyMenuPanel(String id,String label, Class pageClass) {
		super(id);
		 Link link = new BookmarkablePageLink("link", pageClass);
		    add(link);
		    link.add(new Label("label", label));
	}
	
	
	
}
