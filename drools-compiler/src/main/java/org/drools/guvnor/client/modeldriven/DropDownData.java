package org.drools.guvnor.client.modeldriven;

/**
 * Used to drive drop downs.
 * @author Michael Neale
 *
 */
public class DropDownData {

	/**
	 * If this is non null, just show these items.
	 */
	public String[] fixedList = null;

	/**
	 * this would be something that takes the name/value pairs and interpolates them into an MVEL expression
	 * that resolves to a list.
	 */
	public String queryExpression = null;

	/**
	 * Something like as list of:
	 * sex=M, name=Michael etc....
	 */
	public String[] valuePairs = null;

	public static DropDownData create(String[] list) {
		if (list == null) return null;
		return new DropDownData(list);
	}

	public static DropDownData create(String queryExpression, String[] valuePairs) {
		if (queryExpression == null) return null;
		return new DropDownData(queryExpression, valuePairs);
	}

	private DropDownData(String[] list) {
		this.fixedList = list;
	}

	private DropDownData(String queryExpression, String[] valuePairs) {
		this.queryExpression = queryExpression;
		this.valuePairs = valuePairs;
	}



}
