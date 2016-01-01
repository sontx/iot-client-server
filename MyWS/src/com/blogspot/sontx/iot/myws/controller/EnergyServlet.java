package com.blogspot.sontx.iot.myws.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blogspot.sontx.iot.myws.model.bo.SQLMgr;
import com.blogspot.sontx.iot.shared.utils.Convert;

@WebServlet("/EnergyServlet")
public class EnergyServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	private Object responseDay(HttpServletRequest request) {
		int day = Convert.parseInt(request.getParameter("day"), -1);
		int month = Convert.parseInt(request.getParameter("month"), -1);
		int year = Convert.parseInt(request.getParameter("year"), -1);
		return SQLMgr.getInstance().getEnergies(getDeviceId(request), day, month, year);
	}

	private Object responseMonth(HttpServletRequest request) {
		int month = Convert.parseInt(request.getParameter("month"), -1);
		int year = Convert.parseInt(request.getParameter("year"), -1);
		return SQLMgr.getInstance().getEnergies(getDeviceId(request), month, year);
	}

	private Object responseYear(HttpServletRequest request) {
		int year = Convert.parseInt(request.getParameter("year"), -1);
		return SQLMgr.getInstance().getEnergies(getDeviceId(request), year);
	}

	private Object responseAll(HttpServletRequest request) {
		return SQLMgr.getInstance().getEnergies(getDeviceId(request));
	}

	@Override
	protected void doWork(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String req = request.getParameter("req");
		if (req == null)
			return;
		Object data = null;
		switch (req) {
		case "day":
			data = responseDay(request);
			break;
		case "month":
			data = responseMonth(request);
			break;
		case "year":
			data = responseYear(request);
			break;
		case "all":
			data = responseAll(request);
			break;
		}
		doResp(data, response);
	}

}
