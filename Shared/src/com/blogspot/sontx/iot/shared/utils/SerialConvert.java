package com.blogspot.sontx.iot.shared.utils;

public final class SerialConvert implements IDataConvert {
	private static SerialConvert instance = null;

	public static SerialConvert getInstance() {
		if (instance == null)
			instance = new SerialConvert();
		return instance;
	}

	private SerialConvert() {
	}

	@Override
	public byte[] encode(Object in) {
		return in != null ? Convert.objectToBytes(in) : null;
	}

	@Override
	public Object decode(byte[] in) {
		return in != null ? Convert.bytesToObject(in) : null;
	}

}
