package com.spartango.io;

public interface AsyncReadListener {

	public void onDataReceived(AsyncReadEvent e);

	public void onReceiveFailed(AsyncReadEvent e);

	public void onReaderClosed(AsyncReadEvent e);
}
