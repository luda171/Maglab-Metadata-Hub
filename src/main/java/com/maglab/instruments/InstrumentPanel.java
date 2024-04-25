package com.maglab.instruments;

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
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.html.form.Button;
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
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Generics;

import com.maglab.PropConfig;

//import com.maglab.model.Experiment;

public class InstrumentPanel extends Panel {
	@SpringBean
	private InstrumentService service;
	private String filter;
	static PropConfig pconf = PropConfig.getInstance();
    static  String baseurl =  pconf.all().get("instrumentbaseurl");
	public InstrumentPanel(String id, PageParameters parameters) {
		super(id);
		
		Form form = new Form("aform") {
	        protected void onSubmit() {
	            info("Form.onSubmit executed");
	        }
	    };
		List<Instrument> probs = new ArrayList<>();
		Button button1 = new Button("addinstr") {
	        public void onSubmit() {
	            info("button1.onSubmit executed");
	            PageParameters param = new PageParameters();
	    	    param.add("pid", "add");
	    	    //new MyPage(parameters, arguments...)
	           // setResponsePage(InstrumentEditPage.class,param);
	    	    setResponsePage(new InstrumentEditPage(param));
	        }
	    };
	    form.add(button1);
	  
	   
	 
	    add(form);
	    
		if (parameters.isEmpty()) {
			System.out.print("empty");
			probs.addAll(service.getInstruments());

		}
		//ListDataProvider ex = new ListDataProvider(probs);
		 InsSortableProvider sp = new InsSortableProvider(probs);

		// ExFilterProvider fp = new ExFilterProvider(experiments, filter);
		DataTable table = new DataTable("datatable", newColumnList(), sp, 20);

		table.addBottomToolbar(new NavigationToolbar(table));
		table.addTopToolbar(new HeadersToolbar(table, sp));
		// add(new PagingNavigator("navigator",listView ));
		//table.addTopToolbar(new HeadersToolbar(table, null));

		add(table);
	}

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

