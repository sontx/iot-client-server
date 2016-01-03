package com.blogspot.sontx.iot.shared.net.job;

import com.blogspot.sontx.iot.shared.model.bean.Energy;
import com.blogspot.sontx.iot.shared.net.Protocol;
import com.blogspot.sontx.iot.shared.utils.DateTime;

public class EnergyJob extends MonitorJob {

	public EnergyJob(IJobExecutor executor, int clientId, int waitTime, Protocol protocol) {
		super(executor, clientId, waitTime, protocol);
	}

	@Override
	public boolean execute() {
		if (protocol.request(Protocol.TYPE_ENERGY)) {
			byte[] data = protocol.readData();
			int energyValue = Protocol.parseInt(data, Protocol.TYPE_ENERGY);
			if (energyValue < 0)
				return false;
			Energy energy = new Energy();
			energy.setDeviceId(clientId);
			energy.setEnergy(energyValue);
			energy.setUtc(DateTime.now().toInteger());
			executor.onHasEnergy(energy);
			return true;
		} else {
			return false;
		}
	}
}
