package com.blogspot.sontx.iot.shared;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.blogspot.sontx.iot.shared.utils.Convert;

public final class Security {

	public static String toSHA1(byte[] st) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
			return Convert.bytesToHexString(md.digest(st));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getPasswordHash(String password) {
		if (password == null)
			return null;
		return toSHA1(password.getBytes(Charset.forName("UTF-8")));
	}

	private Security() {
	}

}
