package org.drools.brms.client.modeldriven.testing;

import java.io.Serializable;

/**
 * All assertions on results.
 *
 * Types of assertions:
 *	- field of a fact expected/actual not null/MVEL
 *  - rule fired (n times)
 *  - a fact exists (with field values/MVEL)
 *  - a fact does not exist (?)
 *  - a global value (field - or MVEL script)
 *
 * @author Michael Neale
 */
public interface Assertion extends Serializable {



}
