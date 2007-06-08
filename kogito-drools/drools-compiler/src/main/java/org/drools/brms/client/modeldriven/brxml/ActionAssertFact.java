package org.drools.brms.client.modeldriven.brxml;

/**
 * This is used when asserting a new fact.
 * @author Michael Neale
 *
 */
public class ActionAssertFact extends ActionFieldList {


    public String factType;

    public ActionAssertFact(final String type) {
        this.factType = type;
    }

    public ActionAssertFact() {
    }

}
