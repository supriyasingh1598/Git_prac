package com.saviynt.SAPUser;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;


//import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Scanner;

public class Encryption {
    static String key = "$av1ynt#$ecrects"; // 128 bit key
    static String initVector = "InitialVectorVal"; // 16 bytes IV
    
    final static Logger logger = Logger.getLogger(Encryption.class);

    public static String encrypt(String key, String initVector, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            //logger.info("Encrypted String: " + Base64.getEncoder().encodeToString(encrypted));

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String decrypt(String key, String initVector, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            
            System.out.println("Password to decrypt" + encrypted);

            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String decrypt(String encrypted){
    	logger.info("decrypt method Start");
        try{
            return decrypt(key,initVector,encrypted);
        }catch(Exception e){
            e.printStackTrace();
        }
        logger.info("decrypt method ENd");
        return null;
    }


    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);
        System.out.println("Enter Password To Encrypt : " );
        String password = scan.next();
        System.out.println("Encrypted String : " + encrypt(key, initVector, password));

        System.out.println("Decrypted String : " + decrypt(key, initVector, encrypt(key, initVector, password)));
            
           try{
      //        Encode
       //     Base64.getEncoder().encodeToString("to    ".getBytes("utf-8"));
          //  byte[] encodeBase64  = Base64.encodeBase64("to  ".getBytes());
        	   
        	//String encodeBase64  = Base64.getEncoder().encodeToString("to    ".getBytes("utf-8"));
        	String encodeBase64  = Base64.getEncoder().encodeToString(password.getBytes());
        	
            System.out.println("encodeBase64 " + encodeBase64); // Output will be: c29tZSBzdHJpbmc=

            // Decode
            byte[] decodeBase64 = Base64.getDecoder().decode(encodeBase64);
            System.out.println("decodeBase64 " + new String(decodeBase64, "utf-8")); // And the output is: some string

        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

    }
}