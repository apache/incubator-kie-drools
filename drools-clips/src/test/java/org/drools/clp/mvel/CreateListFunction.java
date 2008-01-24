package org.drools.clp.mvel;

public class CreateListFunction implements Function {
    private static final String name = "create$";

    public String getName() {
        return name;
    }
    
    public void dump(LispForm2 lispForm, Appendable appendable) {
        SExpression[] sExpressions = lispForm.getSExpressions();
        
        appendable.append("[");
        
        for ( int i = 1, length = sExpressions.length; i < length; i++) {
            
            FunctionHandlers.getInstance().dump( sExpressions[i], appendable );        
            
            if ( i != length -1 ) { 
                appendable.append( "," );
            }
        }
        appendable.append("]");
    }
}
