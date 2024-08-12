package com.maglab;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.maglab.cal.CalUpdater;



@SpringBootApplication
public class Application {
	static ExecutorService bexec ;
    public static void main(String[] args) throws Exception {
    	 PropConfig.getInstance();
    	 bexec = Executors.newFixedThreadPool( 1);
		 bexec.execute(new CalUpdater());
		 System.out.println("init finished");
		 //mountResource("/images/${filename}", new SharedResourceReference(Application.class, "images/${filename}"));
         SpringApplication.run(Application.class, args);
         PropertyConfigurator.configure("log4j2.xml");
    }
   
   
   // public void addResourceHandlers(final ResourceHandlerRegistry registry) {
     //   registry.addResourceHandler("/images/**").addResourceLocations("file:./src/main/resources/images/");
    //}

}
