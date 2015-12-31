package com.blogspot.sontx.iot.myws.utils;

public final class SQLHelper {
	public static String prepareString(String s) {
		String ret = s.replace("\"", "\"\"");
		if (ret.startsWith("\""))
			ret = "\"" + ret;
		if (ret.endsWith("\""))
			ret = ret + "\"";
		return String.format("\"%s\"", ret);
	}

	private SQLHelper() {
	}

}
