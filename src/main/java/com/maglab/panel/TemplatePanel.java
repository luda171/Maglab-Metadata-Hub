package com.maglab.panel;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class TemplatePanel extends Panel {
	 public TemplatePanel(String id)
	    {
	        super(id);
	    }

	    /**
	     * Construct.
	     * 
	     * @param id
	     *            component id
	     * @param model
	     *            the model
	     */
	    public TemplatePanel(String id, IModel<?> model)
	    {
	        super(id, model);
	    }
}
