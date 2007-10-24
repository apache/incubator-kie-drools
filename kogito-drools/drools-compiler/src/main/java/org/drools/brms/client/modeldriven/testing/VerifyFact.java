package org.drools.brms.client.modeldriven.testing;

/**
 * This is for making assertions over a specific facts value/state AFTER execution.
 * @author Michael Neale
 *
 */
public class VerifyFact implements Assertion {

	public VerifyField[] fieldValues = new VerifyField[0];
	public String factName;



	public VerifyFact() {}
	public VerifyFact(String factName, VerifyField[] fieldValues) {
		this.factName = factName;
		this.fieldValues = fieldValues;
	}


	public boolean wasSuccessful() {
		for (int i = 0; i < fieldValues.length; i++) {
			VerifyField vf = fieldValues[i];
			if (!vf.success) {
				return false;
			}
		}
		return true;
	}

}
