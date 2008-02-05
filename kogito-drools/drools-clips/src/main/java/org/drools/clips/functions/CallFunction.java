package org.drools.clips.functions;

import org.drools.clips.Appendable;
import org.drools.clips.Function;
import org.drools.clips.FunctionHandlers;
import org.drools.clips.LispAtom;
import org.drools.clips.LispForm;
import org.drools.clips.SExpression;

public class CallFunction
    implements
    Function {
    private static final String name = "call";

    public String getName() {
        return name;
    }

    public void dump(LispForm lispForm,
                     Appendable appendable) {
        SExpression[] sExpressions = lispForm.getSExpressions();

        int offset = 0;
        if ( !"call".equals( ((LispAtom) sExpressions[0]).getValue().trim()) ) {
            offset = -1;
        }
        
        String name = ((LispAtom) sExpressions[offset+1]).getValue();

        String field = ((LispAtom) sExpressions[offset+2]).getValue().trim();
        field = field.substring( 1, field.length() -1  );                

        appendable.append( name );
        appendable.append( "." );
        appendable.append( field );
        
        appendable.append( "(" );
        for ( int i = offset+3, length = sExpressions.length; i < length; i++) {            
            FunctionHandlers.dump( sExpressions[i], appendable );         
            
            if ( i != length -1 ) { 
                appendable.append( ", " );
            }
        }        
        appendable.append( ");" );        
    }
}
