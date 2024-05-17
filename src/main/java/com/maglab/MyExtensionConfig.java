package com.maglab;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.orienteer.wicketjersey.WicketJersey;

//import org.apache.wicket.settings.IResourceSettings;
import org.apache.wicket.settings.ResourceSettings;

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
			 //webApplication.mountResource("/images/${filename}", new SharedResourceReference(MyHomePage.class, "src/main/resources/images/${filename}"));
			
			 //webApplication.getSharedResources().add("images", new FolderContentResource(new File("/Users/ludab/Laptop/project2021/mag_public/magmetahub/src/main/resources/images/${filename}")));
			// webApplication.mountResource("images/${filename}", new SharedResourceReference("images"));
			 //configureResourceReferences(webApplication);
	  }
	
	 
	/* private void configureResourceReferences1(WebApplication webApplication) {
	        // Get the resource settings
	        IResourceSettings resourceSettings = webApplication.getResourceSettings();

	        // Get the location of the images folder
	        String imagesFolder = Application.get().getServletContext().getRealPath("/images/");

	        // Mount the images folder as a shared resource
	        resourceSettings.getResourceFinders().add(new AbstractPath(imagesFolder) {
	            @Override
	            public void getResource(HttpServletRequest request, HttpServletResponse response,
	                                    WebApplication webApplication, ServletWebRequest servletWebRequest) {
	                String path = servletWebRequest.getUrl().getPath();
	                super.getResource(path, response, webApplication, servletWebRequest);
	            }
	        });
	    }*/
	/* public void configureResourceReferences(WebApplication webApplication) {
		    // Get the resource settings
		    ResourceSettings resourceSettings = webApplication.getResourceSettings();

		    // Get the location of the images folder
		    String imagesFolder = WebApplication.get().getServletContext().getRealPath("/images/");
		    //String imagesFolder = "/Users/ludab/Laptop/project2021/mag_public/magmetahub/src/main/resources/images";
		    // Create a resource reference for the images folder
		    ResourceReference imagesReference = new ResourceReference("images") {
		        @Override
		        public IResource getResource() {
		            return new AbstractResource() {
		                @Override
		                protected ResourceResponse newResourceResponse(Attributes attributes) {
		                    ResourceResponse response = new ResourceResponse();
		                    response.setWriteCallback(new WriteCallback() {
		                        @Override
		                        public void writeData(Attributes attributes)  {
		                            // Get the servlet web request
		                            ServletWebRequest servletWebRequest = (ServletWebRequest) attributes.getRequest();
		                            // Get the path of the requested resource
		                            String path = servletWebRequest.getUrl().getPath();
		                            // Get the real path of the image file
		                            String realPath = imagesFolder + path;
		                            // Write the resource to the response
		                            try (InputStream inputStream = new FileInputStream(realPath)) {
		                                IOUtils.copy(inputStream, attributes.getResponse().getOutputStream());
		                            } catch (Exception e) {
		                                //throw new ResourceStreamNotFoundException("Error reading image file", e);
		                            }
		                        }
		                    });
		                    return response;
		                }
		            };
		        }
		    };
	 }
	 */
/*	 static class FolderContentResource implements IResource {
         private final File rootFolder;
         public FolderContentResource(File rootFolder) {
             this.rootFolder = rootFolder;
         }
         public void respond(Attributes attributes) {
             PageParameters parameters = attributes.getParameters();
             String fileName = parameters.get(0).toString();
             File file = new File(rootFolder, fileName);
             FileResourceStream fileResourceStream = new FileResourceStream(file);
             ResourceStreamResource resource = new ResourceStreamResource(fileResourceStream);
             resource.respond(attributes);
         }
     }*/
}
