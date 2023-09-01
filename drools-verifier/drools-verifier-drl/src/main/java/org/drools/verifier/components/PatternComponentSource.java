package org.drools.verifier.components;

public abstract class PatternComponentSource extends PatternComponent
    implements
    Source {

    private VerifierComponentType parentType;
    private String                parentPath;
    private int                   orderNumber = 0;

    public PatternComponentSource(Pattern pattern) {
        super( pattern );
    }

    public VerifierComponentType getParentType() {
        return parentType;
    }

    public String getParentPath() {
        return parentPath;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setParentType(VerifierComponentType parentType) {
        this.parentType = parentType;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }
}
