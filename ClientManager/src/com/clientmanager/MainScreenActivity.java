package com.clientmanager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.clientmanager.CommunicationProtos.TableInfo;
import com.clientmanager.CommunicationProtos.TableInfo.TableStatus;
import com.clientmanager.CommunicationProtos.Tables;
import com.clientmanager.CommunicationProtos.SocketMessage;
import com.google.protobuf.InvalidProtocolBufferException;

public class MainScreenActivity extends Activity {
	
	private Tables free_tables = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table_selector);
    }
   
    @Override
    protected void onResume() {
    	super.onResume();
    	getData();
    	fillData();
    }
    
    private void getData() {
    	Socket s;
    	DataOutputStream out;
    	DataInputStream in;
		try {
			s = new Socket("10.0.2.2", 7777);
	    	out = new DataOutputStream(s.getOutputStream());
	    	in = new DataInputStream(s.getInputStream());
	    	SocketMessage m = SocketMessage.newBuilder()
	    			.setMessageType(SocketMessage.MessageType.REQUEST_FREE_TABLES)
	    			.build();
	    	Communication.sendMessage(m, out);
	    	free_tables = Tables.parseFrom(Communication.readMessage(in));
	    	s.close();
	    	s = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void fillData() {
    	if (free_tables == null) {
    		return;
    	}
    	ArrayList<TableInfo> tables = new ArrayList<TableInfo>();
    	for(int i = 0; i < free_tables.getTablesCount(); ++i) {
    		tables.add(free_tables.getTables(i));
    	}
    	GridView gridView = (GridView) findViewById(R.id.gridview);
    	gridView.setAdapter(new TableAdapter(this, R.layout.table_row, tables));
    }
}