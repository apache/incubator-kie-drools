package org.drools.impact.analysis.model.left;

public class Constraint {

    protected Type type;

    protected String property;
    protected Object value;

    public enum Type {
        EQUAL,
        NOT_EQUAL,
        GREATER_THAN,
        GREATER_OR_EQUAL,
        LESS_THAN,
        LESS_OR_EQUAL,
        RANGE,
        UNKNOWN;
    }

    public Constraint() {}

    public Constraint(Type type, String property, Object value) {
        this.type = type;
        this.property = property;
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public void setType( Type type ) {
        this.type = type;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty( String property ) {
        this.property = property;
    }

    public Object getValue() {
        return value;
    }

    public void setValue( Object value ) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Constraint{" +
                "type=" + type +
                ", property='" + property + '\'' +
                ", value=" + value +
                '}';
    }
}
