package com.spartango.io.dataread;

import java.io.IOException;
import java.io.InputStream;
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
public class AsyncDataReader implements Runnable {
	private int sleepTime = 0; // ms

	private List<AsyncDataReadListener> listeners;
	private InputStream input;
	private Thread runner;

	private int readLength;
	private byte[] buffer;

	private boolean running;

	/**
	 * Creates an asynchronous reader to provide events to listeners, but does
	 * not start publishing events.
	 * 
	 * @param bufferedReader
	 */
	public AsyncDataReader(InputStream in, int length) {
		running = false;
		listeners = new Vector<AsyncDataReadListener>();
		input = in;
		runner = new Thread(this);
		readLength = length;
		buffer = new byte[readLength];
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
			int dataLength = input.read(buffer);
			notifyNewData(buffer.clone(), dataLength);
		} catch (IOException e) {
			notifyReadFailure(e);
		}
	}

	private void notifyReadFailure(Exception e) {
		// Create an immutable event
		AsyncDataReadEvent event = new AsyncDataReadEvent(this,
				AsyncDataReadEvent.FAILURE, null, 0, e);
		for (AsyncDataReadListener listener : listeners) {
			listener.onReceiveFailed(event);
		}
	}

	private void notifyNewData(byte[] bs, int dataLength) {
		// Create an immutable event
		AsyncDataReadEvent event = new AsyncDataReadEvent(this,
				AsyncDataReadEvent.SUCCESS, bs, dataLength, null);
		for (AsyncDataReadListener listener : listeners) {
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
		for (AsyncDataReadListener listener : listeners) {
			listener.onReceiveFailed(new AsyncDataReadEvent(this,
					AsyncDataReadEvent.CLOSURE, null, 0, null));
		}
	}

	public void close() {
		running = false;
	}

	public void addAsyncDataReadListener(AsyncDataReadListener listener) {
		listeners.add(listener);
	}

	public void removeAsyncDataReadListener(AsyncDataReadListener listener) {
		listeners.remove(listener);
	}

	public int getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}

	public int getReadLength() {
		return readLength;
	}

	public void setReadLength(int readLength) {
		this.readLength = readLength;
	}

}
