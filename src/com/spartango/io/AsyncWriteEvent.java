package com.spartango.io;

public class AsyncWriteEvent {
	public static final int CLOSED = 3;
	public static final int FAILURE = 2;
	public static final int SUCCESS = 1;

	private final AsyncWriteRequest request;
	private final int type;
	private final Exception error;

	public AsyncWriteEvent(AsyncWriteRequest request, int type, Exception e) {
		this.request = request;
		this.type = type;
		this.error = e;
	}
}
