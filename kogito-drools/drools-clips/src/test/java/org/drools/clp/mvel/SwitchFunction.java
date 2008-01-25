package org.drools.clp.mvel;

public class SwitchFunction implements Function {     
    private static String name = "switch";
    
    public String getName() {
        return name;
    }
    
    public void dump(LispForm2 lispForm, Appendable appendable) {
        SExpression[] sExpressions = lispForm.getSExpressions();

        appendable.append( "switchvar = " );
        LispForm2 expr = ( LispForm2 ) sExpressions[1];
        if ( expr.getSExpressions().length > 1 ) {
            FunctionHandlers.getInstance().dump( expr, appendable );
        } else {
            FunctionHandlers.getInstance().dump( expr.getSExpressions()[0], appendable );
        }
        appendable.append( ";\n" );
        
        LispForm2 caseForm = ( LispForm2 ) sExpressions[2];
        
        appendable.append( "if ( switchvar == " );            
        
        FunctionHandlers.getInstance().dump( caseForm.getSExpressions()[1], appendable );            
        appendable.append( ") {" );
        FunctionHandlers.getInstance().dump( caseForm.getSExpressions()[3], appendable );        
        appendable.append( "}" );
        
        for ( int i = 3, length = sExpressions.length-1; i < length; i++ ) {
            caseForm = ( LispForm2 ) sExpressions[i];
            
            appendable.append( " else if ( switchvar == " );            
            FunctionHandlers.getInstance().dump( caseForm.getSExpressions()[1], appendable );            
            appendable.append( ") {" );
            FunctionHandlers.getInstance().dump( caseForm.getSExpressions()[3], appendable );        
            appendable.append( "}" );
        }
        
        caseForm = ( LispForm2 ) sExpressions[ sExpressions.length-1 ];
        if ( "case".equals( ((LispAtom2)caseForm.getSExpressions()[0]).getValue() ) ) {
            appendable.append( " else if ( switchvar == " );            
            FunctionHandlers.getInstance().dump( caseForm.getSExpressions()[1], appendable );            
            appendable.append( ") {" );
            FunctionHandlers.getInstance().dump( caseForm.getSExpressions()[3], appendable );        
            appendable.append( "}" );            
        } else {
            appendable.append( " else { " ); 
            FunctionHandlers.getInstance().dump( caseForm.getSExpressions()[1], appendable );        
            appendable.append( "}" );            
        }
        
    }
}
