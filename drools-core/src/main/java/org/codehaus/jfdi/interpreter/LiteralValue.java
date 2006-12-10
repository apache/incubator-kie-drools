/**
 * 
 */
package org.codehaus.jfdi.interpreter;


public class LiteralValue
    implements
    ValueHandler {
    
    private static final long serialVersionUID = 320L;

    private final Object value;

    private int id;       

    public LiteralValue(final Object value) {
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }
    
    public void reset() {
        // N/A
    }    
    
    public String toString() {
        return "LiteralValue value=[" + this.value + "]";
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * this.value.hashCode();
        return result;
    }

    public boolean equals(Object object) {
        if ( this == object ) {
            return true;
        }
        if ( object == null || getClass() != object.getClass()) {
            return false;
        }
        final LiteralValue other = (LiteralValue) object;
        return this.value.equals( other.value );
    }

    public int getId() {

        return this.id;
    }

    public Class getType() {       
        return this.value.getClass();
    }

    public boolean isFinal() {
        return true;
    }

    public boolean isLiteral() {
        return true;
    }

    public boolean isLocal() {
        return true;
    }

    public void setValue(Object variable) {
        throw new UnsupportedOperationException("You can't treat a literal value as a variable.");        
    }
    
    

}