package com.spartango.io;

/**
 * 
 * @author anand
 * 
 */
public class AsyncWriteRequest {

	private final AsyncWriteSender parent;
	private final String data;

	/**
	 * @param parent
	 * @param data
	 */
	public AsyncWriteRequest(AsyncWriteSender parent, String data) {
		this.parent = parent;
		this.data = data;
	}

	public AsyncWriteSender getParent() {
		return parent;
	}

	public String getData() {
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
					AsyncWriteEvent.CLOSED, null));
		}
	}

}
