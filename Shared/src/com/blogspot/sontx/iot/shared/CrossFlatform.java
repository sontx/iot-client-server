package com.blogspot.sontx.iot.shared;

import com.blogspot.sontx.iot.shared.model.bean.TransmissionObject;
import com.blogspot.sontx.iot.shared.utils.IDataConvert;
import com.blogspot.sontx.iot.shared.utils.SerialConvert;

public final class CrossFlatform {
	private static IDataConvert dataConvert = SerialConvert.getInstance();

	public static byte[] toBytes(TransmissionObject obj) {
		return dataConvert.encode(obj);
	}

	public static TransmissionObject fromBytes(byte[] bytes) {
		Object obj = dataConvert.decode(bytes);
		if (obj == null || !(obj instanceof TransmissionObject))
			return null;
		return (TransmissionObject) obj;
	}

	private CrossFlatform() {
	}
}
