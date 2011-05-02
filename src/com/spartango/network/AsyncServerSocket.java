package com.spartango.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class AsyncServerSocket implements Runnable {
	private int sleepTime = 500; // ms -- Duration between spins during
									// non-accepting periods

	private ServerSocket server;
	private List<AsyncServerListener> listeners;

	private Thread runner;
	private boolean running;

	private boolean accepting;

	/**
	 * 
	 * @param port
	 * @throws IOException
	 */
	public AsyncServerSocket(int port) throws IOException {
		running = false;
		listeners = new Vector<AsyncServerListener>();
		server = new ServerSocket(port);
		runner = new Thread(this);
		accepting = false;
	}

	/**
	 * Starts publishing events to listeners
	 */
	public void start() {
		if (!running) {
			runner.start();
		}
	}

	@Override
	public void run() {
		running = accepting = true;
		System.out.println("Accepting");
		while (running) {
			if (accepting) {
				accept();
			} else {
				pause();
			}
		}

		cleanup();
	}

	private void accept() {
		try {
			Socket client = server.accept();
			System.out.println("Accepted new client");
			notifyNewClient(client);
		} catch (IOException e) {
			notifyFailure(e);
		}
	}

	public void pause() {
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			close();
		}
	}

	public void close() {
		running = accepting = false;
	}

	public void cleanup() {
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		notifyServerClosed();
	}

	private void notifyServerClosed() {
		for (AsyncServerListener listener : listeners) {
			listener.onServerClosed();
		}
	}

	private void notifyNewClient(Socket client) {
		for (AsyncServerListener listener : listeners) {
			listener.onNewClient(new AsyncServerEvent(
					AsyncServerEvent.NEW_CLIENT, this, client, null));
		}
	}

	private void notifyFailure(Exception e) {
		// Create an immutable event
		AsyncServerEvent event = new AsyncServerEvent(AsyncServerEvent.FAILURE,
				this, null, e);
		for (AsyncServerListener listener : listeners) {
			listener.onNewClient(event);
		}
	}

	public boolean isAccepting() {
		return accepting;
	}

	public void setAccepting(boolean accepting) {
		this.accepting = accepting;
	}

	public boolean isRunning() {
		return running;
	}

	public int getLocalPort() {
		return server.getLocalPort();
	}

	public boolean add(AsyncServerListener arg0) {
		return listeners.add(arg0);
	}

	public boolean remove(Object arg0) {
		return listeners.remove(arg0);
	}

}
