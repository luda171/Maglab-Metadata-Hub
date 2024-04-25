package com.maglab.instruments;


public class InstrumentCategory {
    private String instrumentPid;
    private String category;

    // Constructor
    public InstrumentCategory(String probePid, String category) {
        this.instrumentPid = probePid;
        this.category = category;
    }

    // Getters and Setters
    public String getProbePid() {
        return instrumentPid;
    }

    public void setProbePid(String probePid) {
        this.instrumentPid = probePid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
