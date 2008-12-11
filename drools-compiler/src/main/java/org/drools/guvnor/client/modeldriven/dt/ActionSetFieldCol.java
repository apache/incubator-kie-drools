package org.drools.guvnor.client.modeldriven.dt;

public class ActionSetFieldCol extends ActionCol {

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
	 * These values come from SuggestionCompletionEngine.
	 */
	public String type;

	/**
	 * An optional comma separated list of values.
	 */
	public String valueList;


	/**
	 * This will be true if it is meant to be a modify to the engine, when in inferencing mode.
	 */
	public boolean update = false;
}
