package com.spartango.network;

public interface AsyncServerListener {
	public void onServerClosed();

	public void onNewClient(AsyncServerEvent e);

	public void onServerSocketFailure(AsyncServerEvent e);

}
