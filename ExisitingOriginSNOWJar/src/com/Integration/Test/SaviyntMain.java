package com.Integration.Test;
import java.util.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import sun.misc.BASE64Encoder;

public class SaviyntMain {
	static final Logger logger = Logger.getLogger(SaviyntMain.class.getName());

	static FileHandler fh;

	public static void main(String[] args) {
		try {
			createLogs();
			InputStream inputStream = null;
			AESCrypt aes = new AESCrypt();
			//String propFileName = "/application_instance/custom_code/saviynt/Properties/externalconfig.properties";
			String propFileName = "D:\\SnowJar.properties";
//			String saviyntHome = System.getenv("SAVIYNT_HOME");
//			String saviyntHome = "/application_instance/custom_code/saviynt/Properties";
//			String propFileName = String.valueOf(saviyntHome) + File.separator + "SnowJar.properties";
//			String propFileName = String.valueOf(saviyntHome) + File.separator + "externalconfig.properties";
//			String propFileName = "C:\\Users\\Administrator\\Downloads\\SNOW\\SnowJar.properties";
			System.out.println("SnowJar.properties found at :" + propFileName);
			Properties prop = new Properties();
			inputStream = new FileInputStream(propFileName);
			prop.load(inputStream);
			System.out.println("getting snow values");
			String SNOWuser = prop.getProperty("SNOWUSERNAME");
			String SNOWenpass = prop.getProperty("SNOWPASSWORD");
			String SNOWpass = AESCrypt.decryptIt(SNOWenpass.trim());
			String authString = String.valueOf(SNOWuser) + ":" + SNOWpass;
			String basicAuthen = (new BASE64Encoder()).encode(authString.getBytes());
			String basicAuth = "Basic " + basicAuthen;
			String endpointForSnowMyRequest = prop.getProperty("endpointForSnowMyRequest");
			String endpointForManualFulfillment = prop.getProperty("endpointForManualFulfillment");

			SaviyntDB saviyntDB = new SaviyntDB();
			TokenPojo token = new TokenPojo();
			System.out.println("getting oauth token using callBearerToken");
			String outhToken = token.callBearerToken(prop, logger);
			ArrayList<RequestApprovalPojo> requestList = new ArrayList<>();
			ArrayList<RequestApprovalPojo> requestListA = new ArrayList<>();
			ArrayList<RequestApprovalPojo> requestListC = new ArrayList<>();
			ArrayList<RequestApprovalPojo> requestListD = new ArrayList<>();
			ArrayList<RequestApprovalPojo> requestListM = new ArrayList<>();
			ArrayList<RequestApprovalPojo> requestListE = new ArrayList<>();
			
			System.out.println("Process New Request for SNOW MyRequests - ");
			System.out.println("entering processNewRequest");
			requestList = saviyntDB.processNewRequest(endpointForSnowMyRequest, prop, outhToken, basicAuth,
					"open");
			System.out.println("requestList : " + requestList.toString());
			System.out.println(requestList);
			if (requestList != null && requestList.size() > 0) {
				System.out.println("inside requestList check. entering markNewRequests");
				saviyntDB.markNewRequests(prop, outhToken, requestList);
			} else {
				System.out.println("No data in New Request for SNOW MyRequests ");
			}
			
			/*
			 * // not required anymore since we can't capture ritm of completed requests ?
			System.out.println("Process Auto Approved Request for SNOW MyRequests - ");
			requestListA = saviyntDB.processAutoApprovedRequest(endpointForSnowMyRequest, prop, outhToken,
					basicAuth, "completed");
			if (requestListA != null && requestListA.size() > 0) {
				saviyntDB.markNewRequests(prop, outhToken, requestListA);
			} else {
				System.out.println("No data in Auto Approved Request for SNOW MyRequests ");
			}
			*/
			
			System.out.println("Complete Request for SNOW MyRequests - ");
			requestListC = saviyntDB.processCompletedRequest(endpointForSnowMyRequest, prop, outhToken,
					basicAuth, "completed");
			if (requestListC != null && requestListC.size() > 0) {
				saviyntDB.markNewRequests(prop, outhToken, requestListC);
			} else {
				System.out.println("No data in Completed Request for SNOW MyRequests ");
			}
			
			/*
			System.out.println("Complete Discontinued Request for SNOW MyRequests - ");
			requestListD = saviyntDB.processDiscontinuedRequest(endpointForSnowMyRequest, prop, outhToken,
					basicAuth, "Discontinued");
			if (requestListD != null && requestListD.size() > 0) {
				saviyntDB.markNewRequests(prop, outhToken, requestListD);
			} else {
				System.out.println("No data in Discontinued Request for SNOW MyRequests ");
			}
			*/
			/*
			System.out.println("Complete Expired Request for SNOW MyRequests - ");
			requestListE = saviyntDB.processExpiredRequest(endpointForSnowMyRequest, prop, outhToken,
					basicAuth, "Expired");
			if (requestListE != null && requestListE.size() > 0) {
				saviyntDB.markNewRequests(prop, outhToken, requestListE);
			} else {
				System.out.println("No data in Expired Request for SNOW MyRequests ");
			}
			*/
			/*
			System.out.println("Process Manual Fulfilment task for SNOW MyRequests - ");
			requestListM = saviyntDB.processPendingTask(endpointForManualFulfillment, prop, outhToken,
					basicAuth, "Pending");
			if (requestListM != null && requestListM.size() > 0) {
				saviyntDB.markNewRequests(prop, outhToken, requestListM);
			} else {
				System.out.println("No data in New Request for SNOW MyRequests ");
			}
			*/
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error in Jar:" + e.getMessage());
		} finally {
			System.out.println("External Code done");
			try {
				if (fh != null)
					fh.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	
	public static void createLogs() {
		try {
			String saviyntHome = System.getenv("SAVIYNT_HOME");
			String logFilePath = String.valueOf(saviyntHome) + File.separator + "logs" + File.separator
					+ "SNOWLogJar%g.log";
//			String logFilePath = "C:\\Users\\Administrator\\Downloads\\SNOW\\debugLogJar%g.log";
			System.out.println(logFilePath);
			fh = new FileHandler(logFilePath, 10485760, 10, true);
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
			System.out.println("SNOW SSM Jar Start...");
		} catch (SecurityException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
