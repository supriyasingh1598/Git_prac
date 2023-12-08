package com.Integration.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

public class RequestCreateRest {
	static final Logger logger = Logger.getLogger(SaviyntMain.class.getName());

	public String createSnowRequest(String basicAuth, Properties prop, String requestId, String userID,
			String requestedBy, String shortDesc, String desc) {
		String returnData = "";
		String commentString = "";
		try {
			String URL = prop.getProperty("SNOWREQUESTURL");
			URL url = new URL(String.valueOf(URL) + "createSR");
			System.out.println("snow url : " + url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", basicAuth);
			String input = "{\"event\": {\"entity_id\": \"" + requestId
					+ "\",\"source_system\": \"Saviynt\"},\"detail\": {\"requested_by\": \"" + requestedBy
					+ "\",\"requested_for\": \"" + userID + "\",\"short_description\": \"" + shortDesc
					+ "\",\"description\": \"" + desc + "\"}}";
			System.out.println("Payload being sent " + input);
			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();
			logger.info("Response for create: " + conn.getResponseCode());
			logger.info("HTTP Created: 201");
			if (conn.getResponseCode() != 201)
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			System.out.println("Output from Server .... \n");
			String output;
			while ((output = br.readLine()) != null) {
				returnData = output;
				System.out.println("returnData - " + returnData);
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return returnData;
	}
}
