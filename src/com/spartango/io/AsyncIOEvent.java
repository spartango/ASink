package com.spartango.io;

public abstract class AsyncIOEvent {

	public static final int SUCCESS = 1;
	public static final int FAILURE = 2;
	public static final int CLOSURE = 3;

	protected final int type;
	protected final Exception error;

	public AsyncIOEvent(int type, Exception error) {
		this.type = type;
		this.error = error;
	}

	public int getType() {
		return type;
	}

	public Exception getError() {
		return error;
	}

}
