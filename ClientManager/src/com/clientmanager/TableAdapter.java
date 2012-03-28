package com.clientmanager;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.clientmanager.CommunicationProtos.TableInfo;
import com.clientmanager.CommunicationProtos.TableInfo.TableStatus;

public class TableAdapter extends ArrayAdapter<TableInfo> {
	
	private class MyClickListener implements OnClickListener {

		private int position;
		private Context context;
		
		public MyClickListener(int position, Context context) {
			this.position = position;
			this.context = context;
		}
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, TableScreenActivity.class); 
	    	intent.putExtra(Constants.table_id_key, position);
	    	context.startActivity(intent);
		}
		
	}

	private ArrayList<TableInfo> items;
	private Context context;
	
	public TableAdapter(Context context, int textViewResourceId, ArrayList<TableInfo> items) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.context = context;
	}
	
	@Override
	 public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.table_row, null);
        }
        
        TableInfo item = items.get(position);
        if (item != null) {
        	Button tableButton = (Button) v.findViewById(R.id.tableName);
        	tableButton.setSingleLine(false);
        	if (tableButton != null) {
        		tableButton.setText("Table " + item.getTableId() + "\nClients: " + item.getMaxClients());
        	}
        	if (item.getStatus() == TableStatus.FREE) {
        		tableButton.setBackgroundColor(Color.rgb(106, 217, 123));
    		} else {
    			tableButton.setBackgroundColor(Color.rgb(153, 49, 41));
    		}
        	tableButton.setOnClickListener(new MyClickListener(position, context));
        }
        return v;
	}
	
}
