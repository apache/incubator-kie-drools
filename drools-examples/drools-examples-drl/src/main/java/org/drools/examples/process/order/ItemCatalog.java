package org.drools.examples.process.order;

public interface ItemCatalog {

	Item getItem(String itemId);
	
	void addItem(Item item);
	
}
