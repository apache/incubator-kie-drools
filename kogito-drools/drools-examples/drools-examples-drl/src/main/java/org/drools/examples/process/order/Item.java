package org.drools.examples.process.order;

public class Item {
	
	private String itemId;
	private String name;
	private String description;
	private int minimalAge;
	
	public Item(String itemId) {
		this.itemId = itemId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getMinimalAge() {
		return minimalAge;
	}

	public void setMinimalAge(int minimalAge) {
		this.minimalAge = minimalAge;
	}

	public String getItemId() {
		return itemId;
	}
	
}
