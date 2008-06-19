package org.drools.guvnor.client.modeldriven.testing;


/**
 * All assertions on results.
 *
 *
 * @author Michael Neale
 */
public interface Expectation extends Fixture {

	/**
	 * Return false if the assertion
	 * @return
	 */
	boolean wasSuccessful();



}
