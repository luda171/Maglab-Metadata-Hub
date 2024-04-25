package com.maglab.instruments;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.file.Folder;
import org.apache.wicket.util.string.StringValue;

import com.maglab.PropConfig;

public class InstrumentEditPage extends WebPage {
	// private int userId;
	private final FileListView fileListView;
	static PropConfig pconf = PropConfig.getInstance();
	String mdir =  pconf.all().get("instrumentdir");
	public class FileListView extends ListView<File>
    {
        /**
         * Construct.
         * 
         * @param name
         *            Component name
         * @param files
         *            The file list model
         */
        public FileListView(String name, final IModel<List<File>> files)
        {
            super(name, files);
        }

        /**
         * @see ListView#populateItem(ListItem)
         */
        @Override
        protected void populateItem(ListItem<File> listItem)
        {
            final File file = listItem.getModelObject();
            listItem.add(new Label("file", file.getName()));
            listItem.add(new Link<Void>("delete")
            {
                @Override
                public void onClick()
                {
                    Files.remove(file);
                    info("Deleted " + file);
                }
            });
        }
    }
	    public InstrumentEditPage(PageParameters parameters) {
	        super(parameters);
	        List<StringValue> nameSearch = parameters.getValues("pid");
			StringValue nameValue = nameSearch.get(0);
			String pid = nameValue.toString();
			
			// List<StringValue> nameS = parameters.getValues("start");
			//	StringValue sv = nameS.get(0);
			//	String start = sv.toString();
			fileListView = new FileListView("fileList", new LoadableDetachableModel<List<File>>()
	        {
	            @Override
	            protected List<File> load()
	            {
	                return Arrays.asList(new Folder(mdir).listFiles());
	            }
	        });
	        add(fileListView);
	        if (pid.equals("add")) {
	        	 add(new InstrumentEditForm("instredit"));
	        	
	        } else {
	        add(new InstrumentEditForm("instredit",pid));
	        }
	    }

}
