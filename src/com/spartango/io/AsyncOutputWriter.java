package com.spartango.io;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Provides a means to perform non-blocking IO on a printWriter, with events to
 * notify send success and failure
 * 
 * @author anand
 * 
 */
public class AsyncOutputWriter implements Runnable {
	private int sleepTime = 5; // ms

	private Queue<AsyncWriteRequest> sendQueue;
	private PrintWriter output;
	private Thread runner;

	private boolean running;

	/**
	 * Creates an asynchronous writer bound to the specified printwriter Note
	 * that this does not automatically start, and must be explicitly run
	 * 
	 * @param printWriter
	 */
	public AsyncOutputWriter(PrintWriter printWriter) {
		running = false;
		sendQueue = new LinkedList<AsyncWriteRequest>();
		output = printWriter;
		runner = new Thread(this);
	}

	/**
	 * Starts writing from the queue to the source
	 */
	public void start() {
		if (!running)
			runner.start();
	}

	private synchronized void executeSend() {
		if (!sendQueue.isEmpty()) {
			AsyncWriteRequest request = sendQueue.remove();
			try {
				output.println(request.getData());
				request.notifySendSuccess();
			} catch (Exception e) {
				request.notifySendFailure(e);
			}
		}
	}

	/**
	 * Sends a string to the host, without a parent to be notified. NONBLOCKING
	 * 
	 * @param data
	 */
	public void send(String data) {
		send(new AsyncWriteRequest(null, data));
	}

	/**
	 * Creates and queues a request to be sent, with notifications sent to the
	 * parent. NONBLOCKING
	 * 
	 * @param data
	 * @param parent
	 */
	public void send(String data, AsyncWriteSender parent) {
		send(new AsyncWriteRequest(parent, data));
	}

	/**
	 * Adds a request to be sent NONBLOCKING
	 * 
	 * @param request
	 */
	public synchronized void send(AsyncWriteRequest request) {
		sendQueue.add(request);
	}

	public void run() {

		running = true;
		while (running) {
			executeSend();
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
		output.close();
		if (!sendQueue.isEmpty()) {
			for (AsyncWriteRequest req : sendQueue) {
				req.notifySendUnavailable();
			}
		}
	}

	public void close() {
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	public int getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}

}
