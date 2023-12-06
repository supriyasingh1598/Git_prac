// 
// Decompiled by Procyon v0.5.36
// 

package com.ocis.saviynt.integration;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;

public class AESCrypt
{
    private static final String ALGORITHM = "AES";
    private static final String KEY = "1Hbfh667adfDEJ78";
    
    public static String encryptIt(final String value) {
        Key key = null;
        try {
            key = generateKey();
            final Cipher cipher = Cipher.getInstance("AES");
            cipher.init(1, key);
            final byte[] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
            final String encryptedValue64 = Base64.getEncoder().encodeToString(encryptedByteValue);
            return encryptedValue64;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String decryptIt(final String value) {
        try {
            final Key key = generateKey();
            final Cipher cipher = Cipher.getInstance("AES");
            cipher.init(2, key);
            final byte[] decryptedValue64 = Base64.getDecoder().decode(value);
            final byte[] decryptedByteValue = cipher.doFinal(decryptedValue64);
            final String decryptedValue65 = new String(decryptedByteValue, "utf-8");
            return decryptedValue65;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static Key generateKey() throws Exception {
        final Key key = new SecretKeySpec("1Hbfh667adfDEJ78".getBytes(), "AES");
        return key;
    }
    
    public static void main(final String[] args) {
        final String encryptedPassword = new AESCrypt().decryptIt("afE39mEDiaqkdL+NaPCtzA==");
        System.out.println(encryptedPassword);
    }
}
