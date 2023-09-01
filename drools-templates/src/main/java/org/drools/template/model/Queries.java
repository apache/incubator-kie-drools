package org.drools.template.model;

/**
 * Wrapper for queries. Queries must be written in the appropriate style, no
 * formatting is contributed here.
 */
public class Queries
        implements
        DRLJavaEmitter {

    private String queriesListing;

    public void setQueriesListing(final String queriesListing) {
        this.queriesListing = queriesListing;
    }

    public void renderDRL(final DRLOutput out) {
        if (this.queriesListing != null) {
            out.writeLine(this.queriesListing);
        }
    }

}
