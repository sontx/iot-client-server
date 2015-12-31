package com.blogspot.sontx.iot.myws.model.dao;

import java.util.ArrayList;
import java.util.List;

import com.blogspot.sontx.iot.shared.model.bean.RealTime;

public final class TemporaryObject {
	private List<RealTime> data;

	public void clear() {
		data.clear();
	}

	public RealTime get(int deviceId) {
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).getDeviceId() == deviceId) {
				return data.get(i);
			}
		}
		return null;
	}

	public void add(RealTime rt) {
		data.add(rt);
	}

	public boolean exists(int deviceId) {
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).getDeviceId() == deviceId) {
				return true;
			}
		}
		return false;
	}

	public void update(RealTime rt) {
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).getDeviceId() == rt.getDeviceId()) {
				data.set(i, rt);
			}
		}
	}

	public TemporaryObject() {
		data = new ArrayList<>();
	}

}
