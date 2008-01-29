/**
 * 
 */
package org.drools.clips;

public class LispAtom implements SExpression {
    private String value;

    public LispAtom(String value) {
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