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

public class ExSortableProvider extends SortableDataProvider {

private List<Experiment> experiments = new ArrayList<Experiment>();



//private CustomComparator comparator = new CustomComparator();
    int nav = 1;
	public ExSortableProvider(List<Experiment> experiments) {
        this.experiments=experiments;
        setSort("PI", SortOrder.ASCENDING);
        nav = getSort().isAscending() ? 1 : -1;
		}
	@Override
	public Iterator <? extends Experiment> iterator(long first, long count) {
		int dir = getSort().isAscending() ? 1 : -1;

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
			                     else if("DTSTART".equals(getSort().getProperty())) {
			            			 
			                         return dir * (((Experiment) o1).getSTART().compareTo(((Experiment) o2).getSTART()));
			 
			                     }
			                     else if("Experiment_Title".equals(getSort().getProperty())) {
			            			 
			                         return dir * (((Experiment) o1).getTitle().compareTo(((Experiment) o2).getTitle()));
			 
			                     }
                                else if("Proposal_Number".equals(getSort().getProperty())) {
			            			 
			                         return dir * (((Experiment) o1).getProposal_Number().compareTo(((Experiment) o2).getProposal_Number()));
			 
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
		return new AbstractReadOnlyModel<Experiment>() {
            @Override
            public Experiment getObject() {
                return (Experiment) object;
            }
        };
	
		
	}}
