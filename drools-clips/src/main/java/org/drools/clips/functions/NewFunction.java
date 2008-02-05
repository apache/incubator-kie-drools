package org.drools.clips.functions;

import org.drools.clips.Appendable;
import org.drools.clips.Function;
import org.drools.clips.FunctionHandlers;
import org.drools.clips.LispAtom;
import org.drools.clips.LispForm;
import org.drools.clips.SExpression;

public class NewFunction implements Function {
    private static final String name = "new";    

    public String getName() {
        return name;
    }
    
    public void dump(LispForm lispForm, Appendable appendable) {
        SExpression[] sExpressions = lispForm.getSExpressions();
        appendable.append( "new " );
        
        String name = ( ( LispAtom ) sExpressions[1] ).getValue().trim();
        name = name.substring( 1, name.length() -1  );                
        appendable.append( name );
        appendable.append( "(" );
        
        for ( int i = 2, length = sExpressions.length; i < length; i++) {            
            FunctionHandlers.dump( sExpressions[i], appendable );         
            
            if ( i != length -1 ) { 
                appendable.append( ", " );
            }
        }        
        appendable.append( ");" );
    }
}
