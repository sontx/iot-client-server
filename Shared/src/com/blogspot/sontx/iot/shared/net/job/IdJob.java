package com.blogspot.sontx.iot.shared.net.job;

import com.blogspot.sontx.iot.shared.net.Protocol;

public class IdJob extends ManagerJob {

	public IdJob(IJobExecutor executor, Protocol protocol) {
		super(executor, protocol);
	}

	@Override
	public boolean execute() {
		if (protocol.request(Protocol.TYPE_ID)) {
			byte[] data = protocol.readData();
			int id = Protocol.parseInt(data, Protocol.TYPE_ID);
			if (id < 0)
				return false;
			executor.onHasId(id);
			return true;
		} else {
			return false;
		}
	}
}
