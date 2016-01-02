package com.blogspot.sontx.iot.shared.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class Convert {

	public static byte[] objectToBytes(Object obj) {
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			byte[] ret = baos.toByteArray();
			return ret;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null)
					baos.close();
				if (oos != null)
					oos.close();
			} catch (IOException e) {
			}
		}
		return null;
	}

	public static Object bytesToObject(byte[] bytes) {
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		try {
			bais = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bais);
			Object obj = ois.readObject();
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bais != null)
					bais.close();
				if (ois != null)
					ois.close();
			} catch (IOException e) {
			}
		}
		return null;
	}

	public static int parseInt(String st, int def) {
		if (st == null || st.length() == 0)
			return def;
		try {
			return Integer.parseInt(st);
		} catch (NumberFormatException e) {
			return def;
		}
	}

	public static String bytesToHexString(byte[] b) {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	public static byte[] integerToBytes(int a) {
		return new byte[] { (byte) ((a >> 24) & 0xFF), (byte) ((a >> 16) & 0xFF), 
							(byte) ((a >> 8) & 0xFF), (byte) (a & 0xFF) };
	}
	
	public static int bytesToInteger(byte[] b, int offset) {
		return b[offset + 3] & 0xFF | (b[offset + 2] & 0xFF) << 8 | 
				(b[offset + 1] & 0xFF) << 16 | (b[offset] & 0xFF) << 24;
	}
	
	public static byte[] shortToBytes(short a) {
		return new byte[] { (byte) ((a >> 8) & 0x00FF), (byte) (a & 0x00FF) };
	}

	public static short bytesToShort(byte[] b, int offset) {
		return (short) ((b[offset + 1] & 0xFF) | (b[offset] & 0xFF) << 8);
	}

	private Convert() {
	}

}
