package com.blogspot.sontx.iot.shared.net.job;

import com.blogspot.sontx.iot.shared.net.Protocol;

public abstract class ManagerJob extends BaseJob {

	public ManagerJob(IJobExecutor executor, Protocol protocol) {
		super(executor, 0, 0, protocol);
	}

	@Override
	public boolean canReused() {
		return false;
	}

}
