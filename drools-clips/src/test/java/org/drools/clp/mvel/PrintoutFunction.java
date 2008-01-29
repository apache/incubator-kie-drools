package org.drools.clp.mvel;

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
