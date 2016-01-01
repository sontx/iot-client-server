package com.blogspot.sontx.iot.myws.model.dao;

import java.io.File;
import java.util.List;

import com.almworks.sqlite4java.SQLiteQueue;
import com.blogspot.sontx.iot.myws.utils.SQLHelper;
import com.blogspot.sontx.iot.shared.model.bean.Account;
import com.blogspot.sontx.iot.shared.model.bean.Device;
import com.blogspot.sontx.iot.shared.model.bean.Energy;

public class SQLiteDb implements ISQLDb {
	private final SQLiteQueue mQueue;

	private void createDb() {
		mQueue.execute(new SQLiteHelper.NonQueryJob(TableInfo.ENERGY_TABLE_STRUCT)).complete();
		mQueue.execute(new SQLiteHelper.NonQueryJob(TableInfo.DEVICE_TABLE_STRUCT)).complete();
		mQueue.execute(new SQLiteHelper.NonQueryJob(TableInfo.ACCOUNT_TABLE_STRUCT)).complete();
	}

	public SQLiteDb(String dbFileName) {
		File dbFile = new File(dbFileName);
		if (!dbFile.exists())
			createDb();
		mQueue = new SQLiteQueue(dbFile);
	}

	@Override
	public void open() {
		mQueue.start();
	}

	@Override
	public void close() {
		try {
			mQueue.stop(true).join();
		} catch (InterruptedException e) {
		}
	}

	@Override
	public void addEnergy(Energy energy) {
		String sql = "INSERT INTO %s(device_id, energy, utc) VALUES(%d, %d, %d)";
		sql = String.format(sql, TableInfo.ENERGY_TABLE_NAME, 
				energy.getDeviceId(), energy.getEnergy(), energy.getUtc());
		mQueue.execute(new SQLiteHelper.NonQueryJob(sql)).complete();
	}

	@Override
	public List<Energy> getEnergies(int deviceId) {
		String sql = "SELECT * FROM %s WHERE device_id = %d";
		sql = String.format(sql, TableInfo.ENERGY_TABLE_NAME, deviceId);
		return mQueue.execute(new SQLiteHelper.EnergyQueryJob(sql)).complete();
	}

	@Override
	public List<Energy> getEnergies(int deviceId, int beginUTC, int endUTC) {
		String sql = "SELECT * FROM %s WHERE (device_id = %d) AND (utc >= %d AND utc <= %d)";
		sql = String.format(sql, TableInfo.ENERGY_TABLE_NAME, deviceId, beginUTC, endUTC);
		return mQueue.execute(new SQLiteHelper.EnergyQueryJob(sql)).complete();
	}

	@Override
	public void addDevice(Device newDevice) {
		String sql = "INSERT INTO %s(device_id, name) VALUES(%d, %s)";
		sql = String.format(sql, TableInfo.DEVICE_TABLE_NAME, newDevice.getId(),
				SQLHelper.prepareString(newDevice.getName()));
		mQueue.execute(new SQLiteHelper.NonQueryJob(sql));
	}

	@Override
	public Device getDevice(int deviceId) {
		String sql = "SELECT * FROM %s WHERE device_id = %d LIMIT 1";
		sql = String.format(sql, TableInfo.DEVICE_TABLE_NAME, deviceId);
		List<Device> devices = mQueue.execute(new SQLiteHelper.DeviceQueryJob(sql)).complete();
		return devices != null && !devices.isEmpty() ? devices.get(0) : null;
	}

	@Override
	public List<Device> getAllDevices() {
		String sql = "SELECT * FROM %s";
		sql = String.format(sql, TableInfo.DEVICE_TABLE_NAME);
		return mQueue.execute(new SQLiteHelper.DeviceQueryJob(sql)).complete();
	}

	@Override
	public void updateDevice(Device device) {
		String sql = "UPDATE %s SET device_name = %s WHERE device_id = %d";
		sql = String.format(sql, TableInfo.DEVICE_TABLE_NAME, 
				SQLHelper.prepareString(device.getName()), device.getId());
		mQueue.execute(new SQLiteHelper.NonQueryJob(sql));
	}

	@Override
	public void addAccount(Account account) {
		String sql = "INSERT INTO %s(username, password_hash) VALUES(%s, %s)";
		sql = String.format(sql, TableInfo.ACCOUNT_TABLE_NAME, 
				SQLHelper.prepareString(account.getUserName()),
				SQLHelper.prepareString(account.getPasswordHash()));
		mQueue.execute(new SQLiteHelper.NonQueryJob(sql)).complete();
	}

	@Override
	public Account getAccount(String username) {
		String sql = "SELECT * FROM %s WHERE username = %s";
		sql = String.format(sql, TableInfo.ACCOUNT_TABLE_NAME, SQLHelper.prepareString(username));
		List<Account> accounts = mQueue.execute(new SQLiteHelper.AccountQueryJob(sql)).complete();
		return accounts != null && !accounts.isEmpty() ? accounts.get(0) : null;
	}

	@Override
	public void updateAccount(Account account) {
		String sql = "UPDATE %s SET password_hash = %s WHERE id = %d";
		sql = String.format(sql, TableInfo.ACCOUNT_TABLE_NAME, 
				SQLHelper.prepareString(account.getPasswordHash()),
				account.getId());
		mQueue.execute(new SQLiteHelper.NonQueryJob(sql));
	}

	@Override
	public void removeAccount(int id) {
		String sql = "DELETE FROM %s WHERE id = %d";
		sql = String.format(sql, TableInfo.ACCOUNT_TABLE_NAME, id);
		mQueue.execute(new SQLiteHelper.NonQueryJob(sql));
	}

	@Override
	public List<Account> getAllAccounts() {
		String sql = "SELECT * FROM %s";
		sql = String.format(sql, TableInfo.ACCOUNT_TABLE_NAME);
		return mQueue.execute(new SQLiteHelper.AccountQueryJob(sql)).complete();
	}
}
