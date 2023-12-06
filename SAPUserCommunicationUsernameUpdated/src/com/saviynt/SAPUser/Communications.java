package com.saviynt.SAPUser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import org.json.JSONArray;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONObject;
import com.saviynt.SAPUser.Communications;
import org.apache.commons.codec.binary.Base64;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DataProviderException;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;

public class Communications
{
	static HashMap<String,String> map;
	static {
		try{
			 BufferedReader br = new BufferedReader(new FileReader("/saviynt_shared/saviynt/Import/Datafiles/UPNUsernameWriteback.csv"));
			 //BufferedReader br = new BufferedReader(new FileReader("D:\\\\UPNUsernameWriteback.csv"));
			 String line =null;
			 map = new HashMap<>();

			 while((line=br.readLine())!=null){
			 String str[] = line.split(",");
			 for(int i=0;i<str.length;i++){
			 String arr[] = str[i].split("~");
			 map.put(arr[0], arr[1]);
			 }}
			 }
			 catch(Exception e){
			 System.out.println(e);
			 }
		}
  //public static Properties prop = new Properties();
  
  static class MyDestinationDataProvider
    implements DestinationDataProvider
  {
    private DestinationDataEventListener eL;
    private HashMap<String, Properties> secureDBStorage = new HashMap<String, Properties>();
    
    public Properties getDestinationProperties(String destinationName)
    {
      try
      {
        //Properties p = (Properties)this.secureDBStorage.get(destinationName);
    	  Properties p = secureDBStorage.get(destinationName);
        if (p != null)
        {
          if (p.isEmpty())
          {
            System.out.println("Lam Plm JcoConnector: DataProviderException - destination configured is incorrect");
            
            throw new DataProviderException(DataProviderException.Reason.INVALID_CONFIGURATION, "destination configuration is incorrect", null);
          }
          return p;
        }
        return null;
      }
      catch (RuntimeException re)
      {
        System.out.println("Lam Plm JcoConnector: DataProviderException");
        System.out.println(re);
        throw new DataProviderException(DataProviderException.Reason.INTERNAL_ERROR, re);
      }
      catch (Exception e)
      {
        System.out.println("Lam Plm JcoConnector:");
        System.out.println(e);
      }
      return null;
    }
    
    public void setDestinationDataEventListener(DestinationDataEventListener eventListener)
    {
      try
      {
        this.eL = eventListener;
      }
      catch (Exception e)
      {
        System.out.println("Lam Plm JcoConnector: Event listener");
       
        System.out.println(e);
        
      }
    }
    
    public boolean supportsEvents()
    {
      return true;
    }
    
    void changeProperties(String destName, Properties properties)
    {
      try
      {
        synchronized (this.secureDBStorage)
        {
          if (properties == null)
          {
            if (this.secureDBStorage.remove(destName) != null) {
              this.eL.deleted(destName);
            }
          }
          else
          {
            this.secureDBStorage.put(destName, properties);
            this.eL.updated(destName);
          }
        }
      }
      catch (Exception e)
      {
        System.out.println("Lam Plm JcoConnector: " + e.toString());
        System.out.println(e);
        e.printStackTrace();
      }
    }
  }
  
