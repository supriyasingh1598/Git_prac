// 
// Decompiled by Procyon v0.5.36
// 

package com.saviynt.SAPUser;

import com.sap.conn.jco.ext.DataProviderException;
import java.util.HashMap;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import org.apache.log4j.Level;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.util.TimeZone;
import java.util.HashSet;
import org.apache.commons.codec.binary.Base64;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoRecord;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoParameterList;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import com.sap.conn.jco.JCoContext;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoDestinationManager;
import java.util.Date;
import com.sap.conn.jco.JCoDestination;
import java.sql.PreparedStatement;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Map;
import org.json.JSONArray;

public class CommunicationsUPN
{
    static final Logger logger;
    public static Properties prop;
    public static PreparedStatement pst;
    
    static {
        logger = Logger.getLogger(CommunicationsUPN.class.getName());
        CommunicationsUPN.prop = new Properties();
    }
    
    public static JCoDestination initializeConnectiontoSAP(final MyDestinationDataProvider myProvider) {
        final Properties connectProperties = new Properties();
        JCoDestination ABAP_AS = null;
        try {
            final String msgServer = CommunicationsUPN.prop.getProperty("MESSAGESERVER");
            System.out.println(" initializeConnectiontoSAP :: MESSAGESERVER::" + msgServer);
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + " initializeConnectiontoSAP :: MESSAGESERVER::" + msgServer));
            if (msgServer != null && !msgServer.isEmpty() && msgServer.equalsIgnoreCase("true")) {
                connectProperties.setProperty("jco.client.r3name", CommunicationsUPN.prop.getProperty("JCO_R3NAME"));
                connectProperties.setProperty("jco.client.mshost", CommunicationsUPN.prop.getProperty("JCO_MSHOST"));
                connectProperties.setProperty("jco.client.msserv", CommunicationsUPN.prop.getProperty("JCO_MSSERV"));
                connectProperties.setProperty("jco.client.group", CommunicationsUPN.prop.getProperty("JCO_GROUP"));
            }
            else {
                connectProperties.setProperty("jco.client.ashost", CommunicationsUPN.prop.getProperty("JCO_ASHOST"));
                connectProperties.setProperty("jco.client.sysnr", CommunicationsUPN.prop.getProperty("JCO_SYSNR"));
            }
            connectProperties.setProperty("jco.client.client", CommunicationsUPN.prop.getProperty("JCO_CLIENT"));
            connectProperties.setProperty("jco.client.user", CommunicationsUPN.prop.getProperty("JCO_USER"));
            connectProperties.setProperty("jco.client.passwd", decode(CommunicationsUPN.prop.getProperty("JCO_PASSWD")));
            connectProperties.setProperty("jco.client.lang", CommunicationsUPN.prop.getProperty("JCO_LANG"));
            final String destName = "SAP_UPNWriteBack";
            final JCoFunction userChangeFunction = null;
            removedestinationfile(destName);
            createDestinationDataFile(destName, connectProperties);
            ABAP_AS = JCoDestinationManager.getDestination(destName);
            System.out.println(ABAP_AS.getAttributes());
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + ABAP_AS.getAttributes()));
            ABAP_AS.ping();
            System.out.println("Connection to SAP successful!");
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Connection to SAP successful!"));
            return ABAP_AS;
        }
        catch (Exception var6) {
            System.out.println("Connection to SAP unsuccessful");
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Connection to SAP unsuccessful"));
            var6.printStackTrace();
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + var6));
            return null;
        }
    }
    
    public Boolean checkUPNupdate(final JCoDestination ABAP_AS, final String employeeid) {
        System.out.println("checkUPNupdate :: Method Started");
        CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "checkUPNupdate :: Method Started"));
        Boolean check = false;
        try {
            final JCoFunction userChangeFunction6 = ABAP_AS.getRepository().getFunction("BAPI_EMPLCOMM_GETDETAILEDLIST");
            System.out.println("checkUPNupdate :: BAPI_EMPLCOMM_GETDETAILEDLIST function received");
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "checkUPNupdate :: BAPI_EMPLCOMM_GETDETAILEDLIST function received"));
            userChangeFunction6.getImportParameterList().setValue("EMPLOYEENUMBER", employeeid);
            userChangeFunction6.getImportParameterList().setValue("SUBTYPE", "0030");
            userChangeFunction6.execute(ABAP_AS);
            final JCoTable userCommDetail = userChangeFunction6.getTableParameterList().getTable("COMMUNICATION");
            System.out.println("Usercomm details after executing userChangeFunction6 " + userCommDetail.toString());
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Usercomm details after executing userChangeFunction6 " + userCommDetail.toString()));
            System.out.println("No of rows for existing record " + userCommDetail.getNumRows());
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "No of rows for existing record " + userCommDetail.getNumRows()));
            if (userCommDetail.getNumRows() > 0) {
                check = true;
            }
        }
        catch (JCoException var6) {
            var6.printStackTrace();
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + var6));
        }
        return check;
    }
    
    public JCoDestination process(final JCoDestination ABAP_AS, final String employeeid, final String UPN, final String systemusername, final Timestamp startdate, final PreparedStatement updatePst) {
        JCoFunction userChangeFunction = null;
        JCoFunction userChangeFunction2 = null;
        Label_4898: {
            try {
                new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.ENGLISH);
                final DateFormat targetFormat = new SimpleDateFormat("dd.MM.yyyy");
                userChangeFunction = ABAP_AS.getRepository().getFunction("BAPI_EMPLOYEE_ENQUEUE");
                if (userChangeFunction == null) {
                    System.out.println("JcoConnector: BAPI_EMPLOYEE_ENQUEUE not found in SAP");
                    CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "JcoConnector: BAPI_EMPLOYEE_ENQUEUE not found in SAP"));
                    throw new RuntimeException("BAPI_EMPLOYEE_ENQUEUE not found in SAP");
                }
                System.out.println("JcoConnector: BAPI_EMPLOYEE_ENQUEUE  found in SAP");
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "JcoConnector: BAPI_EMPLOYEE_ENQUEUE  found in SAP"));
                System.out.println("BAPI_EMPLOYEE_ENQUEUE executing for user with employeeid: " + employeeid);
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "BAPI_EMPLOYEE_ENQUEUE executing for user with employeeid: " + employeeid));
                final JCoParameterList ipl = userChangeFunction.getImportParameterList();
                userChangeFunction.getImportParameterList().setValue("NUMBER", employeeid);
                JCoFunction userChangeFunction3 = null;
                userChangeFunction3 = ABAP_AS.getRepository().getFunction("BAPI_EMPLCOMM_GETDETAILEDLIST");
                if (userChangeFunction3 == null) {
                    System.out.println("JcoConnector: BAPI_EMPLCOMM_GETDETAILEDLIST not found in SAP");
                    CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "JcoConnector: BAPI_EMPLCOMM_GETDETAILEDLIST not found in SAP"));
                    throw new RuntimeException("BAPI_EMPLCOMM_GETDETAILEDLIST not found in SAP");
                }
                System.out.println("JcoConnector: BAPI_EMPLCOMM_GETDETAILEDLIST  found in SAP");
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "JcoConnector: BAPI_EMPLCOMM_GETDETAILEDLIST  found in SAP"));
                System.out.println("BAPI_EMPLCOMM_GETDETAILEDLIST executing for user with employeeid: " + employeeid);
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "BAPI_EMPLCOMM_GETDETAILEDLIST executing for user with employeeid: " + employeeid));
                userChangeFunction3.getImportParameterList().setValue("EMPLOYEENUMBER", employeeid);
                userChangeFunction3.execute(ABAP_AS);
                final JCoTable userCommDetail = userChangeFunction3.getTableParameterList().getTable("COMMUNICATION");
                final LinkedList l = new LinkedList();
                for (int i = 0; i < userCommDetail.getNumRows(); ++i) {
                    userCommDetail.setRow(i);
                    final JCoFieldIterator iter = userCommDetail.getFieldIterator();
                    final LinkedHashMap m = new LinkedHashMap();
                    System.out.println("Existing user field values :");
                    CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Existing user field values :"));
                    while (iter.hasNextField()) {
                        final JCoField f = iter.nextField();
                        System.out.println(String.valueOf(f.getName()) + "---" + userCommDetail.getValue(f.getName()));
                        CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + f.getName() + "---" + userCommDetail.getValue(f.getName())));
                        m.put(f.getName(), userCommDetail.getValue(f.getName()));
                    }
                    l.add(m);
                }
                JCoFunction userChangeFunction4 = null;
                final JCoFieldIterator iter = null;
                final LinkedHashMap m = null;
                final JCoField f = null;
                userChangeFunction2 = ABAP_AS.getRepository().getFunction("BAPI_EMPLOYEE_DEQUEUE");
                if (userChangeFunction2 == null) {
                    System.out.println("JcoConnector: BAPI_EMPLOYEE_DEQUEUE not found in SAP");
                    CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "JcoConnector: BAPI_EMPLOYEE_DEQUEUE not found in SAP"));
                    throw new RuntimeException("BAPI_EMPLOYEE_DEQUEUE not found in SAP");
                }
                System.out.println("JcoConnector: BAPI_EMPLOYEE_DEQUEUE  found in SAP");
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "JcoConnector: BAPI_EMPLOYEE_DEQUEUE  found in SAP"));
                System.out.println("BAPI_EMPLCOMM_GETDETAILEDLIST executing for user with employeeid: " + employeeid);
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "BAPI_EMPLCOMM_GETDETAILEDLIST executing for user with employeeid: " + employeeid));
                userChangeFunction2.getImportParameterList().setValue("NUMBER", employeeid);
                JCoContext.begin(ABAP_AS);
                userChangeFunction.execute(ABAP_AS);
                if (!isBapiReturnCodeOkay(userChangeFunction)) {
                    System.out.println("User lock failure: " + isBapiReturnCodeOkay(userChangeFunction));
                    CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "User lock failure: " + isBapiReturnCodeOkay(userChangeFunction)));
                }
                else {
                    System.out.println("User lock successful for user with employeeid " + employeeid);
                    CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "User lock successful for user with employeeid " + employeeid));
                }
                userChangeFunction3.execute(ABAP_AS);
                if (!isBapiReturnCodeOkay(userChangeFunction3)) {
                    System.out.println("Got details for user with employeeid " + employeeid);
                    CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Got details for user with employeeid " + employeeid));
                }
                else {
                    System.out.println("Got details for user with employeeid " + employeeid);
                    CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Got details for user with employeeid " + employeeid));
                }
                try {
                    userChangeFunction4.execute(ABAP_AS);
                }
                catch (Exception var28) {
                    final Boolean isUPNAvailable = this.checkUPNupdate(ABAP_AS, employeeid);
                    if (isUPNAvailable) {
                        for (int j = 0; j < l.size(); ++j) {
                            new LinkedHashMap();
                            final LinkedHashMap<Object, Object> lhm = (LinkedHashMap<Object, Object>) l.get(j);
                            final Set<Object> keys = lhm.keySet();
                            final Object value = lhm.get("SUBTYPE");
                            if (value.equals("0030")) {
                                System.out.println("Business Email subtype present with employeeid " + employeeid);
                                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Business Email subtype present with employeeid " + employeeid));
                                final JCoFunction userChangeFunction5 = ABAP_AS.getRepository().getFunction("BAPI_EMPLCOMM_CREATE");
                                if (userChangeFunction5 == null) {
                                    System.out.println("JcoConnector: BAPI_EMPLCOMM_CHANGE not found in SAP");
                                    CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "JcoConnector: BAPI_EMPLCOMM_CHANGE not found in SAP"));
                                    throw new RuntimeException("BAPI_EMPLCOMM_CHANGE not found in SAP");
                                }
                                System.out.println("JcoConnector: BAPI_EMPLCOMM_CHANGE  found in SAP");
                                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "JcoConnector: BAPI_EMPLCOMM_CHANGE  found in SAP"));
                                System.out.println("BAPI_EMPLCOMM_GETDETAILEDLIST executing for user with employeeid: " + employeeid);
                                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "BAPI_EMPLCOMM_GETDETAILEDLIST executing for user with employeeid: " + employeeid));
                                System.out.println("Setting values before calling BAPI_EMPLCOMM_CHANGE");
                                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Setting values before calling BAPI_EMPLCOMM_CHANGE"));
                                for (final Object k : keys) {
                                    final String kString = (String)k;
                                    final JCoParameterList ipl2 = userChangeFunction5.getImportParameterList();
                                    userChangeFunction5.getImportParameterList().setValue("NOCOMMIT", "");
                                    if (kString.equalsIgnoreCase("EMPLOYEENO")) {
                                        userChangeFunction5.getImportParameterList().setValue("EMPLOYEENUMBER", lhm.get(k));
                                    }
                                    else if (kString.equalsIgnoreCase("ID")) {
                                        userChangeFunction5.getImportParameterList().setValue("COMMUNICATIONID", lhm.get(k));
                                    }
                                    else if (kString.equalsIgnoreCase("SUBTYPE")) {
                                        userChangeFunction5.getImportParameterList().setValue("SUBTYPE", lhm.get(k));
                                    }
                                    else if (kString.equalsIgnoreCase("VALIDBEGIN")) {
                                        System.out.println("begin date " + targetFormat.format(startdate));
                                        CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "begin date " + targetFormat.format(startdate)));
                                        final Date dateE = new SimpleDateFormat("dd.MM.yyyy").parse(targetFormat.format(lhm.get(k)));
                                        userChangeFunction5.getImportParameterList().setValue("VALIDITYBEGIN", (Object)dateE);
                                    }
                                    else if (kString.equalsIgnoreCase("VALIDEND")) {
                                        System.out.println("end date " + targetFormat.format(lhm.get(k)));
                                        CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "end date " + targetFormat.format(lhm.get(k))));
                                        final Date dateE = new SimpleDateFormat("dd.MM.yyyy").parse(targetFormat.format(new Date()));
                                        final SimpleDateFormat sdfNeeded = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
                                        final int currentStartDate = dateE.getDate();
                                        final int newEnd = currentStartDate - 1;
                                        dateE.setDate(newEnd);
                                        final String dateEformatted = sdfNeeded.format(dateE);
                                        final Date Updated = sdfNeeded.parse(dateEformatted);
                                        System.out.println("Updated end date " + Updated);
                                        CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Updated end date " + Updated));
                                        userChangeFunction5.getImportParameterList().setValue("VALIDITYEND", (Object)Updated);
                                    }
                                    System.out.println(k + " -- " + lhm.get(k));
                                }
                                userChangeFunction5.execute(ABAP_AS);
                                System.out.println("Executed BAPI_EMPLCOMM_CHANGE for updating SUBTYPE 0030 since it is found");
                                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Executed BAPI_EMPLCOMM_CHANGE for updating SUBTYPE 0030 since it is found"));
                                if (!isBapiReturnCodeOkay(userChangeFunction5)) {
                                    System.out.println("Could not update UPN for user with employeeid " + employeeid);
                                    CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Could not update UPN for user with employeeid " + employeeid));
                                    System.out.println("UPN updation failure: " + isBapiReturnCodeOkay(userChangeFunction5));
                                    CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "UPN updation failure: " + isBapiReturnCodeOkay(userChangeFunction5)));
                                }
                                else {
                                    System.out.println("UPN successsfully updated for user with employeeid " + employeeid);
                                    CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "UPN successsfully updated for user with employeeid " + employeeid));
                                }
                            }
                        }
                    }
                    if (isUPNAvailable) {
                        userChangeFunction4 = ABAP_AS.getRepository().getFunction("BAPI_EMPLCOMM_CREATESUCCESSOR");
                    }
                    else {
                        userChangeFunction4 = ABAP_AS.getRepository().getFunction("BAPI_EMPLCOMM_CREATE");
                    }
                    userChangeFunction4.getImportParameterList().setValue("EMPLOYEENUMBER", employeeid);
                    userChangeFunction4.getImportParameterList().setValue("COMMUNICATIONID", UPN);
                    userChangeFunction4.getImportParameterList().setValue("NOCOMMIT", "");
                    userChangeFunction4.getImportParameterList().setValue("SUBTYPE", "0030");
                    final Date dt = new Date();
                    final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                    sdf.format(dt);
                    Date startDate;
                    if (isUPNAvailable) {
                        startDate = new SimpleDateFormat("dd.MM.yyyy").parse(targetFormat.format(new Date()));
                    }
                    else {
                        startDate = new SimpleDateFormat("dd.MM.yyyy").parse(targetFormat.format(startdate));
                    }
                    userChangeFunction4.getImportParameterList().setValue("VALIDITYBEGIN", (Object)startDate);
                    new Date();
                    final SimpleDateFormat sdfNow = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                    final SimpleDateFormat sdfNeeded2 = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
                    final Calendar c = new GregorianCalendar();
                    c.set(9999, 11, 31, 23, 59, 59);
                    final Date dateE = c.getTime();
                    final Date date3 = sdfNow.parse(dateE.toString());
                    final String date4 = sdfNeeded2.format(date3);
                    final Date date5 = sdfNeeded2.parse(date4);
                    userChangeFunction4.getImportParameterList().setValue("VALIDITYEND", (Object)date5);
                    userChangeFunction4.execute(ABAP_AS);
                    System.out.println("Creating SUBTYPE 0030 since it is not found");
                    CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Creating SUBTYPE 0030 since it is not found"));
                }
                if (!isBapiReturnCodeOkay(userChangeFunction4)) {
                    System.out.println("Could not create a successor for UPN for user with employeeid " + employeeid);
                    CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Could not create a successor for UPN for user with employeeid " + employeeid));
                    System.out.println("UPN addition failure: " + isBapiReturnCodeOkay(userChangeFunction4));
                    CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "UPN addition failure: " + isBapiReturnCodeOkay(userChangeFunction4)));
                    break Label_4898;
                }
                System.out.println("UPN successsfully added for user with employeeid " + employeeid);
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "UPN successsfully added for user with employeeid " + employeeid));
                updatePst.setString(1, employeeid);
                System.out.println("Executing statement: " + updatePst.toString());
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Executing statement: " + updatePst.toString()));
                updatePst.executeUpdate();
                System.out.println("Statement executed successfully");
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Statement executed successfully"));
            }
            catch (IllegalStateException var25) {
                System.out.println("JcoConnector: " + var25.toString());
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "JcoConnector: " + var25.toString()));
                throw new Error(var25);
            }
            catch (JCoException var26) {
                System.out.println("JcoConnector: " + var26.toString());
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "JcoConnector: " + var26.toString()));
                System.out.println(var26);
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + var26));
                var26.printStackTrace();
            }
            catch (Exception var27) {
                System.out.println("JcoConnector: " + var27.toString());
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "JcoConnector: " + var27.toString()));
                System.out.println(var27);
                var27.printStackTrace();
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + var27));
            }
            finally {
                try {
                    userChangeFunction2.execute(ABAP_AS);
                    if (!isBapiReturnCodeOkay(userChangeFunction2)) {
                        System.out.println("User unlock failure: " + isBapiReturnCodeOkay(userChangeFunction2));
                        CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "User unlock failure: " + isBapiReturnCodeOkay(userChangeFunction2)));
                    }
                    else {
                        System.out.println("User unlock successful for user with employeeid " + employeeid);
                        CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "User unlock successful for user with employeeid " + employeeid));
                    }
                    JCoContext.end(ABAP_AS);
                }
                catch (Exception e) {
                    System.out.println("Exception occured while User Unlock");
                    e.printStackTrace();
                    CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + e));
                }
            }
            try {
                userChangeFunction2.execute(ABAP_AS);
                if (!isBapiReturnCodeOkay(userChangeFunction2)) {
                    System.out.println("User unlock failure: " + isBapiReturnCodeOkay(userChangeFunction2));
                    CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "User unlock failure: " + isBapiReturnCodeOkay(userChangeFunction2)));
                }
                else {
                    System.out.println("User unlock successful for user with employeeid " + employeeid);
                    CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "User unlock successful for user with employeeid " + employeeid));
                }
                JCoContext.end(ABAP_AS);
            }
            catch (Exception e) {
                System.out.println("Exception occured while User Unlock");
                e.printStackTrace();
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + e));
            }
        }
        return ABAP_AS;
    }
    
    public static boolean isBapiReturnCodeOkay(final JCoRecord object) {
        final JCoStructure isStructure = null;
        JCoTable isTable = null;
        try {
            if (object instanceof JCoStructure) {
                return object.getString("TYPE").equals("") || object.getString("TYPE").equals("S");
            }
            if (object instanceof JCoTable) {
                isTable = (JCoTable)object;
                final int count = isTable.getNumRows();
                if (count == 0) {
                    return true;
                }
                boolean allOkay = true;
                for (int i = 0; i < count; ++i) {
                    isTable.setRow(i);
                    if (!isTable.getString("TYPE").equals("") && !isTable.getString("TYPE").equals("S") && !isTable.getString("TYPE").equals("I")) {
                        System.out.println("JcoConnector: Error st" + isTable.getString("MESSAGE").toString());
                        CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "JcoConnector: Error st" + isTable.getString("MESSAGE").toString()));
                        System.out.println("JcoConnector: Error st" + isTable.toString());
                        CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "JcoConnector: Error st" + isTable.toString()));
                        allOkay = false;
                        break;
                    }
                }
                return allOkay;
            }
        }
        catch (UnsatisfiedLinkError var6) {
            System.err.println("ERROR!!!\n" + var6);
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "ERROR!!!\n" + var6));
            System.exit(1);
        }
        catch (Exception var7) {
            System.out.println(var7);
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + var7));
            return false;
        }
        return false;
    }
    
    public static boolean isBapiReturnCodeOkay(final JCoFunction function) {
        final JCoParameterList exports = function.getExportParameterList();
        try {
            if (exports != null) {
                System.out.println("JcoConnector:" + exports.getValue("RETURN"));
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "JcoConnector:" + exports.getValue("RETURN")));
                return isBapiReturnCodeOkay((JCoRecord)exports.getStructure("RETURN"));
            }
            throw new RuntimeException("Return not a structure");
        }
        catch (Exception var6) {
            final JCoParameterList tables = function.getTableParameterList();
            try {
                return tables != null && isBapiReturnCodeOkay((JCoRecord)tables.getTable("RETURN"));
            }
            catch (Exception var5) {
                var5.printStackTrace();
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + var5));
                return false;
            }
        }
    }
    
    public static String decode(final String strToDecrypt) {
        final byte[] key = "S@v!ynt_s@V!YNt_".getBytes();
        if (strToDecrypt != null) {
            try {
                final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
                final SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
                cipher.init(2, secretKey);
                return new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt.getBytes())));
            }
            catch (Exception var4) {
                var4.printStackTrace();
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + var4));
            }
        }
        return null;
    }
    
    public static String encode(final String strToEncrypt) {
        final byte[] key = "S@v!ynt_s@V!YNt_".getBytes();
        try {
            final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            final SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            cipher.init(1, secretKey);
            return Base64.encodeBase64String(cipher.doFinal(strToEncrypt.getBytes()));
        }
        catch (Exception var4) {
            var4.printStackTrace();
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + var4));
            return null;
        }
    }
    
    public static HashSet<String> readBlob() throws IOException, SQLException {
        ResultSet rs3 = null;
        String selectSQL = "";
        final HashSet caflagchangeList = new HashSet();
        try {
            final SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd");
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
            final SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MMM-dd");
            final Calendar cal = Calendar.getInstance();
            cal.add(5, -2);
            final Date currentdate = dateFormatLocal.parse(dateFormatGmt.format(cal.getTime()));
            final java.sql.Date sqlStartDate = new java.sql.Date(currentdate.getTime());
            System.out.println(sqlStartDate);
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + sqlStartDate));
            selectSQL = "SELECT uh.changelog,uh.userkey,u.customproperty20,u.customproperty22 FROM usershistory uh,users u WHERE uh.userkey=u.userkey and uh.updatedate >= ? and u.customproperty38 is null";
            DataBaseConnection dc = null;
            final DataBaseDemo dm = new DataBaseDemo();
            dc = dm.getDatabaseConnection(CommunicationsUPN.prop.getProperty("URLPATH"), CommunicationsUPN.prop.getProperty("SAVUSERNAME"), decode(CommunicationsUPN.prop.getProperty("SAVPASSWORD")), CommunicationsUPN.prop.getProperty("JDBC_DRIVER"));
            final Statement stat = dc.conn.createStatement(1004, 1007);
            (CommunicationsUPN.pst = dc.conn.prepareStatement(selectSQL)).setDate(1, sqlStartDate);
            System.out.println("Executing statement: " + CommunicationsUPN.pst.toString());
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Executing statement: " + CommunicationsUPN.pst.toString()));
            rs3 = CommunicationsUPN.pst.executeQuery();
            System.out.println("Statement executed successfully");
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Statement executed successfully"));
            while (rs3.next()) {
                final Object object = rs3.getObject(1);
                final String className = object.getClass().getName();
                final Blob myBlob = rs3.getBlob("changelog");
                final String userkey = rs3.getString("userkey");
                final InputStream input = myBlob.getBinaryStream();
                final byte[] buffer = new byte[4096];
                ObjectInputStream objectIn = null;
                while (input.read(buffer) > 0) {
                    try {
                        objectIn = new ObjectInputStream(new ByteArrayInputStream(buffer));
                        final Object deSerializedObject = objectIn.readObject();
                        System.out.println("Blob data: " + deSerializedObject);
                        CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Blob data: " + deSerializedObject));
                        final String blobdata = deSerializedObject.toString();
                        System.out.println("Blob data: " + blobdata);
                        CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Blob data: " + blobdata));
                        if (!blobdata.contains("customproperty20") && !blobdata.contains("customproperty22")) {
                            continue;
                        }
                        caflagchangeList.add(userkey);
                    }
                    catch (Exception var26) {
                        var26.printStackTrace();
                        CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + var26));
                    }
                }
            }
            rs3.close();
            selectSQL = "SELECT u.username,u.userkey,u.customproperty20,u.customproperty22,u.customproperty36,u.customproperty37 FROM users u WHERE (((u.customproperty20!=u.customproperty36) or (u.customproperty20 is null and u.customproperty36 is not null) or (u.customproperty20 is not null and u.customproperty36 is null)) or ((u.customproperty22!=u.customproperty37) or (u.customproperty22 is null and u.customproperty37 is not null) or (u.customproperty22 is not null and u.customproperty37 is null))) and u.customproperty38 is null";
            CommunicationsUPN.pst = dc.conn.prepareStatement(selectSQL);
            System.out.println("Executing statement: " + CommunicationsUPN.pst.toString());
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Executing statement: " + CommunicationsUPN.pst.toString()));
            rs3 = CommunicationsUPN.pst.executeQuery();
            System.out.println("Statement executed successfully");
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Statement executed successfully"));
            while (rs3.next()) {
                final String userkey2 = rs3.getString("userkey");
                System.out.println("CP36/37 Change : Userkey selected: " + userkey2);
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "CP36/37 Change : Userkey selected: " + userkey2));
                caflagchangeList.add(userkey2);
            }
            final String caflagchangeUserkeys = String.join(", ", caflagchangeList);
            System.out.println("Userkeys to be processed : " + caflagchangeUserkeys);
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Userkeys to be processed : " + caflagchangeUserkeys));
        }
        catch (Exception var27) {
            System.out.println(var27.getMessage());
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + var27.getMessage()));
        }
        try {
            if (rs3 != null) {
                rs3.close();
            }
        }
        catch (SQLException var28) {
            System.out.println(var28.getMessage());
            var28.printStackTrace();
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + var28));
        }
        return (HashSet<String>)caflagchangeList;
    }
    
    
    private String getAccessToken() {

  		System.out.println("Entering...");
  		String access_token = null;
  		HttpURLConnection conn = null;
  		BufferedReader br = null;
  		String ACCESS_TOKEN_URL=prop.getProperty("ACCESS_TOKEN_URL");
  		try {
  			URL url = new URL((ACCESS_TOKEN_URL));
  			conn = (HttpURLConnection) url.openConnection();
  			conn.setDoOutput(true);
  			conn.setRequestMethod("POST");
  			conn.setRequestProperty("Content-Type", "application/json");
  			String saviyntUsername = decode(prop.getProperty("SSM_USER"));
  			String saviyntPassword = decode(prop.getProperty("SSM_PASS"));

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
		String GET_USER_DETAILS=prop.getProperty("GET_USER_DETAILS");
		HttpURLConnection conn = null;
		String access_token = this.getAccessToken();
		System.out.println(GET_USER_DETAILS);
		try {
			conn = this.getSavConnection(GET_USER_DETAILS, "POST", access_token);
			System.out.println(GET_USER_DETAILS + "..." + access_token);

			String getUserFilter = "{\"userQuery\" : \"(((user.customproperty20!=user.customproperty36) or (user.customproperty20 is null and user.customproperty36 is not null) or (user.customproperty20 is not null and user.customproperty36 is null)) or ((user.customproperty22!=user.customproperty37) or (user.customproperty22 is null and user.customproperty37 is not null) or (user.customproperty22 is not null and user.customproperty37 is null))) and user.customproperty38 is null\",\"responsefields\" : [\"username\",\"userkey\",\"customproperty20\",\"customproperty22\",\"customproperty36\",\"customproperty37\"] }";
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
    
    public void updateUser(String access_token,String userkey,String systemusername) {


		System.out.println("Entering...");

		BufferedReader br = null;
		
		String UPDATE_USER_URL=prop.getProperty("UPDATE_USER_URL");

		HttpURLConnection conn = null;

		String access_token = this.getAccessToken();

		try {

			conn = this.getSavConnection(UPDATE_USER_URL, "POST", access_token);

			System.out.println(UPDATE_USER_URL + "..." + access_token);

			String updateUserDetails = "{\"username\" : \"" + systemusername + "\",\"userkey\" : \"" + userkey + "\",\"customproperty38\" : \"1\"}";

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
	
    
    public static void main(final String[] args) throws SQLException {
        DataBaseConnection dc = null;
        ResultSet rs = null;
        Statement stat = null;
        PreparedStatement updatePst = null;
        try {
            System.out.println("SAP Communication changes start..");
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "SAP Communication changes start.."));
            final String saviyntHome = System.getenv("SAVIYNT_HOME");
            System.out.println("Saviynt home configured at this location :" + saviyntHome);
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Saviynt home configured at this location :" + saviyntHome));
            final InputStream input = new FileInputStream(String.valueOf(saviyntHome) + File.separator + "configurationsNYPA.properties");
            CommunicationsUPN.prop.load(input);
            final Logging logging = new Logging();
            final String loggerLocation = CommunicationsUPN.prop.getProperty("loggerLocation");
            System.out.println(loggerLocation);
            final String loggerName = CommunicationsUPN.prop.getProperty("loggerName");
            final String loggerMaxFileSize = CommunicationsUPN.prop.getProperty("loggerMaxFileSize");
            final int loggerMaxBackupIndex = Integer.parseInt(CommunicationsUPN.prop.getProperty("loggerMaxBackupIndex"));
            final String logLevel = CommunicationsUPN.prop.getProperty("loggerLevel");
            final Level loggerLevel = Level.toLevel(logLevel);
            logging.initiateLoggerSetup(loggerLocation, loggerName, loggerMaxFileSize, loggerMaxBackupIndex, loggerLevel, CommunicationsUPN.logger);
            //CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "SAP Communication changes start.. Logging initiated"));
            final CommunicationsUPN jc = new CommunicationsUPN();
            final MyDestinationDataProvider myProvider = new MyDestinationDataProvider();
            final JCoDestination dest = initializeConnectiontoSAP(myProvider);
            final DataBaseDemo dm = new DataBaseDemo();
            dc = dm.getDatabaseConnection(CommunicationsUPN.prop.getProperty("URLPATH"), CommunicationsUPN.prop.getProperty("SAVUSERNAME"), decode(CommunicationsUPN.prop.getProperty("SAVPASSWORD")), CommunicationsUPN.prop.getProperty("JDBC_DRIVER"));
            stat = dc.conn.createStatement(1004, 1007);
            final HashSet<String> userkeyList = readBlob();
            final String caflagchangeUserkeys = String.join(", ", userkeyList);
            if (caflagchangeUserkeys != null && !caflagchangeUserkeys.equals("")) {
                rs = stat.executeQuery("select employeeid,username,startdate, case when (customproperty20 is not null and customproperty22 is not null and companyname='6510') then customproperty22 when (customproperty20 is not null and customproperty22 is null) then customproperty20 when (customproperty20 is not null and customproperty22 is not null and companyname!='6510') then customproperty20 when (customproperty20 is null and customproperty22 is not null) then customproperty22 END AS customproperty20 from users where userkey in (" + caflagchangeUserkeys + ")");
                rs.last();
                final int size = rs.getRow();
                System.out.println("Number of users to process: " + size);
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Number of users to process: " + size));
                rs.beforeFirst();
                updatePst = getPreparedStatement(dc);
                while (rs.next()) {
                    try {
                        final String employeeid = rs.getString("employeeid");
                        System.out.println("Processing user with employee id: " + employeeid);
                        CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Processing user with employee id: " + employeeid));
                        final String UPN = rs.getString("customproperty20");
                        System.out.println("Updated UPN: " + UPN);
                        CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Updated UPN: " + UPN));
                        final String systemusername = rs.getString("username");
                        System.out.println("Updated username: " + systemusername);
                        CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Updated username: " + systemusername));
                        final Timestamp startdate = rs.getTimestamp("startdate");
                        System.out.println("Updated startdate: " + startdate);
                        CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Updated startdate: " + startdate));
                        if (employeeid == null) {
                            continue;
                        }
                        if (!employeeid.equals("")) {
                            jc.process(dest, employeeid, UPN, systemusername, startdate, updatePst);
                        }
                        else {
                            System.out.println("Could not process user as employeeid is empty");
                            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + ": " + new Date() + "::  " + "Could not process user as employeeid is empty"));
                        }
                    }
                    catch (Exception var23) {
                        var23.printStackTrace();
                        CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + var23));
                    }
                }
                removedestinationfile("SAP_UPNWriteBack");
            }
        }
        catch (SQLException var24) {
            var24.printStackTrace();
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + var24));
        }
        catch (FileNotFoundException var25) {
            var25.printStackTrace();
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + var25));
        }
        catch (Exception var26) {
            var26.printStackTrace();
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + var26));
        }
        finally {
            dc.close();
            System.out.println("SAP Communication changes end");
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "SAP Communication changes end"));
        }
        dc.close();
        System.out.println("SAP Communication changes end");
        CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "SAP Communication changes end"));
    }
    
    private static PreparedStatement getPreparedStatement(final DataBaseConnection dc) throws SQLException {
        PreparedStatement updatePst = null;
        final String updateSQL = "update users set customproperty38=1 where employeeid=?";
        updatePst = dc.conn.prepareStatement(updateSQL);
        return updatePst;
    }
    
    private static void createDestinationDataFile(final String destinationName, final Properties connectProperties) {
        final File destCfg = new File(String.valueOf(destinationName) + ".jcoDestination");
        try {
            final FileOutputStream fos = new FileOutputStream(destCfg, false);
            connectProperties.store(fos, String.valueOf(destinationName) + " file !");
            fos.close();
        }
        catch (Exception var4) {
            var4.printStackTrace();
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + var4));
            throw new RuntimeException("Unable to create the destination files", var4);
        }
    }
    
    private static void removedestinationfile(final String destinationName) {
        final String filename = String.valueOf(destinationName) + ".jcoDestination";
        try {
            final File destCfg = new File(filename);
            if (destCfg.exists()) {
                destCfg.delete();
            }
            else {
                System.out.println("file " + filename + " not found");
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "file " + filename + " not found"));
            }
        }
        catch (Exception var3) {
            var3.printStackTrace();
            CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + var3));
            throw new RuntimeException("Unable to remove the destination files", var3);
        }
    }
    
    static class MyDestinationDataProvider implements DestinationDataProvider
    {
        private DestinationDataEventListener eL;
        private HashMap<String, Properties> secureDBStorage;
        
        MyDestinationDataProvider() {
            this.secureDBStorage = new HashMap<String, Properties>();
        }
        
        public Properties getDestinationProperties(final String destinationName) {
            try {
                final Properties p = this.secureDBStorage.get(destinationName);
                if (p == null) {
                    return null;
                }
                if (p.isEmpty()) {
                    System.out.println("Lam Plm JcoConnector: DataProviderException - destination configured is incorrect");
                    CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Lam Plm JcoConnector: DataProviderException - destination configured is incorrect"));
                    throw new DataProviderException(DataProviderException.Reason.INVALID_CONFIGURATION, "destination configuration is incorrect", (Throwable)null);
                }
                return p;
            }
            catch (RuntimeException var3) {
                System.out.println("Lam Plm JcoConnector: DataProviderException");
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Lam Plm JcoConnector: DataProviderException"));
                System.out.println(var3);
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + var3));
                throw new DataProviderException(DataProviderException.Reason.INTERNAL_ERROR, (Throwable)var3);
            }
            catch (Exception var4) {
                System.out.println("Lam Plm JcoConnector:");
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Lam Plm JcoConnector:"));
                System.out.println(var4);
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + var4));
                return null;
            }
        }
        
        public void setDestinationDataEventListener(final DestinationDataEventListener eventListener) {
            try {
                this.eL = eventListener;
            }
            catch (Exception var3) {
                System.out.println("Lam Plm JcoConnector: Event listener");
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Lam Plm JcoConnector: Event listener"));
                System.out.println(var3);
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + var3));
            }
        }
        
        public boolean supportsEvents() {
            return true;
        }
        
        void changeProperties(final String destName, final Properties properties) {
            try {
                synchronized (this.secureDBStorage) {
                    if (properties == null) {
                        if (this.secureDBStorage.remove(destName) != null) {
                            this.eL.deleted(destName);
                        }
                    }
                    else {
                        this.secureDBStorage.put(destName, properties);
                        this.eL.updated(destName);
                    }
                }
                // monitorexit(this.secureDBStorage)
            }
            catch (Exception var5) {
                System.out.println("Lam Plm JcoConnector: " + var5.toString());
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + "Lam Plm JcoConnector: " + var5.toString()));
                System.out.println(var5);
                CommunicationsUPN.logger.debug((Object)(String.valueOf(CommunicationsUPN.logger.getName()) + ": " + new Date() + "::  " + var5));
                var5.printStackTrace();
            }
        }
    }
}
