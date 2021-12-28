package com.maglab.link;

import java.util.List;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.maglab.model.Experiment;
import com.maglab.panel.MyHomePage;
import com.maglab.service.ExperimentService;

public class EditForm extends Form<EditForm> {
	private FormComponent station;
    private FormComponent dstart;
    private FormComponent finish;
    private FormComponent sum;
    String oldpid;
    String oldstart;
    @SpringBean
	private  ExperimentService serv;
    public EditForm(String id, String pid,String start) {
        super(id);
        System.out.println(pid);
        System.out.println(start);
        this.oldpid=pid;
        this.oldstart=start;
        List<Experiment> oldex = serv.getExperimentByStartPID(pid, start);
        System.out.println("experiment find:"+oldex.size());
       
        Experiment old = oldex.get(0);
        System.out.println(old.getDTSTART());
        System.out.println(old.getDTEND());
        System.out.println(old.getLOCATION());
        System.out.println(old.getSummary());
      // String sum = old.getSummary();
       // this.userId = userId;
        //User oldUser = service.getUserById(userId);
       sum = new TextField<>(
               "sum",
               new PropertyModel<>(old, "SUMMARY")
       );
        finish = new TextField<>(
                "finish",
                new PropertyModel<>(old, "DTEND")
        );
        dstart = new TextField<>(
                "dstart",
                new PropertyModel<>(old, "DTSTART")
        );
        station = new TextField<>(
                "station",
                new PropertyModel<>(old, "LOCATION")
        );
        sum.setEnabled(false);
        add(sum);
        add(finish);
        add(dstart);
        add(station);
        //add(new HomeLink("cancelLink"));
        
        BookmarkablePageLink pl = new BookmarkablePageLink("cancelLink", MyHomePage.class);
		add(pl);
		
    }

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	 @Override
	    protected void onSubmit() {
	     
	       serv.updateExp(oldpid, oldstart, station.getInput(), dstart.getInput(),finish.getInput());
	       setResponsePage(MyHomePage.class); 
	    }

	
}
