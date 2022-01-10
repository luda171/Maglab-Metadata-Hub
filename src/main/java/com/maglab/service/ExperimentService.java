package com.maglab.service;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.maglab.model.DbUtils;
import com.maglab.model.Experiment;


@Service
public class ExperimentService {
	//private static final String SQL_SELECT_ALL_EXPERIMENTS = "SELECT * FROM dedupexperiments;";
	private static final String SQL_SELECT_ALL_EXPERIMENTS = "SELECT * FROM experiments order by dtstart desc;";
	private static final String SQL_SELECT_NOW_EXPERIMENTS = "SELECT * FROM experiments where location=? and `dtend` >= ? and dtstart<= ? ;";
	
	private static final String SQL_SELECT_byLocation_EXPERIMENTS = "SELECT * FROM experiments where location=?  ;";
	private static final String SQL_SELECT_byPI_EXPERIMENTS = "SELECT * FROM experiments where UPPER(PI) like UPPER(?) or  UPPER(PI) like UPPER(?)  ;";
	private static final String SQL_SELECT_EXPERIMENT_byID = "SELECT * FROM experiments where experimentID=?  ;";
	private static final String SQL_SELECT_EXPERIMENT_byPID = "SELECT * FROM experiments where proposal_number=?  ;";
	private static final String SQL_SELECT_EXPERIMENT_byStartPID = "SELECT * FROM experiments where pid=? and dtstart= ? ;";
	private static final String SQL_SELECT_EXPERIMENT_Overlap="select * from experiments where location=? and dtstart<=? and dtend >=? ;";
	
	private static final String SQL_UPDATE_EXP ="UPDATE experiments set location=?,dtstart=?,dtend=?,dtupdate=? where pid=? and dtstart=? and localupdate='Y' ;";
	private static final String SQL_UPDATE_UNASSIGNED ="UPDATE experiments set location='unassigned',dtupdate=? where pid=? and dtstart=? and localupdate='Y' ;";
	
	private static final String SQL_SELECT_bySupport_EXPERIMENTS = "SELECT * FROM experiments where"
			
			//+ " UPPER(support) like UPPER(?) or "
			//+ " UPPER(support) like UPPER(?) or "
			+ " UPPER(summary) like UPPER(?)  ;";
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	public Collection<Experiment> getExperiments() {
        return jdbcTemplate.query(
                SQL_SELECT_ALL_EXPERIMENTS,
                new ExperimentMapper()
        );
    }
	
	public List<Experiment> getExperimentsByLocation(String name) {
        return jdbcTemplate.query(
        		SQL_SELECT_byLocation_EXPERIMENTS,
                new Object[]{name},
                new ExperimentMapper()
        );
    }
	public List<Experiment> getExperimentsByLocationNow(String name) {
		Date now = new Date();
		SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd");
		 DbUtils utils = new DbUtils();
		 System.out.println("was now here1");
		
		 String dr= sqldf.format(now);
        return jdbcTemplate.query(
        		SQL_SELECT_NOW_EXPERIMENTS,
                new Object[]{name,dr,dr},
                new ExperimentMapper()
        );
    }
	public List<Experiment> getExperimentsByPI(String name) {
        return jdbcTemplate.query(
        		SQL_SELECT_byPI_EXPERIMENTS,
                new Object[]{name+ "%","%" +name  },
                new ExperimentMapper()
        );
    }
	public List<Experiment> getExperimentsByPID(String name) {
        return jdbcTemplate.query(
        		SQL_SELECT_EXPERIMENT_byPID,
                new Object[]{name},
                new ExperimentMapper()
        );
    }
	public List<Experiment> getExperimentByID(int name) {
        return jdbcTemplate.query(
        		SQL_SELECT_EXPERIMENT_byID,
                new Object[]{name},
                new ExperimentMapper()
        );
    }
	public List<Experiment> getExperimentBySupportName(String name) {
        return jdbcTemplate.query(
        		SQL_SELECT_bySupport_EXPERIMENTS,
                new Object[]{"%"+name+"%"},
                new ExperimentMapper()
        );
    }
	public List<Experiment> getExperimentByStartPID(String pid,String start) {
        return jdbcTemplate.query(
        		SQL_SELECT_EXPERIMENT_byStartPID,
                new Object[]{pid,start},
                new ExperimentMapper()
        );
    }
	public void updateExp(String pid, String oldsdate, String location,String sdate,String edate) {
		Date now = new Date();
		SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd");
		 DbUtils utils = new DbUtils();
		 System.out.println("was now here1");
		
		 String dr= sqldf.format(now);
		List ex = jdbcTemplate.query(
				SQL_SELECT_EXPERIMENT_Overlap,
                new Object[]{location,edate,sdate},
                new ExperimentMapper()
        );
		if (ex.size()>0) {
			Experiment ex1 = (Experiment) ex.get(0);
			String apid = ex1.getPID();
			String astart = ex1.getDTSTART();
			System.out.println("update UNASSIGNED"+apid);
			jdbcTemplate.update(
					SQL_UPDATE_UNASSIGNED, dr,apid,astart);
		}
        jdbcTemplate.update(
        		SQL_UPDATE_EXP, location,sdate,edate,dr,pid,oldsdate);
    }
 //weekly
	
	
	
}
