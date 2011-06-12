package com.spartango.io.dataread;

public interface AsyncDataReadListener {

	public void onDataReceived(AsyncDataReadEvent e);

	public void onReceiveFailed(AsyncDataReadEvent e);

	public void onReaderClosed(AsyncDataReadEvent e);
}
