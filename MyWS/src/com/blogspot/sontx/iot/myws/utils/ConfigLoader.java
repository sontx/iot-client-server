package com.blogspot.sontx.iot.myws.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class ConfigLoader {
	public static final String[][] DATA_MAP = { 
			{ "dbname", "local.db" }, 
			{ "address", "127.0.0.1" },
			{ "port", "2512" }, 
			{ "timeout", "10000" }, 
			{ "relay-get-realtime", "2000" },
			{ "relay-get-energy", "10000" },
			{ "su-username", "admin"},
			{ "su-password", "admin"}};
	private static final char DELIM = '=';
	private final HashMap<String, String> map;
	private final File file;

	public String get(int index) {
		String key = DATA_MAP[index][0];
		return map.get(key);
	}

	public boolean create() {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			for (int i = 0; i < DATA_MAP.length; i++) {
				String line = String.format("%s%c%s\n", DATA_MAP[i][0], DELIM, DATA_MAP[i][1]);
				writer.write(line);
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public boolean load() {
		if (!file.exists())
			return false;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			while (reader.ready()) {
				String line = reader.readLine();
				String key = line.substring(0, line.indexOf(DELIM));
				String value = line.substring(key.length()).trim();
				map.put(key, value);
			}
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public ConfigLoader(String configFile) {
		map = new HashMap<String, String>();
		file = new File(configFile);
	}
}
