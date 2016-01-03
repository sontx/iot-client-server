package com.blogspot.sontx.iot.myws.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blogspot.sontx.iot.myws.model.bo.SQLMgr;
import com.blogspot.sontx.iot.shared.model.bean.Account;
import com.blogspot.sontx.iot.shared.utils.Convert;

@WebServlet("/AccountServlet")
public class AccountServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	private Object responseCheckLogin() {
		return "OK";
	}
	
	private Object responseChangePasswordHash(HttpServletRequest request) {
		String username = request.getParameter("username");
		String newPasswordHash = request.getParameter("npasshash");
		int userid = Convert.parseInt(request.getParameter("id"), -1);
		Account account = new Account();
		account.setId(userid);
		account.setUserName(username);
		account.setPasswordHash(newPasswordHash);
		SQLMgr.getInstance().updateAccount(account);
		return "OK";
	}
	
	@Override
	protected void doWork(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String req = request.getParameter("req");
		if (req == null)
			return;
		Object data = null;
		switch (req) {
		case "check":
			data = responseCheckLogin();
			break;
		case "changepass":
			data = responseChangePasswordHash(request);
			break;
		}
		doResp(data, response);
	}

}
