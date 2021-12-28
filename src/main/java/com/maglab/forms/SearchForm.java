package com.maglab.forms;


import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;


import com.maglab.panel.HomePanel;
import com.maglab.panel.HomeTemplatePage;
import com.maglab.panel.MyHomePage;

public class SearchForm extends Form<SearchForm> {
    private String locnameSearch;
              Page page;
    public SearchForm(String id) {
        super(id);
        setModel(new CompoundPropertyModel<>(this));
        add(new FeedbackPanel("feedback"));
        add(new RequiredTextField<String>("locnameSearch"));
    }
    
    public void setPage(Page page){
    	this.page=page;
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
