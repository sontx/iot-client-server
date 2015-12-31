package com.blogspot.sontx.model.dao;

import java.util.ArrayList;
import java.util.List;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteJob;
import com.almworks.sqlite4java.SQLiteStatement;
import com.blogspot.sontx.model.bean.Account;
import com.blogspot.sontx.model.bean.Device;
import com.blogspot.sontx.model.bean.Energy;

public final class SQLiteHelper {

	private SQLiteHelper() {
	}

	private static abstract class BaseJob<T> extends SQLiteJob<T> {
		protected final String sql;

		public BaseJob(String sql) {
			this.sql = sql;
		}
	}

	public static class NonQueryJob extends BaseJob<Integer> {
		public NonQueryJob(String sql) {
			super(sql);
		}

		@Override
		protected Integer job(SQLiteConnection connection) throws Throwable {
			if (sql != null) {
				try {
					connection.exec(sql);
					return connection.getChanges();
				} catch (SQLiteException e) {
					e.printStackTrace();
				}
			}
			return -1;
		}

	}

	public static class EnergyQueryJob extends BaseJob<List<Energy>> {
		public EnergyQueryJob(String sql) {
			super(sql);
		}

		@Override
		protected List<Energy> job(SQLiteConnection connection) throws Throwable {
			SQLiteStatement statement = null;
			try {
				statement = connection.prepare(sql);
				List<Energy> energies = new ArrayList<Energy>();
				while (statement.step()) {
					Energy energy = new Energy();
					energy.setDeviceId(statement.columnInt(0));
					energy.setEnergy(statement.columnInt(1));
					energy.setUtc(statement.columnInt(2));
					energies.add(energy);
				}
				return energies;
			} catch (SQLiteException e) {
				e.printStackTrace();
			} finally {
				if (statement != null)
					statement.dispose();
			}
			return null;
		}

	}

	public static class DeviceQueryJob extends BaseJob<List<Device>> {

		public DeviceQueryJob(String sql) {
			super(sql);
		}

		@Override
		protected List<Device> job(SQLiteConnection connection) throws Throwable {
			SQLiteStatement statement = null;
			try {
				statement = connection.prepare(sql);
				List<Device> devices = new ArrayList<Device>();
				while (statement.step()) {
					Device device = new Device();
					device.setId(statement.columnInt(0));
					device.setName(statement.columnString(1));
					devices.add(device);
				}
				return devices;
			} catch (SQLiteException e) {
				e.printStackTrace();
			} finally {
				if (statement != null)
					statement.dispose();
			}
			return null;
		}

	}

	public static class AccountQueryJob extends BaseJob<List<Account>> {

		public AccountQueryJob(String sql) {
			super(sql);
		}

		@Override
		protected List<Account> job(SQLiteConnection connection) throws Throwable {
			SQLiteStatement statement = null;
			try {
				statement = connection.prepare(sql);
				List<Account> accounts = new ArrayList<Account>();
				while (statement.step()) {
					Account account = new Account();
					account.setId(statement.columnInt(0));
					account.setUserName(statement.columnString(1));
					account.setPasswordHash(statement.columnString(2));
					accounts.add(account);
				}
				return accounts;
			} catch (SQLiteException e) {
				e.printStackTrace();
			} finally {
				if (statement != null)
					statement.dispose();
			}
			return null;
		}
	}
}
