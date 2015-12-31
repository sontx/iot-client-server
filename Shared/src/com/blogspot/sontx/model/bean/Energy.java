package com.blogspot.sontx.model.bean;

import java.io.Serializable;

public class Energy implements Serializable {
	private static final long serialVersionUID = 1L;
	private int deviceId;
	private int energy;
	private int utc;

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public int getEnergy() {
		return energy;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public int getUtc() {
		return utc;
	}

	public void setUtc(int utc) {
		this.utc = utc;
	}
}
