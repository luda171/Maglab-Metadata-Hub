package com.maglab;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.protocol.http.WebApplication;
import org.orienteer.wicketjersey.WicketJersey;

import com.giffing.wicket.spring.boot.context.extensions.ApplicationInitExtension;
import com.giffing.wicket.spring.boot.context.extensions.WicketApplicationInitConfiguration;

import com.maglab.cal.CalUpdater;
import com.maglab.panel.HomePanel;
import com.maglab.panel.MyHomePage;
import com.maglab.rest.LocationResource;

@ApplicationInitExtension
public class MyExtensionConfig implements WicketApplicationInitConfiguration{

	 @Override
	  public void init(WebApplication webApplication) {
		     System.out.println("go there");
		    
			 WicketJersey.mount("/rest", LocationResource.class.getPackage().getName());
			 
			// webApplication.getMapperContext()
			 
			 //webApplication.mountPage("result",BannerPanel.class);
	      //...config stuff
			 //mountPage("/shop/${id}", ShopDetailPage.class);
			 
			 webApplication.getRootRequestMapperAsCompound().add(new MountedMapper("/cal", MyHomePage.class));
			// webApplication.getRootRequestMapperAsCompound().add(new MountedMapper("/banner", BannerHomePage.class));
				
			
	  }
	
}
