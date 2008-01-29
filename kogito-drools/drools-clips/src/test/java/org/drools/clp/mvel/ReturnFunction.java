package org.drools.clp.mvel;

public class ReturnFunction implements Function {
    private static final String name = "return";

    public String getName() {
        return name;
    }
    
    public void dump(LispForm lispForm, Appendable appendable, MVELBuildContext context) {
        appendable.append( "return " );
        FunctionHandlers.dump( lispForm.getSExpressions()[1], appendable, context );
        appendable.append( ";\n" );
    }
}
