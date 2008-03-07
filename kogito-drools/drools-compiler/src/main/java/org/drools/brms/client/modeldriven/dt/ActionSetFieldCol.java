package org.drools.brms.client.modeldriven.dt;

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
}
