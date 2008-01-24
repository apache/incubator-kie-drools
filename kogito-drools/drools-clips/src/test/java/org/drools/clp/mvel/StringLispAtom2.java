/**
 * 
 */
package org.drools.clp.mvel;

public class StringLispAtom2 extends LispAtom2 {
    
    public StringLispAtom2(String value) {
        super("\"" + value + "\"");
    }
    
    
}