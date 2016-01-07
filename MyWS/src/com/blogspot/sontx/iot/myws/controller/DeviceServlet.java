package com.blogspot.sontx.iot.myws.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blogspot.sontx.iot.myws.model.bo.SQLMgr;
import com.blogspot.sontx.iot.shared.model.bean.Device;
import com.blogspot.sontx.iot.shared.net.Client;
import com.blogspot.sontx.iot.shared.net.DeviceManager;
import com.blogspot.sontx.iot.shared.utils.Convert;

@WebServlet("/DeviceServlet")
public class DeviceServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	private Object responseDevice(HttpServletRequest request) {
		int deviceId = getDeviceId(request);
		return SQLMgr.getInstance().getDevice(deviceId);
	}

	private Object responseAllDevices() {
		return SQLMgr.getInstance().getAllDevices();
	}

	private Object responseUpdate(HttpServletRequest request) {
		int deviceId = getDeviceId(request);
		String newName = request.getParameter("newname");
		Device device = new Device();
		device.setId(deviceId);
		device.setName(newName);
		SQLMgr.getInstance().updateDevice(device);
		return "OK";
	}

	private Object responseTurn(HttpServletRequest request) {
		int deviceId = getDeviceId(request);
		int off = Convert.parseInt(request.getParameter("off"), 1);
		Client client = DeviceManager.getInstance().getClientById(deviceId);
		if (client != null) {
			if (off != 0)
				client.turnOff();
			else
				client.turnOn();
			return "OK";
		}
		return "FAIL";
	}

	@Override
	protected void doWork(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String req = request.getParameter("req");
		if (req == null)
			return;
		Object data = null;
		switch (req) {
		case "single":
			data = responseDevice(request);
			break;
		case "all":
			data = responseAllDevices();
			break;
		case "rename":
			data = responseUpdate(request);
			break;
		case "turn":
			data = responseTurn(request);
			break;
		}
		doResp(data, response);
	}

}
