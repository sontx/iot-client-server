package com.blogspot.sontx.iot.shared.model.bean;

import java.io.Serializable;

public class Account implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String userName;
	private String passwordHash;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	
	public static boolean checkUserName(String st) {
		if (st.length() == 0)
			return false;
		if (st.indexOf(' ') > -1)
			return false;
		for (int i = 0; i < st.length(); i++) {
			char ch = st.charAt(i);
			if (!Character.isLetterOrDigit(ch)) {
				if (ch != '-' && ch != '_')
					return false;
			}
		}
		return true;
	}
}
