package org.drools.verifier.components;

public class FieldVariable extends PatternComponent implements Variable {

    private String parentPath;
    private int orderNumber;
    private String name;

    public FieldVariable(Pattern pattern) {
        super(pattern);
    }

    public String getName() {
        return name;
    }

    public VerifierComponentType getParentType() {
        return VerifierComponentType.FIELD;
    }


    public String getParentPath() {
        return parentPath;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.FIELD_LEVEL_VARIABLE;
    }
}
