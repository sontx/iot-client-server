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

	private Convert() {
	}

}