	public static void highlight(IModel model, Item cellItem) {
		Instrument ex = (Instrument) model.getObject();
		String edate = ex.getCreateDate();
		// System.out.println("edate"+edate);
		Date now = new Date();
		// Date n = new Date(edate);

		SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd");
		Date n = null;
		try {
			n = sqldf.parse(edate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (n != null) {
			if (inSameWeek(n, now) && inSameYear(n, now)) {
				cellItem.add(AttributeModifier.replace("bgcolor", "lightyellow"));
			}
		}
	}

	@SuppressWarnings("serial")
private static List<IColumn> newColumnList() {
		List<IColumn> columns = Generics.newArrayList();
		/*columns.add(new PropertyColumn(new Model<String>("Instrument ID"), "instrumentPid", "instrumentPid") {
			//public void populateItem(Item cellItem, String componentId, IModel model) {
			//	super.populateItem(cellItem, componentId, model);
			//	highlight(model, cellItem);
			//}
			// add the LinkPanel to the cell item
						public void populateItem(Item cellItem, String componentId, IModel model) {
							super.populateItem(cellItem, componentId, model);
							highlight(model, cellItem);
							Instrument ex = (Instrument) model.getObject();
							System.out.println("ex" + ex.getFilename());

							String url = baseurl + ex.getInstrumentPid();

							ExternalLink capabilitiesLink = new ExternalLink(componentId, Model.of(url), Model.of("See")) {
								@Override
								protected void onComponentTag(ComponentTag tag) {
									super.onComponentTag(tag);
									tag.put("target", "_blank");
								}
							};
							cellItem.add(capabilitiesLink);

							cellItem.add(AttributeModifier.replace("style", "color: blue"));

						}
		});
		*/
		columns.add(new PropertyColumn(Model.of("Instrument ID"), "instrumentPid", "instrumentPid") {
            @Override
            public void populateItem(Item cellItem, String componentId, IModel rowModel) {
                Instrument ex = (Instrument) rowModel.getObject();
                String url = baseurl + ex.getInstrumentPid();
                ExternalLink link = new ExternalLink(componentId, Model.of(url), Model.of(ex.getInstrumentPid()));
                link.add(AttributeModifier.replace("style", "color: blue"));
                cellItem.add(link);
            }
        });
		/* columns.add(new TextFilteredPropertyColumn(new Model("Instrument ID"), "instrumentPid", "instrumentPid") {
			// add the LinkPanel to the cell item
			public void populateItem(Item cellItem, String componentId, IModel model) {
				// final IModel<String> cssModel = Model.of("bkg-default");
				 cellItem.add(new Link<String>(componentId) {

					@Override
					public void onClick() {

						System.out.println("onclick");
						PageParameters parameters = new PageParameters();
						Instrument ex = (Instrument) model.getObject();
						System.out.println("pn" + ex.getInstrumentPid());

						parameters.add("probid", ex.getInstrumentPid());

						InstrumentPanel hp = new InstrumentPanel("probpanel", parameters);

						getPage().replace(hp);

					}

					@Override
					public IMarkupFragment getMarkup() {
						// display the content you like - access the properties of your object
						Instrument ex = (Instrument) model.getObject();
						String edate = ex.getModifyDate();

						return Markup.of("<div wicket:id='cell'>" + ex.getInstrumentPid() + "</div>");
					}
				});

				highlight(model, cellItem);

				cellItem.add(AttributeModifier.replace("style", "color: blue"));

			}
		});
*/
		columns.add(new PropertyColumn(new Model<String>("Title"), "title", "title") {
			public void populateItem(Item cellItem, String componentId, IModel model) {
				super.populateItem(cellItem, componentId, model);
				highlight(model, cellItem);
			}

		});
		
		columns.add(new PropertyColumn(new Model<String>("Instrument Type"), "instrumentType", "instrumentType") {
			public void populateItem(Item cellItem, String componentId, IModel model) {
				super.populateItem(cellItem, componentId, model);
				highlight(model, cellItem);
			}

		});

		columns.add(new TextFilteredPropertyColumn(new Model("Created"), "createDate", "createDate") {
			// add the LinkPanel to the cell item
			public void populateItem(Item cellItem, String componentId, IModel model) {
				cellItem.add(new Link<String>(componentId) {

					@Override
					public void onClick() {
						// experiments.addAll(service.getExperimentBySupportName(name));
						System.out.println("onclick");
						PageParameters parameters1 = new PageParameters();
						Instrument ex = (Instrument) model.getObject();
						System.out.println("pi" + ex.getCreateDate());
						parameters1.add("pi", ex.getCreateDate());

						// ExSortableProvider sp = new ExSortableProvider(exp);
						InstrumentPanel hp1 = new InstrumentPanel("probpanel", parameters1);
						System.out.println("pi" + ex.getCreateDate());
						getPage().replace(hp1);

					}

					@Override
					public IMarkupFragment getMarkup() {
						// display the content you like - access the properties of your object
						Instrument ex = (Instrument) model.getObject();

						return Markup.of("<div wicket:id='cell'>" + ex.getCreateDate() + "</div>");
					}
				});
				highlight(model, cellItem);
				cellItem.add(AttributeModifier.replace("style", "color: blue"));
				// Populate your item here
			}
		});

		//columns.add(new TextFilteredPropertyColumn(new Model<String>("instrument Type"), "instrumentType", "instrumentType"));

/*		columns.add(new TextFilteredPropertyColumn(new Model("Link "), "instrumentPid", "instrumentPid") {
			// add the LinkPanel to the cell item
			public void populateItem(Item cellItem, String componentId, IModel model) {
				Instrument ex = (Instrument) model.getObject();
				System.out.println("ex" + ex.getFilename());

				String url = baseurl + ex.getInstrumentPid();

				ExternalLink capabilitiesLink = new ExternalLink(componentId, Model.of(url), Model.of("See")) {
					@Override
					protected void onComponentTag(ComponentTag tag) {
						super.onComponentTag(tag);
						tag.put("target", "_blank");
					}
				};
				cellItem.add(capabilitiesLink);

				cellItem.add(AttributeModifier.replace("style", "color: blue"));

			}
		});
*/
		
		  columns.add(new TextFilteredPropertyColumn(new Model("Edit"), "instrumentPid", "instrumentPid") {
		  // add the LinkPanel to the cell item
			  public void populateItem(Item cellItem,
		  String componentId, IModel model) { cellItem.add(new
		  Link<String>(componentId) {
		  
		  @Override public void onClick() { //
		  //probs.addAll(service.getExperimentBySupportName(name));
		  System.out.println("onclick"); 
		  PageParameters parameters1 = new PageParameters();
		  Instrument ex = (Instrument) model.getObject();
		  
		  System.out.println("pi" + ex.getInstrumentPid()); parameters1.add("pid", ex.getInstrumentPid());
		  parameters1.add("title", ex.getTitle());
		  
		  setResponsePage(InstrumentEditPage.class, parameters1);
		  
		 }
		  
		  @Override public IMarkupFragment getMarkup() { // display the content you
		 //like - access the properties of your object
			  Instrument ex = (Instrument)model.getObject();
		  
		  return Markup.of("<div wicket:id='cell'>" + "edit" + "</div>"); }
		  
		  });
		  
		  cellItem.add(AttributeModifier.replace("style", "color: blue")); // Populateyour item here
		  } });
		 
		 

		return columns;
	}

}
