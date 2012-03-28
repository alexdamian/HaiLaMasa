package com.clientmanager;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clientmanager.CommunicationProtos.FoodItem;
import com.clientmanager.CommunicationProtos.FoodItem.FoodStatus;

public class FoodAdapter extends ArrayAdapter<FoodItem> {
	
	private ArrayList<FoodItem> items;
	private Context context;
	
	public FoodAdapter(Context context, int textViewResourceId, ArrayList<FoodItem> items) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.context = context;
	}
	
	@Override
	 public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.food_row, null);
        }
        
        FoodItem item = items.get(position);
        if (item != null) {
        	TextView name = (TextView) v.findViewById(R.id.food_name);
        	TextView price = (TextView) v.findViewById(R.id.food_price);
        	TextView time = (TextView) v.findViewById(R.id.food_time);
        	Button delivered = (Button) v.findViewById(R.id.buttonFoodDelivered);
        	if (name != null) {
        		name.setText(item.getName());
        	}
        	if (price != null) {
        		price.setText("RON " + item.getPrice());
        	}
        	if (time != null) {
        		time.setText("" + item.getTime() + " minutes");
        	}
        	if (delivered != null) {
        		if (item.getStatus() == FoodStatus.SERVED) {
        			delivered.setClickable(false);
        			delivered.setText("Delivered");
        			delivered.setEnabled(false);
        		} else if (item.getStatus() == FoodStatus.ORDERED) {
        			delivered.setText("Deliver");
        			delivered.setClickable(true);
        		}
        		delivered.setOnClickListener(new DeliverFood(context, position, item));
        	}
        }
        return v;
	}
}
