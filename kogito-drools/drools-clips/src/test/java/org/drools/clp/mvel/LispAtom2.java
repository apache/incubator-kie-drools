/**
 * 
 */
package org.drools.clp.mvel;

public class LispAtom2 implements SExpression {
    private String value;

    public LispAtom2(String value) {
        super();
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }    
    
    public String toString() {
        return value;
    }
}