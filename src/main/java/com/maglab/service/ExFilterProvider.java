package com.maglab.service;

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

import com.maglab.model.Experiment;

public class ExFilterProvider extends SortableDataProvider {

private List<Experiment> experiments = new ArrayList<Experiment>();
String filter=null;
private transient List<Experiment> filtered;
private List<Experiment> getFiltered() {
if (filtered == null) {
filtered = filter();
}
return filtered;
}
private List<Experiment> filter() {
	 List<Experiment> filtered=new ArrayList<Experiment>(experiments);
	 if (filter != null) {
	 String upper = filter.toUpperCase();
	 Iterator<Experiment> it = filtered.iterator();
	 while (it.hasNext()) {
	 Experiment contact = it.next();
	 if (contact.getPi().toUpperCase().indexOf(upper) < 0
	 && contact.getSummary().toUpperCase().indexOf(upper) < 0) {
	 it.remove();
	 }
	 }
	 }
	 return filtered;
	 }
@Override
public void detach() {
filtered = null;
super.detach();
}


//private CustomComparator comparator = new CustomComparator();
    int nav = 1;
	public ExFilterProvider(List<Experiment> experiments,String filter) {
		this.filter=filter;
        this.experiments=experiments;
        setSort("PI", SortOrder.ASCENDING);
        nav = getSort().isAscending() ? 1 : -1;
		}
	@Override
	public Iterator <? extends Experiment> iterator(long first, long count) {
		int dir = getSort().isAscending() ? 1 : -1;

		if (filter!=null) experiments= getFiltered();
		 List<Experiment> newList = new ArrayList<Experiment>(experiments);
		 Collections.sort(newList, new Comparator() {
			                 @Override
			                 public int compare(Object o1, Object o2) {
			 
			                     int dir = getSort().isAscending() ? 1 : -1;
			 
			                     if ("PI".equals(getSort().getProperty())) {
			 
			                         return dir * (((Experiment) o1).getPi().compareTo(((Experiment) o2).getPi()));
			 
			                     } else if("LOCATION".equals(getSort().getProperty())) {
			 
			                         return dir * (((Experiment) o1).getLOCATION().compareTo(((Experiment) o2).getLOCATION()));
			 
			                     }
			                     
			                     else if("Experiment_Title".equals(getSort().getProperty())) {
			            			 
			                         return dir * (((Experiment) o1).getTitle().compareTo(((Experiment) o2).getTitle()));
			 
			                     }
                                 else if("ExperimentID".equals(getSort().getProperty())) {
                                	 int id1=((Experiment) o1).getId();
                                	 int id2=((Experiment) o2).getId();
			                         return dir * (new Integer(id1).compareTo(new Integer(id2)));
			 
			                     }
			                     else {
			                    	 return dir * (((Experiment) o1).getTitle().compareTo(((Experiment) o2).getTitle()));
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
		return getFiltered().size();
		//return experiments.size();
	}

	public Iterator<? extends Experiment> iterator(int first, int count) {
		 return getFiltered()
		 .subList(
		 first,
		 Math.min(first + count, getFiltered().size()))
		 .iterator();
		 }
	


	@Override
	public IModel model(Object object) {
		// TODO Auto-generated method stub
		return new AbstractReadOnlyModel<Experiment>() {
            @Override
            public Experiment getObject() {
                return (Experiment) object;
            }
        };
	
		
	}}
