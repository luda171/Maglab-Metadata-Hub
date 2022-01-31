package com.maglab.model;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class DbUtils {
	private static final String SQL_SELECT_ALL_EXPERIMENTS = "SELECT * FROM experiments;";
	private static final String SQL_SELECT_NOW_EXPERIMENTS = "SELECT * FROM experiments where location=? and `dtend` >= ? and dtstart<= ? ;";
	private static final String SQL_SELECT_by_pid_EXPERIMENTS = "SELECT * FROM experiments where pid=?;";
	private static final String SQL_SELECT_EXPERIMENTS_RANGE = "SELECT * FROM experiments where  dtstart between ? and ? ;";
	
	 String dbURL = "jdbc:sqlite:pulsefacility.db";
	 
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
	 
	 public static void main(String[] args) {
	        try {
	            Class.forName("org.sqlite.JDBC");
	            String dbURL = "jdbc:sqlite:pulsefacility.db";
	            Connection conn = DriverManager.getConnection(dbURL);
	            if (conn != null) {
	                System.out.println("Connected to the database");
	                DatabaseMetaData dm = (DatabaseMetaData) conn.getMetaData();
	                System.out.println("Driver name: " + dm.getDriverName());
	                System.out.println("Driver version: " + dm.getDriverVersion());
	                System.out.println("Product name: " + dm.getDatabaseProductName());
	                System.out.println("Product version: " + dm.getDatabaseProductVersion());
	                conn.close();
	            }
	        } catch (ClassNotFoundException ex) {
	            ex.printStackTrace();
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	    }
	
 public List <Experiment> getcurrrent(String Location,String now) {
	 
	 try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 List <Experiment> expms= new ArrayList();
	 Connection conn =null;
	 try {
		  conn = DriverManager.getConnection(dbURL);
		
			if (conn != null) {
				PreparedStatement ps = null;
				//Statement statement = conn.createStatement();
				ps = conn.prepareStatement(SQL_SELECT_NOW_EXPERIMENTS);
				ps.setString(1, Location);
				ps.setString(2, now);
				ps.setString(3, now);
				ResultSet rs = ps.executeQuery();
				 while(rs.next()) {
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
				  ex.setPID(rs.getString("pid"));
				  ex.setProposal_Number(rs.getString("proposal_number"));
				  ex.setSupport(rs.getString("support"));
				  ex.setDTSTAMP(rs.getString("dtstamp"));
				  expms.add(ex);
				 }
				 ps.close();
			}
	 }
	 catch (Exception ex) {
		 
	 }
	 finally {
		 try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
		 return expms;
		 
	 }
	  
 
 public List <Experiment> getRange(String start,String end) {
	 
	 try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 List <Experiment> expms= new ArrayList();
	 Connection conn =null;
	 try {
		  conn = DriverManager.getConnection(dbURL);
		
			if (conn != null) {
				PreparedStatement ps = null;
				//Statement statement = conn.createStatement();
				ps = conn.prepareStatement(SQL_SELECT_EXPERIMENTS_RANGE);
				ps.setString(1, start);
				ps.setString(2, end);
				
				ResultSet rs = ps.executeQuery();
				 while(rs.next()) {
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
				  ex.setPID(rs.getString("pid"));
				  ex.setProposal_Number(rs.getString("proposal_number"));
				  ex.setSupport(rs.getString("support"));
				  ex.setDTSTAMP(rs.getString("dtstamp"));
				  expms.add(ex);
				 }
				 ps.close();
			}
	 }
	 catch (Exception ex) {
		 
	 }
	 finally {
		 try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
		 return expms;
		 
	 }
	  
 
 
public List <Experiment> getbyPid(String  pid) {
	 
	 try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 List <Experiment> expms= new ArrayList();
	 Connection conn =null;
	 try {
		  conn = DriverManager.getConnection(dbURL);
		
			if (conn != null) {
				PreparedStatement ps = null;
				//Statement statement = conn.createStatement();
				ps = conn.prepareStatement(SQL_SELECT_by_pid_EXPERIMENTS);
				ps.setString(1, pid);
				
				ResultSet rs = ps.executeQuery();
				 while(rs.next()) {
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
				  ex.setPID(rs.getString("pid"));
				  ex.setProposal_Number(rs.getString("proposal_number"));
				  ex.setSupport(rs.getString("support"));
				  ex.setDTSTAMP(rs.getString("dtstamp"));
				  expms.add(ex);
				 }
				 ps.close();
			}
	 }
	 catch (Exception ex) {
		 
	 }
	 finally {
		 try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
		 return expms;
		 
	 }
	 public List <Experiment> getALL() {
		 try {
				Class.forName("org.sqlite.JDBC");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 List <Experiment> expms= new ArrayList();
		 Connection conn =null;
		 try {
			  conn = DriverManager.getConnection(dbURL);
			
				if (conn != null) {
					PreparedStatement ps = null;
					//Statement statement = conn.createStatement();
					ps = conn.prepareStatement(SQL_SELECT_ALL_EXPERIMENTS);
					 ResultSet rs = ps.executeQuery();
					 while(rs.next()) {
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
					  ex.setPID(rs.getString("pid"));
					  ex.setProposal_Number(rs.getString("proposal_number"));
					  ex.setSupport(rs.getString("support"));
					  ex.setDTSTAMP(rs.getString("dtstamp"));
					  expms.add(ex);
					 }
					 ps.close();
				}
		 }
		 catch (Exception ex) {
			 
		 }
		 finally {
			 try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 return expms;
		 
	 }
	 public void insert_token(String ast,String name,String expire,String state,String proj_id,String exp_id,String wiki_id,String rtoken) {
		 //{"access_token":"AT-25-YtRAh12kWB3SxUuOxnBYpnuqiRQC5ZR1","token_type":"bearer","expires_in":28800,"scope":"osf.full_write"}
		 Connection conn =null;
		 String sql="select * osf_user_access_log where pid=? ";
		String isql= "insert into osf_user_access_log (pid,access_token,osf_name,dtgranted,expire_in,status,projnode,expnode,wikinode,refresh_token)"+
		"values(?,?,?,?,?"+
				",?,?,?,?,?)";
		
		 //Gson gson = new GsonBuilder().setPrettyPrinting().create();
		 //JsonElement jsonElement = new JsonParser().parse(json);
			
			//JsonObject root = jsonElement.getAsJsonObject();
			//String ast=root.get("access_token").getAsString();
			//String expire = root.get("expires_in").getAsString();
			int numRowsInserted = 0;
			
			PreparedStatement ps = null;
			PreparedStatement pcheck = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// Date now = new Date();
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String curdate = sdf.format(timestamp);
			try {
				 conn = DriverManager.getConnection(dbURL);
				
				//if (exp==0) { 
				ps = conn.prepareStatement(isql);
				ps.setString(1,state);
				ps.setString(2,ast);
				ps.setString(3,name);
				ps.setString(4, curdate);
				ps.setString(5, expire);
				ps.setString(6, "A");
			    ps.setString(7, proj_id);
			    ps.setString(8, exp_id);
			    ps.setString(9, wiki_id);
			    ps.setString(10, rtoken);
				numRowsInserted = ps.executeUpdate();
				System.out.print("inserted:" + numRowsInserted);
				//}
				
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					
					if (ps!=null) {
					ps.close();
					}
					if (conn!=null) {
					 conn.close();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	 public void update_token(String ast,String pid,String dt,String expire) {
		 
		 Connection conn =null;
		 //String sql="select * osf_user_access_log where pid=? ";
		String isql= "update osf_user_access_log  set access_token=?,dtgranted=?,expire_in=? where pid=? and dtgranted=?; ";
				
		
			int numRowsInserted = 0;
			
			PreparedStatement ps = null;
			//PreparedStatement pcheck = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// Date now = new Date();
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String curdate = sdf.format(timestamp);
			try {
				 conn = DriverManager.getConnection(dbURL);
				
				//if (exp==0) { 
				ps = conn.prepareStatement(isql);
				
				ps.setString(1,ast);
				
				ps.setString(2, curdate);
				ps.setString(3, expire);
				ps.setString(4,pid);
				ps.setString(5,dt);
				
				numRowsInserted = ps.executeUpdate();
				System.out.print("updated:" + numRowsInserted);
				//}
				
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					
					if (ps!=null) {
					ps.close();
					}
					if (conn!=null) {
					 conn.close();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	 
 public void update_token_status(String ast,String pid,String dt,String status) {
		 
		 Connection conn =null;
		 //String sql="select * osf_user_access_log where pid=? ";
		 String isql= "update osf_user_access_log  set status=?,dtgranted=? where pid=? and dtgranted=? and ast=?; ";
						
			int numRowsInserted = 0;
			
			PreparedStatement ps = null;
			//PreparedStatement pcheck = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// Date now = new Date();
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String curdate = sdf.format(timestamp);
			try {
				 conn = DriverManager.getConnection(dbURL);
				
				
				ps = conn.prepareStatement(isql);
				
				ps.setString(1,status);
				ps.setString(2, curdate);
				ps.setString(3,pid);
				ps.setString(4,dt);
				ps.setString(5,ast);
				
				numRowsInserted = ps.executeUpdate();
				System.out.print("updated:" + numRowsInserted);
			
				
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					
					if (ps!=null) {
					ps.close();
					}
					if (conn!=null) {
					 conn.close();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	 
	 
	 public void insert_auth(String pid, String name,String station) {
		 //{"access_token":"AT-25-YtRAh12kWB3SxUuOxnBYpnuqiRQC5ZR1","token_type":"bearer","expires_in":28800,"scope":"osf.full_write"}
		 Connection conn =null;
		 //String sql="select * osf_user_access_log where pid=? ";
		String isql= "insert into osf_user_access_log (pid,access_token,osf_name,dtgranted,expire_in,status,projnode,expnode,wikinode,refresh_token)"+
		"values(?,?,?,?,?"+
				",?,?,?,?)";
		
		 //Gson gson = new GsonBuilder().setPrettyPrinting().create();
		 //JsonElement jsonElement = new JsonParser().parse(json);
			
			//JsonObject root = jsonElement.getAsJsonObject();
			//String ast=root.get("access_token").getAsString();
			//String expire = root.get("expires_in").getAsString();
			int numRowsInserted = 0;
			
			PreparedStatement ps = null;
			PreparedStatement pcheck = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// Date now = new Date();
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String curdate = sdf.format(timestamp);
			try {
				 conn = DriverManager.getConnection(dbURL);
				
				//if (exp==0) { 
				ps = conn.prepareStatement(isql);
				ps.setString(1,pid);
			
				 ps.setNull(2, Types.NULL);
				ps.setString(3,name);
				ps.setString(4, curdate);
				 ps.setNull(5, Types.NULL);
				ps.setString(6, station);
			   
			    ps.setNull(7, Types.NULL);
			    ps.setNull(8, Types.NULL);
			    ps.setNull(9, Types.NULL);
			    ps.setNull(10, Types.NULL);
				numRowsInserted = ps.executeUpdate();
				System.out.print("inserted:" + numRowsInserted);
				//}
				
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					
					if (ps!=null) {
					ps.close();
					}
					if (conn!=null) {
					 conn.close();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	 
	public  SimpleEntry select_osftokeninfo(String pid,String type) {
		AbstractMap.SimpleEntry<String, String> entry = null ;
		                              //(pid,access_token,osf_name,dtgranted,expire_in,status,projnode,expnode,wikinode
		String mysql= "select access_token,osf_name,expnode,wikinode, max(dtgranted) as dtgranted,refresh_token from osf_user_access_log where pid=? ";
		 Connection conn =null;
		 SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd");
		 try {
			 conn = DriverManager.getConnection(dbURL);

				if (conn != null) {
					PreparedStatement ps = null;
					//Statement statement = conn.createStatement();
					ps = conn.prepareStatement(mysql);
					ps.setString(1, pid);
					 ResultSet rs = ps.executeQuery();
					 while(rs.next()) {
						 
						 String t=rs.getString("access_token").trim();
						 System.out.println(t);
						 String id=rs.getString("wikinode");
						 String ex=rs.getString("expnode");
						 String user=rs.getString("osf_name");
						 System.out.println(user);
						 String dt=rs.getString("dtgranted");
						 String rt=rs.getString("refresh_token").trim();
						  if (type.equals("wiki")) {
							  entry=new AbstractMap.SimpleEntry<>(t, id);
					      }
						  if (type.equals("exp")) {
							  entry=new AbstractMap.SimpleEntry<>(t, ex);
					      }
						  if (type.equals("refresh")) {
							  entry=new AbstractMap.SimpleEntry<>(rt, ex);
					      }
						  if (type.equals("user")) {
							  if (user.equals("authorizing"))
							  {  Date d = sqldf.parse(dt); 
							      Date now = new Date();
							      long duration  = now.getTime() - d.getTime();
							      long timeint = TimeUnit.MILLISECONDS.toMinutes(duration);
							      if (timeint>10) user=null;
							  }
							  entry=new AbstractMap.SimpleEntry<>(user, t);
					      }
					 }
					 ps.close();
				}
		 }
		 catch (Exception ex) {
			 
		 }
		 finally {
			 try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }	
		 return entry;
	}
	
	public  Map select_osfinfo(String pid) {
		//AbstractMap.SimpleEntry<String, String> entry = null ;
		Map m = new HashMap();
		                              //(pid,access_token,osf_name,dtgranted,expire_in,status,projnode,expnode,wikinode
		String mysql= "select access_token,osf_name,expnode,wikinode, max(dtgranted) as dtgranted,refresh_token,DATETIME(dtgranted,expire_in) as exptime  from osf_user_access_log where pid=? ";
		 Connection conn =null;
		 SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd");
		 try {
			 conn = DriverManager.getConnection(dbURL);

				if (conn != null) {
					PreparedStatement ps = null;
					//Statement statement = conn.createStatement();
					ps = conn.prepareStatement(mysql);
					ps.setString(1, pid);
					 ResultSet rs = ps.executeQuery();
					 while(rs.next()) {
						 
						 String t=rs.getString("access_token").trim();
						 System.out.println(t);
						 String id=rs.getString("wikinode");
						 String ex=rs.getString("expnode");
						 String user=rs.getString("osf_name");
						 System.out.println(user);
						 String dt=rs.getString("dtgranted");
						 String rt=rs.getString("refresh_token").trim();
						 String exptime= rs.getString("exptime");
						 m.put("access_token", t);
						 m.put("dtgranted", dt);
						 m.put("wikinode", id);
						 m.put("expnode", ex);
						 m.put("pid", pid);
						 m.put("refresh_token", rt);
						 m.put("exptime", exptime);
						
							  if (user.equals("authorizing"))
							  {  Date d = sqldf.parse(dt); 
							      Date now = new Date();
							      long duration  = now.getTime() - d.getTime();
							      long timeint = TimeUnit.MILLISECONDS.toMinutes(duration);
							      if (timeint>10) user=null;
							  }
							  
							  m.put("user", user);
							 
					      
					 }
					 ps.close();
				}
		 }
		 catch (Exception ex) {
			 
		 }
		 finally {
			 try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }	
		 return m;
	}
	
	
}
