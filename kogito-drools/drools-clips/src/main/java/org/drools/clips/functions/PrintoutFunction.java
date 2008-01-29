package org.drools.clips.functions;

import org.drools.clips.Appendable;
import org.drools.clips.Function;
import org.drools.clips.FunctionHandlers;
import org.drools.clips.LispAtom;
import org.drools.clips.LispForm;
import org.drools.clips.MVELBuildContext;
import org.drools.clips.SExpression;

public class PrintoutFunction implements Function {
    private static final String name = "printout";
    
    private static final int route = 1;

    public String getName() {
        return name;
    }
    
    public void dump(LispForm lispForm, Appendable appendable, MVELBuildContext context) {
        SExpression[] sExpressions = lispForm.getSExpressions();
        appendable.append( "printrouters.get(" + ( ( LispAtom ) lispForm.getSExpressions()[route]).getValue()+ ").print(" );
        //appendable.append( "routers.get(" + ( ( LispAtom2 ) lispForm.getSExpressions()[route]).getValue()+ ").print(" );
        for ( int i = 2, length = sExpressions.length; i < length; i++) {            
            FunctionHandlers.dump( sExpressions[i], appendable, context );         
            
            if ( i != length -1 ) { 
                appendable.append( "+" );
            }
        }        
        appendable.append( ");" );
    }
}
