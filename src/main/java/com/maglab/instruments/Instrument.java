package com.maglab.instruments;

import java.io.Serializable;
import java.util.List;

public class Instrument implements Serializable {
	private static final long serialVersionUID = 1L;
    private String instrumentPid;
    private String title;
    private String filename;
    private String filestorePath;
    //private Integer version;
    private String createDate;
    private String modifyDate;
    private String instrumentType;
    private String outOfService;
    //private String admin;
    //private List<InstrumentCategory> categories;
    private String Categories="";
    // Constructor
    public Instrument(String probePid, String title, String filename, String filestorePath,  String createDate, String modifyDate, String archive, String  instrumentType) {
        this.instrumentPid = probePid;
        this.title = title;
        this.filename = filename;
        this.filestorePath = filestorePath;
        //this.version = version;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
        this.instrumentType = instrumentType;
        this.outOfService = archive;
        //this.categories = categories;
    }

    // Getters and Setters
    public String getInstrumentPid() {
        return instrumentPid;
    }

    public void setInstrumentPid(String probePid) {
        this.instrumentPid = probePid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilestorePath() {
        return filestorePath;
    }

    public void setFilestorePath(String filestorePath) {
        this.filestorePath = filestorePath;
    }
    public String getInstrumenType() {
        return instrumentType;
    }

    public void setInstrumentType(String instrumentType) {
        this.instrumentType = instrumentType;
    }
    //public Integer getVersion() {
     //   return version;
   // }

   // public void setVersion(Integer version) {
    //    this.version = version;
    //}

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }

   //public String getAdmin() {
    //    return admin;
   // }

   // public void setAdmin(String admin) {
    //    this.admin = admin;
   // }

   // public List<InstrumentCategory> getCategories() {
     //   return categories;
    //}

    //public void setCategories(List<InstrumentCategory> categories) {
      //  this.categories = categories;
    //}
}

