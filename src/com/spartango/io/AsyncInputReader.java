package com.spartango.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

/**
 * Provides an asynchronous system for recieving data from a reader as it is
 * available. Complete reads and failed reads provide events to listeners with
 * appropriate data, as it becomes available.
 * 
 * @author anand
 * 
 */
public class AsyncInputReader implements Runnable {
	private List<AsyncReadListener> listeners;
	private BufferedReader input;
	private Thread runner;

	private boolean running;

	/**
	 * Creates an asynchronous reader to provide events to listeners, but does
	 * not start publishing events.
	 * 
	 * @param bufferedReader
	 */
	public AsyncInputReader(BufferedReader bufferedReader) {
		listeners = new Vector<AsyncReadListener>();
		input = bufferedReader;
		runner = new Thread(this);
	}

	/**
	 * Starts publishing events to listeners
	 */
	public void start() {
		if (!running) {
			runner.start();
		}
	}

	private synchronized void executeRecieve() {
		try {
			String data = input.readLine();
			notifyNewData(data);
		} catch (IOException e) {
			notifyReadFailure(e);
		}
	}

	private void notifyReadFailure(Exception e) {
		for (AsyncReadListener listener : listeners) {
			listener.onRecieveFailed(new AsyncReadEvent(this,
					AsyncReadEvent.FAILURE, null, e));
		}
	}

	private void notifyNewData(String data) {
		for (AsyncReadListener listener : listeners) {
			listener.onRecieveFailed(new AsyncReadEvent(this,
					AsyncReadEvent.SUCCESS, data, null));
		}
	}

	@Override
	public void run() {
		running = true;
		while (running) {
			executeRecieve();
		}

		cleanup();
	}

	private void cleanup() {
		try {
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		notifyInputClosed();
	}

	private void notifyInputClosed() {
		for (AsyncReadListener listener : listeners) {
			listener.onRecieveFailed(new AsyncReadEvent(this,
					AsyncReadEvent.CLOSURE, null, null));
		}
	}

	public void close() {
		running = false;
	}

	public synchronized void addAsyncReadListener(AsyncReadListener listener) {
		listeners.add(listener);
	}

	public synchronized void removeAsyncReadListener(AsyncReadListener listener) {
		listeners.remove(listener);
	}

}
