package org.drools.template.model;

/**
 * Wrapper for functions. Functions must be written in the appropriate style, no
 * formatting is contributed here.
 */
public class Functions
        implements
        DRLJavaEmitter {

    private String functionsListing;

    public void setFunctionsListing(final String functionsListing) {
        this.functionsListing = functionsListing;
    }

    public void renderDRL(final DRLOutput out) {
        if (this.functionsListing != null) {
            out.writeLine(this.functionsListing);
        }
    }

}
