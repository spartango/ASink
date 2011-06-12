package com.spartango.io.lineread;

import com.spartango.io.AsyncIOEvent;

public class AsyncLineReadEvent extends AsyncIOEvent {
	private final String data;
	private final AsyncLineReader source;

	public AsyncLineReadEvent(AsyncLineReader source, int type, String data,
			Exception e) {
		super(type, e);
		this.source = source;
		this.data = data;
	}

	public String getData() {
		return data;
	}

	public AsyncLineReader getSource() {
		return source;
	}

}
