package org.drools.clips.functions;

import org.drools.clips.Appendable;
import org.drools.clips.Function;
import org.drools.clips.FunctionHandlers;
import org.drools.clips.LispAtom;
import org.drools.clips.LispForm;
import org.drools.clips.SExpression;

public class PrognFunction implements Function {
    private static final String name = "progn";       

    public String getName() {
        return name;
    }
    
    public void dump(LispForm lispForm, Appendable appendable) {
        Function createList = FunctionHandlers.getInstance().getFunction( "create$" );
        
        SExpression[] sExpressions = lispForm.getSExpressions();
        
        LispForm listSpec = (LispForm) sExpressions[1];
        
        String var = ( ( LispAtom ) listSpec.getSExpressions()[0] ).getValue();        
        
        appendable.append( "foreach( " + var + " : " );        
        
        createList.dump( (LispForm) listSpec.getSExpressions()[1], appendable );
        
        appendable.append( " ) {" );
        
        for ( int i = 2, length = sExpressions.length; i < length; i++ ) {
            FunctionHandlers.dump( sExpressions[i], appendable );
        }          
        
        appendable.append( "}" );
                        
    }
}
