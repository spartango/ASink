package com.spartango.io.dataread;

import com.spartango.io.AsyncIOEvent;

public class AsyncDataReadEvent extends AsyncIOEvent {
	private final byte[] data;
	private final AsyncDataReader source;

	private final int dataLength;

	public AsyncDataReadEvent(AsyncDataReader source, int type, byte[] data,
			int length, Exception e) {
		super(type, e);
		this.source = source;
		this.data = data;
		dataLength = length;
	}

	public byte[] getData() {
		return data;
	}

	public AsyncDataReader getSource() {
		return source;
	}

	public int getDataLength() {
		return dataLength;
	}

}
