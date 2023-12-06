// 
// Decompiled by Procyon v0.5.36
// 

package com.ocis.saviynt.integration;

import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.util.Properties;

public class ReadExternalConfig
{
    public PropertiesPojo readSaviyntExternalConfigProperty() throws FileNotFoundException {
        System.out.println("ReadConfig: Reading config file...");
        InputStream inputStream = null;
        final PropertiesPojo configParams = new PropertiesPojo();
        final AESCrypt aes = new AESCrypt();
        try {
            final String propFileName = "/application_instance/custom_code/saviynt/Properties/externalconfig.properties";
            System.out.println("Config.properties found at :" + propFileName);
            final Properties prop = new Properties();
            System.out.println("LOADING PROPERTIES FILE");
            inputStream = new FileInputStream(propFileName);
            prop.load(inputStream);
            configParams.setSavUrl(prop.getProperty("SSM_URL"));
            configParams.setSavLogin(prop.getProperty("SSM_LOGIN"));
            configParams.setSavName(prop.getProperty("SSM_USERNAME"));
            String savMsg = null;
            final String savMsgEnc = prop.getProperty("SSM_PASSWORD");
            if (savMsgEnc != null && savMsgEnc.trim().length() > 0) {
                savMsg = aes.decryptIt(savMsgEnc.trim());
            }
            configParams.setSavMsg(savMsg);
            configParams.setSavFetchControlDetails(prop.getProperty("SSM_FETCH_CONTTROL_DETAILS"));
            configParams.setSavLimit(prop.getProperty("max"));
            configParams.setIsHttps(prop.getProperty("isHttps"));
            configParams.setcsvFileLocation(prop.getProperty("CSV_FILE_LOCATION"));
            return configParams;
        }
        catch (FileNotFoundException fe) {
            System.out.println("  ReadConfig: Error - " + fe.toString());
            throw fe;
        }
        catch (Exception e) {
            System.out.println("  ReadConfig: Error - " + e.toString());
            return configParams;
        }
        finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            catch (Exception ex) {
                System.out.println("  ReadConfig: " + ex.toString());
            }
        }
    }
}
