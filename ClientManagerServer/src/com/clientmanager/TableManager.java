package com.clientmanager;

import java.util.ArrayList;
import java.util.Collections;

import com.clientmanager.CommunicationProtos.TableInfo.TableStatus;
import com.clientmanager.CommunicationProtos.ClientCheck;
import com.clientmanager.CommunicationProtos.TableInfo;
import com.clientmanager.CommunicationProtos.Tables;

public class TableManager {

	private static ArrayList<TableInfo> all_tables;
	
	public static void Init(int num_tables) {
		all_tables = new ArrayList<TableInfo>();
		for (int i = 0; i < num_tables; ++i) {
			all_tables.add(TableInfo.newBuilder()
					.setTableId(i)
					.setStatus(TableStatus.FREE)
					.setMaxClients((int) Math.floor(Math.random() * 6) + 1)
					.addClientChecks(ClientCheck.newBuilder()
							.setPaid(false)
							.setPriceToPay(0)
							.setTimeToServe(0)
							.build())
					.build());
		}
		Collections.sort(all_tables, new TableManagerComparator());
	}
	
	public static void saveTable(TableInfo table_info) {
		synchronized (all_tables) {
			all_tables.set(table_info.getTableId(), table_info);
			Collections.sort(all_tables, new TableManagerComparator());
		}
	}
	
	public static TableInfo getTableInfo(int id) {
		synchronized (all_tables) {
			for (int i = 0; i < all_tables.size(); ++i) {
				if (all_tables.get(i).getTableId() == id) {
					return all_tables.get(i);
				}
			}
			return null;
		}
	}
	
	public static Tables getFreeTables() {
		synchronized (all_tables) {
			Tables.Builder b = Tables.newBuilder();
			for (int i = 0; i < all_tables.size(); ++i) {
				b.addTables(all_tables.get(i));
			}
			return b.build();
		}
	}
	
	
}
