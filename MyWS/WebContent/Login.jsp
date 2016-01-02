<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
	<center>
		<h3>Login to system, fill some information below:</h3>
		<form action="SuperuserServlet" method="post" target="_top">
			<table>
				<tr>
					<td>User name:</td>
					<td><input id="username" type="text" name="username" /></td>
				</tr>
				<tr>
					<td>Password:</td>
					<td><input id="password" type="password" name="password" /></td>
				</tr>
				<tr>
					<td />
					<td align="right"><input type="submit" value="Login"></td>
				</tr>
			</table>

		</form>
	</center>
</body>
</html>