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

public class RequestComplete {
	static final Logger logger = Logger.getLogger(SaviyntMain.class.getName());

	public String completeSnowRequest(String basicAuth, Properties prop, String requestId, String state) {
		String returnData = "";
		try {
			String URL = prop.getProperty("SNOWREQUESTURL");
			URL url = new URL(String.valueOf(URL) + "completeSR");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", basicAuth);
			String input = "{\"event\": {\"entity_id\": \"" + requestId
					+ "\",\"source_system\": \"Saviynt\"},\"detail\": {\"ritm_state\": \"" + state
					+ "\",\"close_message\": \"Request has been closed by SSM!\"}}";
			System.out.println("Payload being sent " + input);
			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();
			System.out.println("Response: " + conn.getResponseCode());
			System.out.println("HTTP Accepted: 202");
			logger.info("Response for complete: " + conn.getResponseCode());
			logger.info("HTTP Accepted: 202");
			if (conn.getResponseCode() != 202)
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			System.out.println("Output from Server .... \n");
			String output;
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				returnData = output;
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