  public static JCoDestination initializeConnectiontoSAP(MyDestinationDataProvider myProvider)
  {
    Properties connectProperties = new Properties();
    JCoDestination ABAP_AS = null;
    try
    {
    	
    	//Added logic for MESSAGESERVER server Start
        //String msgServer =prop.getProperty("MESSAGESERVER");
        String msgServer =map.get("MESSAGESERVER");
        System.out.println("initializeConnectiontoSAP :: MESSAGESERVER::"+msgServer);
        
        if(null!=msgServer && !msgServer.isEmpty() && msgServer.equalsIgnoreCase("true")) {
        	//connectProperties.setProperty(DestinationDataProvider.JCO_R3NAME, prop.getProperty("JCO_R3NAME"));
            //connectProperties.setProperty(DestinationDataProvider.JCO_MSHOST, prop.getProperty("JCO_MSHOST"));
            //connectProperties.setProperty(DestinationDataProvider.JCO_MSSERV, prop.getProperty("JCO_MSSERV"));
            //connectProperties.setProperty(DestinationDataProvider.JCO_GROUP,  prop.getProperty("JCO_GROUP"));
            connectProperties.setProperty(DestinationDataProvider.JCO_R3NAME, map.get("JCO_R3NAME"));
            connectProperties.setProperty(DestinationDataProvider.JCO_MSHOST, map.get("JCO_MSHOST"));
            connectProperties.setProperty(DestinationDataProvider.JCO_MSSERV, map.get("JCO_MSSERV"));
            connectProperties.setProperty(DestinationDataProvider.JCO_GROUP,  map.get("JCO_GROUP"));
        }
        
        else {
      //connectProperties.setProperty("jco.client.ashost", prop.getProperty("JCO_ASHOST"));
      //connectProperties.setProperty("jco.client.sysnr", prop.getProperty("JCO_SYSNR"));
      connectProperties.setProperty("jco.client.ashost", map.get("JCO_ASHOST"));
      connectProperties.setProperty("jco.client.sysnr", map.get("JCO_SYSNR"));
        }
        
        //connectProperties.setProperty("jco.client.client", prop.getProperty("JCO_CLIENT"));
        //connectProperties.setProperty("jco.client.user", prop.getProperty("JCO_USER"));
        //connectProperties.setProperty("jco.client.passwd", decode(prop.getProperty("JCO_PASSWD")));
        //connectProperties.setProperty("jco.client.lang", prop.getProperty("JCO_LANG"));
        connectProperties.setProperty("jco.client.client", map.get("JCO_CLIENT"));
        connectProperties.setProperty("jco.client.user", map.get("JCO_USER"));
        connectProperties.setProperty("jco.client.passwd", decode(map.get("JCO_PASSWD")));
        connectProperties.setProperty("jco.client.lang", map.get("JCO_LANG"));
      //Commented out for handling Multiple SAP session
      
      // Added for handling SAP Multiple Session Start
     // Environment.unregisterDestinationDataProvider(myProvider);
      //Environment.registerDestinationDataProvider(myProvider);
      String destName = "SAP_UsernameWriteBack";
      
      JCoFunction userChangeFunction = null;
     //myProvider.changeProperties(destName, connectProperties);
	  removedestinationfile(destName);
	  createDestinationDataFile(destName, connectProperties);
      ABAP_AS = JCoDestinationManager.getDestination(destName);
     
      System.out.println(ABAP_AS.getAttributes());
    
      
      ABAP_AS.ping();
      System.out.println("Connection to SAP successful!");
      
      return ABAP_AS;
    }
    catch (Exception e)
    {
      System.out.println("Connection to SAP unsuccessful");
      
      e.printStackTrace();
      
    }
    return null;
  }
  
  private String getAccessToken() {

		System.out.println("Entering...");
		String access_token = null;
		HttpURLConnection conn = null;
		BufferedReader br = null;
		//String ACCESS_TOKEN_URL=prop.getProperty("ACCESS_TOKEN_URL");
		String ACCESS_TOKEN_URL=map.get("ACCESS_TOKEN_URL");
		try {
			URL url = new URL((ACCESS_TOKEN_URL));
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			String saviyntUsername = decode(map.get("SSM_USER"));
			String saviyntPassword = decode(map.get("SSM_PASS"));

			String authInput = "{\"username\":\"" + saviyntUsername + "\",\"password\":\"" + saviyntPassword + "\"}";

			OutputStream os = conn.getOutputStream();
			os.write(authInput.getBytes());
			os.flush();

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed to connect: HTTP error code : " + conn.getResponseCode());
			}

			br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			String res = "";

			while ((output = br.readLine()) != null) {
				res = output;
			}
			String jsonObject = new JSONObject(res).getString("access_token"); 
			access_token = "Bearer " + jsonObject;
			System.out.println("Access Token:" + access_token);
			System.out.println("Got the access token successfully");
			System.out.println("Exiting...");
		} catch (MalformedURLException e) {

			System.out.println("Exception - " + e);

		} catch (IOException e) {

			System.out.println("Exception - " + e);

		} 
		catch(Exception je){
			System.out.println(je);
		}
		finally {
			if (null != conn) {
				try {
					conn.disconnect();
				} catch (Exception e) {
					System.out.println("Error while closing connection - " + e);
				}
			}

			if (null != br) {
				try {
					br.close();
				} catch (Exception e) {
					System.out.println("Error while closing buffer reader - " + e);
				}
			}
		}

