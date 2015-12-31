package com.blogspot.sontx.model.dao;

public final class TableInfo {
	public static final String ENERGY_TABLE_NAME = "tb_energy";
	public static final String ENERGY_TABLE_STRUCT = "CREATE TABLE \"" + ENERGY_TABLE_NAME
			+ "\" (\"device_id\" INT4 NOT NULL , \"energy\" INT4 NOT NULL , \"utc\" INT4 NOT NULL )";

	public static final String DEVICE_TABLE_NAME = "tb_device";
	public static final String DEVICE_TABLE_STRUCT = "CREATE TABLE \"" + DEVICE_TABLE_NAME
			+ "\" (\"device_id\" INT4 PRIMARY KEY  NOT NULL  UNIQUE , \"name\" nvarchar(50) NOT NULL  DEFAULT Unknown)";

	public static final String ACCOUNT_TABLE_NAME = "tb_account";
	public static final String ACCOUNT_TABLE_STRUCT = "CREATE TABLE \"" + ACCOUNT_TABLE_NAME
			+ "\" (\"id\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , \"username\" nvarchar(25) NOT NULL  UNIQUE , \"password_hash\" nvarchar(1000) NOT NULL )";

	private TableInfo() {
	}
}
