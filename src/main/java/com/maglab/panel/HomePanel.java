package com.maglab.panel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Generics;
import org.apache.wicket.util.string.StringValue;

import com.googlecode.wicket.jquery.core.ajax.IJQueryAjaxAware;
import com.googlecode.wicket.jquery.core.ajax.JQueryAjaxBehavior;
import com.googlecode.wicket.kendo.ui.datatable.button.ToolbarAjaxBehavior;
import com.maglab.forms.SearchForm;
import com.maglab.link.EditLink;
import com.maglab.link.EditPage;
import com.maglab.model.Experiment;
import com.maglab.service.ExFilterProvider;
import com.maglab.service.ExSortableProvider;
import com.maglab.service.ExperimentService;

public class HomePanel extends Panel {
	@SpringBean
	private  ExperimentService service;
	private String filter;
		public HomePanel(String id, PageParameters parameters) {
		super(id);

		List<Experiment> experiments = new ArrayList<>();

		if (parameters.isEmpty()) {
			System.out.print("empty");
			experiments.addAll(service.getExperiments());

		} else  {
			if (parameters.getValues("exid").size()>0) {
			List<StringValue> nameSearch = parameters.getValues("exid");
			StringValue nameValue = nameSearch.get(0);
			String name = nameValue.toString();
			Integer i = new Integer(name);
			System.out.print(name);
			
			experiments.addAll(service.getExperimentByID(i));
			}
			else if (parameters.getValues("pi").size()>0) {
				List<StringValue> nameSearch = parameters.getValues("pi");
				StringValue nameValue = nameSearch.get(0);
				String name = nameValue.toString();
				//Integer i = new Integer(name);
				System.out.print(name);
				experiments.addAll(service.getExperimentsByPI(name));
				
			}
			else if (parameters.getValues("station").size()>0) {
				List<StringValue> nameSearch = parameters.getValues("station");
				StringValue nameValue = nameSearch.get(0);
				String name = nameValue.toString();
				//Integer i = new Integer(name);
				System.out.print(name);
				experiments.addAll(service.getExperimentsByLocationNow(name));
				
			}
			
			else if (parameters.getValues("pn").size()>0) {
				List<StringValue> nameSearch = parameters.getValues("pn");
				StringValue nameValue = nameSearch.get(0);
				String name = nameValue.toString();
				//Integer i = new Integer(name);
				System.out.print(name);
				experiments.addAll(service.getExperimentsByPI(name));
				
			}
			else {
				List<StringValue> nameSearch = parameters.getValues("locnameSearch");
				StringValue nameValue = nameSearch.get(0);
				String name = nameValue.toString();
				System.out.print(name);
				experiments.addAll(service.getExperimentBySupportName(name));	
			}
		}
		//else {
			//experiments.addAll(service.getExperiments());
			//List<StringValue> nameSearch = parameters.getValues("locnameSearch");
			//StringValue nameValue = nameSearch.get(0);
			//String name = nameValue.toString();
			//System.out.print(name);
			//experiments.addAll(service.getExperimentBySupportName(name));
			// experiments.addAll(service.getExperimentsByLocation(name));

		//}

		// ExperimentsList ex1 = new ExperimentsList("experiments", experiments);

		// add(new ExperimentsList("experiments", experiments));
		SearchForm sf = new SearchForm("searchForm");
		// sf.setPage(getPage());
		add(sf);
		// List<IColumn<Experiment>> columns = new ArrayList<IColumn<Experiment>>();

		// Form<?> form = new Form<Void>("form");
		// add(form);
		// form.add(new TextField<String>("filter", new PropertyModel<String>(this,
		// "filter")));
		//

		// listView = new PageableListView<Experiment>("exp", new
		// PropertyModel<List<Experiment>>(experiments,"exp",20);
		// add(listView = new PageableListView<Experiment>("exs", new
		// PropertyModel<List<Experiment>>(experiments),"exp";

		// ListDataProvider ex= new ExDataProvider(experiments);
		ListDataProvider ex = new ListDataProvider(experiments);
		// Options options = new Options();
		// options.set("height", 430);
		// options.set("pageable", "{ pageSizes: [ 25, 50, 100 ] }");
		// options.set("columnMenu", true);
		// options.set("persistSelection", true);
		ExSortableProvider sp = new ExSortableProvider(experiments);
		
		ExFilterProvider fp = new ExFilterProvider(experiments, filter);
		DataTable table = new DataTable("datatable", newColumnList(), sp, 20);
		
		//DataTable table = new DataTable("datatable", newColumnList(), sp, 20) {
			
		//	private static final long serialVersionUID = 1L;

		    /**
		    * Triggered when a toolbar button is clicked.
		    */
		   // @Override
		   // public void onClick(AjaxRequestTarget target, String button, List<String> values)
		   // {
		     //   this.info(button + " " + values);
		       // target.add(feedback);
		    //}

		    //@Override
		    //protected JQueryAjaxBehavior newToolbarAjaxBehavior(IJQueryAjaxAware source)
		    //{
		      //  return new ToolbarAjaxBehavior(source, "id"); // 'id' stand for your pk field
		    //}
	//	};

		
		table.addBottomToolbar(new NavigationToolbar(table));
		// add(new PagingNavigator("navigator",listView ));
		table.addTopToolbar(new HeadersToolbar(table, sp));
		// table.addTopToolbar(filterToolbar);

		// final FilterForm form = new FilterForm("filter-form", provider);
		// table.addTopToolbar(new FilterToolbar(table, form, provider));
		// form.add(table);
		// add(form);
		add(table);
	}

