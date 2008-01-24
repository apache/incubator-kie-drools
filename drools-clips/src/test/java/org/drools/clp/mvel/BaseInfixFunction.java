package org.drools.clp.mvel;

public abstract class BaseInfixFunction implements Function {
    public abstract String getMappedSymbol();
    
    public void dump(LispForm2 lispForm, Appendable appendable) {
        SExpression[] sExpressions = lispForm.getSExpressions();        
        
        appendable.append("(");
        for ( int i = 1, length = sExpressions.length; i < length; i++) {
            
            FunctionHandlers.getInstance().dump( sExpressions[i], appendable );          
            
            if ( i != length -1 ) { 
                appendable.append( getMappedSymbol() );
            }
        }
        appendable.append(")");
    }
}
