package com.blogspot.sontx.iot.shared.model.bean;

import java.io.Serializable;

public class TransmissionObject implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int CODE_AUTH_ERR = 1;
	public static final int CODE_DATA_OK = 2;
	public static final int CODE_DATA_NULL = 3;
	private int code;
	private Object data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
