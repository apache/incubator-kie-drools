package org.drools.clp.mvel;

public class CreateListFunction implements Function {
    private static final String name = "create$";

    public String getName() {
        return name;
    }
    
    public void dump(LispForm lispForm, Appendable appendable, MVELBuildContext context) {
        SExpression[] sExpressions = lispForm.getSExpressions();
        
        appendable.append("[");
        
        for ( int i = 1, length = sExpressions.length; i < length; i++) {
            
            FunctionHandlers.dump( sExpressions[i], appendable, context );        
            
            if ( i != length -1 ) { 
                appendable.append( "," );
            }
        }
        appendable.append("]");
    }
}
