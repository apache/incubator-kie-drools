package org.drools.brms.client.modeldriven.testing;

/**
 * Retract a named fact.
 * @author Michael Neale
 *
 */
public class RetractFact implements Fixture {

	public RetractFact() {}
	public RetractFact(String s) {
		this.name = s;
	}

	public String name;

}
