package org.drools.clp.mvel;

public class PrognFunction implements Function {
    private static final String name = "progn";       

    public String getName() {
        return name;
    }
    
    public void dump(LispForm2 lispForm, Appendable appendable) {
        Function createList = FunctionHandlers.getInstance().getFunction( "create$" );
        
        SExpression[] sExpressions = lispForm.getSExpressions();
        
        LispForm2 listSpec = (LispForm2) sExpressions[1];
        
        String var = ( ( LispAtom2 ) listSpec.getSExpressions()[0] ).getValue();        
        
        appendable.append( "foreach( " + var + " : " );        
        
        createList.dump( (LispForm2) listSpec.getSExpressions()[1], appendable );
        
        appendable.append( " ) {" );
        
        FunctionHandlers.getInstance().dump( sExpressions[2], appendable );
        //Function function = FunctionHandlers.getInstance().getFunction( ( (LispAtom2) form.getSExpressions()[0]).getValue() );
        
        appendable.append( "}" );
        
//        SExpression[] sExpressions = lispForm.getSExpressions();
//        appendable.append( "((PrintStream) routers.get(" + ( ( LispAtom2 ) lispForm.getSExpressions()[1]).getValue()+ ")).print(" );
//        for ( int i = 2, length = sExpressions.length; i < length; i++) {            
//            SExpression sExpression = sExpressions[i];            
//            if ( sExpression instanceof LispAtom2 ) {
//                appendable.append( ( ( LispAtom2 ) sExpression).getValue() );
//            } else {
//                LispForm2 form = (LispForm2) sExpression;
//                Function function = FunctionHandlers.getInstance().getFunction( ( (LispAtom2) form.getSExpressions()[0]).getValue() );
//                function.dump(form, appendable);
//                
//            }            
//            
//            if ( i != length -1 ) { 
//                appendable.append( "+" );
//            }
//        }        
//        appendable.append( ");" );        
        
    }
}
