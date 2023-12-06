// 
// Decompiled by Procyon v0.5.36
// 

package com.ocis.saviynt.integration;

import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.simple.JSONObject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

public class CustomUrlConnection
{
    HttpsURLConnection con;
    
    public CustomUrlConnection() {
        this.con = null;
    }
    
    public HttpsURLConnection sendPostHttp(final String url, final String urlParameters, final Map<String, String> headers) {
        System.out.println("Entry sendPostHttp");
        try {
            final URL obj = new URL(url);
            final HttpsURLConnection con = (HttpsURLConnection)obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            final OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(urlParameters);
            wr.flush();
            final int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Successful connection");
            }
            else {
                System.out.println("Connection refused");
            }
            return con;
        }
        catch (MalformedURLException e) {
            System.out.println("Error: recheck URL - " + e.getMessage());
        }
        catch (IOException e2) {
            System.out.println("Error: connection error - " + e2.getMessage());
        }
        finally {
            if (this.con != null) {
                this.con.disconnect();
            }
            System.out.println("Returning sendPostHttp connection");
        }
        return this.con;
    }
    
    public JSONObject fetchJsonObject(final HttpsURLConnection con) {
        System.out.println("Entry fetchJsonObject");
        int responseCode = 0;
        BufferedReader in = null;
        try {
            responseCode = con.getResponseCode();
            if (responseCode == 200) {
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                final StringBuffer response = new StringBuffer();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                System.out.println(response.toString());
                final JSONObject jsonObject = this.parseJsonResponse(response.toString());
                return jsonObject;
            }
            System.out.println("Error : Cannot reach the url or bad arguments, response code is " + responseCode);
            return null;
        }
        catch (Exception e) {
            System.out.println(" Error : Could not parse json object");
            return null;
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            System.out.println("Exit fetchJsonObject");
        }
    }
    
    protected JSONObject parseJsonResponse(final String JSONString) {
        final JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = null;
            if (JSONString != null && JSONString.trim().length() > 0) {
                jsonObject = (JSONObject)parser.parse(JSONString);
            }
            else {
                System.out.println(" HttpUrlConn: Json response is - " + JSONString);
            }
            return jsonObject;
        }
        catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
