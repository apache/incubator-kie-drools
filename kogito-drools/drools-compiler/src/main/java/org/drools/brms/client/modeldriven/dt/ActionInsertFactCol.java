package org.drools.brms.client.modeldriven.dt;

public class ActionInsertFactCol extends ActionCol {

	/**
	 * The fact type (class) that is to be created.
	 * eg Driver, Person, Cheese.
	 */
	public String factType;

	/**
	 * The bound name of the variable to be effected.
	 * If the same name appears twice, is it merged into the same action.
	 */
	public String boundName;

	/**
	 * The field on the fact being effected.
	 */
	public String factField;

	/**
	 * Same as the type in ActionFieldValue - eg, either a String, or Numeric.
	 * Refers to the data type of the literal value in the cell.
	 * Refer to the types in SuggestionCompletionEngine.
	 */
	public String type;

	/**
	 * An optional comman separated list of values.
	 */
	public String valueList;


}