		return access_token;
	}
  
  private HttpURLConnection getSavConnection(String url, String connectionType, String access_token) throws IOException {
		
		System.out.println("Entering...");

		if (null == access_token) {
			access_token = this.getAccessToken();
		}

		URL savurl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) savurl.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod(connectionType);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Authorization", access_token);
		System.out.println("Exiting...");
		return conn;
	}
  
  private StringBuilder getUsers(){

		System.out.println("entering.....");
		BufferedReader br = null;
		StringBuilder result = null;
		//String GET_USER_DETAILS=prop.getProperty("GET_USER_DETAILS");
		String GET_USER_DETAILS=map.get("GET_USER_DETAILS");
		HttpURLConnection conn = null;
		String access_token = this.getAccessToken();
		try {
			conn = this.getSavConnection(GET_USER_DETAILS, "POST", access_token);
			System.out.println(GET_USER_DETAILS + "..." + access_token);
			String getUserFilter = "{\"userQuery\" : \"(date(user.createdate) between curdate()-2 and curdate()+1) and statuskey=1\",\"responsefields\" : [\"username\",\"email\",\"employeeid\",\"startdate\"] }";
			System.out.println("Fetching users");
			OutputStream os = conn.getOutputStream();
			os.write(getUserFilter.getBytes());
			os.flush();

			System.out.println(conn.getResponseCode());

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed to connect: HTTP error code : " + conn.getResponseCode());
			}

			br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String line;
			result = new StringBuilder();
			
			
			while ((line = br.readLine()) != null) {
				result.append(line);
			}

			System.out.println("Users detail" + result);
			br.close();
			
			System.out.println(result);
			System.out.println("Exiting...");
		} catch (MalformedURLException e) {

			System.out.println("Exception " + e);

		} catch (IOException e) {

			System.out.println("Exception " + e);

		} finally {
			if (null != conn) {
				try {
					conn.disconnect();
				} catch (Exception e) {
					System.out.println("Error while closing connection - " + e);
				}
			}

			if (null != br) {
				try {
					br.close();
				} catch (Exception e) {
					System.out.println("Error while closing buffer reader - " + e);
				}
			}
		}
		
		return result;

	}

  
  public JCoDestination process(JCoDestination ABAP_AS, String employeeid, String email, String systemusername, Timestamp startdate)
  {
    JCoFunction userChangeFunction = null;
    JCoFunction userChangeFunction3 = null;
    try
    {
      DateFormat originalFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.ENGLISH);
      DateFormat targetFormat = new SimpleDateFormat("dd.MM.yyyy");
      
      userChangeFunction = ABAP_AS.getRepository().getFunction("BAPI_EMPLOYEE_ENQUEUE");
      if (userChangeFunction == null)
      {
        System.out.println("JcoConnector: BAPI_EMPLOYEE_ENQUEUE not found in SAP");
        
        throw new RuntimeException("BAPI_EMPLOYEE_ENQUEUE not found in SAP");
      }
      System.out.println("JcoConnector: BAPI_EMPLOYEE_ENQUEUE  found in SAP");
      
      System.out.println("BAPI_EMPLOYEE_ENQUEUE executing for user with employeeid: " + employeeid);
      
      JCoParameterList ipl = userChangeFunction.getImportParameterList();
      
      userChangeFunction.getImportParameterList().setValue("NUMBER", employeeid);
      JCoFunction userChangeFunction1 = null;
      
      userChangeFunction1 = ABAP_AS.getRepository().getFunction("BAPI_EMPLCOMM_GETDETAILEDLIST");
      if (userChangeFunction1 == null)
      {
        System.out.println("JcoConnector: BAPI_EMPLCOMM_GETDETAILEDLIST not found in SAP");
        
        throw new RuntimeException("BAPI_EMPLCOMM_GETDETAILEDLIST not found in SAP");
      }
      System.out.println("JcoConnector: BAPI_EMPLCOMM_GETDETAILEDLIST  found in SAP");
      System.out.println("BAPI_EMPLCOMM_GETDETAILEDLIST executing for user with employeeid: " + employeeid);
      
      userChangeFunction1.getImportParameterList().setValue("EMPLOYEENUMBER", employeeid);
      userChangeFunction1.execute(ABAP_AS);
      JCoTable userCommDetail = userChangeFunction1.getTableParameterList().getTable("COMMUNICATION");
      LinkedList l = new LinkedList();
      for (int i = 0; i < userCommDetail.getNumRows(); i++)
      {
        userCommDetail.setRow(i);
        JCoFieldIterator iter = userCommDetail.getFieldIterator();
        LinkedHashMap m = new LinkedHashMap();
        System.out.println("Existing user field values :");
        
        while (iter.hasNextField())
        {
          JCoField f = iter.nextField();
          System.out.println(f.getName() + "---" + userCommDetail.getValue(f.getName()));
          
          m.put(f.getName(), userCommDetail.getValue(f.getName()));
        }
        l.add(m);
      }
      JCoFunction userChangeFunction2 = null;
      JCoFunction userChangeFunction4 = null;
      
      
      userChangeFunction3 = ABAP_AS.getRepository().getFunction("BAPI_EMPLOYEE_DEQUEUE");
      if (userChangeFunction3 == null)
      {
        System.out.println("JcoConnector: BAPI_EMPLOYEE_DEQUEUE not found in SAP");
        
        throw new RuntimeException("BAPI_EMPLOYEE_DEQUEUE not found in SAP");
      }
      System.out.println("JcoConnector: BAPI_EMPLOYEE_DEQUEUE  found in SAP");
      System.out.println("BAPI_EMPLCOMM_GETDETAILEDLIST executing for user with employeeid: " + employeeid);
      
      userChangeFunction3.getImportParameterList().setValue("NUMBER", employeeid);
      JCoContext.begin(ABAP_AS);
      
      userChangeFunction.execute(ABAP_AS);
      if (!isBapiReturnCodeOkay(userChangeFunction)) {
        System.out.println("User lock failure: " + isBapiReturnCodeOkay(userChangeFunction));
        
      } else {
        System.out.println("User lock successful for user with employeeid " + employeeid);
        
      }
      userChangeFunction1.execute(ABAP_AS);
      if (!isBapiReturnCodeOkay(userChangeFunction1)) {
        System.out.println("Got details for user with employeeid " + employeeid);
        
      } else {
        System.out.println("Got details for user with employeeid " + employeeid);
        
      }
      try
      {
        userChangeFunction4 = ABAP_AS.getRepository().getFunction("BAPI_EMPLCOMM_CREATE");
        userChangeFunction4.getImportParameterList().setValue("EMPLOYEENUMBER", employeeid);
        userChangeFunction4.getImportParameterList().setValue("COMMUNICATIONID", systemusername);
        userChangeFunction4.getImportParameterList().setValue("NOCOMMIT", "");
        userChangeFunction4.getImportParameterList().setValue("SUBTYPE", "0001");
        
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String todayDate = sdf.format(dt);
        
        Date date1 = new SimpleDateFormat("dd.MM.yyyy").parse(targetFormat.format(startdate));
        
        userChangeFunction4.getImportParameterList().setValue("VALIDITYBEGIN", date1);
        
        Date dt2 = new Date();
        
        SimpleDateFormat sdfNow = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        SimpleDateFormat sdfNeeded = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
        Calendar c = new GregorianCalendar();
        c.set(9999, 11, 31, 23, 59, 59);
        Date date2 = c.getTime();
        Date date3 = sdfNow.parse(date2.toString());
        String date4 = sdfNeeded.format(date3);
        Date date5 = sdfNeeded.parse(date4);
        
        userChangeFunction4.getImportParameterList().setValue("VALIDITYEND", date5);
        
        userChangeFunction4.execute(ABAP_AS);
        System.out.println("Creating SUBTYPE 0001 since it is not found");
        
      }
      catch (Exception e)
      {
        System.out.println("Could not change systemusername for user with employeeid " + employeeid);
        e.printStackTrace();
        
      }
      if (!isBapiReturnCodeOkay(userChangeFunction4))
      {
        System.out.println("Could not update systemusername for user with employeeid " + employeeid);
        
        System.out.println("Sytemname updation " + isBapiReturnCodeOkay(userChangeFunction4));
        
      }
      else
      {
        System.out.println("Systemusername successfully changed for user with employeeid " + employeeid);
        
      }
      
      //Moving this block to finally
      /*userChangeFunction3.execute(ABAP_AS);
      if (!isBapiReturnCodeOkay(userChangeFunction3)) {
        System.out.println("User unlock failure: " + isBapiReturnCodeOkay(userChangeFunction3));
        logger.debug(logger.getName()+": "+new Date()+"::  "+"User unlock failure: " + isBapiReturnCodeOkay(userChangeFunction3));
      } else {
        System.out.println("User unlock successful for user with employeeid " + employeeid);
        logger.debug(logger.getName()+": "+new Date()+"::  "+"User unlock successful for user with employeeid " + employeeid);
      }
      JCoContext.end(ABAP_AS);*/
      
      //return ABAP_AS;*/
    }
    catch (IllegalStateException providerAlreadyRegisteredException)
    {
      providerAlreadyRegisteredException = 
      
        providerAlreadyRegisteredException;System.out.println("JcoConnector: " + providerAlreadyRegisteredException.toString());throw new Error(providerAlreadyRegisteredException);
    }
    catch (JCoException je)
    {
      je = je;
      System.out.println("JcoConnector: " + je.toString());
      System.out.println(je);
      je.printStackTrace();
      
      
      
      return ABAP_AS;
    }
    catch (Exception e)
    {
      
      System.out.println("JcoConnector: " + e.toString());
      System.out.println(e);
      e.printStackTrace();
      
        
    }finally {
    	try {
    	userChangeFunction3.execute(ABAP_AS);
        if (!isBapiReturnCodeOkay(userChangeFunction3)) {
          System.out.println("User unlock failure: " + isBapiReturnCodeOkay(userChangeFunction3));
          
        } else {
          System.out.println("User unlock successful for user with employeeid " + employeeid);
          
        }
        JCoContext.end(ABAP_AS);
    	}
    	catch(Exception e)
    	{
    		
    	}
    return ABAP_AS;
    }
  }
  
  public static boolean isBapiReturnCodeOkay(JCoRecord object)
  {
    JCoStructure isStructure = null;
    JCoTable isTable = null;
    try
    {
      if ((object instanceof JCoStructure))
      {
        if ((object.getString("TYPE").equals("")) || (object.getString("TYPE").equals("S"))) {
          return true;
        }
        return false;
      }
      if ((object instanceof JCoTable))
      {
        isTable = (JCoTable)object;
        int count = isTable.getNumRows();
        if (count == 0) {
          return true;
        }
        boolean allOkay = true;
        for (int i = 0; i < count; i++)
        {
          isTable.setRow(i);
          if ((!isTable.getString("TYPE").equals("")) && (!isTable.getString("TYPE").equals("S")) && (!isTable.getString("TYPE").equals("I")))
          {
            System.out.println("JcoConnector: Error st" + isTable.getString("MESSAGE").toString());
            System.out.println("JcoConnector: Error st" + isTable.toString());
            
            allOkay = false;
            break;
          }
        }
        return allOkay;
      }
    }
    catch (UnsatisfiedLinkError e)
    {
      System.err.println("ERROR!!!\n" + e);
      
      System.exit(1);
    }
    catch (Exception e)
    {
      System.out.println(e);
      
      return false;
    }
    return false;
  }
  
  public static boolean isBapiReturnCodeOkay(JCoFunction function)
  {
    JCoParameterList exports = function.getExportParameterList();
    try
    {
      if (exports != null)
      {
        System.out.println("JcoConnector:" + exports.getValue("RETURN"));
        
        return isBapiReturnCodeOkay(exports.getStructure("RETURN"));
      }
      throw new RuntimeException("Return not a structure");
    }
    catch (Exception e)
    {
      JCoParameterList tables = function.getTableParameterList();
      try
      {
        if (tables != null) {
          return isBapiReturnCodeOkay(tables.getTable("RETURN"));
        }
        return false;
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
        
      }
    }
    return false;
  }
  
  public static String decode(String strToDecrypt)
  {
    byte[] key = "S@v!ynt_s@V!YNt_".getBytes();
    if (strToDecrypt != null) {
      try
      {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        cipher.init(2, secretKey);
        return new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt)));
      }
      catch (Exception e)
      {
        e.printStackTrace();
        
      }
    }
    return null;
  }
  

  
  public static void main(String[] args)
  {
	  //DataBaseConnection dc = null;
    try
    {
    	 
      System.out.println("SAP Communication changes start..");
      
      
      //InputStream input = new FileInputStream(saviyntHome + File.separator + "configurationsNYPAUsername.properties");
      //String input = "D:\\configurationsNYPAUsername.properties";
		
 /*
  Using csv instead of properties file 
    	InputStream inputStream = null;
        final String propFileName = "/application_instance/custom_code/saviynt/Properties/UPNUsernameWriteback.properties";
        //final String propFileName = "D:\\\\configurationsNYPAUsername.properties";
        System.out.println("Config.properties found at :" + propFileName);
        inputStream = new FileInputStream(propFileName);
        prop.load(inputStream);
             
      Logging logging = new Logging();
      String loggerLocation = prop.getProperty("loggerLocation");
      System.out.println(loggerLocation);
      String loggerName = prop.getProperty("loggerNameUsernameWriteback");
      String loggerMaxFileSize = prop.getProperty("loggerMaxFileSize");
      int loggerMaxBackupIndex = Integer.parseInt(prop.getProperty("loggerMaxBackupIndex"));
      String logLevel = prop.getProperty("loggerLevel");
      Level loggerLevel = Level.toLevel(logLevel);
      logging.initiateLoggerSetup(loggerLocation, loggerName, loggerMaxFileSize, loggerMaxBackupIndex, loggerLevel, logger);
*/
      
 
      Communications jc = new Communications();
      MyDestinationDataProvider myProvider = new MyDestinationDataProvider();
      JCoDestination dest = jc.initializeConnectiontoSAP(myProvider);
      /*DataBaseDemo dm = new DataBaseDemo();
      dc = dm.getDatabaseConnection(prop.getProperty("URLPATH"), prop.getProperty("SAVUSERNAME"), decode(prop.getProperty("SAVPASSWORD")), prop.getProperty("JDBC_DRIVER"));
      Statement stat = dc.conn.createStatement(1004, 1007);    */
      // 
	    StringBuilder result = jc.getUsers();
	    System.out.println(result.toString());
	    JSONObject jsonObject = new JSONObject(result.toString());
	    JSONArray jsonArray = jsonObject.getJSONArray("userdetails");
	   
	    ArrayList < HashMap < String, String >> usersDetail = new ArrayList< HashMap < String, String >> ();
	    for (int i = 0; i < jsonArray.length(); i++) {
	    	HashMap < String, String > hm = new HashMap < String, String > ();
	        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
	        hm.put("username", jsonObject1.getString("username"));
	        hm.put("email", jsonObject1.getString("email"));
	        hm.put("employeeid", jsonObject1.getString("employeeid"));
	        hm.put("startdate", jsonObject1.getString("startdate"));
	        usersDetail.add(hm);
	    }
	    System.out.println(usersDetail);
	    for(HashMap<String, String> user:usersDetail)  {  
	    	System.out.println(user); 
	    	String systemusername=user.get("username");
	    	String employeeid=user.get("employeeid");
	    	String email=user.get("email");
	    	String startdate=user.get("startdate");
	    	Timestamp startdate1 = Timestamp.valueOf(startdate);
	    	System.out.println("username: " + systemusername+ ", employeeid" + employeeid+", email" + email+", startdate" + startdate1);	
	    	jc.process(dest, employeeid, email, systemusername, startdate1);
	    } 
	    
      //Database 
      /*
      ResultSet rs = stat.executeQuery("select employeeid,email,username,startdate from users where date(createdate)=curdate()");
      rs.last();
      int size = rs.getRow();
      System.out.println("Number of users to process: " + size);
      
      rs.beforeFirst();
      while (rs.next()) {
        try
        {
          String employeeid = rs.getString("employeeid");
          System.out.println("Processing user with employee id: " + employeeid);
          
          String email = rs.getString("email");
          System.out.println("Updated email id: " + email);
          
          String systemusername = rs.getString("username");
          System.out.println("Updated username: " + systemusername);
          
          Timestamp startdate = rs.getTimestamp("startdate");
          System.out.println("Updated startdate: " + startdate);
          
          if (employeeid != null) {
            if (!employeeid.equals("")) {
              jc.process(dest, employeeid, email, systemusername, startdate);
            } else {
              System.out.println("Could not process user as employeeid is empty");
              
            }
          }
        }
        catch (Exception e)
        {
          e.printStackTrace();
          
        }
      }
      */
      
      
      //Environment.unregisterDestinationDataProvider(myProvider);
      // Added for handling SAP Multiple Session Start
      //com.sap.conn.jco.ext.Environment.unregisterDestinationDataProvider(myProvider);
      removedestinationfile("SAP_UsernameWriteBack");
      // Added for handling SAP Multiple Session End
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.out.println("Error");
      
    }
    finally
    {
      //dc.close();
      System.out.println("SAP Communication changes end");
      
    }
  }
  
  // Added for handling SAP multiple Session Start
  private static void createDestinationDataFile(String destinationName, Properties connectProperties)
  {
    File destCfg = new File(destinationName + ".jcoDestination");
    try
    {
      FileOutputStream fos = new FileOutputStream(destCfg, false);
      connectProperties.store(fos, destinationName + " file !");
      
      fos.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      
      throw new RuntimeException("Unable to create the destination files", e);
    }
  }
  
  private static void removedestinationfile(String destinationName)
  {
    String filename = destinationName + ".jcoDestination";
    try
    {
      File destCfg = new File(filename);
      if (destCfg.exists()) {
        destCfg.delete();
      } else {
    	  System.out.println( "file " + filename + " not found");
    	  
      }
    }
    catch (Exception ex)
    {
    	ex.printStackTrace();
    	
	      throw new RuntimeException("Unable to remove the destination files", ex);
    }
  }
  
  // Added for handling SAP multiple Session end
}
