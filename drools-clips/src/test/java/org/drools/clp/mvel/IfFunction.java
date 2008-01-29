package org.drools.clp.mvel;

public class IfFunction implements Function {
    private static final String name = "if";       

    public String getName() {
        return name;
    }
    
    public void dump(LispForm lispForm, Appendable appendable, MVELBuildContext context) {
        SExpression[] sExpressions = lispForm.getSExpressions();

        appendable.append( "if " );
        
        FunctionHandlers.dump( sExpressions[1], appendable, context );
        
        appendable.append( "{" );
        int i = 3;
        for ( int length = sExpressions.length; i < length; i++ ) {
            SExpression sExpr = ( SExpression ) sExpressions[i];
            if ( ( sExpr instanceof LispAtom ) && "\"else\"".equals( ((LispAtom)sExpr).getValue() ) ) {
                i++;
                break;
            }
            FunctionHandlers.dump( sExpressions[i], appendable, context );
        }  
        appendable.append( "}" );
        
        
        while ( i < sExpressions.length ) {        
            appendable.append( " else {" );
            for ( int length = sExpressions.length; i < length; i++ ) {
                SExpression sExpr = ( SExpression ) sExpressions[i];
                if ( ( sExpr instanceof LispAtom ) && "\"else\"".equals( ((LispAtom)sExpr).getValue() ) ) {
                    i++;
                    break;
                }
                FunctionHandlers.dump( sExpressions[i], appendable, context );
            }        
            appendable.append( "}" );  
        }             
    }
}
