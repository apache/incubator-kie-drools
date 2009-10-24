package org.drools.verifier.components;

public class EnumField extends Field {
    private static final long serialVersionUID = 7617431515074762479L;

    @Override
    public String toString() {
        return "Enum: " + objectTypeName + "." + name;
    }
}
