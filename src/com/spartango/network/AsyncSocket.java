package com.spartango.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.spartango.io.AsyncInputReader;
import com.spartango.io.AsyncOutputWriter;
import com.spartango.io.AsyncReadListener;
import com.spartango.io.AsyncWriteRequest;
import com.spartango.io.AsyncWriteSender;

/**
 * Implements an asynchronous socket connected to a host and port, allowing for
 * nonblocking sends and providing notifications on
 * data-receive/send/receive-failure
 * 
 * @see AsyncInputReader
 * @see AsyncOutputWriter
 * @author anand
 * 
 */
public class AsyncSocket {
	private Socket socket;
	private AsyncInputReader reader;
	private AsyncOutputWriter writer;

	private boolean running;

	/**
	 * Creates a new socket connection to a host on a given port. This will
	 * throw exceptions if it fails, as the socket will likely be unusable
	 * Successful creation starts the read and write systems immediately.
	 * BLOCKING--will wait for socket connection
	 * 
	 * @param host
	 * @param port
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public AsyncSocket(String host, int port) throws UnknownHostException,
			IOException {
		this(new Socket(host, port));
	}

	/**
	 * Wraps an existing socket (connected) in asynchronous readers and writers
	 * NONBLOCKING
	 * 
	 * @param source
	 * @throws IOException
	 */
	public AsyncSocket(Socket source) throws IOException {
		socket = source;
		reader = new AsyncInputReader(new BufferedReader(new InputStreamReader(
				socket.getInputStream())));
		writer = new AsyncOutputWriter(
				new PrintWriter(socket.getOutputStream()));
		running = false;

		start();
	}

	/**
	 * Starts the read and write threads to handle IO operations
	 */
	private void start() {
		running = true;
		// Start the read thread
		reader.start();
		// Start the write thread
		writer.start();

	}

	/**
	 * Closes this connection safely, stopping all IO and failing any pending
	 * operations
	 */
	public void close() {
		if (running) {
			try {
				reader.close();
				writer.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			running = false;
		}
	}

	/**
	 * Add a listener to be notified when new data is received
	 * 
	 * @param listener
	 */
	public void addAsyncSocketListener(AsyncReadListener listener) {
		reader.addAsyncReadListener(listener);
	}

	/**
	 * Remove a listener
	 * 
	 * @param listener
	 */
	public void removeAsyncSocketListener(AsyncReadListener listener) {
		reader.removeAsyncReadListener(listener);
	}

	/**
	 * Send some data over the socket NONBLOCKING
	 * 
	 * @param data
	 */
	public void send(String data) {
		writer.send(data);
	}

	/**
	 * Send some data over the socket, notifying the parent as necessary
	 * NONBLOCKING
	 * 
	 * @param data
	 * @param parent
	 */
	public void send(String data, AsyncWriteSender parent) {
		writer.send(data, parent);
	}

	/**
	 * Send some data over the socket, specified in a prepackaged request.
	 * 
	 * @param request
	 */
	public void send(AsyncWriteRequest request) {
		writer.send(request);
	}

	/**
	 * Check the status of this socket
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return running;
	}

	public InetAddress getInetAddress() {
		return socket.getInetAddress();
	}

	public int getPort() {
		return socket.getPort();
	}
	
	

}
