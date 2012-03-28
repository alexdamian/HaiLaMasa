package com.clientmanager;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.clientmanager.CommunicationProtos.FoodItem;
import com.clientmanager.CommunicationProtos.TableInfo;
import com.clientmanager.CommunicationProtos.FoodItem.FoodStatus;



public class DeliverFood implements OnClickListener {

	private int id;
	private FoodItem item;
	private TableScreenActivity context;
	
	public DeliverFood(Context context, int id, FoodItem item) {
		this.id = id;
		this.item = item;
		this.context = (TableScreenActivity) context;
	}
	
	@Override
	public void onClick(View v) {
		Button bt = (Button) v;
		// TODO Auto-generated method stub
		TableInfo.Builder b = TableInfo.newBuilder()
				.mergeFrom(context.table_info);
		b.getClientChecksBuilder(context.currentClientId)
				.getFoodItemsBuilder(id)
				.setStatus(FoodStatus.SERVED);
		context.table_info = b.build();
		
		bt.setClickable(false);
		bt.setText("Delivered");
		bt.setEnabled(false);
	}

}
