package com.blogspot.sontx.iot.myws.model.dao;

import java.util.List;

import com.blogspot.sontx.iot.shared.model.bean.Account;
import com.blogspot.sontx.iot.shared.model.bean.Device;
import com.blogspot.sontx.iot.shared.model.bean.Energy;

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
	// remove an exist account by id
	void removeAccount(int id);
	// get an exist account by user name
	Account getAccount(String username);
	// update an exist account 
	void updateAccount(Account account);
}
