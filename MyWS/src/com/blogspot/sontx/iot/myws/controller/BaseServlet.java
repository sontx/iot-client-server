package com.blogspot.sontx.iot.myws.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blogspot.sontx.iot.myws.model.bo.SQLMgr;
import com.blogspot.sontx.iot.shared.CrossFlatform;
import com.blogspot.sontx.iot.shared.model.bean.TransmissionObject;

public abstract class BaseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected abstract void doWork(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException;

	private void responseAuthenticationError(HttpServletResponse response) throws IOException {
		TransmissionObject obj = new TransmissionObject();
		obj.setCode(TransmissionObject.CODE_AUTH_ERR);
		response.getOutputStream().write(CrossFlatform.toBytes(obj));
	}

	@Override
	protected final void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("username");
		String passwordHash = request.getParameter("passhash");
		if (username == null || passwordHash == null) {
			responseAuthenticationError(response);
		} else if (!SQLMgr.getInstance().checkLogin(username, passwordHash)) {
			responseAuthenticationError(response);
		} else {
			doWork(request, response);
		}
	}

	@Override
	protected final void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
