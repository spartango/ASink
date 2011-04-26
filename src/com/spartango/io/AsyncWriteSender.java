package com.spartango.io;

public interface AsyncWriteSender {
	public void onWriteSuccess(AsyncWriteEvent e);

	public void onWriteFailure(AsyncWriteEvent e);

	public void onWriterClosed(AsyncWriteEvent e);

}
