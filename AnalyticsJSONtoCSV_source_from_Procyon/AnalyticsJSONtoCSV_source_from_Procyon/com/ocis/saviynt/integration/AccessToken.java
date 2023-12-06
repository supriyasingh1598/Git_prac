// 
// Decompiled by Procyon v0.5.36
// 

package com.ocis.saviynt.integration;

import javax.net.ssl.HttpsURLConnection;
import java.util.Map;
import org.json.simple.JSONObject;

public class AccessToken
{
    public String getBearerToken(final String url, final String username, final String password) {
        System.out.println("Entry getBearerToken");
        String token = "";
        final CustomUrlConnection httpConn = new CustomUrlConnection();
        final String loginUrl = url;
        System.out.println("loginUrl : " + loginUrl);
        final JSONObject requestBody = new JSONObject();
        requestBody.put((Object)"username", (Object)username);
        requestBody.put((Object)"password", (Object)password);
        final String urlParams = requestBody.toString();
        System.out.println("urlParams: " + urlParams);
        HttpsURLConnection con2 = null;
        JSONObject callResponse = null;
        con2 = httpConn.sendPostHttp(loginUrl, urlParams, null);
        try {
            con2.setReadTimeout(2000000);
            con2.setConnectTimeout(180000);
        }
        catch (Exception e) {
            System.out.println("in connection timeout catch : " + e.getMessage());
        }
        callResponse = httpConn.fetchJsonObject(con2);
        if (callResponse != null) {
            token = callResponse.get((Object)"access_token").toString();
        }
        else {
            System.out.println("Error getting token: " + callResponse);
            try {
                throw new Exception("Error getting token " + callResponse);
            }
            catch (Exception e) {
                System.out.println("Error: JSON Response is null " + e.getMessage());
            }
        }
        System.out.println("Returning getBearerToken");
        return token;
    }
    
    public static void main(final String[] args) {
        System.out.println("START");
        final String url = "https://ssm-devupgrade-originenergy.saviyntcloud.com/ECM/api/login";
        final String username = "svc_snowadmin";
        final String password = "Tiger@1234";
        System.out.println("END");
    }
}
