package com.maglab.panel;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.giffing.wicket.spring.boot.context.scan.WicketHomePage;


@AuthorizeInstantiation("USER")
//@WicketHomePage

public class HomeTemplatePage extends WebPage {
	public static final String CONTENT_ID = "content";
	private Component headerPanel;
	private Component menuPanel;
	private Component footerPanel;

              public HomeTemplatePage(final PageParameters parameters){
            	  super(parameters);
		add(headerPanel = new HeaderPanel("headerPanel"));
		//add(menuPanel = new MenuPanel("menuPanel"));
		add(footerPanel = new FooterPanel("footerPanel"));
		
		//add(new Label(CONTENT_ID, "Put your content here"));
		BookmarkablePageLink pl = new BookmarkablePageLink("homeLink", MyHomePage.class);
		add(pl);
		BookmarkablePageLink prl = new BookmarkablePageLink("probLink", ProbHomePage.class);
		add(prl);
	
		//add(new BookmarkablePageLink("fileLink", MyHomePage.class)); 
		//add(new Label(CONTENT_ID, new MyHomePage(parameters)));
		//add(new MyHomePage(parameters));
		//PageParameters parameters = new PageParameters();
		//add(new HomePanel(CONTENT_ID, parameters));
		//replace(new HomePanel(CONTENT_ID));
		System.out.println("template");
		pl.continueToOriginalDestination();
		System.out.println("template2");
		//add(new HomePanel("homepanel", parameters));
	}
              
            //getters for layout areas
              protected Component getHeaderPanel() {
                  return headerPanel;
              }

              protected Component getMenuPanel() {
                  return menuPanel;
              }

              protected Component getFooterPanel() {
                  return footerPanel;
              }          
}
