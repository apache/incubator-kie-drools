package org.drools.clp.mvel;

public class ModifyFunction implements Function {
    private static final String name = "modify";

    public String getName() {
        return name;
    }
    
    public void dump(LispForm2 lispForm, Appendable appendable) {
        SExpression[] sExpressions = lispForm.getSExpressions();
        
        appendable.append("modify (" + ( (LispAtom2) lispForm.getSExpressions()[1]).getValue() + ") {");
        
        for ( int i = 2, length = sExpressions.length; i < length; i++) {
            LispForm2 setter = (LispForm2) sExpressions[i];
            appendable.append( ( ( LispAtom2 ) setter.getSExpressions()[0]).getValue() );            
            
            appendable.append( " = " );
            
            FunctionHandlers.getInstance().dump( setter.getSExpressions()[1], appendable);  
            
            if ( i != length -1 ) { 
                appendable.append( "," );
            }
        }
        appendable.append("};");
    }
}
