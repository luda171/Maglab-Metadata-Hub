package com.maglab.instruments;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.lang.Bytes;

import com.maglab.PropConfig;
import com.maglab.instruments.InstrumentEditPage.FileListView;
import com.maglab.panel.ProbHomePage;


public class InstrumentEditForm extends Form<InstrumentEditForm> {
	private FormComponent title;
	private FormComponent admin;
	private FormComponent out;
	//private FormComponent instype;
    //private FormComponent ret;
    static PropConfig pconf = PropConfig.getInstance();
    static  String dir =  pconf.all().get("instrumentdir");
    String existedpid;
    /** Reference to listview for easy access. */
    //private final FileListView fileListView;
    FileUploadField fileUploadField;

    @SpringBean
	private  InstrumentService serv;
    private final boolean isCreate;
    
    
    final List<String> instType = Arrays.asList(new String[] { "prob", "magnet" });
    final List<String> outserv = Arrays.asList(new String[] { "F", "T" });
    private String selected = "prob";
    private String selected2 = "F";
    PropertyModel<String> sm ;
    PropertyModel<String> sm2 ;
    CheckBox checkbox;
    //function to add instruments
    public InstrumentEditForm(String id) {
    	super(id);
    	isCreate = true;
    	System.out.println("in create");
    	setMultiPart(true);
    	//setModel(new CompoundPropertyModel<>(this));
    	
        add(fileUploadField = new FileUploadField("fileInput"));
      
        add(admin = new RequiredTextField<String>("admin",Model.of()));
    	add(title=new RequiredTextField<String>("title",Model.of()));
    	//add(instype = new  DropDownChoice<String>("instType",Model.of()));
    	sm = new PropertyModel<String>(this, "selected");
    	sm2 = new PropertyModel<String>(this, "selected2");
    	 DropDownChoice<String> instTypes = 
         		new DropDownChoice<String>("instType", sm, instType);
        add(instTypes);
        //String radioGroupChoice = "delete";
        //RadioGroup group = new RadioGroup("group", new PropertyModel(this, "radioGroupChoice"));
        //group.setEnabled(false);
       // add(group);
        
        checkbox = new CheckBox("check",Model.of(Boolean.FALSE));
        
        checkbox.setEnabled(false);
        add(checkbox);
        //DropDownChoice<String> outTypes = 
         		//new DropDownChoice<String>("out", sm2, outserv);
        //add(outTypes);
        
      // title = new TextField<>(
        //       "title",
          //     "")
       //);
      
      // out = new TextField<>(
        //       "out_of_service",
          //     new PropertyModel<>(old, "outOfService")
      // );
      
       // add(title);
       // add(out);
       
        
        BookmarkablePageLink pl = new BookmarkablePageLink("cancelLink", ProbHomePage.class);
		add(pl);
		
    }

    //function to update the instruments
    public InstrumentEditForm(String id,String pid) {
        super(id);
       
        System.out.println(id);
        isCreate = false;
        
        this.existedpid=pid;
        setMultiPart(true);
        List<Instrument> oldex = serv.getInstrumentsByPID(pid);
        System.out.println("prob find:"+oldex.size());
       
        Instrument old = oldex.get(0);
        System.out.println("instr"+old.getInstrumenType());
       
     // Add one file input field
        add(fileUploadField = new FileUploadField("fileInput"));
       
      
       title = new TextField<>(
               "title",
               new PropertyModel<>(old, "title")
       );
      
      // out = new TextField<>(
        //       "out_of_service",
          //     new PropertyModel<>(old, "outOfService")
      // );
      
        add(title);
        //add(out);
       // setModel(new CompoundPropertyModel<>(this));
    
        add(admin = new RequiredTextField<String>("admin",Model.of()));
        sm = new PropertyModel<String>(this, "selected");
        sm2 = new PropertyModel<String>(old, "outOfService");
        //sm2 = new PropertyModel<String>(this, "selected");
        DropDownChoice<String> instTypes = 
        		new DropDownChoice<String>("instType", sm, instType);
        add(instTypes);
        
       // String radioGroupChoice = "delete";
        //RadioGroup group = new RadioGroup("group", new PropertyModel(this, "radioGroupChoice"));
        //group.setEnabled(false);
        //add(group);
       // CheckBox checkbox = new CheckBox("check",
        //	      new PropertyModel<Boolean>(configEntry, "designatorRange.step"));

        checkbox = new CheckBox("check",Model.of(Boolean.FALSE));
        //checkbox.setEnabled(false);
        add(checkbox);
        
        //DropDownChoice<String> outTypes = 
        // 		new DropDownChoice<String>("out", sm2, outserv);
        //add(outTypes);
       
        
        BookmarkablePageLink pl = new BookmarkablePageLink("cancelLink", ProbHomePage.class);
		add(pl);
		
    }

