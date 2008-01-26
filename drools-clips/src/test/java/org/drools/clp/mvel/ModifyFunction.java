package org.drools.clp.mvel;

public class ModifyFunction implements Function {
    private static final String name = "modify";

    public String getName() {
        return name;
    }
    
    public void dump(LispForm lispForm, Appendable appendable, MVELClipsContext context) {
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
