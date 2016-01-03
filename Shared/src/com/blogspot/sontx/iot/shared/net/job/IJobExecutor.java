package com.blogspot.sontx.iot.shared.net.job;

import com.blogspot.sontx.iot.shared.model.bean.Energy;
import com.blogspot.sontx.iot.shared.model.bean.RealTime;

public interface IJobExecutor {
	void onHasId(int deviceId);
	void onHasEnergy(Energy energy);
	void onHasRealTime(RealTime realtime);
	void onDisconnected(int deviceId);
}
