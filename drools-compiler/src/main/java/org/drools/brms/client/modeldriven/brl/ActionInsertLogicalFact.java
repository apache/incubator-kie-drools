package org.drools.brms.client.modeldriven.brl;

/**
 * Logical assertions are used as part of "truth maintenance". 
 * 
 * @author Michael Neale
 */
public class ActionInsertLogicalFact extends ActionInsertFact {

    public ActionInsertLogicalFact(final String fact) {
        super( fact );
    }

    public ActionInsertLogicalFact() {
        super();
    }

}
