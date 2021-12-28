package com.maglab.panel;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Generics;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.model.IModel;
import com.giffing.wicket.spring.boot.context.scan.WicketHomePage;
import com.maglab.forms.SearchForm;
import com.maglab.model.Experiment;
import com.maglab.service.ExSortableProvider;
import com.maglab.service.ExperimentService;
@AuthorizeInstantiation("USER")
@WicketHomePage
public class MyHomePage extends  HomeTemplatePage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1900139055484193969L;
	@SpringBean
    private ExperimentService service;

	public MyHomePage(final PageParameters parameters) {
		 super(parameters);
		 System.out.println("MyHomePage");
		//  super();
		 
		  add(new HomePanel("homepanel",parameters));
		
}
	
}