package com.blogspot.sontx.model.dao;

import java.util.List;

import com.blogspot.sontx.model.bean.Account;
import com.blogspot.sontx.model.bean.Device;
import com.blogspot.sontx.model.bean.Energy;

public interface ISQLDb {
	// open database
	void open();
	// close database
	void close();
	// add an energy to database
	void addEnergy(Energy energy);
	// get energies of a device
	List<Energy> getEnergies(int deviceId);
	// get energies of a device in period
	List<Energy> getEnergies(int deviceId, int beginUTC, int endUTC);
	// add new device to database
	void addDevice(Device newDevice);
	// get device info by id
	Device getDevice(int deviceId);
	// get all devices in database
	List<Device> getAllDevices();
	// update device info
	void updateDevice(Device device);
	// add new account to database
	void addAccount(Account account);
	// get an exist account by user name
	Account getAccount(String username);
	// update an exist account 
	void updateAccount(Account account);
}
