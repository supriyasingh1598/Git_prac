// 
// Decompiled by Procyon v0.5.36
// 

package com.ocis.saviynt.integration;

import org.json.JSONArray;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.json.CDL;
import java.io.File;
import org.json.JSONObject;

public class ControlResponseProcessor
{
    public Boolean controlDetailsJSONtoCSVConverter(final JSONObject getRequestResponse, final String CSVFileLocation) {
        System.out.println("Enter controlDetailsJSONtoCSVConverter");
        try {
            final File file = new File(CSVFileLocation);
            String csvString = null;
            final JSONArray controlDetails = getRequestResponse.getJSONArray("controlDetails");
            System.out.println("controlDetails : " + controlDetails);
            csvString = CDL.toString(controlDetails);
            try {
                FileUtils.writeStringToFile(file, csvString);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Data has been Sucessfully Written to " + file);
            System.out.println(csvString);
            System.out.println("Exiting controlDetailsJSONtoCSVConverter");
            System.out.println("Returning CSVFileLocation : " + CSVFileLocation);
            return true;
        }
        catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }
}
