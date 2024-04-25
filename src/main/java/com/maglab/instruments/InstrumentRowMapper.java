package com.maglab.instruments;


import org.springframework.jdbc.core.RowMapper;

import com.maglab.model.Experiment;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InstrumentRowMapper implements RowMapper<Instrument> {
    @Override
   // Experiment ex = new Experiment();
	 // ex.setId( rs.getInt("experimentID"));
    public Instrument mapRow(ResultSet rs, int rowNum) throws SQLException {
        Instrument prob = new Instrument(
            rs.getString("instrument_pid"),
            rs.getString("title"),
            rs.getString("filename"),
            rs.getString("filestore_path"),
            rs.getString("create_date"),
            rs.getString("modify_date"),
            rs.getString("out_of_service"),
            rs.getString("instrument_type")
            
            //,null // Categories will be populated separately
        );
        return prob;
    }
}
