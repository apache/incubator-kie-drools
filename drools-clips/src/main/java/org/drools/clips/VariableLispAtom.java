/**
 * 
 */
package org.drools.clips;

import java.util.Map;

public class VariableLispAtom extends LispAtom {
    
    public VariableLispAtom(String var, MVELBuildContext context) {
        super(var);
        Map<String, String> map = context.getVariableNameMap();
        String temp = map.get( var );
        if ( temp == null ) {
            temp = context.makeValid( var );
            if ( !temp.equals( var ) ) {
                map.put( var, temp );
            }
        } 
        setValue( temp );        
    }

    
}