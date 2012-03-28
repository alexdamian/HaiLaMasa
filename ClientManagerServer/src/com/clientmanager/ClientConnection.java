package com.clientmanager;

import java.io.*;
import java.net.Socket;

import com.clientmanager.CommunicationProtos.SocketMessage.MessageType;
import com.clientmanager.CommunicationProtos.*;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

public class ClientConnection implements Runnable {
	
	public ClientConnection(Socket client_socket) {
		this.client_socket = client_socket;
		try {
			out = new DataOutputStream(client_socket.getOutputStream());
			in = new DataInputStream(client_socket.getInputStream());
		} catch (IOException e) {
			Logging.LOG(0, e.getMessage());
		}
		this.run();
	}
	
	@Override
	public void run() {
		Logging.LOG(0, "Client " + client_socket.getInetAddress() + " connected.");
		
		// Parse incoming messages
		SocketMessage current_client_message = null;
		while (true) {
			try {
				current_client_message = SocketMessage.parseFrom(Communication.readMessage(in));
				System.out.println(current_client_message.toString());
				Message reply_message = processAndReturnReplyToCurrentMessage(current_client_message);
				Logging.LOG(2, reply_message.toString());
				if (reply_message != null) {
					Communication.sendMessage(reply_message, out);
				}
			} catch (InvalidProtocolBufferException e) {
				e.printStackTrace();
			}
		}
	}
	private static Message processAndReturnReplyToCurrentMessage(SocketMessage client_message) {
		Message m = null;
		if (client_message.getMessageType() == MessageType.REQUEST_FREE_TABLES) {
			m = TableManager.getFreeTables();
			return m;
		} else if (client_message.getMessageType() == MessageType.REQUEST_TABLE_INFO) {
			m = TableManager.getTableInfo(client_message.getTableId());
			return m;
		} else if (client_message.getMessageType() == MessageType.SAVE_TABLE_INFO) {
			TableManager.saveTable(client_message.getTableInfo());
			return null;
		}
		return null;
	}
	
	// Socket between this server and the client
	private Socket client_socket;
	// Output stream
	DataOutputStream out;
	// Input stream
	DataInputStream in;
}
