/**
 * 
 */
package org.drools.clp.mvel;

public class StringLispAtom extends LispAtom {
    
    public StringLispAtom(String value) {
        super("\"" + value + "\"");
    }
    
    
}