package com.blogspot.sontx.iot.shared.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.blogspot.sontx.iot.shared.net.Client.OnClientStoppedListener;
import com.blogspot.sontx.iot.shared.net.job.IJobExecutor;

public final class DeviceManager implements Runnable, OnClientStoppedListener {
	private static DeviceManager instance = null;
	private final Thread worker = new Thread(this);
	private ServerSocket server;
	private final List<Client> clients = new ArrayList<Client>();
	private final Object lock = new Object();
	private final IJobExecutor executor;
	private int timewaitEnergy = 1000;
	private int timewaitRealtime = 1000;
	private int timeout = 10000;

	public static DeviceManager getInstance() {
		return instance;
	}

	public static void createInstance(InetAddress addr, int port, IJobExecutor executor) {
		instance = new DeviceManager(addr, port, executor);
	}

	public static void destroyInstance() {
		if (instance != null) {
			instance.stop();
			instance = null;
		}
	}

	private DeviceManager(InetAddress addr, int port, IJobExecutor executor) {
		try {
			server = new ServerSocket(port, 100, addr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.executor = executor;
	}

	public Client getClientById(int id) {
		synchronized (lock) {
			for (Client client : clients) {
				if (client.getClientId() == id)
					return client;
			}
		}
		return null;
	}

	public void start() {
		worker.start();
	}

	public void stop() {
		synchronized (lock) {
			for (Client client : clients) {
				client.dispose();
			}
			clients.clear();
		}
		try {
			server.close();
		} catch (IOException e) {
		}
	}

	public void setTimewaitEnergy(int timewaitEnergy) {
		this.timewaitEnergy = timewaitEnergy;
	}

	public void setTimewaitRealtime(int timewaitRealtime) {
		this.timewaitRealtime = timewaitRealtime;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	private void addClient(Socket cnn) {
		synchronized (lock) {
			Client client = new Client(cnn, executor, timewaitEnergy, timewaitRealtime, timeout);
			clients.add(client);
			client.setOnClientStoppedListener(this);
			client.start();
		}
	}

	private void removeClient(Client client) {
		synchronized (lock) {
			client.setOnClientStoppedListener(null);
			client.dispose();
			clients.remove(client);
			executor.onDisconnected(client.getClientId());
		}
	}

	@Override
	public void run() {
		try {
			do {
				Socket client = server.accept();
				addClient(client);
			} while (true);
		} catch (IOException e) {
		}
	}

	@Override
	public void clientStopped(Client client) {
		removeClient(client);
	}
}
