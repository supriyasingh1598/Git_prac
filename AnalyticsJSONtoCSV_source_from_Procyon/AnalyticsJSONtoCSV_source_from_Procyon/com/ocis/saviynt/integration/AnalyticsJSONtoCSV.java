// 
// Decompiled by Procyon v0.5.36
// 

package com.ocis.saviynt.integration;

import java.util.Map;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpEntity;
import java.util.List;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import java.util.ArrayList;
import org.apache.http.client.methods.HttpPost;
import java.io.FileNotFoundException;

public class AnalyticsJSONtoCSV
{
    public static void main(final String[] args) {
        try {
            System.out.println("Printing command line input");
            for (int i = 0; i < args.length; ++i) {
                System.out.println(String.format("Command Line Argument %d is %s", i, args[i]));
            }
            String csvFileName = null;
            final String controlId = args[0];
            csvFileName = args[1];
            System.out.println("controlId is : " + controlId);
            System.out.println("csvFileName is : " + csvFileName);
            PropertiesPojo configParams = new PropertiesPojo();
            final ReadExternalConfig readConfig = new ReadExternalConfig();
            System.out.println("calling readSaviyntExternalConfigProperty from JSONtoCSVMain");
            try {
                configParams = readConfig.readSaviyntExternalConfigProperty();
            }
            catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            System.out.println("loaded configParams : " + configParams);
            final String loginurl = String.valueOf(configParams.getSavUrl().trim()) + configParams.getSavLogin().trim();
            final String username = configParams.getSavName();
            final String decodedPassword = configParams.getSavMsg();
            final String bearerToken = new AccessToken().getBearerToken(loginurl, username, decodedPassword);
            final String ssmFetchDetailsURL = String.valueOf(configParams.getSavUrl()) + configParams.getSavFetchControlDetails();
            System.out.println(ssmFetchDetailsURL);
            final HttpPost post = new HttpPost(ssmFetchDetailsURL);
            post.setHeader("Authorization", "Bearer " + bearerToken);
            final List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add((NameValuePair)new BasicNameValuePair("controlId", controlId));
            urlParameters.add((NameValuePair)new BasicNameValuePair("max", configParams.getSavLimit()));
            System.out.println("urlParameters " + urlParameters.toString());
            try {
                post.setEntity((HttpEntity)new UrlEncodedFormEntity((List)urlParameters));
            }
            catch (UnsupportedEncodingException e2) {
                System.out.println("in UnsupportedEncodingException : " + e2.getMessage());
            }
            System.out.println("post is made : " + post.toString());
            JSONObject getRequestResponse = new JSONObject();
            try {
                Throwable t = null;
                try {
                    final CloseableHttpClient httpClient = HttpClients.createDefault();
                    try {
                        final CloseableHttpResponse response = httpClient.execute((HttpUriRequest)post);
                        try {
                            System.out.println("getting response ");
                            final String resonseBody = EntityUtils.toString(response.getEntity());
                            System.out.println("responseBody : " + resonseBody);
                            getRequestResponse = new JSONObject(resonseBody);
                            System.out.println("jsonObj getRequestResponse : " + getRequestResponse);
                        }
                        finally {
                            if (response != null) {
                                response.close();
                            }
                        }
                        if (httpClient != null) {
                            httpClient.close();
                        }
                    }
                    finally {
                        if (t == null) {
                            final Throwable exception;
                            t = exception;
                        }
                        else {
                            final Throwable exception;
                            if (t != exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        if (httpClient != null) {
                            httpClient.close();
                        }
                    }
                }
                finally {
                    if (t == null) {
                        final Throwable exception2;
                        t = exception2;
                    }
                    else {
                        final Throwable exception2;
                        if (t != exception2) {
                            t.addSuppressed(exception2);
                        }
                    }
                }
            }
            catch (Exception e3) {
                System.out.println("Exception in catch httpClient : " + e3.getMessage());
            }
            final String CSVFileLocation = String.valueOf(configParams.getcsvFileLocation().trim()) + csvFileName.trim();
            System.out.println("CSVFileLocation : " + CSVFileLocation);
            try {
                final ControlResponseProcessor responseProcessor = new ControlResponseProcessor();
                System.out.println("calling controlDetailsJSONtoCSVConverter");
                final Boolean isConverted = responseProcessor.controlDetailsJSONtoCSVConverter(getRequestResponse, CSVFileLocation);
                System.out.println("isConverted : " + isConverted);
            }
            catch (Exception e4) {
                System.out.println("Exception in getting CSV : " + e4.getMessage());
            }
        }
        catch (Exception e5) {
            System.out.println("Exceptiosn in deleting csv in tmp location : " + e5.getMessage());
        }
    }
    
    public static void externalJobConfigMap(final Map jsonMap) {
        try {
            final String controlId = jsonMap.get("CONTROLID");
            final String csvFileName = jsonMap.get("CSVFILENAME");
            final String[] args = { controlId, csvFileName };
            main(args);
        }
        catch (Exception e) {
            System.out.println("Exception in externalJobConfigMap : " + e.getStackTrace());
        }
    }
}
