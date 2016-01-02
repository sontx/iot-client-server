<%@page import="com.blogspot.sontx.iot.myws.model.bo.SQLMgr"%>
<%@page import="com.blogspot.sontx.iot.shared.model.bean.Account"%>
<%@page import="java.util.List"%>
<%@page import="com.blogspot.sontx.iot.myws.controller.SuperuserServlet"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport"
	content="width=device-width, initial-scale=1.0 maximum-scale=1.0, user-scalable=no'">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%
	boolean logined = SuperuserServlet.logined(request);
%>
</head>
<body>

	<%
		if (!logined) {
	%>
	<center>
		<h3>You must login to manage users!</h3>
		<a href="Login.jsp">Login...</a>
	</center>
	<%
		} else {
			List<Account> accounts = SQLMgr.getInstance().getAllAccounts();
			if (accounts != null) {
	%>
	<center>
		<a href="NewAccount.jsp">New...</a>
		<table style="width: 95%" border="1">
			<%
				for (int i = 0; i < accounts.size(); i++) {
							Account account = accounts.get(i);
			%>

			<tr>
				<td align="left"><%=(i + 1) + ". " + account.getUserName()%></td>
				<td align="right"><a
					href="SuperuserServlet?req=delete&id=<%=account.getId()%>"
					onclick="return confirm('Delete <%=account.getUserName()%>, sure?')"
					target="_top">Delete...</a></td>
			</tr>
			<%
				}
			%>
		</table>
	</center>
	<%
			} else {
	%>
	<center>
		<h3>Something wrong in database!</h3>
	</center>
	<%
			}
		}
	%>

</body>
</html>