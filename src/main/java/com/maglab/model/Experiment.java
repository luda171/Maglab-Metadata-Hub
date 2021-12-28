package com.maglab.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Experiment implements Serializable  {
	private static final long serialVersionUID = 1L;
		String DTEND = "";
		String DTSTAMP = "";
		String DTSTART = "";
		String LOCATION = "";
		//ORGANIZER: 
		//SEQUENCE: 0
		String SUMMARY = "";
		//String UID = "";
		String calURL= "https://users.magnet.fsu.edu:443/Experiments/Display.aspx?ExperimentID=40183";
		//Component: VEVENT
		// ATTENDEE: MAILTO:jsingle@lanl.gov
		//DESCRIPTION: 
		//List attendee = new ArrayList();
		String Facility = "";
		String Experiment_Title = "";
		String Magnet_System ="";
		String Proposal_Title="";
		String Proposal_Number="";
		String PI = "";
		String Support;
		int ExperimentID;
		String PID;
		//SimpleDateFormat dfs = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd");
		 public final void setTitle( String string)
		    {
			 Experiment_Title = string;
		    }
		 
		 
		 public final void setMagnet_System( String string)
		    {
			 Magnet_System = string;
		    } 
		 
		 public final void setProposal_Title( String string)
		    {
			 Proposal_Title = string;
		    } 
		 
		 public final void setProposal_Number( String string)
		    {
			 Proposal_Number = string;
		    } 
		 public final void setPi( String string)
		    {
			 PI = string;
		    } 
		 public final void setSummary( String string)
		    {
			 SUMMARY = string;
		    } 
		 public final void setSupport( String string)
		    {
			 Support = string;
		    } 
		 public final void setFacility( String string)
		    {
			 Facility = string;
		    } 
		 public final void setLOCATION( String string)
		    {
			 LOCATION = string;
		    } 
		 public final void setDTSTART( String string)
		    {
			 DTSTART = string;
		    } 
		 
		 public final void setDTSTAMP( String string)
		    {
			 DTSTAMP = string;
		    } 
		 public final void setDTEND( String string)
		    {
			 DTEND = string;
		    } 
		 //public final void setUID( String string)
		   // {
			// UID = string;
		    //} 
		 public final void setPID( String string)
		    {
			 PID = string;
		    } 
		 public final void setId( int id)
		    {
			 ExperimentID = id;
		    } 
		// public final void addAttendee(String at)
		  //  {
			// attendee.add(at);
		    //} 
		 
		 //getters
		 public String getTitle()
		    {
			 return Experiment_Title;
		    }
		 
		 
		 public String getMagnet_System()
		    {
			 return Magnet_System;
		    } 
		 
		 public String getProposal_Title()
		    {
			 return Proposal_Title;
		    } 
		 
		 public String getProposal_Number()
		    {
			 return Proposal_Number;
		    } 
		 public String getPi()
		    {
			 return PI; 
		    } 
		 public String getSummary()
		    {
			 return SUMMARY ;
		    } 
		 public String getSupport()
		    {
			 return Support ;
		    } 
		 public String getFacility()
		    {
			 return Facility;
		    } 
		 public String getLOCATION()
		    {
			 return LOCATION;
		    } 
		 public String getDTSTART()
		    {
			 return DTSTART;
		    } 
		 
		 public Date getSTART()
		    {
			 java.util.Date d = null;
			try {
				d = sqldf.parse(DTSTART);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 return d;
		    } 
		 public String getDTSTAMP()
		    {
			 return DTSTAMP;
		    } 
		 public String  getDTEND()
		    {
			 return DTEND;
		    } 
		// public String getUID()
		  //  {
			// return UID;
		    //} 
		 public String getPID()
		    {
			 return PID;
		    } 
		 public int getId()
		    {
			return  ExperimentID;
		    } 
		// public List getAttendee()
		 //   {
		//	 return attendee;
		 //   } 
		 
}
