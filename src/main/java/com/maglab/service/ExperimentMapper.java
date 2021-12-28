package com.maglab.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.maglab.model.Experiment;

public class ExperimentMapper implements RowMapper { 
	
	/*
	 * experimentID INTEGER, ...> PI VARCHAR(30) NOT NULL, ...> location VARCHAR(10)
	 * NOT NULL, ...> facility VARCHAR(20) NOT NULL, ...> magnetsystem VARCHAR(50)
	 * NOT NULL, ...> experiment_title VARCHAR(80) NOT NULL, ...> proposal_title
	 * VARCHAR(200) NOT NULL, ...> proposal_number VARCHAR(20) NOT NULL, ...>
	 * dtstart TEXT, ...> dtend TEXT, ...> dtstamp TEXT, ...> summary VARCHAR(80),
	 * ...> support VARCHAR(200), ...> attendee VARCHAR(200), ...> uid VARCHAR(36),
	 * ...> dtupdate TEXT
	 * 
	 */
	
	 public Experiment mapRow(ResultSet rs, int rowNum) throws SQLException {  
	  
	  Experiment ex = new Experiment();
	  ex.setId( rs.getInt("experimentID"));
	  ex.setPi(rs.getString("PI"));
	  ex.setLOCATION(rs.getString("location"));
	  ex.setFacility(rs.getString("facility"));
	  ex.setMagnet_System(rs.getString("magnetsystem"));
	  ex.setTitle(rs.getString("experiment_title"));
	  ex.setProposal_Title(rs.getString("proposal_title"));
	  ex.setSummary(rs.getString("summary"));
	  ex.setDTSTART(rs.getString("dtstart"));
	  ex.setDTEND(rs.getString("dtend"));
	  ex.setProposal_Number(rs.getString("proposal_number"));
	  ex.setPID(rs.getString("pid"));
	  //ex.setUID(rs.getString("uid"));
	  ex.setSupport(rs.getString("support"));
	  return ex;  
	 }  
	}   