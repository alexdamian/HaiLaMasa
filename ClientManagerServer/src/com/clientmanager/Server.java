package com.clientmanager;

public class Server {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TableManager.Init(15);
		InboundConnectionListener.Init(7777, 10);
		InboundConnectionListener connection_listener = InboundConnectionListener.getConnectionListener();
		connection_listener.startListening();
	}

}