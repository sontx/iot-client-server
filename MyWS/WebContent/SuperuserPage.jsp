<%@page import="com.blogspot.sontx.iot.myws.controller.SuperuserServlet"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0 maximum-scale=1.0, user-scalable=no'">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Superuser</title>
<%
	boolean logined = SuperuserServlet.logined(request);
%>
</head>
<frameset rows="80px, *" frameborder="no">
	<frame src="Header.jsp" scrolling="no"/>
	<%
		if (!logined) {
	%>
	<frame src="Login.jsp"/>
	<%
		} else {
	%>
	<frame src="Manager.jsp" name="content" />
	<%
		}
	%>
</frameset>
</html>