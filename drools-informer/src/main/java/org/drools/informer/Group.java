/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.drools.informer;




import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * Represents any arbitrary way of grouping items. e.g.
 * </p>
 * 
 * <ul>
 * <li>sections within a page</li>
 * <li>a multi-column layout</li>
 * <li>a group of items which are all centre-aligned</li>
 * </ul>
 * 
 * <p>
 * How a particular <code>Group</code> is rendered to screen is determined solely by <code>presentationStyles</code>.
 * </p>
 * 
 * @author Damon Horrell
 */
public class Group extends Item {

	public static final String COMMA_SEPARATOR = ",";

	private static final long serialVersionUID = 1L;

	private String label;

	/**
	 * Items are represented internally as a comma-delimited string for efficient XML transport.
	 */
	private String items;

	public Group() {
	}

	public Group(String id) {
		super(id);
	}

	public Group(String id, String label) {
		super(id);
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets list of item ids.
	 * 
	 * @return
	 */
	public List<String> getItems() {
		return items == null ? null : (Arrays.asList(items.split(COMMA_SEPARATOR)));
	}

	/**
	 * @param itemId
	 * @return
	 */
	protected boolean validItemId(String itemId) {
		if ((itemId == null) || (itemId.length() == 0)) {
			return false;
		}

		if (itemId.contains(COMMA_SEPARATOR)) {
			throw new IllegalArgumentException();
		}
		return true;
	}

	/**
	 * Sets list of item ids. Values will NOT be trimmed.
	 * 
	 * @param items
	 */
	public void setItems(String[] items) {
		if (items == null) {
			this.items = null;
		} else {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < items.length; i++) {
				String temp = items[i];
				if (validItemId(temp)) {
					if (sb.length() > 0) {
						sb.append(COMMA_SEPARATOR);
					}
					sb.append(items[i]);
				}
			}
			if (sb.length() > 0) {
				this.items = sb.toString();
			} else {
				this.items = null;
			}
		}
	}



    /**
	 * Sets list of item ids. Values will NOT be trimmed.
	 *
	 * @param items
	 */
	public void setItems(List<String> items) {
		if (items == null) {
			this.items = null;
		} else {
			StringBuilder sb = new StringBuilder();
            Iterator<String> iter = items.iterator();
			while (iter.hasNext()) {
				String temp = iter.next();
				if (validItemId(temp)) {
					if (sb.length() > 0) {
						sb.append(COMMA_SEPARATOR);
					}
					sb.append(temp);
				}
			}
			if (sb.length() > 0) {
				this.items = sb.toString();
			} else {
				this.items = null;
			}
		}
	}

	/**
	 * Adds itemId to the existing list. Value will NOT be trimmed. Duplicates allowed. Null will be ignored
	 * 
	 * @param itemId
	 *            - cannot contain a comma
	 */
	public void addItem(String itemId) {
		if (validItemId(itemId)) {
			if ((this.items == null) || (this.items.length() == 0)) {
				this.items = itemId;
			} else {
				this.items = this.items + COMMA_SEPARATOR + itemId;
			}
		}
	}

	/**
	 * Adds itemId to the existing list before the position of the second item. If the second item is not in the list, it will be
	 * added on the end. Value will NOT be trimmed.
	 * 
	 * @param itemId
	 *            The value to insert - cannot contain a comma. Duplicates allowed. Null will be ignored
	 * @param beforeItemId
	 *            The entry before which the new item is to be inserted
	 */
	public void insertItem(String itemId, String beforeItemId) {
		if ((beforeItemId == null) || (beforeItemId.length() == 0) || (this.items == null)) {
			addItem(itemId);
		} else if (validItemId(itemId)) {
//			List<String> items = new ArrayList<String>(Arrays.asList(getItems()));
            List<String> items = getItems();
			int pos = items.indexOf(beforeItemId);
			if (pos < 0) {
				addItem(itemId);
			} else {
				items.add(pos, itemId);
				setItems(items.toArray());
			}
		}
	}

	/**
	 * Adds itemId to the existing list after the position of the second item. If the second item is not in the list, it will be
	 * added on the end. Value will NOT be trimmed.
	 * 
	 * @param itemId
	 *            The value to insert - cannot contain a comma. Duplicates allowed. Null will be ignored
	 * @param afterItemId
	 *            The entry after which the new item is to be inserted
	 */
	public void appendItem(String itemId, String afterItemId) {
		if ((afterItemId == null) || (afterItemId.length() == 0) || (this.items == null)) {
			addItem(itemId);
		} else if (validItemId(itemId)) {
//			List<String> items = new ArrayList<String>(Arrays.asList(getItems()));
            List<String> items = getItems();
			int pos = items.indexOf(afterItemId);
			if ((pos < 0) || ((pos + 1) == items.size())) {
				addItem(itemId);
			} else {
				items.add(pos + 1, itemId);
				setItems(items.toArray());
			}
		}
	}

	/**
	 * Removes itemId from the existing list. If it is the only items, will (re)set the list of items to null.
	 * 
	 * @param itemId
	 *            The value to remove. Ignore if null or doesn't exist
	 * @return The index of the removed item, or -1 if not found
	 */
	public int removeItem(String itemId) {
		if (validItemId(itemId)) {
//			List<String> items = new ArrayList<String>(Arrays.asList(getItems()));
            List<String> items = getItems();
			int pos = items.indexOf(itemId);
			if (pos >= 0) {
				if (items.size() == 1) {
					this.items = null;
				} else {
					items.remove(itemId);
					setItems(items.toArray());
				}
				return pos;
			}
		}
		return -1;
	}

	/**
	 * Sets list of item ids.
	 * 
	 * This method is provided to support the MVEL syntax in rules e.g.
	 * <p>
	 * <code>group.setItems({"a", "b"});</code>
	 * </p>
	 * 
	 * @param items
	 */
	public void setItems(Object[] items) {
		if (items == null) {
			this.items = null;
		} else {
			setItems((String[]) Arrays.asList(items).toArray(new String[] {}));
		}
	}

	/**
	 * Gets list of item ids as a comma delimited string.
	 * 
	 * TODO this method can be removed when Guvnor supports String[]
	 * 
	 * @return
	 * @deprecated
	 */
	public String getItemsAsString() {
		return items;
	}

	/**
	 * Gets list of item ids as a comma delimited string. Implemented for testing purpose only - package visibility
	 * 
	 * @return
	 */
	String getInternalItemsAsString() {
		return items;
	}

	/**
	 * Set list of item ids as a comma-delimited string.
	 * 
	 * TODO this method can be removed when Guvnor supports String[]
	 * 
	 * Note: setItemsAsString("") silently converts the value to null
	 * 
	 * @param items
	 * @deprecated
	 */
	public void setItemsAsString(String items) {
		if (items != null && items.equals("")) {
			items = null;
		}
		this.items = items;
	}

	/**
	 * For debugging purposes.
	 */
	@Override
	public String toString() {
		return super.toString() + " label=" + label + " items=" + items;
	}
}
