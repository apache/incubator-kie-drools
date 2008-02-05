/**
 * 
 */
package org.drools.clips;


public class VariableLispAtom extends LispAtom {
    
    public VariableLispAtom(String var) {
        super( var.replace( '?', '$' ) );
        //super( ( var.startsWith( "?" ) ) ? var.substring( 1 ) : var ); // strip leading ?   
    }

    
}