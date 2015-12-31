package com.blogspot.sontx.iot.myws.utils;

public final class Config {
	public static final String WORKING_DIR;
	public static final String CONFIG_FILE_NAME;
	public static final String DB_PATH;
	public static final String SERVER_ADDRESS;
	public static final int SERVER_PORT;
	public static final int DATA_IN_TIMEOUT;
	public static final int RELAY_GET_REALTIME;
	public static final int RELAY_GET_ENERGY;

	private Config() {
	}

	static {
		WORKING_DIR = System.getProperty("catalina.base") + "/webapps/MYWS";
		CONFIG_FILE_NAME = WORKING_DIR + "/config.in";

		ConfigLoader loader = new ConfigLoader(CONFIG_FILE_NAME);
		if (!loader.load()) {
			if (!loader.create()) {
				System.err.println("System can't create file in " + CONFIG_FILE_NAME);
				System.exit(1);
			}
		}

		DB_PATH = WORKING_DIR + "/" + loader.get(0);
		SERVER_ADDRESS = loader.get(1);
		SERVER_PORT = Convert.parseInt(loader.get(2), 2512);
		DATA_IN_TIMEOUT = Convert.parseInt(loader.get(3), 10000);
		RELAY_GET_REALTIME = Convert.parseInt(loader.get(4), 2000);
		RELAY_GET_ENERGY = Convert.parseInt(loader.get(5), 10000);
	}
}
