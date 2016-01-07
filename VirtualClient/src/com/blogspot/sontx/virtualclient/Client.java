package com.blogspot.sontx.virtualclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;

import com.blogspot.sontx.iot.shared.net.Protocol;
import com.blogspot.sontx.iot.shared.utils.Convert;

public class Client implements Runnable {
	private final Socket socket;
	private final Protocol protocol;
	private final int id;
	private boolean pendingStop = false;
	private Random rand = new Random(System.currentTimeMillis());
	private byte state = (byte) 0;
	private OnStateChangedListener mOnStateChangedListener = null;
	
	public void setOnStateChangedListener(OnStateChangedListener listener) {
		mOnStateChangedListener = listener;
	}
	
	private void fireOnStateChangedListener() {
		if (mOnStateChangedListener != null)
			mOnStateChangedListener.stateChanged(this);
	}
	
	public int getId() {
		return id;
	}

	public byte getState() {
		return state;
	}
	
	public void setState(byte state) {
		if (state != this.state) {
			this.state = state;
			fireOnStateChangedListener();
		}
	}
	
	private void log(String st) {
		System.out.println(String.format("[%d] %s", id, st));
	}

	public Client(Socket socket, int id) {
		this.socket = socket;
		InputStream in = null;
		OutputStream out = null;
		try {
			in = socket.getInputStream();
			out = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		protocol = new Protocol(in, out, 10000);
		this.id = id;
	}
	
	private boolean response(byte[] actualData, byte type) {
		byte[] data = new byte[1 + actualData.length];
		data[0] = type;
		System.arraycopy(actualData, 0, data, 1, actualData.length);
		return protocol.writeData(data);
	}
	
	private boolean responseEnergy() {
		int energy = rand.nextInt(90000) + 7000;
		log(String.format("Response energy %d", energy));
		return response(Convert.integerToBytes(energy), Protocol.TYPE_ENERGY);
	}
	
	private boolean responseId() {
		log(String.format("Response id %d", id));
		return response(Convert.integerToBytes(id), Protocol.TYPE_ID);
	}

	private boolean responseRealtime() {
		byte[] actualData = new byte[11];
		short vol = (short) (rand.nextInt(200) + 100);
		int am = rand.nextInt(5000) + 45000;
		int pw = rand.nextInt(3000) + 12000;
		int offset = 0;
		System.arraycopy(Convert.integerToBytes(pw), 0, actualData, offset, 4);
		offset += 4;
		System.arraycopy(Convert.shortToBytes(vol), 0, actualData, offset, 2);
		offset += 2;
		System.arraycopy(Convert.integerToBytes(am), 0, actualData, offset, 4);
		offset += 4;
		actualData[offset] = state;
		log(String.format("Response real-time PW = %d, VOL = %d, AM = %d, %s", pw, vol, am, state != 0 ? "ON" : "OFF"));
		return response(actualData, Protocol.TYPE_REALTIME);
	}
	
	private void turn(boolean off) {
		if (off) {
			log("Turn OFF device....");
			state = (byte) 0;
		} else {
			log("Turn ON device....");
			state = (byte) 1;
		}
		fireOnStateChangedListener();
	}

	private boolean processData(byte[] data) {
		byte type = data[0];
		switch (type) {
		case Protocol.TYPE_ENERGY:
			return responseEnergy();
		case Protocol.TYPE_ID:
			return responseId();
		case Protocol.TYPE_REALTIME:
			//state = (byte) (rand.nextInt() % 2);
			return responseRealtime();
		case Protocol.TYPE_TURN_OFF:
			turn(true);
			return true;
		case Protocol.TYPE_TURN_ON:
			turn(false);
			return true;
		default:
			return false;
		}
	}

	@Override
	public void run() {
		log("Started");
		while (!pendingStop) {
			if (!protocol.ready()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					break;
				}
				continue;
			}
			byte[] data = protocol.readData();
			if (data == null)
				break;
			if (!processData(data))
				break;
		}
		if (!pendingStop) 
			log("Corrupt :|");
		log("Stopped!");
	}

	public void dispose() {
		pendingStop = true;
		protocol.dispose();
		try {
			socket.close();
		} catch (IOException e) {
		}
	}
	
	public interface OnStateChangedListener {
		void stateChanged(Client client);
	}
}
