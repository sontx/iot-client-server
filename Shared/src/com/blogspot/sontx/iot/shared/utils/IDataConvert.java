package com.blogspot.sontx.iot.shared.utils;

public interface IDataConvert {
	byte[] encode(Object in);
	Object decode(byte[] in);
}
