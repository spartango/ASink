package com.spartango.io;

public class AsyncReadEvent {

	// Event typess
	public static final int SUCCESS = 1;
	public static final int FAILURE = 2;
	public static final int CLOSURE = 3;

	private final String data;
	private final AsyncInputReader source;
	private final int type;
	private final Exception error;

	public AsyncReadEvent(AsyncInputReader source, int type, String data,
			Exception e) {
		this.source = source;
		this.type = type;
		this.data = data;
		this.error = e;
	}

	public String getData() {
		return data;
	}

	public AsyncInputReader getSource() {
		return source;
	}

	public int getType() {
		return type;
	}

	public Exception getError() {
		return error;
	}

}
