
package com.saviynt.SAPUser;

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
import com.sap.conn.jco.ext.DataProviderException.Reason;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.log4j.Level;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
public class CommunicationsUPN {
	
	final static Logger logger = Logger.getLogger(CommunicationsUPN.class.getName()); // PSL Changes: Added custom logger
    //static final Logger logger = Logger.getLogger(CommunicationsUPN.class);
	static HashMap<String,String> map;
	static {
		try{
			 //BufferedReader br = new BufferedReader(new FileReader("/saviynt_shared/saviynt/Import/Datafiles/UPNUsernameWriteback.csv"));
			 BufferedReader br = new BufferedReader(new FileReader("D:\\\\UPNUsernameWriteback.csv"));
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
   public static PreparedStatement pst;

   public static JCoDestination initializeConnectiontoSAP(CommunicationsUPN.MyDestinationDataProvider myProvider) {
      Properties connectProperties = new Properties();
      JCoDestination ABAP_AS = null;

      try {
         //String msgServer = prop.getProperty("MESSAGESERVER");
         String msgServer = map.get("MESSAGESERVER");
         System.out.println("initializeConnectiontoSAP :: MESSAGESERVER::" + msgServer);
         
         if (msgServer != null && !msgServer.isEmpty() && msgServer.equalsIgnoreCase("true")) {
            /*connectProperties.setProperty("jco.client.r3name", prop.getProperty("JCO_R3NAME"));
            connectProperties.setProperty("jco.client.mshost", prop.getProperty("JCO_MSHOST"));
            connectProperties.setProperty("jco.client.msserv", prop.getProperty("JCO_MSSERV"));
            connectProperties.setProperty("jco.client.group", prop.getProperty("JCO_GROUP"));*/
            connectProperties.setProperty("jco.client.r3name", map.get("JCO_R3NAME"));
            connectProperties.setProperty("jco.client.mshost", map.get("JCO_MSHOST"));
            connectProperties.setProperty("jco.client.msserv", map.get("JCO_MSSERV"));
            connectProperties.setProperty("jco.client.group", map.get("JCO_GROUP"));
         } else {
            //connectProperties.setProperty("jco.client.ashost", prop.getProperty("JCO_ASHOST"));
            //connectProperties.setProperty("jco.client.sysnr", prop.getProperty("JCO_SYSNR"));
            connectProperties.setProperty("jco.client.ashost", map.get("JCO_ASHOST"));
            connectProperties.setProperty("jco.client.sysnr", map.get("JCO_SYSNR"));
         }

         /*connectProperties.setProperty("jco.client.client", prop.getProperty("JCO_CLIENT"));
         connectProperties.setProperty("jco.client.user", prop.getProperty("JCO_USER"));
         connectProperties.setProperty("jco.client.passwd", decode(prop.getProperty("JCO_PASSWD")));
         connectProperties.setProperty("jco.client.lang", prop.getProperty("JCO_LANG"));*/
         connectProperties.setProperty("jco.client.client", map.get("JCO_CLIENT"));
         connectProperties.setProperty("jco.client.user", map.get("JCO_USER"));
         connectProperties.setProperty("jco.client.passwd", decode(map.get("JCO_PASSWD")));
         connectProperties.setProperty("jco.client.lang", map.get("JCO_LANG"));
         String destName = "SAP_UPNWriteBack";
         JCoFunction userChangeFunction = null;
         removedestinationfile(destName);
         createDestinationDataFile(destName, connectProperties);
         ABAP_AS = JCoDestinationManager.getDestination(destName);
         System.out.println(ABAP_AS.getAttributes());
         
         ABAP_AS.ping();
         System.out.println("Connection to SAP successful!");
         
         return ABAP_AS;
      } catch (Exception var6) {
         System.out.println("Connection to SAP unsuccessful");
         
         var6.printStackTrace();
         
         return null;
      }
   }

   public Boolean checkUPNupdate(JCoDestination ABAP_AS, String employeeid) {
      System.out.println("checkUPNupdate :: Method Started");
      
      Boolean check = false;

      try {
         JCoFunction userChangeFunction6 = ABAP_AS.getRepository().getFunction("BAPI_EMPLCOMM_GETDETAILEDLIST");
         System.out.println("checkUPNupdate :: BAPI_EMPLCOMM_GETDETAILEDLIST function received");
         
         userChangeFunction6.getImportParameterList().setValue("EMPLOYEENUMBER", employeeid);
         userChangeFunction6.getImportParameterList().setValue("SUBTYPE", "0030");
         userChangeFunction6.execute(ABAP_AS);
         JCoTable userCommDetail = userChangeFunction6.getTableParameterList().getTable("COMMUNICATION");
         System.out.println("Usercomm details after executing userChangeFunction6 " + userCommDetail.toString());
         
         System.out.println("No of rows for existing record " + userCommDetail.getNumRows());
         
         if (userCommDetail.getNumRows() > 0) {
            check = true;
         }
      } catch (JCoException var6) {
         var6.printStackTrace();
         
         
      }

      return check;
   }

   public JCoDestination process(JCoDestination ABAP_AS, String employeeid, String UPN, String systemusername, Timestamp startdate, String access_token) {
      JCoFunction userChangeFunction = null;
      JCoFunction userChangeFunction3 = null;

      try {
         new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.ENGLISH);
         DateFormat targetFormat = new SimpleDateFormat("dd.MM.yyyy");
         userChangeFunction = ABAP_AS.getRepository().getFunction("BAPI_EMPLOYEE_ENQUEUE");
         if (userChangeFunction == null) {
            System.out.println("JcoConnector: BAPI_EMPLOYEE_ENQUEUE not found in SAP");
            
            throw new RuntimeException("BAPI_EMPLOYEE_ENQUEUE not found in SAP");
         }

         System.out.println("JcoConnector: BAPI_EMPLOYEE_ENQUEUE  found in SAP");
         
         System.out.println("BAPI_EMPLOYEE_ENQUEUE executing for user with employeeid: " + employeeid);
         
         JCoParameterList ipl = userChangeFunction.getImportParameterList();
         userChangeFunction.getImportParameterList().setValue("NUMBER", employeeid);
         JCoFunction userChangeFunction1 = null;
         userChangeFunction1 = ABAP_AS.getRepository().getFunction("BAPI_EMPLCOMM_GETDETAILEDLIST");
         if (userChangeFunction1 == null) {
            System.out.println("JcoConnector: BAPI_EMPLCOMM_GETDETAILEDLIST not found in SAP");
            
            throw new RuntimeException("BAPI_EMPLCOMM_GETDETAILEDLIST not found in SAP");
         }

         System.out.println("JcoConnector: BAPI_EMPLCOMM_GETDETAILEDLIST  found in SAP");
         
         System.out.println("BAPI_EMPLCOMM_GETDETAILEDLIST executing for user with employeeid: " + employeeid);
         
         userChangeFunction1.getImportParameterList().setValue("EMPLOYEENUMBER", employeeid);
         userChangeFunction1.execute(ABAP_AS);
         JCoTable userCommDetail = userChangeFunction1.getTableParameterList().getTable("COMMUNICATION");
         LinkedList l = new LinkedList();

         JCoFieldIterator iter;
         LinkedHashMap m;
         JCoField f;
         for(int i = 0; i < userCommDetail.getNumRows(); ++i) {
            userCommDetail.setRow(i);
            iter = userCommDetail.getFieldIterator();
            m = new LinkedHashMap();
            System.out.println("Existing user field values :");
            

            while(iter.hasNextField()) {
               f = iter.nextField();
               System.out.println(f.getName() + "---" + userCommDetail.getValue(f.getName()));
               
               m.put(f.getName(), userCommDetail.getValue(f.getName()));
            }

            l.add(m);
         }

         JCoFunction userChangeFunction2 = null;
         iter = null;
         m = null;
         f = null;
         userChangeFunction3 = ABAP_AS.getRepository().getFunction("BAPI_EMPLOYEE_DEQUEUE");
         if (userChangeFunction3 == null) {
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

         try {
            userChangeFunction2.execute(ABAP_AS);
         } catch (Exception var38) {
            Boolean isUPNAvailable = this.checkUPNupdate(ABAP_AS, employeeid);
            Date dateE;
            if (isUPNAvailable) {
               for(int i = 0; i < l.size(); ++i) {
                  new LinkedHashMap();
                  LinkedHashMap<Object, Object> lhm = (LinkedHashMap)l.get(i);
                  Set<Object> keys = lhm.keySet();
                  Object value = lhm.get("SUBTYPE");
                  if (value.equals("0030")) {
                     System.out.println("Business Email subtype present with employeeid " + employeeid);
                     
                     JCoFunction userChangeFunction5 = ABAP_AS.getRepository().getFunction("BAPI_EMPLCOMM_CREATE");
                     if (userChangeFunction5 == null) {
                        System.out.println("JcoConnector: BAPI_EMPLCOMM_CHANGE not found in SAP");
                        
                        throw new RuntimeException("BAPI_EMPLCOMM_CHANGE not found in SAP");
                     }

                     System.out.println("JcoConnector: BAPI_EMPLCOMM_CHANGE  found in SAP");
                     
                     System.out.println("BAPI_EMPLCOMM_GETDETAILEDLIST executing for user with employeeid: " + employeeid);
                     
                     System.out.println("Setting values before calling BAPI_EMPLCOMM_CHANGE");
                     

                     Object k;
                     for(Iterator var24 = keys.iterator(); var24.hasNext(); System.out.println(k + " -- " + lhm.get(k))) {
                        k = var24.next();
                        String kString = (String)k;
                        JCoParameterList ipl1 = userChangeFunction5.getImportParameterList();
                        userChangeFunction5.getImportParameterList().setValue("NOCOMMIT", "");
                        if (kString.equalsIgnoreCase("EMPLOYEENO")) {
                           userChangeFunction5.getImportParameterList().setValue("EMPLOYEENUMBER", lhm.get(k));
                        } else if (kString.equalsIgnoreCase("ID")) {
                           userChangeFunction5.getImportParameterList().setValue("COMMUNICATIONID", lhm.get(k));
                        } else if (kString.equalsIgnoreCase("SUBTYPE")) {
                           userChangeFunction5.getImportParameterList().setValue("SUBTYPE", lhm.get(k));
                        } else if (kString.equalsIgnoreCase("VALIDBEGIN")) {
                           System.out.println("begin date " + targetFormat.format(startdate));
                           
                           dateE = (new SimpleDateFormat("dd.MM.yyyy")).parse(targetFormat.format(lhm.get(k)));
                           userChangeFunction5.getImportParameterList().setValue("VALIDITYBEGIN", dateE);
                        } else if (kString.equalsIgnoreCase("VALIDEND")) {
                           System.out.println("end date " + targetFormat.format(lhm.get(k)));
                           
                           dateE = (new SimpleDateFormat("dd.MM.yyyy")).parse(targetFormat.format(new Date()));
                           SimpleDateFormat sdfNeeded = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
                           int currentStartDate = dateE.getDate();
                           int newEnd = currentStartDate - 1;
                           dateE.setDate(newEnd);
                           String dateEformatted = sdfNeeded.format(dateE);
                           Date Updated = sdfNeeded.parse(dateEformatted);
                           System.out.println("Updated end date " + Updated);
                           
                           userChangeFunction5.getImportParameterList().setValue("VALIDITYEND", Updated);
                        }
                     }

                     userChangeFunction5.execute(ABAP_AS);
                     System.out.println("Executed BAPI_EMPLCOMM_CHANGE for updating SUBTYPE 0030 since it is found");
                     
                     if (!isBapiReturnCodeOkay(userChangeFunction5)) {
                        System.out.println("Could not update UPN for user with employeeid " + employeeid);
                        
                        System.out.println("UPN updation failure: " + isBapiReturnCodeOkay(userChangeFunction5));
                        
                     } else {
                        System.out.println("UPN successsfully updated for user with employeeid " + employeeid);
                        
                     }
                  }// if 0030
               }
            }

            if (isUPNAvailable) {
               userChangeFunction2 = ABAP_AS.getRepository().getFunction("BAPI_EMPLCOMM_CREATESUCCESSOR");
            } else {
               userChangeFunction2 = ABAP_AS.getRepository().getFunction("BAPI_EMPLCOMM_CREATE");
            }

            userChangeFunction2.getImportParameterList().setValue("EMPLOYEENUMBER", employeeid);
            userChangeFunction2.getImportParameterList().setValue("COMMUNICATIONID", UPN);
            userChangeFunction2.getImportParameterList().setValue("NOCOMMIT", "");
            userChangeFunction2.getImportParameterList().setValue("SUBTYPE", "0030");
            Date dt = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            sdf.format(dt);
            Date startDate;
            if (isUPNAvailable) {
               startDate = (new SimpleDateFormat("dd.MM.yyyy")).parse(targetFormat.format(new Date()));
            } else {
               startDate = (new SimpleDateFormat("dd.MM.yyyy")).parse(targetFormat.format(startdate));
            }

            userChangeFunction2.getImportParameterList().setValue("VALIDITYBEGIN", startDate);
            new Date();
            SimpleDateFormat sdfNow = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
            SimpleDateFormat sdfNeeded = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
            Calendar c = new GregorianCalendar();
            c.set(9999, 11, 31, 23, 59, 59);
            dateE = c.getTime();
            Date date3 = sdfNow.parse(dateE.toString());
            String date4 = sdfNeeded.format(date3);
            Date date5 = sdfNeeded.parse(date4);
            userChangeFunction2.getImportParameterList().setValue("VALIDITYEND", date5);
            userChangeFunction2.execute(ABAP_AS);
            System.out.println("Creating SUBTYPE 0030 since it is not found");
            
         }//catch block complete

         if (!isBapiReturnCodeOkay(userChangeFunction2)) {
            System.out.println("Could not create a successor for UPN for user with employeeid " + employeeid);
            
            System.out.println("UPN addition failure: " + isBapiReturnCodeOkay(userChangeFunction2));
            
         } else {
            System.out.println("UPN successsfully added for user with employeeid " + employeeid);
            System.out.println("Update in Saviynt");
            String access_token1=access_token;
            String employeeid1=employeeid;
            String systemusername1=systemusername;
            updateUser(access_token1,employeeid1,systemusername1);
            
            //PSL Changes: executing update query to modify CP38=1 in SSM. Please confirm if this is right place to add this logic.
           /* updatePst.setString(1, employeeid);
            System.out.println("Executing statement: " + updatePst.toString());
            
            updatePst.executeUpdate();
            System.out.println("Statement executed successfully"); */
            
            
            
         }

         // Moving this block to finally
         /*userChangeFunction3.execute(ABAP_AS);
         if (!isBapiReturnCodeOkay(userChangeFunction3)) {
            System.out.println("User unlock failure: " + isBapiReturnCodeOkay(userChangeFunction3));
            logger.debug(logger.getName()+": "+new Date()+"::  "+"User unlock failure: " + isBapiReturnCodeOkay(userChangeFunction3));
         } else {
            System.out.println("User unlock successful for user with employeeid " + employeeid);
            logger.debug(logger.getName()+": "+new Date()+"::  "+"User unlock successful for user with employeeid " + employeeid);
         }

         JCoContext.end(ABAP_AS);*/
      } catch (IllegalStateException var39) {
         System.out.println("JcoConnector: " + var39.toString());
         
         throw new Error(var39);
      } catch (JCoException var40) {
         System.out.println("JcoConnector: " + var40.toString());
         
         System.out.println(var40);
         
         var40.printStackTrace();
      } catch (Exception var41) {
         System.out.println("JcoConnector: " + var41.toString());
         
         System.out.println(var41);
         var41.printStackTrace();
         
      } finally {
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
    		  System.out.println("Exception occured while User Unlock");
    		  e.printStackTrace();
    		  
    	  }
    
      }

      return ABAP_AS;
   }

   public static boolean isBapiReturnCodeOkay(JCoRecord object) {
      JCoStructure isStructure = null;
      JCoTable isTable = null;

      try {
         if (object instanceof JCoStructure) {
            if (!object.getString("TYPE").equals("") && !object.getString("TYPE").equals("S")) {
               return false;
            }

            return true;
         }

         if (object instanceof JCoTable) {
            isTable = (JCoTable)object;
            int count = isTable.getNumRows();
            if (count == 0) {
               return true;
            }

            boolean allOkay = true;

            for(int i = 0; i < count; ++i) {
               isTable.setRow(i);
               if (!isTable.getString("TYPE").equals("") && !isTable.getString("TYPE").equals("S") && !isTable.getString("TYPE").equals("I")) {
                  System.out.println("JcoConnector: Error st" + isTable.getString("MESSAGE").toString());
                  
                  System.out.println("JcoConnector: Error st" + isTable.toString());
                  
                  allOkay = false;
                  break;
               }
            }

            return allOkay;
         }
      } catch (UnsatisfiedLinkError var6) {
         System.err.println("ERROR!!!\n" + var6);
         
         System.exit(1);
      } catch (Exception var7) {
         System.out.println(var7);
         
         return false;
      }

      return false;
   }

   public static boolean isBapiReturnCodeOkay(JCoFunction function) {
      JCoParameterList exports = function.getExportParameterList();

      try {
         if (exports != null) {
            System.out.println("JcoConnector:" + exports.getValue("RETURN"));
            
            return isBapiReturnCodeOkay((JCoRecord)exports.getStructure("RETURN"));
         } else {
            throw new RuntimeException("Return not a structure");
         }
      } catch (Exception var6) {
         JCoParameterList tables = function.getTableParameterList();

         try {
            return tables != null ? isBapiReturnCodeOkay((JCoRecord)tables.getTable("RETURN")) : false;
         } catch (Exception var5) {
            var5.printStackTrace();
            
            return false;
         }
      }
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
 			//String saviyntUsername = decode(prop.getProperty("SSM_USER"));
 			//String saviyntPassword = decode(prop.getProperty("SSM_PASS"));
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
   
   private StringBuilder getUsers(String access_token){

		System.out.println("entering.....");
		BufferedReader br = null;
		StringBuilder result = null;
		//String GET_USER_DETAILS=prop.getProperty("GET_USER_DETAILS");
		String GET_USER_DETAILS=map.get("GET_USER_DETAILS");
		HttpURLConnection conn = null;
		try {
			conn = this.getSavConnection(GET_USER_DETAILS, "POST", access_token);
			System.out.println(GET_USER_DETAILS + "..." + access_token);

			String getUserFilter = "{\"userQuery\" : \"(((user.customproperty20!=user.customproperty36) or (user.customproperty20 is null and user.customproperty36 is not null) or (user.customproperty20 is not null and user.customproperty36 is null)) or ((user.customproperty22!=user.customproperty37) or (user.customproperty22 is null and user.customproperty37 is not null) or (user.customproperty22 is not null and user.customproperty37 is null))) and user.customproperty38 is null\",\"responsefields\" : [\"username\",\"userkey\",\"employeeid\",\"startdate\",\"customproperty20\",\"customproperty22\",\"customproperty36\",\"customproperty37\"] }";
			System.out.println("Fetching users" + getUserFilter);
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
   
   public void updateUser(String access_token,String employeeid,String systemusername) {


		System.out.println("Entering...");

		BufferedReader br = null;
		
		//String UPDATE_USER_URL=prop.getProperty("UPDATE_USER_URL");
		String UPDATE_USER_URL=map.get("UPDATE_USER_URL");

		HttpURLConnection conn = null;

		try {

			conn = this.getSavConnection(UPDATE_USER_URL, "POST", access_token);

			System.out.println(UPDATE_USER_URL + "..." + access_token);

			String updateUserDetails = "{\"username\" : \"" + systemusername + "\",\"employeeid\" : \"" + employeeid + "\",\"customproperty38\" : \"1\"}";

			System.out.println("updating the user" + updateUserDetails);

			OutputStream os = conn.getOutputStream();

			os.write(updateUserDetails.getBytes());

			os.flush();

			System.out.println(conn.getResponseCode());

			if (conn.getResponseCode() != 200) {

				throw new RuntimeException("Failed to connect: HTTP error code : " + conn.getResponseCode());

			}

			br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;

			while ((output = br.readLine()) != null) {

				System.out.println(output);

			}
			

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
	}
   

   public static String decode(String strToDecrypt) {
      byte[] key = "S@v!ynt_s@V!YNt_".getBytes();
      if (strToDecrypt != null) {
         try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            cipher.init(2, secretKey);
            return new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt.getBytes())));
         } catch (Exception var4) {
            var4.printStackTrace();
            
         }
      }

      return null;
   }



  /* public static HashSet<String> readBlob() throws IOException, SQLException {
      ResultSet rs3 = null;
      String selectSQL = "";
      HashSet caflagchangeList = new HashSet();

      try {
		  SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd");
		  dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		  SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MMM-dd");
		  Calendar cal = Calendar.getInstance();
		  cal.add(Calendar.DATE,-2);
		  //java.util.Date currentdate = dateFormatLocal.parse(dateFormatGmt.format(new java.util.Date()));
		  java.util.Date currentdate = dateFormatLocal.parse(dateFormatGmt.format(cal.getTime()));
		  java.sql.Date sqlStartDate = new java.sql.Date(currentdate.getTime());
		  System.out.println(sqlStartDate);
		  
		  //PSL Changes: Added customproperty38 not null condition inside where clause to avoid reprocessing of buffer which may occur due to 2 days buffer period
         selectSQL = "SELECT uh.changelog,uh.userkey,u.customproperty20,u.customproperty22 FROM usershistory uh,users u WHERE uh.userkey=u.userkey and uh.updatedate >= ? and u.customproperty38 is null";
         DataBaseConnection dc = null;
         DataBaseDemo dm = new DataBaseDemo();
         dc = dm.getDatabaseConnection(prop.getProperty("URLPATH"), prop.getProperty("SAVUSERNAME"), decode(prop.getProperty("SAVPASSWORD")), prop.getProperty("JDBC_DRIVER"));
         Statement stat = dc.conn.createStatement(1004, 1007);
         pst = dc.conn.prepareStatement(selectSQL);
         pst.setDate(1, sqlStartDate);
         System.out.println("Executing statement: " + pst.toString());
         
         rs3 = pst.executeQuery();
         System.out.println("Statement executed successfully");
         

         while(rs3.next()) {
            Object object = rs3.getObject(1);
            String className = object.getClass().getName();
            Blob myBlob = rs3.getBlob("changelog");
            String userkey = rs3.getString("userkey");
            InputStream input = myBlob.getBinaryStream();
            byte[] buffer = new byte[4096];
            ObjectInputStream objectIn = null;

            while(input.read(buffer) > 0) {
               try {
                  objectIn = new ObjectInputStream(new ByteArrayInputStream(buffer));
                  Object deSerializedObject = objectIn.readObject();
                  System.out.println("Blob data: " + deSerializedObject);
                  
                  String blobdata = deSerializedObject.toString();
                  System.out.println("Blob data: " + blobdata);
                 
                  if (blobdata.contains("customproperty20") || blobdata.contains("customproperty22")) {
                     caflagchangeList.add(userkey);
                  }
               } catch (Exception var26) {
                  var26.printStackTrace();
                  
               }
            }
         }

         String caflagchangeUserkeys = String.join(", ", caflagchangeList);
         System.out.println("Userkeys to be processed : " + caflagchangeUserkeys);
         
      } catch (Exception var27) {
         System.out.println(var27.getMessage());
      } finally {
         ;
      }

      try {
         if (rs3 != null) {
            rs3.close();
         }
      } catch (SQLException var25) {
         System.out.println(var25.getMessage());
         var25.printStackTrace();
         
      }

      return caflagchangeList;
   }*/

   public static void main(String[] args) throws SQLException {
	  //DataBaseConnection dc = null;
	  //ResultSet rs=null;
	  //Statement stat=null;
	  //PreparedStatement updatePst=null;
	 
      try {
    	//PSL Changes: Added custom logger. Please ensure configurationNYPA.properties file has logging attributes present in it.
    	
         System.out.println("SAP Communication changes start..");
         
         //String saviyntHome = System.getenv("SAVIYNT_HOME");
         //System.out.println("Saviynt home configured at this location :" + saviyntHome);
         //InputStream input = new FileInputStream(saviyntHome + File.separator + "configurationsNYPA.properties");
         //InputStream input = new FileInputStream("D:\\configurationsNYPAUsername.properties");
         //prop.load(input);
         /*InputStream inputStream = null;
         final String propFileName = "/application_instance/custom_code/saviynt/Properties/externalconfig.properties";
         System.out.println("Config.properties found at :" + propFileName);
         inputStream = new FileInputStream(propFileName);
         prop.load(inputStream);*/
         /*Using csv instead of properties file 
     	InputStream inputStream = null;
        //final String propFileName = "/application_instance/custom_code/saviynt/Properties/UPNUsernameWriteback.properties";
        final String propFileName = "D:\\\\configurationsNYPAUsername.properties";
        System.out.println("Config.properties found at :" + propFileName);
        inputStream = new FileInputStream(propFileName);
        prop.load(inputStream);
         
         
         Logging logging = new Logging();
         String loggerLocation = prop.getProperty("loggerLocation");
         System.out.println(loggerLocation);
         String loggerName = prop.getProperty("loggerName");
         String loggerMaxFileSize = prop.getProperty("loggerMaxFileSize");
         int loggerMaxBackupIndex = Integer.parseInt(prop.getProperty("loggerMaxBackupIndex"));
         String logLevel = prop.getProperty("loggerLevel");
         Level loggerLevel = Level.toLevel(logLevel);
         logging.initiateLoggerSetup(loggerLocation, loggerName, loggerMaxFileSize, loggerMaxBackupIndex, loggerLevel, logger);
         
         logger.debug(logger.getName()+": "+new Date()+"::  "+"SAP Communication changes start.. Logging initiated");
         */
         CommunicationsUPN jc = new CommunicationsUPN();
         CommunicationsUPN.MyDestinationDataProvider myProvider = new CommunicationsUPN.MyDestinationDataProvider();
         JCoDestination dest = initializeConnectiontoSAP(myProvider);
         //DataBaseConnection dc = null;
         /*
         DataBaseDemo dm = new DataBaseDemo();
         dc = dm.getDatabaseConnection(prop.getProperty("URLPATH"), prop.getProperty("SAVUSERNAME"), decode(prop.getProperty("SAVPASSWORD")), prop.getProperty("JDBC_DRIVER"));
         stat = dc.conn.createStatement(1004, 1007);
         */
        try { 
        String access_token=jc.getAccessToken();
        StringBuilder result = jc.getUsers(access_token);
 	    System.out.println(result.toString());
 	    JSONObject jsonObject = new JSONObject(result.toString());
 	    JSONArray jsonArray = jsonObject.getJSONArray("userdetails");
 	    Set < HashMap < String, String >> usersDetail = new HashSet< HashMap < String, String >> ();
 	    for (int i = 0; i < jsonArray.length(); i++) {
 	    	HashMap < String, String > hm = new HashMap < String, String > ();
 	        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
 	        hm.put("username", jsonObject1.getString("username"));
 			hm.put("employeeid", jsonObject1.getString("employeeid"));
 			if(jsonObject1.has("Custom Property 20")) {
 		        hm.put("customproperty20", jsonObject1.getString("Custom Property 20"));
 		        }else {
 		        hm.put("customproperty20", null);
 		    }
 			if(jsonObject1.has("Custom Property 22")) {
 		        hm.put("customproperty22", jsonObject1.getString("Custom Property 22"));
 		        }else {
 		        hm.put("customproperty22", null);
 		    }
 	        hm.put("startdate", jsonObject1.getString("startdate"));
 	        usersDetail.add(hm);
 	    }
 	    System.out.println(usersDetail);
 	    for(HashMap<String, String> user:usersDetail)  {  	    	
 	    	System.out.println(user); 
 	    	String systemusername=user.get("username");
 	    	String employeeid=user.get("employeeid");
 	    	String customproperty20=user.get("customproperty20");
 			String customproperty22=user.get("customproperty22");
 	    	String startdate=user.get("startdate");	    
 			String UPN=null;
 	    	Timestamp startdate1 = Timestamp.valueOf(startdate);
 			if((customproperty20!=null && customproperty22!=null) || (customproperty20!=null && customproperty22==null))
 			UPN=customproperty20;
 			else if(customproperty20==null &&  customproperty22!=null)
 			UPN=customproperty22;
 			else
 			UPN=null;
 	    	System.out.println("username: " + systemusername+ ", employeeid: " + employeeid+ ", UPN :" + UPN+", startdate: " + startdate1);	
 	    	 if (employeeid != null) {
                 if (!employeeid.equals("")) {
                    jc.process(dest, employeeid, UPN, systemusername, startdate1,access_token);
                 } else {
                    System.out.println("Could not process user as employeeid is empty");
                    
                 }
              }
 	    
 		}
         
        /* HashSet<String> userkeyList = readBlob();
         String caflagchangeUserkeys = String.join(", ", userkeyList);
         if (caflagchangeUserkeys != null && !caflagchangeUserkeys.equals("")) {
            rs = stat.executeQuery("select employeeid,username,startdate, case when (customproperty20 is not null and customproperty22 is not null) then customproperty20 when customproperty20 is not null and  customproperty22 is null then customproperty20 when customproperty20 is null and  customproperty22 is not null then customproperty22  END AS customproperty20 from users where userkey in (" + caflagchangeUserkeys + ")");
            rs.last();
            int size = rs.getRow();
            System.out.println("Number of users to process: " + size);
           
            rs.beforeFirst();
            updatePst = getPreparedStatement(dc); // PSL Change: updatePst is passed as an argument to jc.process() function

            while(rs.next()) {
               try {
                  String employeeid = rs.getString("employeeid");
                  System.out.println("Processing user with employee id: " + employeeid);
                  
                  String UPN = rs.getString("customproperty20");
                  System.out.println("Updated UPN: " + UPN);
                  
                  String systemusername = rs.getString("username");
                  System.out.println("Updated username: " + systemusername);
                 
                  Timestamp startdate = rs.getTimestamp("startdate");
                  System.out.println("Updated startdate: " + startdate);
                  
                  if (employeeid != null) {
                     if (!employeeid.equals("")) {
                        jc.process(dest, employeeid, UPN, systemusername, startdate,updatePst);
                     } else {
                        System.out.println("Could not process user as employeeid is empty");
                        
                     }
                  }*/
               } catch (Exception var23) {
                  var23.printStackTrace();
                  
               }

            removedestinationfile("SAP_UPNWriteBack");
      } catch (Exception var26) {
         var26.printStackTrace();
     
      } finally {
    	 //dc.close();
         System.out.println("SAP Communication changes end");
         
      }

   }
   
   /*PSL changes: This function returns PreparedStatement used for updating CP38*/
   private static PreparedStatement getPreparedStatement(DataBaseConnection dc) throws SQLException
   {
	   PreparedStatement updatePst=null;
	   String updateSQL = "update users set customproperty38=1 where employeeid=?";
       //DataBaseConnection dc = null;
       //DataBaseDemo dm = new DataBaseDemo();
       //dc = dm.getDatabaseConnection(prop.getProperty("URLPATH"), prop.getProperty("SAVUSERNAME"), decode(prop.getProperty("SAVPASSWORD")), prop.getProperty("JDBC_DRIVER"));
       updatePst = dc.conn.prepareStatement(updateSQL);
       return updatePst;
   }

   private static void createDestinationDataFile(String destinationName, Properties connectProperties) {
      File destCfg = new File(destinationName + ".jcoDestination");

      try {
         FileOutputStream fos = new FileOutputStream(destCfg, false);
         connectProperties.store(fos, destinationName + " file !");
         fos.close();
      } catch (Exception var4) {
         var4.printStackTrace();
         
         throw new RuntimeException("Unable to create the destination files", var4);
      }
   }

   private static void removedestinationfile(String destinationName) {
      String filename = destinationName + ".jcoDestination";

      try {
         File destCfg = new File(filename);
         if (destCfg.exists()) {
            destCfg.delete();
         } else {
            System.out.println("file " + filename + " not found");
            
         }

      } catch (Exception var3) {
         var3.printStackTrace();
         
         throw new RuntimeException("Unable to remove the destination files", var3);
      }
   }

   static class MyDestinationDataProvider implements DestinationDataProvider {
      private DestinationDataEventListener eL;
      private HashMap<String, Properties> secureDBStorage = new HashMap();

      public Properties getDestinationProperties(String destinationName) {
         try {
            Properties p = (Properties)this.secureDBStorage.get(destinationName);
            if (p != null) {
               if (p.isEmpty()) {
                  System.out.println("Lam Plm JcoConnector: DataProviderException - destination configured is incorrect");
                  
                  throw new DataProviderException(Reason.INVALID_CONFIGURATION, "destination configuration is incorrect", (Throwable)null);
               } else {
                  return p;
               }
            } else {
               return null;
            }
         } catch (RuntimeException var3) {
            System.out.println("Lam Plm JcoConnector: DataProviderException");
            
            System.out.println(var3);
           
            throw new DataProviderException(Reason.INTERNAL_ERROR, var3);
         } catch (Exception var4) {
            System.out.println("Lam Plm JcoConnector:");
            
            System.out.println(var4);
            
            return null;
         }
      }

      public void setDestinationDataEventListener(DestinationDataEventListener eventListener) {
         try {
            this.eL = eventListener;
         } catch (Exception var3) {
            System.out.println("Lam Plm JcoConnector: Event listener");
            
            System.out.println(var3);
            
         }

      }

      public boolean supportsEvents() {
         return true;
      }

      void changeProperties(String destName, Properties properties) {
         try {
            synchronized(this.secureDBStorage) {
               if (properties == null) {
                  if (this.secureDBStorage.remove(destName) != null) {
                     this.eL.deleted(destName);
                  }
               } else {
                  this.secureDBStorage.put(destName, properties);
                  this.eL.updated(destName);
               }
            }
         } catch (Exception var5) {
            System.out.println("Lam Plm JcoConnector: " + var5.toString());
            
            System.out.println(var5);
            
            var5.printStackTrace();
         }

      }
   }
}