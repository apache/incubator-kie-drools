package org.drools.examples.process.order;

import java.util.HashMap;
import java.util.Map;

public class DefaultItemCatalog implements ItemCatalog {

	private Map<String, Item> items = new HashMap<String, Item>();
	
	public Item getItem(String itemId) {
		return items.get(itemId);
	}
	
	public void addItem(Item item) {
		this.items.put(item.getItemId(), item);
	}
	
}
