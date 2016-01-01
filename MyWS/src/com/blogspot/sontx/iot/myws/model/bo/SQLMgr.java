package com.blogspot.sontx.iot.myws.model.bo;

import java.util.List;

import com.blogspot.sontx.iot.myws.model.dao.ISQLDb;
import com.blogspot.sontx.iot.myws.model.dao.SQLiteDb;
import com.blogspot.sontx.iot.myws.utils.Config;
import com.blogspot.sontx.iot.myws.utils.DateTime;
import com.blogspot.sontx.iot.shared.model.bean.Account;
import com.blogspot.sontx.iot.shared.model.bean.Device;
import com.blogspot.sontx.iot.shared.model.bean.Energy;

public final class SQLMgr {
	private static SQLMgr instance = null;
	private ISQLDb mSQLDb;

	public static SQLMgr getInstance() {
		if (instance == null)
			instance = new SQLMgr();
		return instance;
	}

	public static void destroyInstance() {
		if (instance != null) {
			instance.mSQLDb.close();
			instance = null;
		}
	}

	private SQLMgr() {
		mSQLDb = new SQLiteDb(Config.DB_PATH);
		mSQLDb.open();
	}

	public void addEnergy(Energy energy) {
		if (energy != null && energy.getDeviceId() > 0 && energy.getEnergy() > 0 && energy.getUtc() > 0)
			mSQLDb.addEnergy(energy);
	}

	public List<Energy> getEnergies(int deviceId) {
		return deviceId > 0 ? mSQLDb.getEnergies(deviceId) : null;
	}

	private List<Energy> getEnergies(int deviceId, DateTime begin, DateTime end) {
		int beginUTC = begin.toInteger();
		int endUTC = end.toInteger();
		return mSQLDb.getEnergies(deviceId, beginUTC, endUTC);
	}

	public List<Energy> getEnergies(int deviceId, int year) {
		if (deviceId <= 0 || year < 2015)// just for fun :|
			return null;
		DateTime begin = new DateTime(1, 1, year, 0, 0, 0);
		DateTime end = new DateTime(31, 12, year, 23, 59, 59);
		return getEnergies(deviceId, begin, end);
	}

	public List<Energy> getEnergies(int deviceId, int month, int year) {
		if (deviceId <= 0 || year < 2015 || month < 1 || month > 12)// just for
																	// fun :|
			return null;
		DateTime begin = new DateTime(1, month, year, 0, 0, 0);
		DateTime end = new DateTime(DateTime.getMaxDay(month, year), month, year, 23, 59, 59);
		return getEnergies(deviceId, begin, end);
	}

	public List<Energy> getEnergies(int deviceId, int day, int month, int year) {
		if (deviceId <= 0 || year < 2015 || month < 1 || month > 12 || day < 0 || day > DateTime.getMaxDay(month, year))
			return null;
		DateTime begin = new DateTime(day, month, year, 0, 0, 0);
		DateTime end = new DateTime(day, month, year, 23, 59, 59);
		return getEnergies(deviceId, begin, end);
	}

	private void validDeviceInfo(Device device) {
		if (device.getName() != null) {
			String name = device.getName().trim();
			if (name.length() == 0)
				name = "Unknown";
			else if (name.length() > 25)
				name.substring(0, 25);
			name = name.replace(' ', '-');
			device.setName(name);
		}
	}

	public void addDevice(Device newDevice) {
		if (newDevice.getId() > 0 && newDevice.getName() != null) {
			validDeviceInfo(newDevice);
			mSQLDb.addDevice(newDevice);
		}
	}

	public Device getDevice(int deviceId) {
		return deviceId > 0 ? mSQLDb.getDevice(deviceId) : null;
	}

	public List<Device> getAllDevices() {
		return mSQLDb.getAllDevices();
	}

	public void updateDevice(Device device) {
		if (device.getId() > 0 && device.getName() != null) {
			validDeviceInfo(device);
			mSQLDb.updateDevice(device);
		}
	}

	public boolean addAccount(Account account) {
		if (account.getUserName() != null && account.getPasswordHash() != null) {
			if (!Account.checkUserName(account.getUserName()))
				return false;
			if (account.getPasswordHash().length() == 0)
				return false;
			mSQLDb.addAccount(account);
			return true;
		}
		return false;
	}

	public boolean checkLogin(String username, String passwordHash) {
		Account account = getAccount(username);
		if (account == null)
			return false;
		return account.getPasswordHash().compareToIgnoreCase(passwordHash) == 0;
	}

	public Account getAccount(String username) {
		return (username == null || !Account.checkUserName(username)) ? null : mSQLDb.getAccount(username);
	}

	public void updateAccount(Account account) {
		if (account.getId() > 0 && account.getPasswordHash() != null) {
			if (account.getPasswordHash().length() > 0)
				mSQLDb.updateAccount(account);
		}
	}
}
