package com.clientmanager;

import java.util.Comparator;

import com.clientmanager.CommunicationProtos.TableInfo;
import com.clientmanager.CommunicationProtos.TableInfo.TableStatus;

public class TableManagerComparator implements Comparator<TableInfo> {

	@Override
	public int compare(TableInfo a, TableInfo b) {
		if (1 == 1) {
			return 0;
		}
		// Sort it like this:
		// Free tables first
		if (a.getStatus() == TableStatus.FREE && b.getStatus() == TableStatus.TAKEN) {
			return -1;
		}
		if (a.getStatus() == TableStatus.TAKEN && b.getStatus() == TableStatus.FREE) {
			return 1;
		}
		// Smaller tables first
		return (a.getMaxClients() - b.getMaxClients());
	}
	
}
