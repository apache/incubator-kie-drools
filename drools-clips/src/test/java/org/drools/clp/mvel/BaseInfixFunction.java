package org.drools.clp.mvel;

public abstract class BaseInfixFunction implements Function {
    public abstract String getMappedSymbol();
    
    public void dump(LispForm lispForm, Appendable appendable, MVELClipsContext context) {
        SExpression[] sExpressions = lispForm.getSExpressions();        
        
        appendable.append("(");
        for ( int i = 1, length = sExpressions.length; i < length; i++) {
            
            FunctionHandlers.dump( sExpressions[i], appendable, context );          
            
            if ( i != length -1 ) { 
                appendable.append( getMappedSymbol() );
            }
        }
        appendable.append(")");
    }
}
