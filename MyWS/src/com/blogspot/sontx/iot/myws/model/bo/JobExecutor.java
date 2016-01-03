package com.blogspot.sontx.iot.myws.model.bo;

import com.blogspot.sontx.iot.shared.model.bean.Device;
import com.blogspot.sontx.iot.shared.model.bean.Energy;
import com.blogspot.sontx.iot.shared.model.bean.RealTime;
import com.blogspot.sontx.iot.shared.net.job.IJobExecutor;

public class JobExecutor implements IJobExecutor {
	@Override
	public void onHasId(int deviceId) {
		Device device = SQLMgr.getInstance().getDevice(deviceId);
		if (device == null && deviceId > 0) {
			device = new Device();
			device.setId(deviceId);
			device.setName("Unknown");
			SQLMgr.getInstance().addDevice(device);
		}
	}

	@Override
	public void onHasEnergy(Energy energy) {
		SQLMgr.getInstance().addEnergy(energy);
	}

	@Override
	public void onHasRealTime(RealTime realtime) {
		TemporaryManager.getInstance().set(realtime);
	}

	@Override
	public void onDisconnected(int deviceId) {
		TemporaryManager.getInstance().off(deviceId);
	}
}
