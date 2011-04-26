package com.spartango.io;

public interface AsyncReadListener {

	public void onDataRecieved(AsyncReadEvent e);

	public void onRecieveFailed(AsyncReadEvent e);

	public void onReaderClosed(AsyncReadEvent e);
}
