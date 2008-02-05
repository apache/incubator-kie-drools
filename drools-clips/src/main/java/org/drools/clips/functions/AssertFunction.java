package org.drools.clips.functions;

import org.drools.clips.Appendable;
import org.drools.clips.Function;
import org.drools.clips.FunctionHandlers;
import org.drools.clips.LispAtom;
import org.drools.clips.LispForm;
import org.drools.clips.SExpression;

public class AssertFunction implements Function {
    private static final String name = "assert";

    public String getName() {
        return name;
    }
    
    public void dump(LispForm lispForm, Appendable appendable) {
        LispForm form = ( LispForm) lispForm.getSExpressions()[1];
        
        String type = ((LispAtom) form.getSExpressions()[0]).getValue();
        
        appendable.append("insert( with ( new " + type + "() ) {");        
        
        for ( int i = 1, length = form.getSExpressions().length; i < length; i++) {
            LispForm slot =  ( LispForm) form.getSExpressions()[i];
                                                                                
            appendable.append( ( ( LispAtom ) slot.getSExpressions()[0]).getValue() );            
            
            appendable.append( " = " );
            
            FunctionHandlers.dump( slot.getSExpressions()[1], appendable);  
            
            if ( i != length -1 ) { 
                appendable.append( "," );
            }
        }
        appendable.append("} );\n");
    }
}
