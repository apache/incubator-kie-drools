package org.drools.template.model;

/**
 * Wrapper for declarative types. Declared Types must be written in the
 * appropriate style, no formatting is contributed here.
 */
public class DeclaredType
        implements
        DRLJavaEmitter {

    private String declaredTypeListing;

    public void setDeclaredTypeListing(final String declaredTypeListing) {
        this.declaredTypeListing = declaredTypeListing;
    }

    public void renderDRL(final DRLOutput out) {
        if (this.declaredTypeListing != null) {
            out.writeLine(this.declaredTypeListing);
        }
    }

}
