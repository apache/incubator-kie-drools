package org.drools.brms.client.modeldriven.brxml;

/**
 * Logical assertions are used as part of "truth maintenance". 
 * 
 * @author Michael Neale
 */
public class ActionAssertLogicalFact extends ActionAssertFact {

    public ActionAssertLogicalFact(final String fact) {
        super( fact );
    }

    public ActionAssertLogicalFact() {
        super();
    }

}
