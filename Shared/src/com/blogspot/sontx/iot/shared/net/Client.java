package com.blogspot.sontx.iot.shared.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.blogspot.sontx.iot.shared.model.bean.Energy;
import com.blogspot.sontx.iot.shared.model.bean.RealTime;
import com.blogspot.sontx.iot.shared.net.job.BaseJob;
import com.blogspot.sontx.iot.shared.net.job.EnergyJob;
import com.blogspot.sontx.iot.shared.net.job.IJobExecutor;
import com.blogspot.sontx.iot.shared.net.job.IdJob;
import com.blogspot.sontx.iot.shared.net.job.RealTimeJob;
import com.blogspot.sontx.iot.shared.net.job.TurnOffJob;
import com.blogspot.sontx.iot.shared.net.job.TurnOnJob;

public class Client implements Runnable {
	private OnClientStoppedListener mOnClientStoppedListener = null;
	private final Socket connection;
	private final Protocol protocol;
	private int clientId;
	private final Thread worker = new Thread(this);
	private boolean penddingStop = false;
	private final List<BaseJob> jobs = new ArrayList<BaseJob>();
	private final Object lock = new Object();
	private final IJobExecutor executor;
	private final int timewaitEnergy;
	private final int timewaitRealtime;

	public int getClientId() {
		return clientId;
	}

	public void turnOff() {
		BaseJob job = new TurnOffJob(protocol);
		synchronized (lock) {
			jobs.add(job);
		}
	}

	public void turnOn() {
		BaseJob job = new TurnOnJob(protocol);
		synchronized (lock) {
			jobs.add(job);
		}
	}

	public void setOnClientStoppedListener(OnClientStoppedListener listener) {
		mOnClientStoppedListener = listener;
	}

	public void start() {
		if (worker != null)
			worker.start();
	}

	public void stop(boolean force) {
		penddingStop = true;
		if (force) {
			worker.interrupt();
			fireClientStopped();
		}
	}

	private void fireClientStopped() {
		if (mOnClientStoppedListener != null)
			mOnClientStoppedListener.clientStopped(this);
	}

	public Client(Socket connection, IJobExecutor executor, int timewaitEnergy, int timewaitRealtime, int timeout) {
		this.connection = connection;
		this.executor = executor;
		this.timewaitEnergy = timewaitEnergy;
		this.timewaitRealtime = timewaitRealtime;
		InputStream in = null;
		OutputStream out = null;
		try {
			in = connection.getInputStream();
			out = connection.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.protocol = new Protocol(in, out, timeout);
	}

	private void initJobQueue() {
		EnergyJob energyJob = new EnergyJob(executor, clientId, timewaitEnergy, protocol);
		RealTimeJob realTimeJob = new RealTimeJob(executor, clientId, timewaitRealtime, protocol);
		jobs.add(energyJob);
		jobs.add(realTimeJob);
	}

	private void startJobQueue() {
		boolean crashConnection = false;
		boolean interrupted = false;
		penddingStop = false;
		while (!penddingStop && !crashConnection && !interrupted) {
			synchronized (lock) {
				for (int i = 0; i < jobs.size(); i++) {
					BaseJob job = jobs.get(i);
					if (job.checkAndReduce(100)) {
						if (!job.execute()) {
							crashConnection = true;
							break;
						} else {
							if (job.canReused())
								job.reset();
							else
								jobs.remove(job);
						}
					}
				}
			}
			if (!crashConnection) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					interrupted = true;
				}
			}
		}
		fireClientStopped();
	}

	@Override
	public void run() {
		BaseJob idJob = new IdJob(new IJobExecutor() {
			@Override
			public void onHasRealTime(RealTime realtime) {
			}

			@Override
			public void onHasId(int deviceId) {
				executor.onHasId(deviceId);
				clientId = deviceId;
				initJobQueue();
				startJobQueue();
			}

			@Override
			public void onHasEnergy(Energy energy) {
			}

			@Override
			public void onDisconnected(int deviceId) {
			}
		}, protocol);
		if (!idJob.execute()) {
			fireClientStopped();
		}
	}

	public void dispose() {
		protocol.dispose();
		try {
			connection.shutdownInput();
			connection.shutdownOutput();
			connection.close();
		} catch (IOException e) {
		}
	}

	public interface OnClientStoppedListener {
		void clientStopped(Client client);
	}
}
