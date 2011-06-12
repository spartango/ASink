package com.spartango.netdata;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.spartango.io.dataread.AsyncDataReadListener;
import com.spartango.io.dataread.AsyncDataReader;
import com.spartango.io.write.AsyncOutputWriter;
import com.spartango.io.write.AsyncWriteRequest;
import com.spartango.io.write.AsyncWriteSender;

/**
 * Implements an asynchronous socket connected to a host and port, allowing for
 * nonblocking sends and providing notifications on
 * data-receive/send/receive-failure
 * 
 * @see AsyncDataReader
 * @see AsyncOutputWriter
 * @author anand
 * 
 */
public class AsyncDataSocket {
	private Socket socket;
	private AsyncDataReader reader;
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
	public AsyncDataSocket(String host, int port, int dataLength)
			throws UnknownHostException, IOException {
		this(new Socket(host, port), dataLength);
	}

	/**
	 * Wraps an existing socket (connected) in asynchronous readers and writers
	 * NONBLOCKING
	 * 
	 * @param source
	 * @throws IOException
	 */
	public AsyncDataSocket(Socket source, int dataLength) throws IOException {
		socket = source;
		reader = new AsyncDataReader(socket.getInputStream(), dataLength);
		writer = new AsyncOutputWriter(socket.getOutputStream());
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
	public void addAsyncSocketListener(AsyncDataReadListener listener) {
		reader.addAsyncDataReadListener(listener);
	}

	/**
	 * Remove a listener
	 * 
	 * @param listener
	 */
	public void removeAsyncSocketListener(AsyncDataReadListener listener) {
		reader.removeAsyncDataReadListener(listener);
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
	 * Send some data over the socket NONBLOCKING
	 * 
	 * @param data
	 */
	public void send(byte[] data) {
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
	 * Send some data over the socket, notifying the parent as necessary
	 * NONBLOCKING
	 * 
	 * @param data
	 * @param parent
	 */
	public void send(byte[] data, AsyncWriteSender parent) {
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
