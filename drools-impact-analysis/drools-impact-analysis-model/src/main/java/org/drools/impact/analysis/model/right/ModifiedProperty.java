package org.drools.impact.analysis.model.right;

public class ModifiedProperty {
    protected final String property;
    protected final Object value;

    public ModifiedProperty( String property ) {
        this(property, null);
    }

    public ModifiedProperty( String property, Object value ) {
        this.property = property;
        this.value = value;
    }

    public String getProperty() {
        return property;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ModifiedProperty{" +
                "property='" + property + '\'' +
                ", value=" + value +
                '}';
    }
}
