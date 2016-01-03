package com.blogspot.sontx.iot.shared.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.blogspot.sontx.iot.shared.model.bean.RealTime;
import com.blogspot.sontx.iot.shared.utils.Convert;

/**
 * Package: start[1] length[1] data[n] checksum[1] stop[1]
 */
public final class Protocol {
	private static final int DATA_IN_BUFFER_SIZE = 128;
	private static final byte FLAG_BYTE_START = 0x01;
	private static final byte FLAG_BYTE_STOP = 0x02;

	private static final int CRC_POLYNOM = 0x9c;
	private static final int CRC_PRESET = 0xFF;

	public static final byte TYPE_ID = 1;
	public static final byte TYPE_REALTIME = 2;
	public static final byte TYPE_ENERGY = 3;
	public static final byte TYPE_TURN_OFF = 4;
	public static final byte TYPE_TURN_ON = 5;

	private OutputStream out;
	private InputStream in;
	private int timeout;

	public Protocol(InputStream in, OutputStream out, int timeout) {
		this.in = in;
		this.out = out;
		this.timeout = timeout;
	}

	private static byte crc8(byte[] b, int offset, int len) {
		long crc = CRC_PRESET;
		for (int i = 0; i < len; i++) {
			crc ^= b[i + offset];
			for (int j = 0; j < 8; j++) {
				if ((crc & 0x01) == 0x01)
					crc = (crc >> 1) ^ CRC_POLYNOM;
				else
					crc = crc >> 1;
			}
		}
		return (byte) (crc & 0xFF);
	}

	private boolean waitForAvailable() {
		long current = System.currentTimeMillis();
		try {
			while ((in.available() < 1) && (timeout > System.currentTimeMillis() - current)) {
				Thread.sleep(50);
			}
			return in.available() > 0;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean ready() {
		try {
			return in.available() > 0;
		} catch (IOException e) {
			return false;
		}
	}
	
	public byte[] readData() {
		if (!waitForAvailable())
			return null;
		try {
			boolean start = false;
			int length = -1;
			byte[] data = new byte[DATA_IN_BUFFER_SIZE];
			int index = 0;
			byte crc = -1;
			boolean crcOK = false;
			boolean stop = false;
			while (true) {
				if (in.available() < 1 && !waitForAvailable())
					break;
				int b = in.read();
				if (b == -1)
					break;

				if (!start) {
					if (b == FLAG_BYTE_START)
						start = true;
					continue;
				}

				if (length < 0) {
					length = b;
					if (length < 1)
						start = false;// restart protocol
					continue;
				}

				if (index < length) {
					data[index++] = (byte) b;
					continue;
				}

				if (!crcOK) {
					crc = (byte) b;
					crcOK = true;
					continue;
				}

				if (b == FLAG_BYTE_STOP) {
					stop = true;
					break;
				} else {
					start = true;// failed protocol, restart protocol
				}
			}
			// check result
			if (stop) {
				// checksum crc
				byte checkCRC = crc8(data, 0, length);
				if (checkCRC == crc) {
					byte[] buff = new byte[length];
					System.arraycopy(data, 0, buff, 0, length);
					return buff;// everything is OK
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean writeData(byte[] data) {
		// create package to hold actual data when send to remote
		byte[] buff = new byte[4 + data.length];
		int offset = 0;

		// init start byte
		buff[offset++] = FLAG_BYTE_START;
		// init data length
		buff[offset++] = (byte) data.length;
		// init actual data
		System.arraycopy(data, 0, buff, offset, data.length);
		offset += data.length;
		// init checksum crc
		buff[offset++] = crc8(data, 0, data.length);
		// init stop byte
		buff[offset] = FLAG_BYTE_STOP;

		// now! send package to remote
		try {
			out.write(buff);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean request(byte type, byte[] extra) {
		byte[] data = new byte[1 + (extra != null ? extra.length : 0)];
		data[0] = type;
		if (extra != null)
			System.arraycopy(extra, 0, data, 1, extra.length);
		return writeData(data);
	}

	public boolean request(byte type) {
		return request(type, null);
	}

	public void dispose() {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
			}
		}
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
			}
		}
	}

	// data: type[1] int[4]
	public static int parseInt(byte[] data, byte type) {
		if (data == null || data[0] != type || data.length != 5)
			return -1;
		int int4 = Convert.bytesToInteger(data, 1);
		return int4;
	}

	// data: type[1] power[4] voltage[2] amperage[4] state[1]
	public static RealTime parseRealTime(byte[] data) {
		if (data == null || data[0] != TYPE_REALTIME || data.length != 12)
			return null;
		int offset = 1;
		int power = Convert.bytesToInteger(data, offset);
		offset += 4;
		short voltage = Convert.bytesToShort(data, offset);
		offset += 2;
		int amperage = Convert.bytesToInteger(data, offset);
		offset += 4;
		byte state = data[offset];
		RealTime realTime = new RealTime();
		realTime.setPower(power);
		realTime.setVoltage(voltage);
		realTime.setAmperage(amperage);
		realTime.setState(state);
		return realTime;
	}
}