	/*@Override
	public void onEvent(IEvent event) {
	    if (event.getPayload() instanceof Notification) {
	        Notification notification = (Notification) event.getPayload();
	       // ... do whatever you want before updating the panel ...
	        // Update the panel 
	        notification.getTarget().add(this);
	    }
	}
*/
		
		public static boolean inSameWeek(Date date1, Date date2) {
		    if (null == date1 || null == date2) {
		        return false;
		    }

		    Calendar earlier = Calendar.getInstance();
		    Calendar later = Calendar.getInstance();

		    if (date1.before(date2)) {
		        earlier.setTime(date1);
		        later.setTime(date2);
		    } else {
		        earlier.setTime(date2);
		        later.setTime(date1);
		    }
		    if (inSameYear(date1, date2)) {
		        int week1 = earlier.get(Calendar.WEEK_OF_YEAR);
		        int week2 = later.get(Calendar.WEEK_OF_YEAR);
		        if (week1 == week2) {
		            return true;
		        }
		    } else {
		        int dayOfWeek = earlier.get(Calendar.DAY_OF_WEEK); 
		        earlier.add(Calendar.DATE, 7 - dayOfWeek);
		        if (inSameYear(earlier.getTime(), later.getTime())) {
		            int week1 = earlier.get(Calendar.WEEK_OF_YEAR);
		            int week2 = later.get(Calendar.WEEK_OF_YEAR);
		            if (week1 == week2) {
		                return true;
		            }
		        }
		    }
		    return false;
		}

		public static boolean inSameYear(Date date1, Date date2) {
		    if (null == date1 || null == date2) {
		        return false;
		    }
		    Calendar cal1 = Calendar.getInstance();
		    cal1.setTime(date1);
		    int year1 = cal1.get(Calendar.YEAR);
		    Calendar cal2 = Calendar.getInstance();
		    cal2.setTime(date2);
		    int year2 = cal2.get(Calendar.YEAR);
		    if (year1 == year2)
		        return true;

		    return false;
		}
	
