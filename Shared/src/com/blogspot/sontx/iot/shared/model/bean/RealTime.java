package com.blogspot.sontx.iot.shared.model.bean;

import java.io.Serializable;

public class RealTime implements Serializable {
	private static final long serialVersionUID = 1L;
	private int deviceId;
	private int power;
	private short voltage;
	private int amperage;
	private byte state;

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public short getVoltage() {
		return voltage;
	}

	public void setVoltage(short voltage) {
		this.voltage = voltage;
	}

	public int getAmperage() {
		return amperage;
	}

	public void setAmperage(int amperage) {
		this.amperage = amperage;
	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

}
