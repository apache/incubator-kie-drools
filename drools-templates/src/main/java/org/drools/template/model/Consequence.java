package org.drools.template.model;

/**
 * This represents a RHS fragement. A rule may have many of these, or just one.
 * They are all mushed together.
 */
public class Consequence extends DRLElement
        implements
        DRLJavaEmitter {

    private String _snippet;

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
