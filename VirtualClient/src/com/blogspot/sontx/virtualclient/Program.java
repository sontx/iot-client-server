package com.blogspot.sontx.virtualclient;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Program {
	private Client[] clients;
	private String addr;
	private int port;

	private Socket genSocket(String addr, int port) {
		try {
			Socket socket = new Socket(addr, port);
			return socket;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void enterInfo() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Server address: ");
		addr = scanner.nextLine();
		System.out.println("Server port: ");
		port = scanner.nextInt();
		scanner.close();
	}
	
	private void start(int count) {
		System.out.println(String.format("Creating %d client and connect to %s:%d", count, addr, port));
		clients = new Client[count];
		for (int i = 0; i < clients.length; i++) {
			Socket socket = genSocket(addr, port);
			Client client = new Client(socket, 102120250 + i);// setup id here!
			new Thread(client).start();
			clients[i] = client;
		}
	}

	private Program(int count) {
		enterInfo();
		start(count);
	}

	public static void main(String[] args) {
		new Program(3);
	}
}
