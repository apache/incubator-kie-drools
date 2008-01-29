package org.drools.clips.functions;

import org.drools.clips.Appendable;
import org.drools.clips.Function;
import org.drools.clips.FunctionHandlers;
import org.drools.clips.LispAtom;
import org.drools.clips.LispForm;
import org.drools.clips.MVELBuildContext;
import org.drools.clips.SExpression;

public class ModifyFunction implements Function {
    private static final String name = "modify";

    public String getName() {
        return name;
    }
    
    public void dump(LispForm lispForm, Appendable appendable, MVELBuildContext context) {
        SExpression[] sExpressions = lispForm.getSExpressions();
        
        appendable.append("modify (" + ( (LispAtom) lispForm.getSExpressions()[1]).getValue() + ") {");
        
        for ( int i = 2, length = sExpressions.length; i < length; i++) {
            LispForm setter = (LispForm) sExpressions[i];
            appendable.append( ( ( LispAtom ) setter.getSExpressions()[0]).getValue() );            
            
            appendable.append( " = " );
            
            FunctionHandlers.dump( setter.getSExpressions()[1], appendable, context);  
            
            if ( i != length -1 ) { 
                appendable.append( "," );
            }
        }
        appendable.append("};");
    }
}
