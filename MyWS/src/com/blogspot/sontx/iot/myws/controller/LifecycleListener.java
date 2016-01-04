package com.blogspot.sontx.iot.myws.controller;

import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.blogspot.sontx.iot.myws.model.bo.JobExecutor;
import com.blogspot.sontx.iot.myws.model.bo.SQLMgr;
import com.blogspot.sontx.iot.myws.model.bo.TemporaryManager;
import com.blogspot.sontx.iot.myws.utils.Config;
import com.blogspot.sontx.iot.shared.model.bean.Device;
import com.blogspot.sontx.iot.shared.net.DeviceManager;

@WebListener
public class LifecycleListener implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent arg0) {
		SQLMgr.destroyInstance();
		TemporaryManager.destroyInstance();
		DeviceManager.getInstance().stop();
	}

	private void initializeDeviceManager() {
		DeviceManager.createInstance(Config.SERVER_ADDRESS, Config.SERVER_PORT, new JobExecutor());
		DeviceManager deviceManager = DeviceManager.getInstance();
		deviceManager.setTimeout(Config.DATA_IN_TIMEOUT);
		deviceManager.setTimewaitEnergy(Config.RELAY_GET_ENERGY);
		deviceManager.setTimewaitRealtime(Config.RELAY_GET_REALTIME);
		deviceManager.start();
	}
	
	private void initializeTemporaryManager() {
		List<Device> devices = SQLMgr.getInstance().getAllDevices();
		TemporaryManager.createInstance(devices);
	}

	public void contextInitialized(ServletContextEvent arg0) {
		SQLMgr.getInstance();
		initializeTemporaryManager();
		initializeDeviceManager();
	}
}
