package com.maglab.instruments;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;




@Service
public class InstrumentService {
	private static final String SQL_SELECT_ALL_ins = "SELECT instrument_pid,title,filename,filestore_path,"
			+ "create_date,modify_date,out_of_service, instrument_type "
			+ " FROM instruments p where out_of_service='F' ";
	private static final String SQL_SELECT_bypid_ins = "SELECT instrument_pid,title,filename,filestore_path,"
			+ "create_date,modify_date,out_of_service, instrument_type "
			+ " FROM instruments p where out_of_service='F' and instrument_pid =?;";
		
	private static final String SQL_update_ins = "Update \n"
			+ " instruments set title=?,filename=?, out_of_service=?,modify_date=?,instrument_type=? where instrument_pid =?\n;";		
	private static final String SQL_insert_prob="insert or replace into instruments  "
			+ "  (instrument_pid,title,filename,filestore_path,create_date,modify_date,out_of_service, instrument_type)"
			+ "   values(?,?,?,?," + "?,?,?,?)";
			

@Autowired
private JdbcTemplate jdbcTemplate;

public Collection<Instrument> getInstruments() {
    return jdbcTemplate.query(
            SQL_SELECT_ALL_ins,
            new InstrumentRowMapper()
    );
}

public List<Instrument> getInstrumentsByPID(String name) {
    return jdbcTemplate.query(
    		SQL_SELECT_bypid_ins,
            new Object[]{name},
            new InstrumentRowMapper()
    );
}

public void updateInstrument(String pid, String filename, String title, String out,String type) {
	Date now = new Date();
	SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd");
	
	 System.out.println("was now here1");
	
	 String dr= sqldf.format(now);
    jdbcTemplate.update(SQL_update_ins, title,filename,out,dr,type,pid);
}
public void insertInstrument(String pid, String filename, String title, String fpath,String out,String type) {
	Date now = new Date();
	SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd");
	
	 System.out.println("was now here1");
	
	 String dr= sqldf.format(now);
    jdbcTemplate.update(SQL_insert_prob, pid,title,filename,fpath,dr,dr,out,type);
}
}