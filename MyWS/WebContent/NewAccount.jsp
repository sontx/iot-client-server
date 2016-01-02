<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
	function isNumber(code) {
		return (code >= 48) && (code <= 57);
	}
	function isLetter(code) {
		return ((code >= 65) && (code <= 90))
				|| ((code >= 97) && (code <= 122));
	}
	function checkSingup() {
		var username = document.getElementById("username").value.trim();
		var password = document.getElementById("password").value.trim();
		var confirm = document.getElementById("confirm").value.trim();
		if (username.length < 3) {
			alert("User name must be at least 3 characters!");
			return false;
		}
		if (username.indexOf(' ') > -1) {
			alert("User name can not be any spaces!");
			return false;
		}
		for (i = 0; i < username.length; i++) {
			var code = username.charCodeAt(i);
			if (!isNumber(code) && !isLetter(code)) {
				if (code != 45 && code != 95) {
					alert("User name only numbers, letters, - or _");
					return false;
				}
			}
		}
		if (password.length < 6) {
			alert("Password must be at least 6 characters!");
			return false;
		}
		if (password.localeCompare(confirm) != 0) {
			alert("confirm the password again!");
			return false;
		}
		return true;
	}
	function eventFire(el, etype) {
		if (el.fireEvent) {
			el.fireEvent('on' + etype);
		} else {
			var evObj = document.createEvent('Events');
			evObj.initEvent(etype, true, false);
			el.dispatchEvent(evObj);
		}
	}
	function goback() {
		eventFire(document.getElementById('hiddenLink'), 'click');
	}
</script>
</head>
<body>
	<center>
		<h3>Create new account, fill some information below:</h3>
		<form action="SuperuserServlet" method="post"
			onsubmit="return checkSingup();" target="_top">
			<input type="hidden" name="req" value="new"> <a
				id="hiddenLink" href="Manager.jsp"></a>
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
					<td>Confirm:</td>
					<td><input id="confirm" type="password" name="confirm" /></td>
				</tr>
				<tr>
					<td />
					<td align="right"><input type="button" value="Cancel"
						onclick="goback();"> <input type="submit" value="Create">
					</td>
				</tr>
			</table>
		</form>
	</center>
</body>
</html>