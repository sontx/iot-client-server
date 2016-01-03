package com.blogspot.sontx.iot.shared.net.job;

import com.blogspot.sontx.iot.shared.net.Protocol;

public class TurnOnJob extends ManagerJob {

	public TurnOnJob(Protocol protocol) {
		super(null, protocol);
	}

	@Override
	public boolean execute() {
		return protocol.request(Protocol.TYPE_TURN_ON);
	}

}
