package org.drools.brms.client.modeldriven.brxml;

/**
 * This is used when asserting a new fact.
 * @author Michael Neale
 *
 */
public class ActionInsertFact extends ActionFieldList {


    public String factType;

    public ActionInsertFact(final String type) {
        this.factType = type;
    }

    public ActionInsertFact() {
    }

}