		public static void   highlight(IModel model,Item cellItem) {
			Experiment ex = (Experiment) model.getObject();
            String edate = ex.getDTSTART();
           // System.out.println("edate"+edate);
            Date now = new Date();
           // Date n = new Date(edate);
           
    		SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd");
    		Date n=null;
			try {
				n = sqldf.parse(edate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (n!=null) {
            if (inSameWeek(n, now) &&inSameYear(n, now)) {
            	cellItem.add(AttributeModifier.replace("bgcolor", "lightyellow")); 
            }}
		}
		
	@SuppressWarnings("serial")
	private static List<IColumn> newColumnList() {
		List<IColumn> columns = Generics.newArrayList();

		// columns.add(new IdPropertyColumn("ID", "id", 50));
		// columns.add(new PropertyColumn("Name", "name"));
		// columns.add(new PropertyColumn("Description", "description"));
		// columns.add(new CurrencyPropertyColumn("Price", "price", 70));
		// columns.add(new IColumn("Date", "date"));
		// columns.add(new PropertyColumn("Vendor", "vendor.name"));

		// columns.add(new ClickablePropertyColumn(new IModel<String>("Experiment ID"),
		// "ExperimentID","ExperimentID"));
	
		
		columns.add(new TextFilteredPropertyColumn(new Model("Proposal ID"), "Proposal_Number", "Proposal_Number") {
			// add the LinkPanel to the cell item
			public void populateItem(Item cellItem, String componentId, IModel model) {
				//final IModel<String> cssModel = Model.of("bkg-default");
				cellItem.add(new Link<String>(componentId) {

					@Override
					public void onClick() {
						// experiments.addAll(service.getExperimentBySupportName(name));
						System.out.println("onclick");
						PageParameters parameters = new PageParameters();
						Experiment ex = (Experiment) model.getObject();
						System.out.println("pn" + ex.getProposal_Number());
						//parameters.add("pn", ex.getProposal_Number());
						parameters.add("exid", ex.getId());
						// this.replace(table);
						// replace(new HomePanel("homepanel",parameters));
						// setResponsePage(HomePanel.class,parameters);
						
						HomePanel hp = new HomePanel("homepanel", parameters);
						//Page p = getPage();
						//System.out.println("path" + p.getPath());
						getPage().replace(hp);
						// or do what you want when the link is clicked
					}

					@Override
					public IMarkupFragment getMarkup() {
						// display the content you like - access the properties of your object
						Experiment ex = (Experiment) model.getObject();
                        String edate = ex.getDTSTART();
                       
						return Markup.of("<div wicket:id='cell'>" + ex.getProposal_Number() + "</div>");
					}
				});
				//cssModel.setObject("bkg-green");
				 //cellItem.add(AttributeModifier.append("\"style\"", "font-style:italic"));
				 highlight( model,cellItem);
				 //new AttributeModifier("style", "font-style:italic");
				 //cellItem.add(AttributeModifier.append("class", "goto")); 
				 cellItem.add(AttributeModifier.replace("style", "color: blue"));
				 
               // if edate
				//cellItem.add(AttributeModifier.replace("bgcolor", "lightblue")); 
				// Populate your item here
			}
		});
		
		columns.add(new PropertyColumn(new Model<String>("Experiment ID"), "PID","PID") {
			public void populateItem(Item cellItem, String componentId, IModel model) {
				super.populateItem(cellItem, componentId, model);
				 highlight( model,cellItem);
			}
			 
		});
		
		columns.add(new TextFilteredPropertyColumn(new Model("PI"), "PI", "PI") {
			// add the LinkPanel to the cell item
			public void populateItem(Item cellItem, String componentId, IModel model) {
				cellItem.add(new Link<String>(componentId) {

					@Override
					public void onClick() {
						// experiments.addAll(service.getExperimentBySupportName(name));
						System.out.println("onclick");
						PageParameters parameters1 = new PageParameters();
						Experiment ex = (Experiment) model.getObject();
						System.out.println("pi" + ex.getPi());
						parameters1.add("pi", ex.getPi());
						
						
						//ExSortableProvider sp = new ExSortableProvider(exp);
						HomePanel hp1 = new HomePanel("homepanel", parameters1);
						System.out.println("pi" + ex.getPi());
						getPage().replace(hp1);
						
					}

					@Override
					public IMarkupFragment getMarkup() {
						// display the content you like - access the properties of your object
						Experiment ex = (Experiment) model.getObject();

						return Markup.of("<div wicket:id='cell'>" + ex.getPi() + "</div>");
					}
				});
				 highlight( model,cellItem);
				 cellItem.add(AttributeModifier.replace("style", "color: blue"));
				// Populate your item here
			}
		});
		
		//columns.add(new PropertyColumn(new Model<String>("Experiment ID"), "ExperimentID", "ExperimentID"));

		//columns.add(new PropertyColumn(new Model<String>("Date Start"), "DTSTART","DTSTART"));
		columns.add(new TextFilteredPropertyColumn(new Model<String>("Date Start"), "DTSTART","DTSTART"));
		//columns.add(new PropertyColumn(new Model<String>("PI"), "PI", "PI"));
		//columns.add(new PropertyColumn(new Model<String>("Summary"), "SUMMARY"));
		// columns.add(new PropertyColumn(new Model<String>("LOCATION"), "LOCATION"));
		columns.add(new PropertyColumn(new Model<String>("Magnet System"), "Magnet_System"));
		
		columns.add(new PropertyColumn(new Model<String>("Experiment Title"), "Experiment_Title", "Experiment_Title"));
		// columns.add(new TextFilteredPropertyColumn<FilterPage.Entity,
		// FilterPage.Entity, FilterPage.Entity>(Model.of("Start Date"),
		// dataProvider.getFilterState(), "start"));
		columns.add(new TextFilteredPropertyColumn(new Model<String>("Station"), "LOCATION", "LOCATION"));
		columns.add(new TextFilteredPropertyColumn(new Model<String>("Support"), "Support", "Support"));
		//columns.add(new EditLink("editLink",))
		
		
		//columns.add(new TextFilteredPropertyColumn(new Model<String>("UID"), "UID", "UID"));
		
		columns.add(new TextFilteredPropertyColumn(new Model("ID"), "ExperimentID", "ExperimentID") {
			 public void populateItem(Item cellItem, String componentId, IModel model) {	
				
				cellItem.add(new Link<String>(componentId) {

					@Override
					public void onClick() {
						// experiments.addAll(service.getExperimentBySupportName(name));
						System.out.println("onclick");
						PageParameters parameters = new PageParameters();
						Experiment ex = (Experiment) model.getObject();
						System.out.println("ex" + ex.getId());
						parameters.add("exid", ex.getId());
						// this.replace(table);
						// replace(new HomePanel("homepanel",parameters));
						// setResponsePage(HomePanel.class,parameters);
						//ExternalLink("link", new PropertyModel<>(model, "url"));
						HomePanel hp = new HomePanel("homepanel", parameters);
						
						//Page p = getPage();
						//System.out.println("path" + p.getPath());
						//getPage().replace(hp);
						// or do what you want when the link is clicked
					}

					@Override
					public IMarkupFragment getMarkup() {
						// display the content you like - access the properties of your object
						Experiment ex = (Experiment) model.getObject();
                       
						return Markup.of("<div wicket:id='cell'>" + ex.getId() + "</div>");
						
					}
				});
				// highlight( model,cellItem);
				// Populate your item here
			}
		});
		
	
		
		
		
		columns.add(new TextFilteredPropertyColumn(new Model("Link "), "ExperimentID", "ExperimentID") {
			// add the LinkPanel to the cell item
			public void populateItem(Item cellItem, String componentId, IModel model) {
				Experiment ex = (Experiment) model.getObject();
				System.out.println("ex" + ex.getId());
				//parameters.add("exid", ex.getId());
				
				 String url="https://users.magnet.fsu.edu:443/Experiments/Display.aspx?ExperimentID="+ ex.getId();
				 
		        ExternalLink capabilitiesLink =
		                new ExternalLink(componentId, Model.of(url), Model.of("GotoFSU"))
		                {
		                    @Override
		                    protected void onComponentTag(ComponentTag tag)
		                    {
		                        super.onComponentTag(tag);
		                        tag.put("target", "_blank");
		                    }
		                };
		        cellItem.add(capabilitiesLink);
		       // highlight( model,cellItem);
		       // cellItem.add(AttributeModifier.replace("bgcolor", "lightblue")); 
		        cellItem.add(AttributeModifier.replace("style", "color: blue"));
				// Populate your item here
			}
		});
		
		
		
		columns.add(new TextFilteredPropertyColumn(new Model("Edit"), "PID", "PID") {
			// add the LinkPanel to the cell item
			public void populateItem(Item cellItem, String componentId, IModel model) {
				cellItem.add(new Link<String>(componentId) {

					@Override
					public void onClick() {
						// experiments.addAll(service.getExperimentBySupportName(name));
						System.out.println("onclick");
						PageParameters parameters1 = new PageParameters();
						Experiment ex = (Experiment) model.getObject();
						
						System.out.println("pi" + ex.getPi());
						parameters1.add("pid", ex.getPID());
						parameters1.add("start", ex.getDTSTART());
						
						 setResponsePage(EditPage.class, parameters1);
						
					}

					@Override
					public IMarkupFragment getMarkup() {
						// display the content you like - access the properties of your object
						Experiment ex = (Experiment) model.getObject();

						return Markup.of("<div wicket:id='cell'>" + "edit" + "</div>");
					}
					
				});
				
				 cellItem.add(AttributeModifier.replace("style", "color: blue"));
				// Populate your item here
			}
		});
	
		
		
		
		return columns;
	}
	// add(new MyHomePage(parameters));
	// setResponsePage(MyHomePage.class);

}
