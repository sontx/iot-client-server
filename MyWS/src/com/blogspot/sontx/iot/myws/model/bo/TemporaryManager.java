package com.blogspot.sontx.iot.myws.model.bo;

import com.blogspot.sontx.iot.myws.model.dao.TemporaryObject;
import com.blogspot.sontx.iot.shared.model.bean.RealTime;

public final class TemporaryManager {
	private static TemporaryManager instance = null;
	private TemporaryObject temporaryObject;

	public static TemporaryManager getInstance() {
		if (instance == null)
			instance = new TemporaryManager();
		return instance;
	}

	public static void destroyInstance() {
		if (instance != null) {
			instance.temporaryObject.clear();
			instance = null;
		}
	}

	private TemporaryManager() {
		temporaryObject = new TemporaryObject();
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
