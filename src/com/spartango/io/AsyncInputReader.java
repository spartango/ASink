package com.spartango.io;

import java.io.BufferedReader;
import java.io.IOException;
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
public class AsyncInputReader implements Runnable {
	private int sleepTime = 0; // ms

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
		running = false;
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

	private synchronized void executeReceive() {
		try {
			String data = input.readLine();
			notifyNewData(data);
		} catch (IOException e) {
			notifyReadFailure(e);
		}
	}

	private void notifyReadFailure(Exception e) {
		// Create an immutable event
		AsyncReadEvent event = new AsyncReadEvent(this, AsyncReadEvent.FAILURE,
				null, e);
		for (AsyncReadListener listener : listeners) {
			listener.onReceiveFailed(event);
		}
	}

	private void notifyNewData(String data) {
		//Create an immutable event 
		AsyncReadEvent event =  new AsyncReadEvent(this,
				AsyncReadEvent.SUCCESS, data, null);
		for (AsyncReadListener listener : listeners) {
			listener.onReceiveFailed(event);
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
		for (AsyncReadListener listener : listeners) {
			listener.onReceiveFailed(new AsyncReadEvent(this,
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

	public int getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}

}
