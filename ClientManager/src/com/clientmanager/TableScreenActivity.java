package com.clientmanager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clientmanager.CommunicationProtos.ClientCheck;
import com.clientmanager.CommunicationProtos.FoodItem;
import com.clientmanager.CommunicationProtos.SocketMessage;
import com.clientmanager.CommunicationProtos.Tables;
import com.clientmanager.CommunicationProtos.FoodItem.FoodStatus;
import com.clientmanager.CommunicationProtos.TableInfo;
import com.clientmanager.CommunicationProtos.TableInfo.TableStatus;
import com.google.protobuf.InvalidProtocolBufferException;

public class TableScreenActivity extends Activity {
	
	
	protected int tableId = 0;
	protected int currentClientId = 0;
	protected TableInfo table_info;
	
	protected Socket socket = null;
	protected DataOutputStream out;
	protected DataInputStream in;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.table_screen_layout);
		
		tableId = getIntent().getIntExtra(Constants.table_id_key, 0);
		try {
			socket = new Socket("10.0.2.2", 7777);
    		out = new DataOutputStream(socket.getOutputStream());
    		in = new DataInputStream(socket.getInputStream());
    		getTableInfo();
    		updateScreen();
    	} catch (Exception e) {
    		Log.e("MainScreenActivity", e.getMessage());
    	}
		
		addButtonListeners();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		SocketMessage m = SocketMessage.newBuilder()
				.setMessageType(SocketMessage.MessageType.SAVE_TABLE_INFO)
				.setTableInfo(table_info)
				.build();
		Communication.sendMessage(m, out);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (socket != null) {
			getTableInfo();
			updateScreen();
		}
	}
	
	private void getTableInfo() {
		if (socket == null) {
			return;
		}
		SocketMessage m = SocketMessage.newBuilder()
    			.setMessageType(SocketMessage.MessageType.REQUEST_TABLE_INFO)
    			.setTableId(tableId)
    			.build();
    	Communication.sendMessage(m, out);
    	
    	try {
			table_info = TableInfo.parseFrom(Communication.readMessage(in));
			table_info = TableInfo.newBuilder()
					.mergeFrom(table_info)
					.setStatus(TableStatus.TAKEN)
					.build();
		} catch (InvalidProtocolBufferException e) {
			Log.e("MainScreenActivity", e.getMessage());
		}
	}
	
	private void addNewClient() {
		// Create a new check for this client
		ClientCheck newClientCheck = ClientCheck.newBuilder()
				.setPaid(false)
				.setPriceToPay(0)
				.setTimeToServe(0)
				.build();
		table_info = TableInfo.newBuilder()
				.mergeFrom(table_info)
				.addClientChecks(newClientCheck)
				.build();
		// Move to it
		currentClientId = table_info.getClientChecksCount() - 1;
	}
	
	private void addButtonListeners() {
		// Add listener for "Client Pay" button
		Button clientPayButton = (Button) findViewById(R.id.buttonClientPay);
		clientPayButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Remove the current client. This is a little trickier.
				TableInfo.Builder b = TableInfo.newBuilder();
				b.mergeFrom(table_info).clearClientChecks();
				for (int i = 0; i < table_info.getClientChecksCount(); ++i) {
					if (i != currentClientId) {
						b.addClientChecks(table_info.getClientChecks(i));
					}
				}
				table_info = b.build();
				--currentClientId;
				if (currentClientId < 0) {
					currentClientId = 0;
				}
				if (table_info.getClientChecksCount() == 0) {
					table_info = TableInfo.newBuilder()
							.mergeFrom(table_info)
							.addClientChecks(ClientCheck.newBuilder()
									.setPaid(false)
									.setPriceToPay(0)
									.setTimeToServe(0)
									.build())
							.build();
				}
				updateScreen();
				
			}
		});
		// Add listener for "Add Client" button
		Button addClientButton = (Button) findViewById(R.id.buttonAddClient);
		addClientButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addNewClient();
				updateScreen();
				
			}
		});
		// Add listener for the "Add Food" button
		Button addFoodButton = (Button) findViewById(R.id.buttonAddFood);
		addFoodButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (table_info.getClientChecksCount() != 0) {
					// Add a new piece of food to this client
					// This is now mock
					FoodItem newFoodItem = FoodItem.newBuilder()
							.setName("Food " + (int) Math.floor(Math.random() * 100))
							.setPrice((int) Math.floor(Math.random() * 1000))
							.setTime((int) Math.floor(Math.random() * 20))
							.setStatus(FoodStatus.ORDERED)
							.build();
					
					int priceToPay = table_info.getClientChecks(currentClientId).getPriceToPay();
					int timeToServe = table_info.getClientChecks(currentClientId).getTimeToServe();
					
					TableInfo.Builder b = TableInfo.newBuilder()
							.mergeFrom(table_info);
					b.getClientChecksBuilder(currentClientId)
							.addFoodItems(newFoodItem)
							.setTimeToServe(Math.max(timeToServe, newFoodItem.getTime()))
							.setPriceToPay(priceToPay + newFoodItem.getPrice());
					table_info = b.build();
					updateScreen();
				}
			}
		});
		// Add Listeners for the Next and Previous buttons
		Button prevClientButton = (Button) findViewById(R.id.buttonPrevClient);
		prevClientButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				--currentClientId;
				updateScreen();
				
			}
		});
		Button nextClientButton = (Button) findViewById(R.id.buttonNextClient);
		nextClientButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				++currentClientId;
				updateScreen();
				
			}
		});
	}
	
	private void updateScreen() {
		if (table_info == null) {
			return;
		}
		ClientCheck clientCheck = table_info.getClientChecks(currentClientId);
		if (clientCheck == null) {
			return;
		}
		// HEADER
		// Table number
		TextView headerText = (TextView) findViewById(R.id.tableHeader);
		if (headerText != null) {
			headerText.setText("Table " + tableId);
		}
		// Client Number
		TextView clientNameText = (TextView) findViewById(R.id.clientId);
		if (clientNameText != null) {
			if (table_info.getClientChecksCount() != 0) {
				clientNameText.setText("Client " + (currentClientId + 1));
			} else {
				clientNameText.setText("No Clients");
			}
		}
		// Time to serve
		TextView timeToServe = (TextView) findViewById(R.id.timeToServe);
		if (timeToServe != null) {
			timeToServe.setText("Waiting time: " + clientCheck.getTimeToServe() + " minutes");
		}
		// Price to pay
		TextView priceToPay = (TextView) findViewById(R.id.priceToPay);
		if (priceToPay != null) {
			priceToPay.setText("Price to pay: " + clientCheck.getPriceToPay() + " RON");
		}
		// BUTTONS
		Button prevClientButton = (Button) findViewById(R.id.buttonPrevClient);
		Button nextClientButton = (Button) findViewById(R.id.buttonNextClient);
		prevClientButton.setEnabled(currentClientId != 0);
		nextClientButton.setEnabled(currentClientId != table_info.getClientChecksCount() - 1);
		Button addClientButton = (Button) findViewById(R.id.buttonAddClient);
		addClientButton.setEnabled(table_info.getClientChecksCount() < table_info.getMaxClients());
		// CONTENT
		// List of items for the current client
		ArrayList<FoodItem> orderedFoods = new ArrayList<FoodItem>();
		for (int i = 0; i < clientCheck.getFoodItemsCount(); ++i) {
			orderedFoods.add(clientCheck.getFoodItems(i));
		}
		ListView lv = (ListView) findViewById(R.id.orderedFoodList);
		if (lv != null) {
			lv.setAdapter(new FoodAdapter(this,	R.layout.food_row, orderedFoods));
		}
	}
}
