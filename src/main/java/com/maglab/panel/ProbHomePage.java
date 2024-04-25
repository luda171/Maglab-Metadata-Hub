package com.maglab.panel;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.giffing.wicket.spring.boot.context.scan.WicketHomePage;
import com.maglab.instruments.InstrumentPanel;
import com.maglab.instruments.InstrumentService;


@AuthorizeInstantiation("USER")
//@WicketHomePage
public class ProbHomePage extends  HomeTemplatePage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1900139055484193969L;
	@SpringBean
    private InstrumentService service;

	public ProbHomePage(final PageParameters parameters) {
		 super(parameters);
		 System.out.println("ProbPage");
		//  super();
		 
		  add(new InstrumentPanel("probpanel",parameters));
		  
}
	
}