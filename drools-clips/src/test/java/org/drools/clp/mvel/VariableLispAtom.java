/**
 * 
 */
package org.drools.clp.mvel;

import java.util.HashMap;
import java.util.Map;

public class VariableLispAtom extends LispAtom {
    private static Map<String, String> mapping = new HashMap<String, String>();
    
    public VariableLispAtom(String var) {
        super(var);
        String temp = mapping.get( var );
        if ( temp == null ) {
            temp = makeValid( var );
            if ( !temp.equals( var ) ) {
                mapping.put( var, temp );
            }
        } 
        setValue( temp );        
    }
    
    private static String makeValid(String var) {
        var = var.replaceAll( "\\?", "_Q_" );
        return var;
    }
    
}