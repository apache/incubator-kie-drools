package org.drools.clips.functions;

import org.drools.clips.Appendable;
import org.drools.clips.Function;
import org.drools.clips.FunctionHandlers;
import org.drools.clips.LispForm;
import org.drools.clips.SExpression;

public class CreateListFunction implements Function {
    private static final String name = "create$";

    public String getName() {
        return name;
    }
    
    public void dump(LispForm lispForm, Appendable appendable) {
        SExpression[] sExpressions = lispForm.getSExpressions();
        
        appendable.append("[");
        
        for ( int i = 1, length = sExpressions.length; i < length; i++) {
            
            FunctionHandlers.dump( sExpressions[i], appendable );        
            
            if ( i != length -1 ) { 
                appendable.append( "," );
            }
        }
        appendable.append("]");
    }
}
