package com.spartango.io.lineread;

public interface AsyncLineReadListener {

	public void onDataReceived(AsyncLineReadEvent e);

	public void onReceiveFailed(AsyncLineReadEvent e);

	public void onReaderClosed(AsyncLineReadEvent e);
}
