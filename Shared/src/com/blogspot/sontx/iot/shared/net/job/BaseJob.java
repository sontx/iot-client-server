package com.blogspot.sontx.iot.shared.net.job;

import com.blogspot.sontx.iot.shared.net.Protocol;

public abstract class BaseJob {
	private final int initWaitTime;
	private int currentWaitTime = 0;
	protected final Protocol protocol;
	protected final int clientId;
	protected final IJobExecutor executor;

	public void reset() {
		currentWaitTime = initWaitTime;
	}

	public boolean checkAndReduce(int milis) {
		boolean canExecute = currentWaitTime <= 0;
		currentWaitTime -= milis;
		return canExecute;
	}

	public abstract boolean execute();

	public abstract boolean canReused();

	public BaseJob(IJobExecutor executor, int clientId, int waitTime, Protocol protocol) {
		this.executor = executor;
		this.clientId = clientId;
		initWaitTime = waitTime;
		currentWaitTime = waitTime;
		this.protocol = protocol;
	}
}
