package org.drools.clp.mvel;

public class PrintoutFunction implements Function {
    private static final String name = "printout";
    
    private static final int route = 1;

    public String getName() {
        return name;
    }
    
    public void dump(LispForm2 lispForm, Appendable appendable) {
        SExpression[] sExpressions = lispForm.getSExpressions();
        appendable.append( "((PrintStream) routers.get(" + ( ( LispAtom2 ) lispForm.getSExpressions()[route]).getValue()+ ")).print(" );
        for ( int i = 2, length = sExpressions.length; i < length; i++) {            
            FunctionHandlers.getInstance().dump( sExpressions[i], appendable );         
            
            if ( i != length -1 ) { 
                appendable.append( "+" );
            }
        }        
        appendable.append( ");" );
    }
}
