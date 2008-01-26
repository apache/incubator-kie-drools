package org.drools.clp.mvel;

public class SwitchFunction implements Function {     
    private static String name = "switch";
    
    public String getName() {
        return name;
    }
    
    public void dump(LispForm lispForm, Appendable appendable, MVELClipsContext context) {
        SExpression[] sExpressions = lispForm.getSExpressions();

        appendable.append( "switchvar = " );
        LispForm expr = ( LispForm ) sExpressions[1];
        if ( expr.getSExpressions().length > 1 ) {
            FunctionHandlers.dump( expr, appendable, context );
        } else {
            FunctionHandlers.dump( expr.getSExpressions()[0], appendable, context );
        }
        appendable.append( ";\n" );
        
        LispForm caseForm = ( LispForm ) sExpressions[2];
        
        appendable.append( "if ( switchvar == " );            
        
        FunctionHandlers.dump( caseForm.getSExpressions()[1], appendable, context );            
        appendable.append( ") {" );
        for ( int j = 3, jlength = caseForm.getSExpressions().length; j < jlength; j++ ) {
            FunctionHandlers.dump( caseForm.getSExpressions()[j], appendable, context );
        }        
        appendable.append( "}" );
        
        for ( int i = 3, length = sExpressions.length-1; i < length; i++ ) {
            caseForm = ( LispForm ) sExpressions[i];
            
            appendable.append( " else if ( switchvar == " );            
            FunctionHandlers.dump( caseForm.getSExpressions()[1], appendable, context );            
            appendable.append( ") {" );
            
            for ( int j = 3, jlength = caseForm.getSExpressions().length; j < jlength; j++ ) {
                FunctionHandlers.dump( caseForm.getSExpressions()[j], appendable, context );
            }
            appendable.append( "}" );
        }
        
        caseForm = ( LispForm ) sExpressions[ sExpressions.length-1 ];
        if ( "case".equals( ((LispAtom)caseForm.getSExpressions()[0]).getValue() ) ) {
            appendable.append( " else if ( switchvar == " );            
            FunctionHandlers.dump( caseForm.getSExpressions()[1], appendable, context );            
            appendable.append( ") {" );
            for ( int j = 3, length = caseForm.getSExpressions().length; j < length; j++ ) {
                FunctionHandlers.dump( caseForm.getSExpressions()[j], appendable, context );
            }        
            appendable.append( "}" );            
        } else {
            appendable.append( " else { " ); 
            for ( int j = 1, length = caseForm.getSExpressions().length; j < length; j++ ) {
                FunctionHandlers.dump( caseForm.getSExpressions()[j], appendable, context );
            }        
            appendable.append( "}" );            
        }        
    }
    
}
