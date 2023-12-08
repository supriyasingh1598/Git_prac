package com.Integration.Test;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class AESCrypt {

	// final static Logger logger = Logger.getLogger(SaviyntMain.class.getName());
	private static final String ALGORITHM = "AES";
	private static final String KEY = "1Hbfh667adfDEJ78";

	public static String encryptIt(String value) {
		Key key = null;
		try {
			key = generateKey();
			Cipher cipher = Cipher.getInstance(AESCrypt.ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
			String encryptedValue64 = new BASE64Encoder().encode(encryptedByteValue);
			return encryptedValue64;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String decryptIt(String value) {
		try {
			Key key = generateKey();
			Cipher cipher = Cipher.getInstance(AESCrypt.ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] decryptedValue64 = new BASE64Decoder().decodeBuffer(value);
			byte[] decryptedByteValue = cipher.doFinal(decryptedValue64);
			String decryptedValue = new String(decryptedByteValue, "utf-8");
			return decryptedValue;
		} catch (Exception e) {
			e.printStackTrace();
			// logger.info("error in getting the string message "+e.toString());
			// logger.log(Level.INFO,e.getMessage(),e);
			return null;
		}
	}

	private static Key generateKey() throws Exception {
		Key key = new SecretKeySpec(AESCrypt.KEY.getBytes(), AESCrypt.ALGORITHM);
		return key;
	}

	public static void main(String[] args) {
		String a = encryptIt("d2E5Qf#dkfhpQW!ME");
		System.out.println(a);
		String b = decryptIt("afE39mEDiaqkdL+NaPCtzA==");
		System.out.println(b);

	}

}