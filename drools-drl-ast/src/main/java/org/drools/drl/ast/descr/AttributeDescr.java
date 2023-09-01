package org.drools.drl.ast.descr;

public class AttributeDescr extends BaseDescr {
    public static enum Type {
        STRING, NUMBER, DATE, BOOLEAN, LIST, EXPRESSION
    }

    private static final long serialVersionUID = 510l;
    private String            name;
    private String            value;
    private Type              type;
    
    // default constructor for serialization
    public AttributeDescr() {}

    public AttributeDescr(final String name) {
        this(name,
             null, 
             Type.EXPRESSION );
    }

    public AttributeDescr(final String name,
                          final String value) {
        this( name,
              value,
              Type.EXPRESSION );
    }

    public AttributeDescr(final String name,
                          final String value,
                          final Type type ) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue( final String value ) {
        this.value = value;
    }

    public void setType( Type type ) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }
    
    public String getValueString() {
        if( type == Type.STRING || type == Type.DATE || name.equals("dialect") ) {
            // needs escaping
            return "\""+this.value+"\"";
        }

        if(this.name.equals("timer") || this.name.equals("duration")) {
            return "("+this.value+")";
        }

        return this.value;
    }
}
