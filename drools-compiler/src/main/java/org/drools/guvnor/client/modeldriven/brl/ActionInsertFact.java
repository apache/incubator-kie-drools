package org.drools.guvnor.client.modeldriven.brl;

/**
 * This is used when asserting a new fact.
 * 
 * @author Michael Neale
 * 
 */
public class ActionInsertFact extends ActionFieldList {

	public String factType;
	private String boundName;
	private boolean isBound ;
	
	public boolean isBound() {
		return isBound;
	}

	public ActionInsertFact(final String type) {
		this.factType = type;
	}

	public ActionInsertFact() {
	}
	public String getBoundName() {
		return boundName;
	}

	public void setBoundName(String boundName) {
		this.boundName = boundName;
		isBound = true;
	}
}
