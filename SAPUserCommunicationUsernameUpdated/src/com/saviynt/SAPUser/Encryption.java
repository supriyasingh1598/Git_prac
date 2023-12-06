// 
// Decompiled by Procyon v0.5.36
// 

package com.saviynt.SAPUser;

import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.Base64;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import org.apache.log4j.Logger;

public class Encryption
{
    static String key;
    static String initVector;
    static final Logger logger;
    
    static {
        Encryption.key = "$av1ynt#$ecrects";
        Encryption.initVector = "InitialVectorVal";
        logger = Logger.getLogger((Class)Encryption.class);
    }
    
    public static String encrypt(final String key, final String initVector, final String value) {
        try {
            final IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            final SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(1, skeySpec, iv);
            final byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static String decrypt(final String key, final String initVector, final String encrypted) {
        try {
            final IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            final SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(2, skeySpec, iv);
            System.out.println("Password to decrypt" + encrypted);
            final byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(original);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static String decrypt(final String encrypted) {
        Encryption.logger.info((Object)"decrypt method Start");
        try {
            return decrypt(Encryption.key, Encryption.initVector, encrypted);
        }
        catch (Exception e) {
            e.printStackTrace();
            Encryption.logger.info((Object)"decrypt method ENd");
            return null;
        }
    }
    
    public static void main(final String[] args) {
        final Scanner scan = new Scanner(System.in);
        System.out.println("Enter Password To Encrypt : ");
        final String password = scan.next();
        System.out.println("Encrypted String : " + encrypt(Encryption.key, Encryption.initVector, password));
        System.out.println("Decrypted String : " + decrypt(Encryption.key, Encryption.initVector, encrypt(Encryption.key, Encryption.initVector, password)));
        try {
            final String encodeBase64 = Base64.getEncoder().encodeToString(password.getBytes());
            System.out.println("encodeBase64 " + encodeBase64);
            final byte[] decodeBase64 = Base64.getDecoder().decode(encodeBase64);
            System.out.println("decodeBase64 " + new String(decodeBase64, "utf-8"));
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
