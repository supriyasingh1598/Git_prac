package com.Integration.Test;

import java.net.HttpURLConnection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class SaviyntDB {
	static final Logger logger = Logger.getLogger(SaviyntMain.class.getName());

	public String getAccessDetails(Properties prop, DatabaseConnector db, Statement dbStmt, String outhToken,
			String requestKey) {
		StringBuffer sb = new StringBuffer();
		try {
			String url = String.valueOf(prop.getProperty("URLPATH")) + "api/v5/fetchRequestApprovalDetails";
			HttpUrlConn httpConn = new HttpUrlConn();
			HttpURLConnection con2 = null;
			JSONObject getAccessResponse = null;
			JSONObject jsonRequest = new JSONObject();
			jsonRequest.put("requestKey", requestKey);
			jsonRequest.put("userName", prop.getProperty("SAVUSERNAME"));
			Map<String, String> httpConnectionHeaders = new HashMap<>();
			httpConnectionHeaders.put("Authorization", "Bearer " + outhToken);
			System.out.println("Sending 'POST' request to URL : " + url);
			con2 = httpConn.sendPostHttps(url, jsonRequest.toString(), httpConnectionHeaders);
			getAccessResponse = httpConn.fetchJsonObject(con2);
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getAccessResponse.toString());
			JSONObject AppRequestDetails = (JSONObject) json.get("ApprovalRequestDetails");
			JSONArray AccessRequestDetails = (JSONArray) AppRequestDetails.get("AccessRequestDetails");
			JSONObject tasksList = (JSONObject) AccessRequestDetails.get(0);
			JSONArray taskjson = (JSONArray) tasksList.get("tasksList");
			for (int i = 0; i < taskjson.size(); i++) {
				JSONObject rSet = (JSONObject) taskjson.get(i);
				String access = (String) rSet.get("access");
				sb.append(access);
				sb.append(",");
			}
			if (sb.length() > 0)
				sb.setLength(sb.length() - 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public ArrayList<RequestApprovalPojo> processNewRequest(String endpointForSnowMyRequest, Properties prop,
			String outhToken, String basicAuth, String status) {
		System.out.println("Enter Get new requests from DB");
		ResultSet resultSet = null;
		ArrayList<RequestApprovalPojo> approvalList = new ArrayList<>();
		try {
			RequestCreateRest requestCreate = new RequestCreateRest();
			try {
				String url = String.valueOf(prop.getProperty("URLPATH")).trim() + "/api/v5/fetchRequestHistory";
				System.out.println(url);
				HttpUrlConn httpConn = new HttpUrlConn();
				HttpURLConnection con2 = null;
				JSONObject getPendingRequestsResponse = null;
				JSONObject jsonRequest = new JSONObject();
				jsonRequest.put("username", "admin");
				jsonRequest.put("status", status);
				jsonRequest.put("max", prop.getProperty("MAXREQUESTS"));
				Map<String, String> httpConnectionHeaders = new HashMap<>();
				httpConnectionHeaders.put("Authorization", "Bearer " + outhToken);
				httpConnectionHeaders.put("Content-Type", "application/json");
				httpConnectionHeaders.put("SAVUSERNAME", prop.getProperty("SAVUSERNAME"));
				String urlParams = jsonRequest.toString();
				System.out.println(urlParams);
				System.out.println("Sending 'POST' request to URL : " + url);
				con2 = httpConn.sendPostHttps(url, urlParams, httpConnectionHeaders);
				getPendingRequestsResponse = httpConn.fetchJsonObject(con2);
				System.out.println("getPendingRequestsResponse " + getPendingRequestsResponse.toString());
				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(getPendingRequestsResponse.toString());
				Long totalRequests = (Long) json.get("count");
				JSONArray requestDetails = (JSONArray) json.get("requests");
				System.out.println("total pending requests in new request" + totalRequests);
				for (int i = 0; i < totalRequests.longValue(); i++) {
					JSONObject req = (JSONObject) requestDetails.get(i);
					System.out.println("req : " + req.toString());
					String requestEndpoints = (String) req.get("endpoints");
					String requesttype = (String) req.get("requesttype");
					String reqkey = (String) req.get("reqkey");
					System.out.println("Requesttype for this request" + requesttype);
					System.out.println("Requestkey for this request-" + reqkey);
					String shortDesc = "";
					String desc = "";
					String role_name = "";
					String applications = "";
					boolean process = false;
					boolean isER = false;
					boolean isPR = false;
					boolean isAR = false;
					boolean processER = Boolean.parseBoolean(prop.getProperty("ProcessEnterpriseRoleRequest"));
					boolean processPR = Boolean.parseBoolean(prop.getProperty("ProcessPrivilegedRoleRequest"));
					if (requestEndpoints != null && !requestEndpoints.equals("")) {
						String allowedEndpoints = prop.getProperty("endpointForSnowMyRequest");
						String[] allowedEndpointsList = allowedEndpoints.split("\\s*,\\s*");
						byte b;
						int j;
						String[] arrayOfString1;
						for (j = (arrayOfString1 = allowedEndpointsList).length, b = 0; b < j;) {
							String endpoint = arrayOfString1[b];
							if (requestEndpoints.contains(endpoint)) {
								process = true;
								isAR = true;
								applications = requestEndpoints;
								break;
							}
							b++;
						}
					}
					if (!process && processER)
						if (requesttype.equalsIgnoreCase("Enterprise Role Request")) {
							System.out.println("in ERR check");

							String url3 = String.valueOf(prop.getProperty("URLPATH"))
									+ "/api/v5/fetchRequestHistoryDetails";
							System.out.println("Sending 'POST' request to URL : " + url3);
							JSONObject jsonRequest3 = new JSONObject();
							jsonRequest3.put("username", "admin");
							jsonRequest3.put("status", status);
							jsonRequest3.put("max", prop.getProperty("MAXREQUESTS"));
							jsonRequest3.put("requestkey", reqkey);
							String urlParams3 = jsonRequest3.toString();
							con2 = httpConn.sendPostHttps(url3, urlParams3, httpConnectionHeaders);
							JSONObject getPendingRequestsResponse3 = httpConn.fetchJsonObject(con2);
							JSONParser parser3 = new JSONParser();
							JSONObject json3 = (JSONObject) parser3.parse(getPendingRequestsResponse3.toString());
							JSONObject requestDetails3 = (JSONObject) json3.get("requestHistoryDetails");
							JSONArray rolePrivAccesses = (JSONArray) requestDetails3.get("requestAccessDetails");

							if (rolePrivAccesses != null && rolePrivAccesses.size() > 0) {
								role_name = (String) ((JSONObject) rolePrivAccesses.get(0)).get("Access");
							}

							process = true;
							System.out.println("Requested Enterprise rolename " + role_name);
							isER = true;

						}
					if (!process && processPR) {
						if (requesttype.equalsIgnoreCase("Privileged Role Request")) {
							System.out.println("in PRR check");
							String url3 = String.valueOf(prop.getProperty("URLPATH"))
									+ "/api/v5/fetchRequestHistoryDetails";
							System.out.println("Sending 'POST' request to URL : " + url3);
							JSONObject jsonRequest3 = new JSONObject();
							jsonRequest3.put("username", "admin");
							jsonRequest3.put("status", status);
							jsonRequest3.put("max", prop.getProperty("MAXREQUESTS"));
							jsonRequest3.put("requestkey", reqkey);
							String urlParams3 = jsonRequest3.toString();
							con2 = httpConn.sendPostHttps(url3, urlParams3, httpConnectionHeaders);
							JSONObject getPendingRequestsResponse3 = httpConn.fetchJsonObject(con2);
							JSONParser parser3 = new JSONParser();
							JSONObject json3 = (JSONObject) parser3.parse(getPendingRequestsResponse3.toString());
							JSONObject requestDetails3 = (JSONObject) json3.get("requestHistoryDetails");
							JSONArray rolePrivAccesses = (JSONArray) requestDetails3.get("RolePrivAccessApproval");

							if (rolePrivAccesses != null && rolePrivAccesses.size() > 0) {
								role_name = (String) ((JSONObject) rolePrivAccesses.get(0)).get("Access");
							}

							process = true;
							System.out.println("process=true. Requested privileged rolename " + role_name);
							isPR = true;
						}
						System.out.println("No Request found for privileged Role" + role_name);
					}
					if (process) {
						System.out.println("in process block");
						String requestId = (String) req.get("requestid");
						String requestkey = (String) req.get("reqkey");
						String RequestedFor = (String) req.get("requestedfor");
						String RequestedBy = (String) req.get("requestor");
						String requestForID = RequestedFor.substring(RequestedFor.length() - 9,
								RequestedFor.length() - 1);
						String requestedForName = ((String) req.get("requestedfor")).replaceAll("\\(.*\\)", "").trim();
						String requestedBy = RequestedBy.substring(RequestedBy.length() - 9, RequestedBy.length() - 1);
						if (isAR) {
							System.out.println("in isAR block");
							if (requesttype.equalsIgnoreCase("New Account")) {
								shortDesc = "New Account Request for " + requestedForName;
								desc = "Request has been raised for " + requestedForName
										+ " for granting access to the following systems: " + applications;
							}
							if (requesttype.equalsIgnoreCase("Modify Account")) {
								shortDesc = "Modify Account Request for " + requestedForName;
								desc = "Request has been raised for " + requestedForName
										+ " for modifying access to the following systems: " + applications;
							}
							if (requesttype.equalsIgnoreCase("Enable Account")) {
								shortDesc = "Enable Account Request for " + requestedForName;
								desc = "Request has been raised for " + requestedForName
										+ " for modifying access to the following systems: " + applications;
							}
							if (requesttype.equalsIgnoreCase("Disable Account")) {
								shortDesc = "Disable Account Request for " + requestedForName;
								desc = "Request has been raised for " + requestedForName
										+ " for modifying access to the following systems: " + applications;
							}
							if (requesttype.equalsIgnoreCase("Remove Account")) {
								shortDesc = "Remove Account Request for " + requestedForName;
								desc = "Request has been raised for " + requestedForName
										+ " for modifying access to the following systems: " + applications;
							}
							if (requesttype.equalsIgnoreCase("Revoke Access")) {
								shortDesc = "Revoke Access Request for " + requestedForName;
								desc = "Request has been raised for " + requestedForName
										+ " for revoking access to the following systems: " + applications;
							}
							if (requesttype.equalsIgnoreCase("Grant Access")) {
								shortDesc = "Grant Access Request for " + requestedForName;
								desc = "Request has been raised for " + requestedForName
										+ " for granting access to the following systems: " + applications;
							}
						}
						if (isER) {
							System.out.println("in isER block");

							shortDesc = "Enterprise Role Request : " + role_name;
							desc = "Access Request has been raised for " + requestedForName
									+ " for the following Enterprise Role: " + role_name;
						}
						if (isPR) {
							System.out.println("in isPR block");

							shortDesc = "Privileged Role Request : " + role_name;
							desc = "Access Request has been raised for " + requestedForName
									+ " for the following Privileged Role: " + role_name;
						}
						String url2 = String.valueOf(prop.getProperty("URLPATH"))
								+ "/api/v5/fetchRequestHistoryDetails";
						System.out.println("Sending 'POST' request to URL : " + url2);
						JSONObject jsonRequest2 = new JSONObject();
						jsonRequest2.put("username", "admin");
						jsonRequest2.put("status", status);
						jsonRequest2.put("max", prop.getProperty("MAXREQUESTS"));
						jsonRequest2.put("requestkey", requestkey);
						String urlParams2 = jsonRequest2.toString();
						con2 = httpConn.sendPostHttps(url2, urlParams2, httpConnectionHeaders);
						JSONObject getPendingRequestsResponse2 = httpConn.fetchJsonObject(con2);
						JSONParser parser2 = new JSONParser();
						JSONObject json2 = (JSONObject) parser2.parse(getPendingRequestsResponse2.toString());
						JSONObject requestDetails2 = (JSONObject) json2.get("requestHistoryDetails");

						String commentsString = (String) requestDetails2.get("comments");
						System.out.println("commentsString : " + commentsString);

						if (commentsString == null)
							commentsString = "";
						if (!commentsString.contains("CREATED SR")
								&& !commentsString.contains("provisioned from servicenow")) {
							System.out.println("requestId for this request- " + requestId);
							System.out.println("shortdesc for this request-" + shortDesc);
							System.out.println("desc for this request-" + desc);
							System.out.println(
									"requestForID for this request-" + requestForID + " name- " + requestedForName);
							System.out.println("requestedBy for this request-" + requestedBy);
							System.out.println("Requesttype for this request-" + requesttype);
							String returnData = requestCreate.createSnowRequest(basicAuth, prop, requestId,
									requestForID, requestedBy, shortDesc, desc);
							System.out.println("returnData : " + returnData);
							String sys_id = "";
							String setCommentString = "";
							if (returnData.contains("\"success\":true")) {
								System.out.println("in return data check");
								if (returnData.contains("\"inserted\":true")) {
									System.out.println("in inserted check");
									String[] splitString = returnData.split("\"ritm_number\"");
									String[] ritmString = splitString[1].split("\"");
									String ritmNumber = ritmString[1];
									splitString = returnData.split("\"ritm_sys_id\"");
									String[] sysidString = splitString[1].split("\"");
									sys_id = sysidString[1];
									splitString = returnData.split("\"sent_timestamp\"");
									String[] timeString = splitString[1].split("\"");
									String timeStamp = timeString[1];
									splitString = returnData.split("\"ssm_entity_id\"");
									String[] requestString = splitString[1].split("\"");
									String requestNumber = requestString[1];
									setCommentString = "CREATED SR:::: Request ID: " + requestNumber + "; RITM Number: "
											+ ritmNumber + "; Request Creation TimeStamp: " + timeStamp + "; \n";
									System.out.println("setCommentString : " + setCommentString);
									RequestApprovalPojo requestApprovalPojo = new RequestApprovalPojo();
									requestApprovalPojo.setMarkingComment(setCommentString);
									requestApprovalPojo.setRequestKey(requestkey);
									requestApprovalPojo.setSnowSysId(sys_id);
									requestApprovalPojo.setArComments(setCommentString);
									approvalList.add(requestApprovalPojo);
									System.out.println("approvalList :");
									System.out.println(approvalList);
								}
							} else {
								System.out.println("Unable to create in SNOW for requestId : " + requestId);
//								logger.info("Unable to create in SNOW for requestId : " + requestId);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception se) {
			se.printStackTrace();
		}
		System.out.println("final approvalList : ");
		System.out.println(approvalList);
		return approvalList;
	}

	public ArrayList<RequestApprovalPojo> processAutoApprovedRequest(String endpointForSnowMyRequest, Properties prop,
			String outhToken, String basicAuth, String status) {
		System.out.println("Enter Get new requests from Saviynt");

		RequestCreateRest requestCreate = new RequestCreateRest();
		int totalEndpointRequest = 0;

		ArrayList<RequestApprovalPojo> approvalList = new ArrayList<>();
		try {

			String endpointNames = prop.getProperty("endpointnames");
			String[] endpointName = endpointNames.split(",");
			for (String endpoint : endpointName) {
				endpoint = endpoint.substring(1, endpoint.length() - 1);
				String url = String.valueOf(prop.getProperty("URLPATH")) + "/api/v5/fetchRequestHistory";

				HttpUrlConn httpConn = new HttpUrlConn();
				HttpURLConnection con2 = null;
				JSONObject getPendingRequestsResponse = null;
				JSONObject jsonRequest = new JSONObject();
				jsonRequest.put("username", "admin");
				jsonRequest.put("status", status);
				jsonRequest.put("max", prop.getProperty("MAXREQUESTS"));
				jsonRequest.put("endpoint", endpoint);
				Map<String, String> httpConnectionHeaders = new HashMap<>();
				httpConnectionHeaders.put("Authorization", "Bearer " + outhToken);
				httpConnectionHeaders.put("Content-Type", "application/json");
				httpConnectionHeaders.put("SAVUSERNAME", prop.getProperty("SAVUSERNAME"));
				String urlParams = jsonRequest.toString();

				con2 = httpConn.sendPostHttps(url, urlParams, httpConnectionHeaders);
				getPendingRequestsResponse = httpConn.fetchJsonObject(con2);
				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(getPendingRequestsResponse.toString());
				Long totalRequests = 12345678910L;
				totalRequests = (Long) json.get("count");
				JSONArray requestDetails = (JSONArray) json.get("requests");
				System.out.println("total pending requests " + totalRequests);
				for (int i = 0; i < totalRequests.longValue(); i++) {
					JSONObject req = (JSONObject) requestDetails.get(i);
					String requestEndpoints = (String) req.get("endpoints");
					String requesttype = (String) req.get("requesttype");
					String reqkey = (String) req.get("reqkey");
					String shortDesc = "";
					String desc = "";
					String role_name = "";
					String applications = "";
					boolean process = false;
					boolean isER = false;
					boolean isPR = false;
					boolean isAR = false;
					boolean processER = Boolean.parseBoolean(prop.getProperty("ProcessEnterpriseRoleRequest"));
					boolean processPR = Boolean.parseBoolean(prop.getProperty("ProcessPrivilegedRoleRequest"));
					if (requestEndpoints != null && !requestEndpoints.equals("")) {
						String allowedEndpoints = prop.getProperty("endpointForSnowMyRequest");
						String[] allowedEndpointsList = allowedEndpoints.split("\\s*,\\s*");
						byte b;
						int k;
						String[] arrayOfString1;
						for (k = (arrayOfString1 = allowedEndpointsList).length, b = 0; b < k;) {
							String endpointFromList = arrayOfString1[b];
							if (requestEndpoints.contains(endpointFromList)) {
								process = true;
								isAR = true;
								applications = requestEndpoints;
								break;
							}
							b++;
						}
					}
					if (!process && processER)
						if (requesttype.equalsIgnoreCase("Enterprise Role Request")) {

							System.out.println("Inside Enterprise Role Request*****fetchRequestHistoryDetails*****");
							String url3 = String.valueOf(prop.getProperty("URLPATH"))
									+ "/api/v5/fetchRequestHistoryDetails";

							JSONObject jsonRequest3 = new JSONObject();
							jsonRequest3.put("username", "admin");
							jsonRequest3.put("status", status);
							jsonRequest3.put("max", prop.getProperty("MAXREQUESTS"));
							jsonRequest3.put("requestkey", reqkey);
							String urlParams3 = jsonRequest3.toString();
							con2 = httpConn.sendPostHttps(url3, urlParams3, httpConnectionHeaders);
							JSONObject getPendingRequestsResponse3 = httpConn.fetchJsonObject(con2);
							JSONParser parser3 = new JSONParser();
							JSONObject json3 = (JSONObject) parser3.parse(getPendingRequestsResponse3.toString());
							JSONObject requestDetails3 = (JSONObject) json3.get("requestHistoryDetails");
							JSONArray rolePrivAccesses = (JSONArray) requestDetails3.get("requestAccessDetails");

							if (rolePrivAccesses != null && rolePrivAccesses.size() > 0) {
								role_name = (String) ((JSONObject) rolePrivAccesses.get(0)).get("Access");
							}

							process = true;
							isER = true;

						}
					if (!process && processPR)
						if (requesttype.equalsIgnoreCase("Privileged Role Request")) {
							process = true;
							System.out.println("Inside Privileged Role Request*****fetchRequestHistoryDetails*****");
							String url3 = String.valueOf(prop.getProperty("URLPATH"))
									+ "/api/v5/fetchRequestHistoryDetails";

							JSONObject jsonRequest3 = new JSONObject();
							jsonRequest3.put("username", "admin");
							jsonRequest3.put("status", status);
							jsonRequest3.put("max", prop.getProperty("MAXREQUESTS"));
							jsonRequest3.put("requestkey", reqkey);
							String urlParams3 = jsonRequest3.toString();
							con2 = httpConn.sendPostHttps(url3, urlParams3, httpConnectionHeaders);
							JSONObject getPendingRequestsResponse3 = httpConn.fetchJsonObject(con2);
							JSONParser parser3 = new JSONParser();
							JSONObject json3 = (JSONObject) parser3.parse(getPendingRequestsResponse3.toString());
							JSONObject requestDetails3 = (JSONObject) json3.get("requestHistoryDetails");
							JSONArray rolePrivAccesses = (JSONArray) requestDetails3.get("RolePrivAccessApproval");

							if (rolePrivAccesses != null && rolePrivAccesses.size() > 0) {
								role_name = (String) ((JSONObject) rolePrivAccesses.get(0)).get("Access");
							}

							isPR = true;

						}
					if (process) {
						String requestId = (String) req.get("requestid");
						String requestkey = (String) req.get("reqkey");
						String RequestedFor = (String) req.get("requestedfor");
						String RequestedBy = (String) req.get("requestor");
						String requestForID = RequestedFor.substring(RequestedFor.length() - 9,
								RequestedFor.length() - 1);
						String requestedForName = ((String) req.get("requestedfor")).replaceAll("\\(.*\\)", "").trim();
						String requestedBy = RequestedBy.substring(RequestedBy.length() - 9, RequestedBy.length() - 1);
						if (isAR) {
							if (requesttype.equalsIgnoreCase("New Account")) {
								shortDesc = "New Account Request for " + requestedForName;
								desc = "Request has been raised for " + requestedForName
										+ " for granting access to the following systems: " + applications;
							}
							if (requesttype.equalsIgnoreCase("Modify Account")) {
								shortDesc = "Modify Account Request for " + requestedForName;
								desc = "Request has been raised for " + requestedForName
										+ " for modifying access to the following systems: " + applications;
							}
							if (requesttype.equalsIgnoreCase("Enable Account")) {
								shortDesc = "Enable Account Request for " + requestedForName;
								desc = "Request has been raised for " + requestedForName
										+ " for modifying access to the following systems: " + applications;
							}
							if (requesttype.equalsIgnoreCase("Disable Account")) {
								shortDesc = "Disable Account Request for " + requestedForName;
								desc = "Request has been raised for " + requestedForName
										+ " for modifying access to the following systems: " + applications;
							}
							if (requesttype.equalsIgnoreCase("Remove Account")) {
								shortDesc = "Remove Account Request for " + requestedForName;
								desc = "Request has been raised for " + requestedForName
										+ " for modifying access to the following systems: " + applications;
							}
							if (requesttype.equalsIgnoreCase("Revoke Access")) {
								shortDesc = "Revoke Access Request for " + requestedForName;
								desc = "Request has been raised for " + requestedForName
										+ " for revoking access to the following systems: " + applications;
							}
							if (requesttype.equalsIgnoreCase("Grant Access")) {
								shortDesc = "Grant Access Request for " + requestedForName;
								desc = "Request has been raised for " + requestedForName
										+ " for granting access to the following systems: " + applications;
							}
						}
						if (isER) {

							shortDesc = "Enterprise Role Request : " + role_name;
							desc = "Access Request has been raised for " + requestedForName
									+ " for the following Enterprise Role: " + role_name;

						}
						if (isPR) {

							shortDesc = "Privileged Role Request : " + role_name;
							desc = "Access Request has been raised for " + requestedForName
									+ " for the following Privileged Role: " + role_name;

						}
						String commentsString = "";

						String url3 = String.valueOf(prop.getProperty("URLPATH"))
								+ "/api/v5/fetchRequestHistoryDetails";

						JSONObject jsonRequest3 = new JSONObject();
						jsonRequest3.put("username", "admin");
						jsonRequest3.put("status", status);
						jsonRequest3.put("max", prop.getProperty("MAXREQUESTS"));
						jsonRequest3.put("requestkey", reqkey);
						String urlParams3 = jsonRequest3.toString();
						con2 = httpConn.sendPostHttps(url3, urlParams3, httpConnectionHeaders);
						JSONObject getPendingRequestsResponse3 = httpConn.fetchJsonObject(con2);
						JSONParser parser3 = new JSONParser();
						JSONObject json3 = (JSONObject) parser3.parse(getPendingRequestsResponse3.toString());
						JSONObject requestDetails3 = (JSONObject) json3.get("requestHistoryDetails");

						commentsString = (String) requestDetails3.get("comments");

						if (commentsString == null)
							commentsString = "";
						if (!commentsString.contains("CREATED SR")
								&& !commentsString.contains("provisioned from servicenow")) {
							System.out.println("requestId for this request- " + requestId);
							System.out.println("shortdesc for this request-" + shortDesc);
							System.out.println("desc for this request-" + desc);
							System.out.println(
									"requestForID for this request-" + requestForID + " name- " + requestedForName);
							System.out.println("requestedBy for this request-" + requestedBy);
							System.out.println("Requesttype for this request-" + requesttype + "\n");
							String returnData = requestCreate.createSnowRequest(basicAuth, prop, requestId,
									requestForID, requestedBy, shortDesc, desc);
							String sys_id = "";
							String setCommentString = "";
							if (returnData.contains("\"success\":true")) {
								if (returnData.contains("\"inserted\":true")
										|| returnData.contains("\"inserted\":false")) {
									String[] splitString = returnData.split("\"ritm_number\"");
									String[] ritmString = splitString[1].split("\"");
									String ritmNumber = ritmString[1];
									splitString = returnData.split("\"ritm_sys_id\"");
									String[] sysidString = splitString[1].split("\"");
									sys_id = sysidString[1];
									splitString = returnData.split("\"sent_timestamp\"");
									String[] timeString = splitString[1].split("\"");
									String timeStamp = timeString[1];
									splitString = returnData.split("\"ssm_entity_id\"");
									String[] requestString = splitString[1].split("\"");
									String requestNumber = requestString[1];
									setCommentString = "CREATED SR:::: Request ID: " + requestNumber + "; RITM Number: "
											+ ritmNumber + "; Request Creation TimeStamp: " + timeStamp + "; \n";
									RequestApprovalPojo requestApprovalPojo = new RequestApprovalPojo();
									requestApprovalPojo.setMarkingComment(setCommentString);
									requestApprovalPojo.setRequestKey(requestkey);
									requestApprovalPojo.setSnowSysId(sys_id);
									requestApprovalPojo.setArComments(setCommentString);
									approvalList.add(requestApprovalPojo);
								}
							} else {
								System.out.println("Unable to create in SNOW for requestId : " + requestId);
//								logger.info("Unable to create in SNOW for requestId : " + requestId);
							}
						}
					}
				}
				totalEndpointRequest = (int) (totalEndpointRequest + totalRequests);

			}
			System.out.println("**************Total Pending Requests****************** " + totalEndpointRequest);

		} catch (Exception se) {
			se.printStackTrace();
		}
		return approvalList;
	}

	public ArrayList<RequestApprovalPojo> processCompletedRequest(String endpointForSnowMyRequest, Properties prop,
			String outhToken, String basicAuth, String status) {
		System.out.println("Enter Get Completed requests from SSM");
		ResultSet resultSet = null;
		ArrayList<RequestApprovalPojo> approvalList = new ArrayList<>();
		try {
			RequestComplete RC = new RequestComplete();
			try {
				String url = String.valueOf(prop.getProperty("URLPATH")) + "/api/v5/fetchRequestHistory";
				HttpUrlConn httpConn = new HttpUrlConn();
				HttpURLConnection con2 = null;
				JSONObject getPendingRequestsResponse = null;
				JSONObject jsonRequest = new JSONObject();
				jsonRequest.put("username", "admin");
				jsonRequest.put("status", status);
				jsonRequest.put("max", prop.getProperty("MAXREQUESTS"));
				Map<String, String> httpConnectionHeaders = new HashMap<>();
				httpConnectionHeaders.put("Authorization", "Bearer " + outhToken);
				httpConnectionHeaders.put("Content-Type", "application/json");
				httpConnectionHeaders.put("SAVUSERNAME", prop.getProperty("SAVUSERNAME"));
				String urlParams = jsonRequest.toString();
				System.out.println("Sending 'POST' request to URL : " + url);
				con2 = httpConn.sendPostHttps(url, urlParams, httpConnectionHeaders);
				getPendingRequestsResponse = httpConn.fetchJsonObject(con2);
				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(getPendingRequestsResponse.toString());
				Long totalRequests = (Long) json.get("count");
				JSONArray requestDetails = (JSONArray) json.get("requests");
				System.out.println("total completed requests " + totalRequests);
				// totalRequests is 6754, can't we check for requests still under max due date for today?
				for (int i = 0; i < totalRequests.longValue(); i++) {
					JSONObject req = (JSONObject) requestDetails.get(i);
					String requestEndpoints = ((String) req.get("endpoints")).toLowerCase();
					String requesttype = (String) req.get("requesttype");
					String shortDesc = "";// 2022-12-22 09:54:43

					String requestsubmittedon = (String) req.get("requestsubmittedon");

					int requestDays = Integer.parseInt(prop.getProperty("REQUESTDAYS"));
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date reqDate = simpleDateFormat.parse(requestsubmittedon);
					Calendar calendar = Calendar.getInstance();
					calendar.add(Calendar.DATE, -requestDays);
					if (calendar.getTime().after(reqDate)) {
						continue;
					}

					boolean process = false;
					boolean processER = Boolean.parseBoolean(prop.getProperty("ProcessEnterpriseRoleRequest"));
					boolean processPR = Boolean.parseBoolean(prop.getProperty("ProcessPrivilegedRoleRequest"));
					if (requestEndpoints != null && !requestEndpoints.equals("")) {
						String allowedEndpoints = prop.getProperty("endpointForSnowMyRequest");
						String[] allowedEndpointsList = allowedEndpoints.split("\\s*,\\s*");
						byte b;
						int j;
						String[] arrayOfString1;
						for (j = (arrayOfString1 = allowedEndpointsList).length, b = 0; b < j;) {
							String endpoint = arrayOfString1[b];
							if (requestEndpoints.contains(endpoint.toLowerCase())) {
								process = true;
								shortDesc = "Application Access Request:" + requestEndpoints;
								break;
							}
							b++;
						}
					}
					if (processPR && requesttype.equalsIgnoreCase("Privileged Role Request"))
						process = true;
					if (processER && requesttype.equalsIgnoreCase("Enterprise Role Request"))
						process = true;
					if (process) {
						String requestId = (String) req.get("requestid");
						String requestkey = (String) req.get("reqkey");
						System.out.println("RequestKey: " + requestkey);
						String commentsString = "";

						String url3 = String.valueOf(prop.getProperty("URLPATH"))
								+ "/api/v5/fetchRequestHistoryDetails";

						JSONObject jsonRequest3 = new JSONObject();
						jsonRequest3.put("username", "admin");
						jsonRequest3.put("max", prop.getProperty("MAXREQUESTS"));
						jsonRequest3.put("requestkey", requestkey);
						String urlParams3 = jsonRequest3.toString();
						con2 = httpConn.sendPostHttps(url3, urlParams3, httpConnectionHeaders);
						JSONObject getPendingRequestsResponse3 = httpConn.fetchJsonObject(con2);
						JSONParser parser3 = new JSONParser();
						JSONObject json3 = (JSONObject) parser3.parse(getPendingRequestsResponse3.toString());
						JSONObject requestDetails3 = (JSONObject) json3.get("requestHistoryDetails");

						commentsString = (String) requestDetails3.get("comments");

						if (commentsString == null) {
							commentsString = "";
						} else if (commentsString.contains("CREATED SR")
								&& !commentsString.contains("COMPLETED SERVICE REQUEST")) {
							System.out.println("in comments check");
							String taskstate = "";
							TaskCreate taskc = new TaskCreate();
							String taskComplete = taskc.checkAllTasksComplete(requestId, prop, outhToken);
							if (taskComplete.equals("0"))
								taskstate = "COMPLETE";
							if (taskstate.equalsIgnoreCase("COMPLETE")) {
								System.out.println("in task state complete check");
								ResultSet resultSet2 = null;
								boolean iscomplete = false;
								String requestStatus = "";
								String accesstype = "";
								String state = null;
								System.out.println("///////////Testing for completed requests///////////////////");

								JSONObject Tasks = null;
								String url2 = String.valueOf(prop.getProperty("URLPATH"))
										+ "/api/v5/fetchRequestHistoryDetails";

								JSONObject jsonRequest2 = new JSONObject();
								jsonRequest2.put("username", "admin");
								jsonRequest2.put("status", status);
								jsonRequest2.put("max", prop.getProperty("MAXREQUESTS"));
								jsonRequest2.put("requestkey", requestkey);
								String urlParams2 = jsonRequest2.toString();
								con2 = httpConn.sendPostHttps(url2, urlParams2, httpConnectionHeaders);
								JSONObject getPendingRequestsResponse2 = httpConn.fetchJsonObject(con2);
								JSONParser parser2 = new JSONParser();
								JSONObject json2 = (JSONObject) parser2.parse(getPendingRequestsResponse2.toString());
								JSONObject requestHistoryDetails2 = (JSONObject) json2.get("requestHistoryDetails");
								JSONArray taskDetails = (JSONArray) requestHistoryDetails2.get("Tasks");
								int taskDetailsLength = taskDetails.size();

								for (int k = 0; k < taskDetailsLength; k++) {
									JSONObject taskDetail = (JSONObject) taskDetails.get(k);
									accesstype = (String) taskDetail.get("tasktype");
									requestStatus = (String) taskDetail.get("State");

									System.out.println("The requestkey is " + requestkey);
									System.out.println("requestStatus " + requestStatus);
									System.out.println("accesstype " + accesstype);

									if (accesstype.equalsIgnoreCase("ADD") || accesstype.equalsIgnoreCase("DEL")) {
										if (requestStatus.equalsIgnoreCase("Completed"))
											iscomplete = true;
										if (iscomplete) {
											state = "3";
										} else {
											state = "5";
										}
									} else {
										if (requestStatus.equalsIgnoreCase("Discontinued"))
											state = "5";
										if (requestStatus.equalsIgnoreCase("Completed"))
											state = "3";
									}
									System.out.println("STATE---- " + state);
								}

								System.out.println("/////Code Change////// " + requestStatus);
								System.out.println("STATE---- " + state);

								System.out.println("Closing RITM in SNOW ");
								String response = RC.completeSnowRequest(basicAuth, prop, requestId, state);
								if (response.contains("\"success\":true,\"completed_sr\":true")) {
									RequestApprovalPojo requestApprovalPojo = new RequestApprovalPojo();
									requestApprovalPojo.setMarkingComment("\n COMPLETED SERVICE REQUEST");
									requestApprovalPojo.setRequestKey(requestkey);
									requestApprovalPojo.setArComments("\n COMPLETED SERVICE REQUEST");
									approvalList.add(requestApprovalPojo);
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Exit Fetch Tasks");
		} catch (Exception se) {
			se.printStackTrace();
		}
		return approvalList;
	}

	public ArrayList<RequestApprovalPojo> processDiscontinuedRequest(String endpointForSnowMyRequest, Properties prop,
			String outhToken, String basicAuth, String status) {
		System.out.println("Enter Get Discontinued requests from DB");
		ResultSet resultSet = null;
		ArrayList<RequestApprovalPojo> approvalList = new ArrayList<>();
		try {
			RequestComplete RC = new RequestComplete();
			try {
				String url = String.valueOf(prop.getProperty("URLPATH")) + "/api/v5/fetchRequestHistory";
				HttpUrlConn httpConn = new HttpUrlConn();
				HttpURLConnection con2 = null;
				JSONObject getPendingRequestsResponse = null;
				JSONObject jsonRequest = new JSONObject();
				jsonRequest.put("username", "admin");
				jsonRequest.put("status", status);
				jsonRequest.put("max", prop.getProperty("MAXREQUESTS"));
				Map<String, String> httpConnectionHeaders = new HashMap<>();
				httpConnectionHeaders.put("Authorization", "Bearer " + outhToken);
				httpConnectionHeaders.put("Content-Type", "application/json");
				httpConnectionHeaders.put("SAVUSERNAME", prop.getProperty("SAVUSERNAME"));
				String urlParams = jsonRequest.toString();
				System.out.println("Sending 'POST' request to URL : " + url);
				con2 = httpConn.sendPostHttps(url, urlParams, httpConnectionHeaders);
				getPendingRequestsResponse = httpConn.fetchJsonObject(con2);
				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(getPendingRequestsResponse.toString());
				Long totalRequests = (Long) json.get("count");
				JSONArray requestDetails = (JSONArray) json.get("requests");
				System.out.println("total Discontinued requests " + totalRequests);
				for (int i = 0; i < totalRequests.longValue(); i++) {
					JSONObject req = (JSONObject) requestDetails.get(i);
					String requestEndpoints = (String) req.get("endpoints");
					String requesttype = (String) req.get("requesttype");

					String requestsubmittedon = (String) req.get("requestsubmittedon");

					int requestDays = Integer.parseInt(prop.getProperty("REQUESTDAYS"));
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date reqDate = simpleDateFormat.parse(requestsubmittedon);
					Calendar calendar = Calendar.getInstance();
					calendar.add(Calendar.DATE, -requestDays);
					if (calendar.getTime().after(reqDate)) {
						continue;
					}

					boolean process = false;
					boolean processER = Boolean.parseBoolean(prop.getProperty("ProcessEnterpriseRoleRequest"));
					boolean processPR = Boolean.parseBoolean(prop.getProperty("ProcessPrivilegedRoleRequest"));
					if (requestEndpoints != null && !requestEndpoints.equals("")) {
						String allowedEndpoints = prop.getProperty("endpointForSnowMyRequest");
						String[] allowedEndpointsList = allowedEndpoints.split("\\s*,\\s*");
						byte b;
						int j;
						String[] arrayOfString1;
						for (j = (arrayOfString1 = allowedEndpointsList).length, b = 0; b < j;) {
							String endpoint = arrayOfString1[b];
							if (requestEndpoints.contains(endpoint)) {
								process = true;
								break;
							}
							b++;
						}
					}
					if (processPR && requesttype.equalsIgnoreCase("Privileged Role Request"))
						process = true;
					if (processER && requesttype.equalsIgnoreCase("Enterprise Role Request"))
						process = true;
					if (process) {
						String requestId = (String) req.get("requestid");
						String requestkey = (String) req.get("reqkey");
						String commentsString = "";

						String url3 = String.valueOf(prop.getProperty("URLPATH"))
								+ "/api/v5/fetchRequestHistoryDetails";

						JSONObject jsonRequest3 = new JSONObject();
						jsonRequest3.put("username", "admin");
						jsonRequest3.put("status", status);
						jsonRequest3.put("max", prop.getProperty("MAXREQUESTS"));
						jsonRequest3.put("requestkey", requestkey);
						String urlParams3 = jsonRequest3.toString();
						con2 = httpConn.sendPostHttps(url3, urlParams3, httpConnectionHeaders);
						JSONObject getPendingRequestsResponse3 = httpConn.fetchJsonObject(con2);
						JSONParser parser3 = new JSONParser();
						JSONObject json3 = (JSONObject) parser3.parse(getPendingRequestsResponse3.toString());
						JSONObject requestDetails3 = (JSONObject) json3.get("requestHistoryDetails");

						commentsString = (String) requestDetails3.get("comments");

						if (commentsString == null) {
							commentsString = "";
						} else if (commentsString.contains("CREATED SR")
								&& !commentsString.contains("COMPLETED SERVICE REQUEST")) {
							System.out.println("The requestkey is " + requestkey);
							String response = RC.completeSnowRequest(basicAuth, prop, requestId, "4");
							if (response.contains("\"success\":true,\"completed_sr\":true")) {
								RequestApprovalPojo requestApprovalPojo = new RequestApprovalPojo();
								requestApprovalPojo.setMarkingComment("\n COMPLETED SERVICE REQUEST");
								requestApprovalPojo.setRequestKey(requestkey);
								requestApprovalPojo.setArComments("\n COMPLETED SERVICE REQUEST");
								approvalList.add(requestApprovalPojo);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception se) {
			se.printStackTrace();
		}
		return approvalList;
	}

	public ArrayList<RequestApprovalPojo> processExpiredRequest(String endpointForSnowMyRequest, Properties prop,
			String outhToken, String basicAuth, String status) {
		System.out.println("Enter Get Expired requests from DB");
		ResultSet resultSet = null;
		ArrayList<RequestApprovalPojo> approvalList = new ArrayList<>();
		try {
			RequestComplete RC = new RequestComplete();
			try {
				String url = String.valueOf(prop.getProperty("URLPATH")) + "/api/v5/fetchRequestHistory";
				HttpUrlConn httpConn = new HttpUrlConn();
				HttpURLConnection con2 = null;
				JSONObject getPendingRequestsResponse = null;
				JSONObject jsonRequest = new JSONObject();
				jsonRequest.put("username", "admin");
				jsonRequest.put("status", status);
				jsonRequest.put("max", prop.getProperty("MAXREQUESTS"));
				Map<String, String> httpConnectionHeaders = new HashMap<>();
				httpConnectionHeaders.put("Authorization", "Bearer " + outhToken);
				httpConnectionHeaders.put("Content-Type", "application/json");
				httpConnectionHeaders.put("SAVUSERNAME", prop.getProperty("SAVUSERNAME"));
				String urlParams = jsonRequest.toString();
				System.out.println("Sending 'POST' request to URL : " + url);
				con2 = httpConn.sendPostHttps(url, urlParams, httpConnectionHeaders);
				getPendingRequestsResponse = httpConn.fetchJsonObject(con2);
				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(getPendingRequestsResponse.toString());
				Long totalRequests = (Long) json.get("count");
				JSONArray requestDetails = (JSONArray) json.get("requests");
				System.out.println("total Expired requests " + totalRequests);
				for (int i = 0; i < totalRequests.longValue(); i++) {
					JSONObject req = (JSONObject) requestDetails.get(i);
					String requestEndpoints = (String) req.get("endpoints");
					String requesttype = (String) req.get("requesttype");

					String requestsubmittedon = (String) req.get("requestsubmittedon");

					int requestDays = Integer.parseInt(prop.getProperty("REQUESTDAYS"));
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date reqDate = simpleDateFormat.parse(requestsubmittedon);
					Calendar calendar = Calendar.getInstance();
					calendar.add(Calendar.DATE, -requestDays);
					if (calendar.getTime().after(reqDate)) {
						continue;
					}

					boolean process = false;
					boolean processER = Boolean.parseBoolean(prop.getProperty("ProcessEnterpriseRoleRequest"));
					boolean processPR = Boolean.parseBoolean(prop.getProperty("ProcessPrivilegedRoleRequest"));
					if (requestEndpoints != null && !requestEndpoints.equals("")) {
						String allowedEndpoints = prop.getProperty("endpointForSnowMyRequest");
						String[] allowedEndpointsList = allowedEndpoints.split("\\s*,\\s*");
						byte b;
						int j;
						String[] arrayOfString1;
						for (j = (arrayOfString1 = allowedEndpointsList).length, b = 0; b < j;) {
							String endpoint = arrayOfString1[b];
							if (requestEndpoints.contains(endpoint)) {
								process = true;
								break;
							}
							b++;
						}
					}
					if (processPR && requesttype.equalsIgnoreCase("Privileged Role Request"))
						process = true;
					if (processER && requesttype.equalsIgnoreCase("Enterprise Role Request"))
						process = true;
					if (process) {
						String requestId = (String) req.get("requestid");
						String requestkey = (String) req.get("reqkey");
						String commentsString = "";

						String url3 = String.valueOf(prop.getProperty("URLPATH"))
								+ "/api/v5/fetchRequestHistoryDetails";

						JSONObject jsonRequest3 = new JSONObject();
						jsonRequest3.put("username", "admin");
						jsonRequest3.put("status", status);
						jsonRequest3.put("max", prop.getProperty("MAXREQUESTS"));
						jsonRequest3.put("requestkey", requestkey);
						String urlParams3 = jsonRequest3.toString();
						con2 = httpConn.sendPostHttps(url3, urlParams3, httpConnectionHeaders);
						JSONObject getPendingRequestsResponse3 = httpConn.fetchJsonObject(con2);
						JSONParser parser3 = new JSONParser();
						JSONObject json3 = (JSONObject) parser3.parse(getPendingRequestsResponse3.toString());
						JSONObject requestDetails3 = (JSONObject) json3.get("requestHistoryDetails");

						commentsString = (String) requestDetails3.get("comments");

						if (commentsString == null) {
							commentsString = "";
						} else if (commentsString.contains("CREATED SR")
								&& !commentsString.contains("COMPLETED SERVICE REQUEST")) {
							System.out.println("The requestkey is " + requestkey);
							String response = RC.completeSnowRequest(basicAuth, prop, requestId, "4");
							if (response.contains("\"success\":true,\"completed_sr\":true")) {
								RequestApprovalPojo requestApprovalPojo = new RequestApprovalPojo();
								requestApprovalPojo.setMarkingComment("\n COMPLETED SERVICE REQUEST");
								requestApprovalPojo.setRequestKey(requestkey);
								requestApprovalPojo.setArComments("\n COMPLETED SERVICE REQUEST");
								approvalList.add(requestApprovalPojo);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception se) {
			se.printStackTrace();
		}
		return approvalList;
	}

	public ArrayList<RequestApprovalPojo> processPendingTask(String endpointForManualFulfillment, Properties prop,
			String outhToken, String basicAuth, String status) {
		System.out.println("Enter processPendingTask Get new tasks from SSM");
		ResultSet resultSet = null;
		String taskID = "";
		ArrayList<RequestApprovalPojo> approvalList = new ArrayList<>();
		try {
			ManualFulfilmentSR requestCreate = new ManualFulfilmentSR();
			try {
				int offset = 0;
				int max = 50;
				int totalTasks = 500;

				while (offset < totalTasks) {
					String url = String.valueOf(prop.getProperty("URLPATH")) + "/api/v5/fetchTasks";
					HttpUrlConn httpConn = new HttpUrlConn();
					HttpURLConnection con2 = null;
					JSONObject getPendingRequestsResponse = null;
					JSONObject jsonRequest = new JSONObject();
					// jsonRequest.put("TASKNAME", "PENDING"); -- removed as part of remediation as
					// taskname is not a valid variable
					jsonRequest.put("TASKSTATUS", "PENDING");
					// update max and offset. set pagination
					jsonRequest.put("max", max);
					jsonRequest.put("offset", offset);
					Map<String, String> httpConnectionHeaders = new HashMap<>();
					httpConnectionHeaders.put("Authorization", "Bearer " + outhToken);
					httpConnectionHeaders.put("Content-Type", "application/json");
					httpConnectionHeaders.put("SAVUSERNAME", prop.getProperty("SAVUSERNAME"));
					String urlParams = jsonRequest.toString();
					System.out.println("Sending 'POST' request to URL : " + url);
					con2 = httpConn.sendPostHttps(url, urlParams, httpConnectionHeaders);
					getPendingRequestsResponse = httpConn.fetchJsonObject(con2);
					JSONParser parser = new JSONParser();
					JSONObject json = (JSONObject) parser.parse(getPendingRequestsResponse.toString());

					String tasks = (String) json.get("totalrecords");
					Long totalRequests = Long.valueOf(Long.parseLong(tasks));
					JSONArray requestDetails = (JSONArray) json.get("tasks");
					System.out.println("total pending tasks " + totalRequests);
					for (int i = 0; i < totalRequests.longValue(); i++) {
						JSONObject req = (JSONObject) requestDetails.get(i);
						System.out.println("got req : " + req.toString());
						String requestEndpoints = (String) req.get("ENDPOINT");
						String requesttype = (String) req.get("TASKTYPE");
						Long requestkey = null;
						if (req.get("REQUESTKEY") != null && !req.get("REQUESTKEY").toString().isEmpty())
							requestkey = (Long) req.get("REQUESTKEY");
						String shortDesc = "";
						String desc = "";
						String applications = "";
						boolean process = false;
						if (requestkey != null) {
							System.out.println("requestkey" + requestkey);
							System.out.println("requestEndpoints : " + requestEndpoints);
							if (requestEndpoints != null)
								if (!requestEndpoints.equals("")) {
									String allowedEndpoints = prop.getProperty("endpointForManualFulfilment");
									String[] allowedEndpointsList = allowedEndpoints.split("\\s*,\\s*");
									byte b;
									int j;
									String[] arrayOfString1;
									for (j = (arrayOfString1 = allowedEndpointsList).length, b = 0; b < j;) {
										String endpoint = arrayOfString1[b];
										if (requestEndpoints.contains(endpoint)) {
											process = true;
											applications = requestEndpoints;
											break;
										}
										b++;
									}
								}
							if (process) {
								System.out.println("enter process check");
								String requestId = String.valueOf((Long) req.get("REQUESTKEY"));
								String taskkey = (String) req.get("TASKID");
								taskID = taskkey;
								String entitlement = (String) req.get("ENTITLEMENT_VALUEKEY");
								String username = (String) req.get("USER");
								String requestedForName = "";

								String url3 = String.valueOf(prop.getProperty("URLPATH")) + "api/v5/getUser";
								System.out.println("getting user : " + username);
								
								JSONObject jsonRequest3 = new JSONObject();
								jsonRequest3.put("username", username);
								String urlParams3 = jsonRequest3.toString();
								con2 = httpConn.sendPostHttps(url3, urlParams3, httpConnectionHeaders);
								JSONObject getPendingRequestsResponse3 = httpConn.fetchJsonObject(con2);
								JSONParser parser3 = new JSONParser();
								JSONObject json3 = (JSONObject) parser3.parse(getPendingRequestsResponse3.toString());
								JSONObject requestDetails3 = (JSONObject) json3.get("userdetails");

								try {
									String requestedForFirstName = (String) requestDetails3.get("firstname");
									;
									String requestedForLastName = (String) requestDetails3.get("lastname");
									;
									requestedForName = String.valueOf(requestedForFirstName) + " "
											+ requestedForLastName + "(" + username + ")";
								} catch (Exception exception) {
								}
								System.out.println("requesttype : " + requesttype);
								if (requesttype.equalsIgnoreCase("New Account")) {
									shortDesc = "New Account Request for " + requestedForName;
									desc = "Request has been raised for " + requestedForName
											+ " for granting access to the following systems: " + applications;
								}
								if (requesttype.equalsIgnoreCase("Modify Account")) {
									shortDesc = "Modify Account Request for " + requestedForName;
									desc = "Request has been raised for " + requestedForName
											+ " for modifying access to the following systems: " + applications;
								}
								if (requesttype.equalsIgnoreCase("Enable Account")) {
									shortDesc = "Enable Account Request for " + requestedForName;
									desc = "Request has been raised for " + requestedForName
											+ " for modifying access to the following systems: " + applications;
								}
								if (requesttype.equalsIgnoreCase("Disable Account")) {
									shortDesc = "Disable Account Request for " + requestedForName;
									desc = "Request has been raised for " + requestedForName
											+ " for modifying access to the following systems: " + applications;
								}
								if (requesttype.equalsIgnoreCase("Remove Account")) {
									shortDesc = "Remove Account Request for " + requestedForName;
									desc = "Request has been raised for " + requestedForName
											+ " for modifying access to the following systems: " + applications;
								}
								if (requesttype.equalsIgnoreCase("Revoke Access")) {
									shortDesc = "Revoke Access Request for " + requestedForName;
									desc = "Request has been raised for " + requestedForName
											+ " for revoking access to the following systems: " + applications;
								}
								if (requesttype.equalsIgnoreCase("Grant Access")) {
									shortDesc = "Grant Access Request for " + requestedForName;
									desc = "Request has been raised for " + requestedForName
											+ " for granting access to the following systems: " + applications;
								}
								String commentsString = "";

								System.out.println("fetching requesthistorydetails");
								String url2 = String.valueOf(prop.getProperty("URLPATH"))
										+ "/api/v5/fetchRequestHistoryDetails";

								JSONObject jsonRequest2 = new JSONObject();
								jsonRequest2.put("username", "admin");
								jsonRequest2.put("status", status);
								jsonRequest2.put("max", prop.getProperty("MAXREQUESTS"));
								jsonRequest2.put("requestkey", requestkey);
								String urlParams2 = jsonRequest2.toString();
								con2 = httpConn.sendPostHttps(url2, urlParams2, httpConnectionHeaders);
								JSONObject getPendingRequestsResponse2 = httpConn.fetchJsonObject(con2);
								JSONParser parser2 = new JSONParser();
								JSONObject json2 = (JSONObject) parser2.parse(getPendingRequestsResponse2.toString());
								JSONObject requestDetails2 = (JSONObject) json2.get("requestHistoryDetails");

								commentsString = (String) requestDetails2.get("comments");
								System.out.println("commentsString : " + commentsString);
								
								if (commentsString == null)
									commentsString = "";
								if (!commentsString.contains("CREATED MANUAL FULFILMENT SR")
										&& !commentsString.contains("provisioned from servicenow")) {
									System.out.println("Shortdesc   - " + shortDesc);
									System.out.println("desc   - " + desc);
									System.out.println("requestId   - " + requestId);
									System.out.println("applications   - " + applications);
									String returnData = requestCreate.createSnowRequest(basicAuth, prop, requestId,
											shortDesc, desc, applications);
									String sys_id = "";
									String setCommentString = "";
									if (returnData.contains("\"success\":true")) {
										if (returnData.contains("\"manual_task\":true")) {
											String[] splitString = returnData.split("\"ritm_number\"");
											String[] ritmString = splitString[1].split("\"");
											String ritmNumber = ritmString[1];
											splitString = returnData.split("\"ritm_sys_id\"");
											String[] sysidString = splitString[1].split("\"");
											sys_id = sysidString[1];
											splitString = returnData.split("\"sent_timestamp\"");
											String[] timeString = splitString[1].split("\"");
											String timeStamp = timeString[1];
											splitString = returnData.split("\"ssm_entity_id\"");
											String[] requestString = splitString[1].split("\"");
											String requestNumber = requestString[1];
											setCommentString = "CREATED MANUAL FULFILMENT SR:::: Request ID: "
													+ requestNumber + "; RITM Number: " + ritmNumber
													+ "; Request Creation TimeStamp: " + timeStamp + "; \n";
											RequestApprovalPojo requestApprovalPojo = new RequestApprovalPojo();
											requestApprovalPojo.setMarkingComment(setCommentString);
											requestApprovalPojo.setRequestKey(requestId);
											requestApprovalPojo.setSnowSysId(sys_id);
											requestApprovalPojo.setArComments(setCommentString);
											approvalList.add(requestApprovalPojo);
											String returnMessage = completeTask(prop, outhToken, taskID);
											if (!returnMessage.equalsIgnoreCase("Task Completed"))
												returnMessage = completeTask(prop, outhToken, taskID);
											System.out.println("Message: " + returnMessage);
										}
									} else {
										System.out.println("Unable to create in SNOW for requestId : " + requestId);
									}
								}
							}
						}
					}
					// totalrecords is offset; totaltasks is total results; 
					// max is no of records pulled in one request
					System.out.println("setting offset and max for next iteration");
					offset = offset + Integer.parseInt(json.get("totalrecords").toString());
					System.out.println("offset " + offset);
					totalTasks = Integer.parseInt(json.get("totaltasks").toString());
					System.out.println("totalTasks " + totalTasks);
					System.out.println("max " + max);
				}
				System.out.printf("Exit fetchTasks while loop: max %d, offset %d, totalTasks %d %n", max, offset, totalTasks);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Exit Fetch Tasks");
		} catch (Exception se) {
			se.printStackTrace();
		}
		return approvalList;
	}

	public String completeTask(Properties prop, String outhToken, String taskID) {
		String returnMessage = "";
		String urlCT = String.valueOf(prop.getProperty("URLPATH")) + "api/v5/completetask";
		HttpUrlConn httpConn1 = new HttpUrlConn();
		HttpURLConnection conCT2 = null;
		JSONObject getPendingRequestsResponseCT = null;
		JSONObject jsonRequestCT = new JSONObject();
		jsonRequestCT.put("taskid", taskID);
		Map<String, String> httpConnectionHeadersCT = new HashMap<>();
		httpConnectionHeadersCT.put("Authorization", "Bearer " + outhToken);
		httpConnectionHeadersCT.put("Content-Type", "application/json");
		httpConnectionHeadersCT.put("SAVUSERNAME", prop.getProperty("SAVUSERNAME"));
		String urlParamsCT = jsonRequestCT.toString();
		System.out.println("Sending 'POST' request to URL : " + urlCT);
		try {
			conCT2 = httpConn1.sendPostHttps(urlCT, urlParamsCT, httpConnectionHeadersCT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		getPendingRequestsResponseCT = httpConn1.fetchJsonObject(conCT2);
		System.out.println("Task completion  Response : " + getPendingRequestsResponseCT.toString());
		String returnData = getPendingRequestsResponseCT.toString();
		if (returnData.contains("Taskid" + taskID + "=true")) {
			System.out.println("task completed");
			returnMessage = "Task Completed";
		}
		return returnMessage;
	}

	public void markNewRequests(Properties prop, String outhToken, ArrayList<RequestApprovalPojo> approvalList) {
		try {
			System.out.println("entered markNewRequests");
			for (RequestApprovalPojo requestApprovalPojo : approvalList) {
				try {
					System.out.println("requestApprovalPojo : ");
					System.out.println(requestApprovalPojo);

					String urlUR = String.valueOf(prop.getProperty("URLPATH")) + "/api/v5/updateRequest";
					HttpUrlConn httpConn1 = new HttpUrlConn();
					HttpURLConnection conUR = null;
					JSONObject getPendingRequestsResponseUR = null;
					JSONObject jsonRequestUR = new JSONObject();
					jsonRequestUR.put("requestor", prop.getProperty("SAVUSERNAME"));
					jsonRequestUR.put("requestkey", requestApprovalPojo.getRequestKey());
					jsonRequestUR.put("comments", requestApprovalPojo.getArComments());
					jsonRequestUR.put("addcomments", "true");
					jsonRequestUR.put("updateapprover", prop.getProperty("SAVUSERNAME"));
					jsonRequestUR.put("allowreassign", "true");
					System.out.println(jsonRequestUR.toString());

					Map<String, String> httpConnectionHeadersUR = new HashMap<>();
					httpConnectionHeadersUR.put("Authorization", "Bearer " + outhToken);
					httpConnectionHeadersUR.put("Content-Type", "application/json");
					httpConnectionHeadersUR.put("SAVUSERNAME", prop.getProperty("SAVUSERNAME"));
					System.out.println(httpConnectionHeadersUR);

					String urlParamsCT = jsonRequestUR.toString();
					System.out.println("Sending 'POST' request to URL : " + urlUR);
					try {
						System.out.println("calling post");
						conUR = httpConn1.sendPostHttps(urlUR, urlParamsCT, httpConnectionHeadersUR);
					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.println("getting pending request response");
					getPendingRequestsResponseUR = httpConn1.fetchJsonObject(conUR);
					System.out.println(getPendingRequestsResponseUR.toString());

				} catch (Exception ex) {
					System.out.println("Update failed");
					ex.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void markCompletedTask1(DatabaseConnector db, Statement dbStmt, ArrayList<TaskPojo> taskList,
			Properties prop) {
		try {
			for (TaskPojo taskPojo : taskList) {
				try {
					String provComment = taskPojo.getProvMetadata();
					if (provComment != null && provComment.trim().length() > 0) {
						provComment = provComment.replace("null", "");
						provComment = provComment.replace("'", "\\'");
					}
					String query = "update arstasks set provisioningcomments='" + provComment + "' where taskkey="
							+ taskPojo.getTaskKey();
					System.out.println("Update task query - " + query);
					db.updateTable(dbStmt, query);
				} catch (Exception ex) {
					System.out.println("Update failed");
//					logger.info("Update failed");
					ex.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
