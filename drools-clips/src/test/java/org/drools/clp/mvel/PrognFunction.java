package org.drools.clp.mvel;

public class PrognFunction implements Function {
    private static final String name = "progn";       

    public String getName() {
        return name;
    }
    
    public void dump(LispForm lispForm, Appendable appendable, MVELBuildContext context) {
        Function createList = FunctionHandlers.getInstance().getFunction( "create$" );
        
        SExpression[] sExpressions = lispForm.getSExpressions();
        
        LispForm listSpec = (LispForm) sExpressions[1];
        
        String var = ( ( LispAtom ) listSpec.getSExpressions()[0] ).getValue();        
        
        appendable.append( "foreach( " + var + " : " );        
        
        createList.dump( (LispForm) listSpec.getSExpressions()[1], appendable, context );
        
        appendable.append( " ) {" );
        
        for ( int i = 2, length = sExpressions.length; i < length; i++ ) {
            FunctionHandlers.dump( sExpressions[i], appendable, context );
        }          
        
        appendable.append( "}" );
                        
    }
}
