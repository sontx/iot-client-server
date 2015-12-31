package com.blogspot.sontx.utils;

public final class Convert {

	public static int parseInt(String st, int def) {
		try {
			return Integer.parseInt(st);
		} catch (NumberFormatException e) {
			return def;
		}
	}

	private Convert() {
	}

}
