<%@page import="com.blogspot.sontx.iot.myws.controller.SuperuserServlet"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%
	boolean logined = SuperuserServlet.logined(request);
%>
</head>
<body bgcolor="green">
	<table style="width: 95%; height: 100%">
		<tr>
			<td><h2>Welcome to blabla</h2></td>
			<%
				if (logined) {
			%>
			<td align="right"><a href="SuperuserServlet?req=logout" target="_top">Logout...</a></td>
			<%
				}
			%>
		</tr>
	</table>
</body>
</html>