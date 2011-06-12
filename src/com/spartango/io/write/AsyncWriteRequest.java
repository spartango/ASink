package com.spartango.io.write;

import com.spartango.io.AsyncIOEvent;

/**
 * 
 * @author anand
 * 
 */
public class AsyncWriteRequest {

	private final AsyncWriteSender parent;
	private final byte[] data;

	/**
	 * @param parent
	 * @param data
	 */
	public AsyncWriteRequest(AsyncWriteSender parent, String data) {
		this(parent, data.getBytes());
	}

	public AsyncWriteRequest(AsyncWriteSender parent, byte[] data) {
		this.data = data;
		this.parent = parent;
	}

	public AsyncWriteSender getParent() {
		return parent;
	}

	public byte[] getData() {
		return data;
	}

	public void notifySendSuccess() {
		if (parent != null) {
			parent.onWriteSuccess(new AsyncWriteEvent(this,
					AsyncWriteEvent.SUCCESS, null));
		}

	}

	public void notifySendFailure(Exception e) {
		if (parent != null)
			parent.onWriteFailure(new AsyncWriteEvent(this,
					AsyncWriteEvent.FAILURE, e));
	}

	public void notifySendUnavailable() {
		if (parent != null) {
			parent.onWriterClosed(new AsyncWriteEvent(this,
					AsyncIOEvent.CLOSURE, null));
		}
	}

}
