package com.spartango.network;

import java.net.Socket;

public class AsyncServerEvent {
	public static final int NEW_CLIENT = 0;
	public static final int FAILURE = 1;
	public static final int CLOSED = 2;

	private final int type;
	private final AsyncServerSocket source;
	private final Socket client;
	private final Exception error;
	
	/**
	 * @param type
	 * @param source
	 * @param client
	 * @param error
	 */
	public AsyncServerEvent(int type, AsyncServerSocket source, Socket client,
			Exception error) {
		this.type = type;
		this.source = source;
		this.client = client;
		this.error = error;
	}

	public int getType() {
		return type;
	}

	public AsyncServerSocket getSource() {
		return source;
	}

	public Socket getClient() {
		return client;
	}

	public Exception getError() {
		return error;
	}

	
}