    private void checkFileExists_(File newFile)
    {
        if (newFile.exists())
        {
        	
            // Try to delete the file
            if (!Files.remove(newFile))
            {
                throw new IllegalStateException("Unable to overwrite " + newFile.getAbsolutePath());
            }
        }
    }
	
    private void checkFileExists(File newFile) {
        if (newFile.exists()) {
            // File with the same name exists
            
            // Get current date in "yyyyMMdd" format
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            String currentDate = dateFormat.format(new Date());

            // Create a new file name by appending current date to the original file name
            String fileNameWithDate = newFile.getName() + "_" + currentDate;

            // Get the parent directory of the file
            File parentDirectory = newFile.getParentFile();

            // Create a new File object with the modified file name
            File newFileName = new File(parentDirectory, fileNameWithDate);

            // Move the existing file to the new file name
            try {
            	java.nio.file.Files.move(newFile.toPath(), newFileName.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new IllegalStateException("Unable to move file: " + e.getMessage());
            }
        }
    }
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void setPermission(File file) throws IOException{
	    Set<PosixFilePermission> perms = new HashSet<>();
	    perms.add(PosixFilePermission.OWNER_READ);
	    perms.add(PosixFilePermission.OWNER_WRITE);
	    perms.add(PosixFilePermission.OWNER_EXECUTE);

	    perms.add(PosixFilePermission.OTHERS_READ);
	    perms.add(PosixFilePermission.OTHERS_WRITE);
	    perms.add(PosixFilePermission.OTHERS_EXECUTE);

	    perms.add(PosixFilePermission.GROUP_READ);
	    perms.add(PosixFilePermission.GROUP_WRITE);
	    perms.add(PosixFilePermission.GROUP_EXECUTE);

	   // Files.setPosixFilePermissions(file.toPath(), perms);
	}
	@Override
    protected void onSubmit()
    {    
		String directoryPath = dir; // Provide the directory path here
        String newFolderPrefix = "INSTR";
        String uploaddir="";
        String delstatus="F";
        System.out.println("smcheck:"+checkbox.getModelObject().toString());
        System.out.println("sm"+sm.getObject().toString());
        boolean isChecked = checkbox.getModelObject();
        if(isChecked) {
        	delstatus="T";
        }
       // System.out.println("sm2"+sm2.getObject().toString());
        String newFolderName = generateNewFolderName(directoryPath, newFolderPrefix);
        if (isCreate) {
        createFolder(directoryPath, newFolderName);
        uploaddir =  directoryPath+File.separator+ newFolderName;
        }
        else {
        	uploaddir =	dir+File.separator+existedpid;
        }
        System.out.println(uploaddir);
        final List<FileUpload> uploads = fileUploadField.getFileUploads();
       
        String filename="";
        
        if (uploads != null)
        {
            for (FileUpload upload : uploads)
            {
                // Create a new file
            	  String ofilename = upload.getClientFileName();
                 // Replace spaces with underscores or any other character
                    filename = ofilename.replaceAll(" ", "_");
                    
                    Set<PosixFilePermission> permissions
                    = PosixFilePermissions.fromString("rwxrwxrwx");
            FileAttribute<Set<PosixFilePermission>> fileAttributes
                    = PosixFilePermissions.asFileAttribute(permissions);    
               
                File newFile = new File(uploaddir, filename);
                newFile.setReadable(true,false);
                //Files.setPosixFilePermissions(newFile.toPath(), permissions);
                //Files.setPosixFilePermissions(newFile.toPath(), permissions);
                // Check new file, delete if it already existed
                checkFileExists(newFile);
                try
                {
                    // Save to new file
                    newFile.createNewFile();
                    upload.writeTo(newFile);
                    newFile.setReadable(true,false);
                   // newFile.setReadable(true);
                    
                    
                   // String fn=newFile.getAbsolutePath();
                    //Path path = Paths.get(fn);
                  
                    //file.setExecutable(true, false);
                    //file.setWritable(true, false);
                    InstrumentEditForm .this.info("saved file: " + filename);
                }
                catch (Exception e)
                {
                    throw new IllegalStateException("Unable to write file", e);
                }
            }
        }
        
        if   (!isCreate) {
            serv.updateInstrument(existedpid,filename,  title.getInput(), delstatus, sm.getObject().toString());
            } else {
            	//insertInstrument(String pid, String filename, String title, String fpath,String out,String type)
            serv.insertInstrument(newFolderName, filename,  title.getInput(), directoryPath,"F", sm.getObject().toString());
            }
        setResponsePage(ProbHomePage.class); 
    }


	
	public static String generateNewFolderName(String directoryPath, String prefix) {
        int maxNumber = 0;

        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && file.getName().startsWith(prefix)) {
                    String folderName = file.getName();
                    try {
                        int number = Integer.parseInt(folderName.substring(prefix.length()));
                        maxNumber = Math.max(maxNumber, number);
                    } catch (NumberFormatException e) {
                        // Folder name doesn't follow expected format, ignore it
                    }
                }
            }
        }

        return prefix + (maxNumber + 1);
    }

	
	public static void createFolder(String directoryPath, String folderName) {
	    String folderPath = directoryPath + File.separator + folderName;

	   // File newFolder = new File(folderPath);

	    // Set folder permissions
	    if (System.getProperty("os.name").startsWith("Windows")) {
	    	 System.out.println("Windows:");
	    	File newFolder = new File(folderPath);
	        // For Windows
	        if (newFolder.mkdir()) {	        	
	        	newFolder.setReadable(true,false);
	            System.out.println("Folder created successfully: " + newFolder.getAbsolutePath());
	        } else {
	            System.out.println("Failed to create folder: " + newFolder.getAbsolutePath());
	        }
	    } else {
	        // For Unix-like systems
	    	 System.out.println("Linux");
	        try {
	        	 // Create the folder
	        	java.nio.file.Files.createDirectory(Paths.get(folderPath));
	        	 // Set permissions to read, write, execute for owner, group, and others
	            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
	            java.nio.file.Files.setPosixFilePermissions(Paths.get(folderPath), permissions);

	            //Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
	            //java.nio.file.Files.createDirectory(Paths.get(folderPath), PosixFilePermissions.asFileAttribute(permissions));
	            System.out.println("Folder created successfully: " + folderPath);
	        } catch (IOException e) {
	            System.out.println("Failed to create folder: " + folderPath);
	            e.printStackTrace();
	        }
	    }
	}
	
    public static void createFolder2(String directoryPath, String folderName) {
    	Set<PosixFilePermission> permissions
        = PosixFilePermissions.fromString("rwxrwxrwx");
        FileAttribute<Set<PosixFilePermission>> fileAttributes
        = PosixFilePermissions.asFileAttribute(permissions); 
        String uploaddir =  directoryPath+File.separator+ folderName;
        //Path newDirectoryPath = Paths.get(uploaddir);
       // Files.createDirectories(newDirectoryPath, fileAttributes);        
        File newFolder = new File(directoryPath, folderName);       
        newFolder.setReadable(true,false);
        try {
			java.nio.file.Files.createDirectory(Paths.get("uploaddir"), 
				     PosixFilePermissions.asFileAttribute(      
				         PosixFilePermissions.fromString("rwxrwxrwx")
				      ));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
       // if (newFolder.mkdir()) {
        //	 newFolder.setReadable(true,false);
          //  System.out.println("Folder created successfully: " + newFolder.getAbsolutePath());
        //} else {
          //  System.out.println("Failed to create folder: " + newFolder.getAbsolutePath());
        //}
    }   

	
}
