package com.blogspot.sontx.iot.shared.net.job;

import com.blogspot.sontx.iot.shared.net.Protocol;

public abstract class MonitorJob extends BaseJob {

	public MonitorJob(IJobExecutor executor, int clientId, int waitTime, Protocol protocol) {
		super(executor, clientId, waitTime, protocol);
	}

	@Override
	public boolean canReused() {
		return true;
	}

}
