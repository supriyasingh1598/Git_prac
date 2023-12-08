package com.Integration.Test;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.TrustManager;

public class HttpUrlConn {
	// static final Logger logger =
	// Logger.getLogger(SapCustomRoleRemove.class.getName());

	protected final String USER_AGENT = "Mozilla/5.0";

	public void doTrustToCertificates() throws Exception {
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
				return;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
				return;
			}
		} };
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				if (!urlHostName.equalsIgnoreCase(session.getPeerHost())) {
					System.out.println("Warning: URL host '" + urlHostName + "' is different to SSLSession host '"
							+ session.getPeerHost() + "'.");
				}
				return true;
			}
		};
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
	}

	// HTTP GET request
	@SuppressWarnings("finally")
	protected String sendGet(String url, Map<String, String> headers) throws Exception {
		doTrustToCertificates();//
		StringBuffer response = new StringBuffer();
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		try {
			// String url = "";

			// optional default is GET
			con.setRequestMethod("GET");

			// add request header
			con.setRequestProperty("User-Agent", USER_AGENT);
			if (headers != null && headers.size() > 0) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					con.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			if (responseCode == 200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				System.out.println(response.toString());
			} else {
				System.out.println("Error: Response code obtained is " + responseCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return response.toString();
		}
	}

	// HTTP POST request
	@SuppressWarnings("finally")
	protected HttpURLConnection sendPostHttp(String url, String urlParameters, Map<String, String> headers)
			throws Exception {
		DataOutputStream wr = null;
		HttpURLConnection con = null;
		try {
			URL obj = new URL(url);
			System.out.println("Not using certificate");
			con = (HttpURLConnection) obj.openConnection();
			// add request header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			if (headers != null && headers.size() > 0) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					con.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}

			// String urlParameters = "ccn=&locale=&caller=&num=12345";

			// Send post request
			con.setDoOutput(true);
			System.out.println("Lam Plm HttpUrlConn: Calling post for the url.");
			wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);

			int responseCode = con.getResponseCode();
			System.out.println("Lam Plm HttpUrlConn: Response Code : " + responseCode);

		} catch (Exception e) {
			System.out.println("Exception is: " + e.getMessage());
		} finally {
			try {
				if (wr != null) {
					wr.flush();
					wr.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println(ex.getMessage());
			}
			return con;
		}
	}

	protected HttpsURLConnection sendPostHttps(String url, String urlParameters, Map<String, String> headers)
			throws Exception {
		System.out.println("Send POST message..");
		doTrustToCertificates();
		DataOutputStream wr = null;
		// HttpsURLConnection con = null;
		HttpsURLConnection con = null;

		try {
			URL obj = new URL(url);
			System.out.println("Calling url : " + url);
			// System.out.println("Arguments : "+urlParameters);

			con = (HttpsURLConnection) obj.openConnection();
			// con = (HttpURLConnection) obj.openConnection();
			// add request header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			con.setRequestProperty("Accept", "application/json");

			if (headers != null && headers.size() > 0) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					con.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}

//String urlParameters = "ccn=&locale=&caller=&num=12345";

// Send post request
			con.setDoOutput(true);
//logger.info("Lam Plm HttpUrlConn: Calling post for the url.");
			wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);

			int responseCode = con.getResponseCode();
//System.out.println("Response Code : " + responseCode);
//logger.info("Lam Plm HttpUrlConn: Response code =" + responseCode);

		} catch (Exception e) {
			System.out.println("Exception is " + e.getMessage());
		} finally {
			try {
				if (wr != null) {
					wr.flush();
					wr.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return con;
		}
	}

	protected int fetchResponseCode(HttpURLConnection con) {
		int responseCode = 0;
		try {
			responseCode = con.getResponseCode();
			System.out.println("Response Code : " + responseCode);
		} catch (IOException e) {
			System.out.println("Lam Plm HttpUrlConn: " + e.toString());
		} finally {
			return responseCode;
		}
	}

	protected StringBuffer fetchResponseString(HttpURLConnection con) {
		int responseCode = 0;
		BufferedReader in = null;
		StringBuffer response = null;
		try {
			responseCode = con.getResponseCode();
			if (responseCode == 200) {
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;

				response = new StringBuffer();

				System.out.println("Response string is " + response);
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}

				System.out.println("Lam Plm HttpUrlConn: Got response");
			} else {
				System.out.println("Lam Plm HttpUrlConn: Cannot reach the url or bad arguments, response code is " + responseCode);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Lam Plm HttpUrlConn: Could not get response String");
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Lam Plm HttpUrlConn: " + ex.toString());
			}
			return response;
		}
	}

	protected JSONArray fetchJsonArray(HttpURLConnection con) {
		int responseCode = 0;
		BufferedReader in = null;
		try {
			responseCode = con.getResponseCode();
			if (responseCode == 200) {
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;

				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				JSONArray jsonArray = parseJsonResponseArray(response.toString());
				System.out.println("Lam Plm HttpUrlConn: Got Json response");
				return jsonArray;
			} else {
				System.out.println("Lam Plm HttpUrlConn: Cannot reach the url or bad arguments, response code is " + responseCode);
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Lam Plm HttpUrlConn: Could not parse json object");
			return null;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Lam Plm HttpUrlConn: " + ex.toString());
			}
		}
	}

	protected JSONObject fetchJsonObject(HttpURLConnection con) {
		int responseCode = 0;
		BufferedReader in = null;
		try {
			responseCode = con.getResponseCode();
			if (responseCode == 200) {
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;

				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				JSONObject jsonObject = parseJsonResponse(response.toString());
				System.out.println("Lam Plm HttpUrlConn: Got Json response");
				return jsonObject;
			} else {
				System.out.println("Lam Plm HttpUrlConn: Cannot reach the url or bad arguments, response code is " + responseCode);
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Lam Plm HttpUrlConn: Could not parse json object");
			return null;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Lam Plm HttpUrlConn: " + ex.toString());
			}
		}
	}

	protected int fetchResponseCode2(HttpsURLConnection con) {
		int responseCode = 0;
		try {
			responseCode = con.getResponseCode();
			System.out.println("Response Code : " + responseCode);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Lam Plm HttpUrlConn: " + e.toString());
		} finally {
			return responseCode;
		}
	}

	protected StringBuffer fetchResponseString2(HttpsURLConnection con) {
		int responseCode = 0;
		BufferedReader in = null;
		StringBuffer response = null;
		try {
			responseCode = con.getResponseCode();
			if (responseCode == 200) {
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;

				response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				System.out.println("Lam Plm HttpUrlConn: Got response " + response);
			} else {
				System.out.println("Lam Plm HttpUrlConn: Cannot reach the url or bad arguments, response code is " + responseCode);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Lam Plm HttpUrlConn: Could not parse json object");
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Lam Plm HttpUrlConn: " + ex.toString());
			}
			return response;
		}
	}

	protected JSONObject fetchJsonObject2(HttpsURLConnection con) {
		int responseCode = 0;
		BufferedReader in = null;
		try {
			responseCode = con.getResponseCode();
			if (responseCode == 200) {
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;

				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				JSONObject jsonObject = parseJsonResponse(response.toString());
				System.out.println("Lam Plm HttpUrlConn: Got Json response");
				return jsonObject;
			} else {
				System.out.println("Lam Plm HttpUrlConn: Cannot reach the url or bad arguments, response code is " + responseCode);
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Lam Plm HttpUrlConn: Could not parse json object");
			return null;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Lam Plm HttpUrlConn: " + ex.toString());
			}
		}
	}

	protected JSONArray parseJsonResponseArray(String JSONString) {
		JSONParser parser = new JSONParser();
		try {
			JSONArray jsonArray = null;
			if (JSONString != null && JSONString.trim().length() > 0) {
				jsonArray = (JSONArray) parser.parse(JSONString);
			} else {
				System.out.println("Lam Plm HttpUrlConn: Json response is - " + JSONString);
			}
			return jsonArray;
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("Lam Plm HttpUrlConn: " + e.toString());
			return null;
		}
	}

	protected JSONObject parseJsonResponse(String JSONString) {
		JSONParser parser = new JSONParser();
		try {
			JSONObject jsonObject = null;
			if (JSONString != null && JSONString.trim().length() > 0) {
				jsonObject = (JSONObject) parser.parse(JSONString);
			} else {
				System.out.println("Lam Plm HttpUrlConn: Json response is - " + JSONString);
			}
			return jsonObject;
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("Lam Plm HttpUrlConn: " + e.toString());
			return null;
		}
	}

	protected String getCallBarrier(String getUrl, String barrier) throws IOException {
		URL url = new URL(getUrl);
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setRequestProperty("Authorization", "Bearer " + barrier);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestMethod("GET");
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuffer response = new StringBuffer();
		String output;
		while ((output = in.readLine()) != null)
			response.append(output);
		in.close();
		System.out.println("Response:-" + response.toString());
		return response.toString();
	}

	protected HttpsURLConnection sendPutBarrier(String url, String urlParameters, Map<String, String> headers,
			String barrier) throws Exception {
		System.out.println(url);
		DataOutputStream wr = null;
		HttpsURLConnection con = null;
		try {
			try {
				URL obj = new URL(url);
				con = (HttpsURLConnection) obj.openConnection();
				con.setRequestMethod("PUT");
				con.setRequestProperty("User-Agent", "Mozilla/5.0");
				con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
				con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				con.setRequestProperty("Accept", "application/json");
				con.setRequestProperty("Authorization", "Bearer " + barrier);
				if (headers != null && headers.size() > 0)
					for (Map.Entry<String, String> entry : headers.entrySet())
						con.setRequestProperty(entry.getKey(), entry.getValue());
				con.setDoOutput(true);
				wr = new DataOutputStream(con.getOutputStream());
				wr.writeBytes(urlParameters);
				int i = con.getResponseCode();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Exception is " + e.getMessage());
			}
		} catch (Throwable throwable) {
		}
		try {
			if (wr != null) {
				wr.flush();
				wr.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex.getMessage());
		}
		return con;
	}
}
