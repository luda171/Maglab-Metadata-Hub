package com.maglab.instruments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

public class InsSortableProvider extends SortableDataProvider {

private List<Instrument> experiments = new ArrayList<Instrument>();



//private CustomComparator comparator = new CustomComparator();
    int nav = 1;
	public InsSortableProvider(List<Instrument> experiments) {
        this.experiments=experiments;
        setSort("title", SortOrder.DESCENDING);
        nav = getSort().isAscending() ? 1 : -1;
		}
	@Override
	public Iterator <? extends Instrument> iterator(long first, long count) {
		int dir = getSort().isAscending() ? 1 : -1;

		 List<Instrument> newList = new ArrayList<Instrument>(experiments);
		 Collections.sort(newList, new Comparator() {
			                 @Override
			                 public int compare(Object o1, Object o2) {
			 
			                     int dir = getSort().isAscending() ? 1 : -1;
			 
			                     if ("instrumentPid".equals(getSort().getProperty())) {
			 
			                         return dir * (((Instrument) o1).getInstrumentPid()).compareTo(((Instrument) o2).getInstrumentPid());
			 
			                     } else if("filestorePath".equals(getSort().getProperty())) {
			 
			                         return dir * (((Instrument) o1).getFilestorePath().compareTo(((Instrument) o2).getFilestorePath()));
			 
			                     }
			                     else if("createDate".equals(getSort().getProperty())) {
			            			 
			                         return dir * (((Instrument) o1).getCreateDate().compareTo(((Instrument) o2).getCreateDate()));
			 
			                     }
			                     else if("title".equals(getSort().getProperty())) {
			            			 
			                         return dir * (((Instrument) o1).getTitle().compareTo(((Instrument) o2).getTitle()));
			 
			                     }
                                else if("instrumentType".equals(getSort().getProperty())) {
			            			 
			                         return dir * (((Instrument) o1).getInstrumenType().compareTo(((Instrument) o2).getInstrumenType()));
			 
			                     }
                               //  else if("modifyDate".equals(getSort().getProperty())) {
                               // 	 int id1=((Instrument) o1).getModifyDate();
                               // 	 int id2=((Instrument) o2).getId();
			                   //      return dir * (new Integer(id1).compareTo(new Integer(id2)));
			 
			                   //  }
			                     else {
			                    	 return dir * (((Instrument) o1).getTitle().compareTo(((Instrument) o2).getTitle()));
			                     }
			                 }

							
			 
			             });

		// Collections.sort(newList, comparator);
		// TODO Auto-generated method stub
		 return newList.subList((int)first, (int)first + (int)count).iterator();
		//return null;
	}

	@Override
	public long size() {
		// TODO Auto-generated method stub
		return experiments.size();
	}

	
/*
	 public class CustomComparator implements Comparator<Experiment> {
    	 
		 
			@Override
			public int compare(Experiment o1, Experiment o2) {
				// TODO Auto-generated method stub
			if(getSort().getProperty().equals("PI")) {
				return (o1.getPi().toUpperCase()).compareTo(o2.getPi().toUpperCase());
				//return 0;
				
			}else if(getSort().getProperty().equals("Experiment_Title")) {
				return nav*(o1.getTitle().toUpperCase()).compareTo(o2.getTitle().toUpperCase());
			}
			else if (getSort().getProperty().equals("LOCATION")) {
				return nav*(o1.getLOCATION().toUpperCase()).compareTo(o2.getLOCATION().toUpperCase());
			}
			else {
				return 0;
			}
		
 	}
	
	
	 }*/



	@Override
	public IModel model(Object object) {
		// TODO Auto-generated method stub
		return new AbstractReadOnlyModel<Instrument>() {
            @Override
            public Instrument getObject() {
                return (Instrument) object;
            }
        };
	
		
	}}
