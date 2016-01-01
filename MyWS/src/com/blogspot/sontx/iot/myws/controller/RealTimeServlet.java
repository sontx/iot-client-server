package com.blogspot.sontx.iot.myws.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blogspot.sontx.iot.myws.model.bo.TemporaryManager;

@WebServlet("/RealTimeServlet")
public class RealTimeServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doWork(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int deviceId = getDeviceId(request);
		doResp(TemporaryManager.getInstance().get(deviceId), response);
	}

}
