package com.maglab.panel;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;

public class MenuPanel extends Panel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5858380498692017983L;

	public MenuPanel(String id) {
		super(id);	
		
		
		RepeatingView menu = new RepeatingView("menuItem");
		menu.add(new MyMenuPanel(menu.newChildId(), "Home", MyHomePage.class));
		
		add(menu);
		
		
	}
}
