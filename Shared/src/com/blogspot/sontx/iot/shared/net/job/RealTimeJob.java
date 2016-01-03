package com.blogspot.sontx.iot.shared.net.job;

import com.blogspot.sontx.iot.shared.model.bean.RealTime;
import com.blogspot.sontx.iot.shared.net.Protocol;

public class RealTimeJob extends MonitorJob {

	public RealTimeJob(IJobExecutor executor, int clientId, int waitTime, Protocol protocol) {
		super(executor, clientId, waitTime, protocol);
	}

	@Override
	public boolean execute() {
		if (protocol.request(Protocol.TYPE_REALTIME)) {
			byte[] data = protocol.readData();
			RealTime realTime = Protocol.parseRealTime(data);
			if (realTime == null)
				return false;
			realTime.setDeviceId(clientId);
			executor.onHasRealTime(realTime);
			return true;
		} else {
			return false;
		}
	}
}
