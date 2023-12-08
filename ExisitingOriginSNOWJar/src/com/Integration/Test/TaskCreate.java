package com.Integration.Test;

import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class TaskCreate {
	static final Logger logger = Logger.getLogger(SaviyntMain.class.getName());

	static Connection dbConn = null;

	static Statement dbStmt = null;

	public String getARComments(String requestkey, Properties prop, Statement dbStmt, DatabaseConnector db) {
		String comments = "";
		try {
			AESCrypt aes = new AESCrypt();
			String db_url = prop.getProperty("db_url");
			String user = prop.getProperty("user");
			String pass = prop.getProperty("pass");
			pass = AESCrypt.decryptIt(pass.trim());
			ResultSet resultSet1 = null;
			String userQry1 = "SELECT COMMENTS FROM ARS_REQUESTS WHERE JBPMPROCESSINSTANCEID like '%" + requestkey
					+ "%'";
			logger.info("Request comments query  - " + userQry1);
			resultSet1 = db.getResultSet(dbStmt, userQry1);
			while (resultSet1.next()) {
				try {
					comments = resultSet1.getString("COMMENTS");
					logger.info("Comments: " + comments);
				} catch (Exception exception) {
				}
			}
		} catch (Exception exception) {
		}
		return comments;
	}

	public String checkAllTasksComplete(String requestkey, Properties prop, String outhToken) {
		String endpointascsv = "";
		try {
			
			HttpUrlConn httpConn = new HttpUrlConn();
			Map<String, String> httpConnectionHeaders = new HashMap<>();
			httpConnectionHeaders.put("Authorization", "Bearer " + outhToken);
			httpConnectionHeaders.put("Content-Type", "application/json");
			httpConnectionHeaders.put("SAVUSERNAME", prop.getProperty("SAVUSERNAME"));
			HttpURLConnection con2 = null;
			
			String url3 = String.valueOf(prop.getProperty("URLPATH")) + "/api/v5/fetchTasks";
			JSONObject jsonRequest3 = new JSONObject();
			jsonRequest3.put("username", "admin");
			jsonRequest3.put("TASKSTATUS", "PENDING,PENDINGCREATE");
			jsonRequest3.put("max", prop.getProperty("MAXREQUESTS"));
			jsonRequest3.put("requestkey", requestkey);
			String urlParams3 = jsonRequest3.toString();
			con2 = httpConn.sendPostHttps(url3, urlParams3, httpConnectionHeaders);
			JSONObject getPendingRequestsResponse3 = httpConn.fetchJsonObject(con2);
			JSONParser parser3 = new JSONParser();
			JSONObject json3 = (JSONObject) parser3.parse(getPendingRequestsResponse3.toString());
			String totalTasks = (String) json3.get("totaltasks");
			endpointascsv = totalTasks;
//			JSONArray rolePrivAccesses = (JSONArray) requestDetails3.get("RolePrivAccessApproval");
			
//			commentsString = (String) requestDetails3.get("comments");
			
			
			
//			AESCrypt aes = new AESCrypt();
//			String db_url = prop.getProperty("db_url");
//			String user = prop.getProperty("user");
//			String pass = prop.getProperty("pass");
//			pass = AESCrypt.decryptIt(pass.trim());
//			ResultSet resultSet1 = null;
//			String requestableEndpoints = "";
//			String allowedEndpoints = prop.getProperty("endpointForSnowMyRequest");
//			List<String> allowedEndpointsList = Arrays.asList(allowedEndpoints.split("\\s*,\\s*"));
//			List<String> c = new ArrayList<>(allowedEndpointsList);
//			String list = StringUtils.join(c, "','");
//			list = "'" + list + "'";
//			String userQry1 = "SELECT count(*) as num FROM ARS_REQUESTS AR,ARSTASKS ATK WHERE AR.JBPMPROCESSINSTANCEID like '%"
//					+ requestkey + "%' and AR.REQUESTKEY=ATK.REQUESTKEY and atk.status IN (1,2) and \n"
//					+ "ATK.ENDPOINT in (select endpointkey from endpoints where displayname in (" + list + "))";
//			logger.info("Request endpoints query  - " + userQry1);
//			resultSet1 = db.getResultSet(dbStmt, userQry1);
//			while (resultSet1.next()) {
//				try {
//					endpointascsv = resultSet1.getString("num");
//					logger.info("endpointascsv: " + endpointascsv);
//				} catch (Exception exception) {
//				}
//			}
		} catch (Exception exception) {
		}
		return endpointascsv;
	}

	public String getProvisionedEndpoints(String requestkey, Properties prop, Statement dbStmt, DatabaseConnector db) {
		String endpointascsv = "";
		try {
			AESCrypt aes = new AESCrypt();
			String db_url = prop.getProperty("db_url");
			String user = prop.getProperty("user");
			String pass = prop.getProperty("pass");
			pass = AESCrypt.decryptIt(pass.trim());
			ResultSet resultSet1 = null;
			String requestableEndpoints = "";
			String allowedEndpoints = prop.getProperty("endpointForSnowMyRequest");
			String autoAllowedEndpoints = prop.getProperty("endpointForAutoprovisioning");
			List<String> allowedEndpointsList = Arrays.asList(allowedEndpoints.split("\\s*,\\s*"));
			List<String> autoAllowedEndpointsList = Arrays.asList(autoAllowedEndpoints.split("\\s*,\\s*"));
			List<String> c = new ArrayList<>(allowedEndpointsList);
			c.removeAll(autoAllowedEndpointsList);
			String list = StringUtils.join(c, "','");
			list = "'" + list + "'";
			String userQry1 = "SELECT count(*) as num FROM ARS_REQUESTS AR,ARSTASKS ATK WHERE AR.JBPMPROCESSINSTANCEID like '%"
					+ requestkey + "%' and AR.REQUESTKEY=ATK.REQUESTKEY and \n"
					+ "ATK.ENDPOINT in (select endpointkey from endpoints where displayname in (" + list + "))";
			logger.info("Request endpoints query  - " + userQry1);
			resultSet1 = db.getResultSet(dbStmt, userQry1);
			while (resultSet1.next()) {
				try {
					endpointascsv = resultSet1.getString("num");
					logger.info("endpointascsv: " + endpointascsv);
				} catch (Exception exception) {
				}
			}
		} catch (Exception exception) {
		}
		return endpointascsv;
	}

	public String getCommentsUsingRequestKey(String requestkey, Properties prop, Statement dbStmt,
			DatabaseConnector db) {
		String comments = "";
		try {
			AESCrypt aes = new AESCrypt();
			String db_url = prop.getProperty("db_url");
			String user = prop.getProperty("user");
			String pass = prop.getProperty("pass");
			pass = AESCrypt.decryptIt(pass.trim());
			ResultSet resultSet1 = null;
			String userQry1 = "SELECT COMMENTS FROM REQUEST_ACCESS WHERE REQUESTKEY='" + requestkey + "' limit 1";
			logger.info("Request comments query  - " + userQry1);
			resultSet1 = db.getResultSet(dbStmt, userQry1);
			while (resultSet1.next()) {
				try {
					comments = resultSet1.getString("COMMENTS");
					logger.info("Comments: " + comments);
				} catch (Exception exception) {
				}
			}
		} catch (Exception exception) {
		}
		return comments;
	}

	public String test(String requestkey, Properties prop) {
		String statusCheck = "";
		try {
			AESCrypt aes = new AESCrypt();
			String db_url = prop.getProperty("db_url");
			String user = prop.getProperty("user");
			String pass = prop.getProperty("pass");
			pass = AESCrypt.decryptIt(pass.trim());
			DatabaseConnector db = new DatabaseConnector();
			dbConn = DatabaseConnector.getDatabaseConnection(db_url, user, pass);
			dbStmt = db.getDbCreateStatement(dbConn);
			ResultSet resultSet1 = null;
			String userQry1 = "SELECT STATUS FROM ARS_REQUESTS  WHERE JBPMPROCESSINSTANCEID like '%" + requestkey
					+ "%'";
			logger.info("Request check status query  - " + userQry1);
			resultSet1 = db.getResultSet(dbStmt, userQry1);
			while (resultSet1.next()) {
				try {
					statusCheck = resultSet1.getString("STATUS");
					logger.info("statusCheck: " + statusCheck);
				} catch (Exception exception) {
				}
			}
		} catch (Exception exception) {
		}
		return statusCheck;
	}

	public String getComments(String requestaccesskey, Properties prop, Statement dbStmt, DatabaseConnector db) {
		String comments = "";
		try {
			AESCrypt aes = new AESCrypt();
			String db_url = prop.getProperty("db_url");
			String user = prop.getProperty("user");
			String pass = prop.getProperty("pass");
			pass = AESCrypt.decryptIt(pass.trim());
			ResultSet resultSet1 = null;
			String userQry1 = "SELECT COMMENTS FROM REQUEST_ACCESS WHERE REQUEST_ACCESSKEY='" + requestaccesskey + "'";
			logger.info("Request comments query  - " + userQry1);
			resultSet1 = db.getResultSet(dbStmt, userQry1);
			while (resultSet1.next()) {
				try {
					comments = resultSet1.getString("COMMENTS");
					logger.info("Comments: " + comments);
				} catch (Exception exception) {
				}
			}
		} catch (Exception exception) {
		}
		return comments;
	}

	public String updateRequest(Properties prop, String comments, String reqItemComments, String parentTask) {
		String sysId = "";
		String reqnumber = "";
		String reqSysId = "";
		AESCrypt aes = new AESCrypt();
		try {
			if (reqItemComments != null)
				if (!reqItemComments.equals("")) {
					String str1 = reqItemComments.substring(0, reqItemComments.lastIndexOf("::::"));
					sysId = str1.substring(str1.lastIndexOf("::::") + 4, str1.length());
					reqnumber = str1.substring(str1.indexOf("::::") + 4, str1.lastIndexOf("::::"));
					reqSysId = reqnumber.substring(reqnumber.indexOf("::::") + 4, reqnumber.length());
					System.out.println("SysID is " + sysId);
					System.out.println("Request sysId is " + reqSysId);
				}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return sysId;
	}
}
