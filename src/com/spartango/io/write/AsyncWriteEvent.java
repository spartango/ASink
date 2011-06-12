package com.spartango.io.write;

import com.spartango.io.AsyncIOEvent;

public class AsyncWriteEvent extends AsyncIOEvent {

	private final AsyncWriteRequest request;

	public AsyncWriteEvent(AsyncWriteRequest request, int type, Exception e) {
		super(type, e);
		this.request = request;
	}

	public AsyncWriteRequest getRequest() {
		return request;
	}

}
