package org.drools.impact.analysis.model.right;

public class InsertedProperty extends ModifiedProperty {

    public InsertedProperty( String property ) {
        this(property, null);
    }

    public InsertedProperty( String property, Object value ) {
        super(property, value);
    }

    @Override
    public String toString() {
        return "InsertedProperty{" +
                "property='" + property + '\'' +
                ", value=" + value +
                '}';
    }
}
