package com.maglab.cal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import com.maglab.PropConfig;
import com.maglab.model.Experiment;


import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.util.Calendars;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.model.Property;

public class CalParser {
	HttpClient instance;
    static PropConfig pconf = PropConfig.getInstance();
    static  String sproxy =  pconf.all().get("cal.proxy");
	boolean proxy = Boolean.parseBoolean(sproxy);  ;
	static public String proxyhost =pconf.all().get("proxyhost");
	
	static public String facilityfilter =  pconf.all().get("cal.facility");
	
			
	String INSERT_SQL = "insert or replace into experiments" + " (experimentID, PI,location,facility,magnetsystem,"
			+ " experiment_title,proposal_title,proposal_number,dtstart,dtend, "
			+ " dtstamp, summary, support ," + " dtupdate,pid,calID) " + "values(?,?,?,?,?," + "?,?,?,?,?"
			+ ",?,?,?" + ",?,?,?)";
	String delete_SQL="delete from experiments where dtupdate < DATETIME('now', '-10 hour') and localupdate is NULL and dtstart>  DATETIME('now', '-10 day') ";
	private static final String CHECK_EXPERIMENT = "SELECT count(*) FROM experiments where calID=? and localupdate IS NOT NULL  ;";
	SimpleDateFormat dfs = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd");
	//20210316T230439Z
	SimpleDateFormat dfstamp = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
	{
		createHTTP();
	}
	void load_calendar() {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String dbURL = "jdbc:sqlite:pulsefacility.db";
		
		
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(dbURL);
			if (conn != null) {
				Statement statement = conn.createStatement();
				statement.setQueryTimeout(130); // sec

				URL url;
				//Properties props = System.getProperties();
				//props.setProperty("net.fortuna.ical4j.timezone.update.proxy.enabled", "true");
				//props.setProperty("net.fortuna.ical4j.timezone.update.proxy.host", "proxy1.lanl.gov");
				//props.setProperty("net.fortuna.ical4j.timezone.update.proxy.port", "8080");
				
				
				CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
				CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);
				CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
				try {
					//url = new URL("https://users.magnet.fsu.edu/Experiments/Calendar.aspx");
					String u="https://users.magnet.fsu.edu/Experiments/Calendar.aspx";
					GetMethod method = new GetMethod(u);
					method.setFollowRedirects(true);
					int statusCode = instance.executeMethod(method);
					System.out.println("statusCode"+statusCode);
					String body = method.getResponseBodyAsString();
					File targetFile = new File("targetFile.tmp");
					FileWriter fileWriter = new FileWriter(targetFile);
					PrintWriter printWriter = new PrintWriter(fileWriter);
					printWriter.print(body);
					printWriter.close();
				
				   
					//Calendar icsCalendar = new Calendar();
						//Calendar calendar = Calendars.load(url);
						Calendar calendar = Calendars.load("targetFile.tmp");
					for (final Object o : calendar.getComponents()) {
						Component component = (Component) o;

						//System.out.println("Component: " + component.getName());
						Experiment ex = new Experiment();
						for (final Object o1 : component.getProperties()) {
							Property property = (Property) o1;
							String name = property.getName();
							//System.out.println("name:"+name);

							String value = property.getValue();
							//System.out.println("value:"+value);
							
							if (name.equals("DESCRIPTION")) {
								StringTokenizer st= new StringTokenizer(value, "\n"); 
								while (st.hasMoreElements()) {
								String token=st.nextToken();
								//System.out.println("token:"+token);
								          if (token.contains("Experiment Title")) {
									
									       String tok= token.substring(token.indexOf(":") + 1);
									      // System.out.println("title"+tok);
									       ex.setTitle(tok.trim());
								           }
								          if (token.contains("Magnet System")) {
									
									       String tok= token.substring(token.indexOf(":") + 1);
									       ex.setMagnet_System(tok.trim());
								          }
								
								          if (token.contains("Proposal Number")) {
									        String tok = token.substring(token.indexOf(":") + 1);
									        ex.setProposal_Number(tok.trim());
								          }
								         if (token.contains("Proposal Title")) {
									       String tok = token.substring(token.indexOf(":") + 1);
									       ex.setProposal_Title(tok.trim());
								          }
                                        if (token.contains("Facility")) {
                            	   
                            	           String tok = token.substring(token.indexOf(":") + 1);
                            	           ex.setFacility(tok.trim());
								
								         }
                                         if (token.contains("PI")) {
                            	   
                            	          String tok= token.substring(token.indexOf(":") + 1);
                            	          ex.setPi(tok.trim());
								
								           }
                             //  if (token.startsWith("http")) {
                                
                               // String ID = token.substring(token.indexOf("=") + 1);
   							//	int inum = Integer.parseInt(ID);
   							//	ex.setId(inum);
							//	}
								
							             }//while
							}
							
							if (name.equals("DTSTART")) {
								try {
									java.util.Date d = dfs.parse(value);
									String sqldate =sqldf.format(d);
									ex.setDTSTART(sqldate);
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								 
								
							}
							if (name.equals("DTEND")) {
								try {
									java.util.Date d = dfs.parse(value);
									String sqldate =sqldf.format(d);
									ex.setDTEND(sqldate);
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								 
								
							}
							if (name.equals("DTSTAMP")) {
								try {
								java.util.Date d = dfstamp.parse(value);
								String sqldate =sqldf.format(d);
								ex.setDTSTAMP(sqldate);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
								
							}
							if (name.equals("LOCATION")) {
								value=value.replace(" ", "_");
								ex.setLOCATION(value.trim());
							}
							//if (value.contains("Facility")) {
							//	ex.setLOCATION(value);
							//}
							if (name.equals("SUMMARY")) {
								
								ex.setSummary(value);
								StringTokenizer st= new StringTokenizer(value, ";"); 
								String id = st.nextToken();
								if (id!=null) {
									ex.setPID(id.trim());
								}
								while (st.hasMoreElements()) {
								String token=st.nextToken();
								
								//System.out.println("token:"+token);
								 if (token.contains("Support")) {
										
								       String tok= token.substring(token.indexOf(":") + 1);
								       System.out.println("support"+tok);
								       ex.setSupport(tok.trim());
								 }     }
								
							}
							if (name.equals("URL")) {
								String ID = value.substring(value.indexOf("=") + 1);
								int inum = Integer.parseInt(ID);
								ex.setId(inum);
							}

							//if (name.equals("UID")) {
							//	ex.setUID(value);
							//}

							//if (name.equals("ATTENDEE")) {
							//	ex.addAttendee(value);
							//}
							// System.out.println(
							// property.getName() + ": " + property.getValue());

							// if

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
						}
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						//if ((ex.getFacility().trim()).equals("Pulsed Field")) {
						if (facilityfilter.equals("all")) {
							insert(ex, conn);
						}
						else {
							if ((ex.getFacility().trim()).equals(facilityfilter)) {
							
						insert(ex, conn);
						}
						}
							delete(conn);
						System.out.println("finished loading calendar for: " +facilityfilter);
						System.out.println("proxy"+proxy);
						// statement.close();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
public void delete(Connection conn) {
	PreparedStatement ps = null;
	int numRowsInserted = 0;
	try {
		ps = conn.prepareStatement(delete_SQL);
		numRowsInserted = ps.executeUpdate();
		System.out.print("deleted:" + numRowsInserted);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	finally {
		
			
			if (ps!=null) {
			try {
				ps.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			
		} 
	
}
	public void insert(Experiment ex, Connection conn) {
		int numRowsInserted = 0;
		PreparedStatement pss = null;
		PreparedStatement ps = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// Date now = new Date();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String curdate = sdf.format(timestamp);
		try {
			
			pss = conn.prepareStatement(CHECK_EXPERIMENT);
			pss.setString(1,  ex.getDTSTART()+";"+ex.getPID());
			//pss.setString(2, ex.getDTSTART());
			ResultSet res = pss.executeQuery();
			int exp = 0;
			 while(res.next()) {
				 exp=res.getInt(1);
			 }
			if (exp==0) { 
			ps = conn.prepareStatement(INSERT_SQL);
			ps.setInt(1, ex.getId());
			ps.setString(2, ex.getPi());
			ps.setString(3, ex.getLOCATION());
			ps.setString(4, ex.getFacility());
			ps.setString(5, ex.getMagnet_System());
			ps.setString(6, ex.getTitle());
			ps.setString(7, ex.getProposal_Title());
			ps.setString(8, ex.getProposal_Number());
			ps.setString(9, ex.getDTSTART());
			ps.setString(10, ex.getDTEND());
			ps.setString(11, ex.getDTSTAMP());
			ps.setString(12, ex.getSummary());
			ps.setString(13, ex.getSupport());
			//ps.setString(14, "");
			//ps.setString(15, ex.getUID());
			ps.setString(14, curdate);
			ps.setString(15,ex.getPID());
			ps.setString(16,ex.getDTSTART()+";"+ex.getPID());
			numRowsInserted = ps.executeUpdate();
			System.out.print("inserted:" + numRowsInserted);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pss!=null) {
					pss.close();
					}
				if (ps!=null) {
				ps.close();
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	public void createHTTP() {
		Integer socktimeout = 15000;
		Integer sockconntimeout = 15000;
		HttpClientTest httptest = new HttpClientTest(socktimeout, sockconntimeout);
		System.out.println("proxy"+proxy);
		if (proxy) {

			instance = httptest.initclient(proxyhost, 8080);
		} else {
			instance = httptest.initclient(null, 0);
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		 * URL url;
		 * 
		 * 
		 * CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING,
		 * true);
		 * CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION,
		 * true);
		 * CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING,
		 * true); try { url = new
		 * URL("https://users.magnet.fsu.edu/Experiments/Calendar.aspx"); Calendar
		 * calendar = Calendars.load(url); for (final Object o :
		 * calendar.getComponents()) { Component component = (Component)o;
		 * 
		 * System.out.println("Component: " + component.getName());
		 * 
		 * for (final Object o1 : component.getProperties()) { Property property =
		 * (Property)o1; System.out.println( property.getName() + ": " +
		 * property.getValue());
		 * 
		 * } } } catch ( IOException | ParserException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); }
		 */
		
		CalParser cp = new CalParser();
		//cp.createHTTP();
		cp.load_calendar();

		// Reader r = new InputStreamReader(url.openStream(), "ISO-8859-15");
		// CalendarBuilder builder = new CalendarBuilder();
		// Calendar calendar = builder.build(r);

	}

}
