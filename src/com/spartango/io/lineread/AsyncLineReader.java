package com.spartango.io.lineread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Vector;

/**
 * Provides an asynchronous system for receiving data from a reader as it is
 * available. Complete reads and failed reads provide events to listeners with
 * appropriate data, as it becomes available.
 * 
 * @author anand
 * 
 */
public class AsyncLineReader implements Runnable {
	private int sleepTime = 0; // ms

	private List<AsyncLineReadListener> listeners;
	private BufferedReader input;
	private Thread runner;

	private boolean running;

	public AsyncLineReader(Reader reader) {
		this(new BufferedReader(reader));
	}

	/**
	 * Creates an asynchronous reader to provide events to listeners, but does
	 * not start publishing events.
	 * 
	 * @param bufferedReader
	 */
	public AsyncLineReader(BufferedReader bufferedReader) {
		running = false;
		listeners = new Vector<AsyncLineReadListener>();
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

	private synchronized void executeReceive() {
		try {
			String data = input.readLine();
			if (data != null)
				notifyNewData(data);
		} catch (IOException e) {
			notifyReadFailure(e);
		}
	}

	private void notifyReadFailure(Exception e) {
		// Create an immutable event
		AsyncLineReadEvent event = new AsyncLineReadEvent(this,
				AsyncLineReadEvent.FAILURE, null, e);
		for (AsyncLineReadListener listener : listeners) {
			listener.onReceiveFailed(event);
		}
	}

	private void notifyNewData(String data) {
		// Create an immutable event
		AsyncLineReadEvent event = new AsyncLineReadEvent(this,
				AsyncLineReadEvent.SUCCESS, data, null);
		for (AsyncLineReadListener listener : listeners) {
			listener.onDataReceived(event);
		}
	}

	@Override
	public void run() {
		running = true;
		while (running) {
			executeReceive();
			pause();
		}

		cleanup();
	}

	private void pause() {
		if (sleepTime > 0) {
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				close();
			}
		}
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
		for (AsyncLineReadListener listener : listeners) {
			listener.onReceiveFailed(new AsyncLineReadEvent(this,
					AsyncLineReadEvent.CLOSURE, null, null));
		}
	}

	public void close() {
		running = false;
	}

	public void addAsyncLineReadListener(AsyncLineReadListener listener) {
		listeners.add(listener);
	}

	public void removeAsyncLineReadListener(AsyncLineReadListener listener) {
		listeners.remove(listener);
	}

	public int getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}

}
