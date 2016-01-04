package com.blogspot.sontx.iot.myws.model.bo;

import java.util.List;

import com.blogspot.sontx.iot.myws.model.dao.TemporaryObject;
import com.blogspot.sontx.iot.shared.model.bean.Device;
import com.blogspot.sontx.iot.shared.model.bean.RealTime;

public final class TemporaryManager {
	private static TemporaryManager instance = null;
	private TemporaryObject temporaryObject;

	public static TemporaryManager getInstance() {
		return instance;
	}

	public static void destroyInstance() {
		if (instance != null) {
			instance.temporaryObject.clear();
			instance = null;
		}
	}

	public static void createInstance(List<Device> devices) {
		instance = new TemporaryManager(devices);
	}
	
	private TemporaryManager(List<Device> devices) {
		temporaryObject = new TemporaryObject();
		if (devices != null) {
			for (Device device : devices) {
				RealTime realTime = new RealTime();
				int deviceId = device.getId();
				realTime.setDeviceId(deviceId);
				temporaryObject.add(realTime);
				off(deviceId);
			}
		}
	}

	public void off(int deviceId) {
		if (deviceId > 0) {
			RealTime realTime = temporaryObject.get(deviceId);
			if (realTime != null) {
				realTime.setAmperage(0);
				realTime.setPower(0);
				realTime.setVoltage((short) 0);
				realTime.setState((byte) 0);// 0 - OFF, else ON :|
			}
		}
	}

	public RealTime get(int deviceId) {
		return deviceId > 0 ? temporaryObject.get(deviceId) : null;
	}

	public void set(RealTime realTime) {
		if (realTime != null) {
			if (temporaryObject.exists(realTime.getDeviceId()))
				temporaryObject.update(realTime);
			else
				temporaryObject.add(realTime);
		}
	}
}
