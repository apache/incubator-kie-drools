package org.drools.template.model;

/**
 * The LayerSupertype for this model/parse tree.
 */
public abstract class DRLElement {

    private String _comment;

    public void setComment(final String comment) {
        this._comment = comment;
    }

    String getComment() {
        return this._comment;
    }

    boolean isCommented() {
        return (this._comment != null && !("".equals(this._comment)));
    }

}
