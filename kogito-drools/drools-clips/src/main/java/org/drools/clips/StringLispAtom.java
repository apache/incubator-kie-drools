/**
 * 
 */
package org.drools.clips;

public class StringLispAtom extends LispAtom {
    
    public StringLispAtom(String value) {
        super("\"" + value + "\"");
    }
    
    
}