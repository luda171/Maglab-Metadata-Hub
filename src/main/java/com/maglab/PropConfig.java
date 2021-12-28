package com.maglab;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

public class PropConfig {

	private final static String propertiesFileName = "config.properties";
	private static Map<String, String> all;
	private static PropConfig singleton = null;
	
	

	public PropConfig(Map all) {
		this.all = all;
		
	}

	public static PropConfig getInstance() {
		if (singleton == null) {
			Properties prop = new Properties();
			InputStream is = null;
			
			try {
				is = PropConfig.class.getClassLoader().getResourceAsStream(propertiesFileName);
							    				
				prop.load(is);

			    all = new HashMap<String, String>();
				Set<Entry<Object, Object>> entries = prop.entrySet();

				for (Entry<Object, Object> entry : entries) {
					String key = (String) entry.getKey();
					String value = (String) entry.getValue();
					System.out.println("key:"+key);
					System.out.println("value"+value);
					all.put(key, value);

				}
				
				
				
				
				 singleton = new PropConfig(all);
			} catch (Exception e) {
				e.printStackTrace();
				
			} finally {
				try {
					is.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		return singleton;
	}

	public Map<java.lang.String, java.lang.String> all() {
		return all;
	}

	

	private final String propertiesFileName() {
		return "config.properties";
	}

	
}
