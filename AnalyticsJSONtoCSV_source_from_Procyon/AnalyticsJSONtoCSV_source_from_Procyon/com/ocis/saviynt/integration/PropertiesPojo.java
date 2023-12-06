// 
// Decompiled by Procyon v0.5.36
// 

package com.ocis.saviynt.integration;

public class PropertiesPojo
{
    private String savUrl;
    private String savLogin;
    private String savLimit;
    private String savControlID;
    private String savName;
    private String savMsg;
    private String savFetchControlDetails;
    private String isHttps;
    private String csvFileLocation;
    
    public PropertiesPojo() {
        this.savUrl = null;
        this.savLogin = null;
        this.savLimit = null;
        this.savControlID = null;
        this.savName = null;
        this.savMsg = null;
        this.savFetchControlDetails = null;
        this.isHttps = null;
        this.csvFileLocation = null;
    }
    
    public String getSavLimit() {
        return this.savLimit;
    }
    
    public void setSavLimit(final String savLimit) {
        this.savLimit = savLimit;
    }
    
    public String getSavControlID() {
        return this.savControlID;
    }
    
    public void setSavControlID(final String savControlID) {
        this.savControlID = savControlID;
    }
    
    public String getSavUrl() {
        return this.savUrl;
    }
    
    public void setSavUrl(final String savUrl) {
        this.savUrl = savUrl;
    }
    
    public String getSavLogin() {
        return this.savLogin;
    }
    
    public void setSavLogin(final String savLogin) {
        this.savLogin = savLogin;
    }
    
    public String getSavName() {
        return this.savName;
    }
    
    public void setSavName(final String savName) {
        this.savName = savName;
    }
    
    public String getSavMsg() {
        return this.savMsg;
    }
    
    public void setSavMsg(final String savMsg) {
        this.savMsg = savMsg;
    }
    
    public String getSavFetchControlDetails() {
        return this.savFetchControlDetails;
    }
    
    public void setSavFetchControlDetails(final String savFetchControlDetails) {
        this.savFetchControlDetails = savFetchControlDetails;
    }
    
    public String getcsvFileLocation() {
        return this.csvFileLocation;
    }
    
    public void setcsvFileLocation(final String csvFileLocation) {
        this.csvFileLocation = csvFileLocation;
    }
    
    public String getIsHttps() {
        return this.isHttps;
    }
    
    public void setIsHttps(final String isHttps) {
        this.isHttps = isHttps;
    }
}
