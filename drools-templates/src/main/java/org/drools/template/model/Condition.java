package org.drools.template.model;

/**
 * This class represents a single LHS item (which will be the same as a line in
 * traditional DRL).
 */
public class Condition extends DRLElement
        implements
        DRLJavaEmitter {

    public String _snippet;

    /**
     * @param snippet The snippet to set.
     */
    public void setSnippet(final String snippet) {
        this._snippet = snippet;
    }

    public String getSnippet() {
        return this._snippet;
    }

    public void renderDRL(final DRLOutput out) {
        out.writeLine("\t\t" + this._snippet);
    }
}
