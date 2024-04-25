package com.maglab;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.wicket.request.resource.SharedResourceReference;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

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
    }
   
   
   // public void addResourceHandlers(final ResourceHandlerRegistry registry) {
     //   registry.addResourceHandler("/images/**").addResourceLocations("file:./src/main/resources/images/");
    //}

}
