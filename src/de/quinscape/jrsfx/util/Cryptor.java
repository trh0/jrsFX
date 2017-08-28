package de.quinscape.jrsfx.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;


public class Cryptor {

	private Cryptor() {

	}


	public static String encrypt(String value) {
		String key = "enc.hashkey";
		String initVector ="enc.vector";
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
			byte[] encrypted = cipher.doFinal(value.getBytes());
			return Base64.encodeBase64String(encrypted);
		}
		catch (Exception ex) {
			ApplicationIO.toErrorStream(ex);
		}
		return null;
	}

	public static String decrypt(String encrypted) {
		String key = ("enc.hashkey");
		String initVector = ("enc.vector");
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));
			return new String(original);
		}
		catch (Exception ex) {
			ApplicationIO.toErrorStream(ex);
		}
		return null;
	}

}
