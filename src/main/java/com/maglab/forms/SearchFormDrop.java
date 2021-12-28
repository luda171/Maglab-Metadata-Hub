package com.maglab.forms;


import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;


import com.maglab.panel.HomePanel;
import com.maglab.panel.HomeTemplatePage;
import com.maglab.panel.MyHomePage;
import com.googlecode.wicket.kendo.ui.form.dropdown.DropDownList;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;
import com.googlecode.wicket.kendo.ui.form.button.AjaxButton;
import com.googlecode.wicket.kendo.ui.form.button.Button;

public class SearchFormDrop extends Form {
    private String locnameSearch;
              Page page;
    private static final long serialVersionUID = 1L;
    private static final List<String> GENRES = Arrays.asList("PI", "Summary", "Location");
         
    public SearchFormDrop(String id) {
        super(id);
        setModel(new CompoundPropertyModel<>(this));
        add(new FeedbackPanel("feedback"));
        add(new RequiredTextField<String>("locnameSearch"));
     // FeedbackPanel //
     		final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback");
     		add(feedback);

     		// DropDownList //
     		final DropDownList<String> dropdown = new DropDownList<String>("select", new Model<String>(), Model.ofList(GENRES));
     		add(dropdown.setOutputMarkupId(true));
     // Buttons //
     		add(new Button("submit") {

     			private static final long serialVersionUID = 1L;

     			@Override
     			public void onSubmit()
     			{
     				this.info(dropdown);
     			}
     		});

     		add(new AjaxButton("button") {

     			private static final long serialVersionUID = 1L;

     			@Override
     			protected void onSubmit(AjaxRequestTarget target)
     			{
     				this.info(dropdown);
     				target.add(feedback);
     			}
     		});
        
        
    }
    
   
    private void info(DropDownList<String> dropdown)
	{
		String choice = dropdown.getModelObject();

		this.info(choice != null ? choice : "no choice");
	}
    
    @Override
    protected void onSubmit() {
        PageParameters parameters = new PageParameters();
        parameters.add("locnameSearch", locnameSearch);
        System.out.println("parent"+this.getParent());
        System.out.println("this"+this);
        //this.getParent().getParent().replace(new HomePanel("homepanel",parameters));
       // HomePanel hp = new HomePanel("homepanel",parameters);
 	   // getPage().replace(hp);
        //replace(new HomePanel("homepanel",parameters));
        //this.replace(table);
        //add(new HomePanel("homepanel", parameters));
        //setResponsePage( MyHomePage.class, parameters);
        MyHomePage np= new MyHomePage(parameters);
        setResponsePage(np);
       // page=getPage().getParent().getPage();//.getParent().getPage();
        //System.out.println("pathsearch"+page.getPath());
        //HomePanel hp = new HomePanel("homepanel",parameters);
 	    //page.replace(hp);
    }
}
