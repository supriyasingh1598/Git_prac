package com.Integration.Test;

import java.net.HttpURLConnection;
import java.util.Properties;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONObject;

public class TokenPojo {
	private String oAuthToken = "";

	//static final Logger logger = Logger.getLogger(SaviyntMain.class.getName());

	public String getOAuthToken() {
		return this.oAuthToken;
	}

	public void setOAuthToken(String token) {
		this.oAuthToken = token;
	}

	public String callBearerToken(Properties prop, Logger logger) throws Exception {
		String token = "";
		try {
			HttpUrlConn httpConn = new HttpUrlConn();
			String getOAuthUrl = String.valueOf(prop.getProperty("URLPATH")).trim() + "/api/login";
			System.out.println("oauthUrl : " + getOAuthUrl);
			AESCrypt aes = new AESCrypt();
			JSONObject jsonRequest = new JSONObject();
			jsonRequest.put("username", prop.getProperty("SAVUSERNAME"));
			System.out.println(prop.getProperty("SAVPASSWORD"));
			jsonRequest.put("password", AESCrypt.decryptIt(prop.getProperty("SAVPASSWORD")));
			String urlParams = jsonRequest.toString();
			System.out.println("urlParams : " + urlParams);
			HttpsURLConnection con1 = null;
			HttpURLConnection con2 = null;
			JSONObject getOAuthResponse = null;
			con2 = httpConn.sendPostHttps(getOAuthUrl, urlParams, null);
			getOAuthResponse = httpConn.fetchJsonObject(con2);
			if (getOAuthResponse != null) {
				token = getOAuthResponse.get("access_token").toString();
			} else {
				System.out.println("Could not get the token :" + getOAuthResponse);
				logger.info("Could not get the token :" + getOAuthResponse);
				throw new Exception("Could not get token");
			}
		} catch (Exception e) {
			logger.info("Error: " + e.toString());
			throw e;
		}
		return token;
	}
}
