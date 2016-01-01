package com.blogspot.sontx.iot.myws.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blogspot.sontx.iot.myws.utils.Config;
import com.blogspot.sontx.iot.shared.utils.Convert;

@WebServlet("/SuperuserServlet")
public class SuperuserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static boolean logined(HttpServletRequest request) {
		String s_logined = (String) request.getSession().getAttribute("logined");
		boolean logined = Convert.parseInt(s_logined, 0) != 0;
		return logined;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!logined(request)) {
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			if ((username == null || password == null)
				|| (!username.equals(Config.SU_USERNAME) || 
				!password.equals(Config.SU_PASSWORD))) {
				request.getSession().setAttribute("logined", 0);
			} else {
				request.getSession().setAttribute("logined", 1);
			}
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher("SuperuserPage.jsp");
		dispatcher.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
