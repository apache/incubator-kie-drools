package org.drools.clp.mvel;

public class IfFunction implements Function {
    private static final String name = "if";       

    public String getName() {
        return name;
    }
    
    public void dump(LispForm2 lispForm, Appendable appendable) {
        SExpression[] sExpressions = lispForm.getSExpressions();

        appendable.append( "if " );
        
        FunctionHandlers.getInstance().dump( sExpressions[1], appendable );
        
        appendable.append( "{" );
        FunctionHandlers.getInstance().dump( sExpressions[3], appendable );
        appendable.append( "}" );
        
        if ( sExpressions.length > 4 ) {
            appendable.append( "else {" );
                FunctionHandlers.getInstance().dump( sExpressions[5], appendable );
            appendable.append( "}" );            
        }
        
    }
}
