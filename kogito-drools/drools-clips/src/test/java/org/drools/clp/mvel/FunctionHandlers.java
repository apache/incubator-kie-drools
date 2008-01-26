package org.drools.clp.mvel;

import java.util.HashMap;
import java.util.Map;

public class FunctionHandlers {
    public static final FunctionHandlers INSTANCE = new FunctionHandlers();
    
    public static FunctionHandlers getInstance() {
        return INSTANCE;
    }
    
    private Map<String, Function> map = new HashMap<String, Function>();
    
    private FunctionHandlers() {
        
    }
    
    public Function getFunction(String name) {
        return this.map.get( name );
    }
    
    public void registerFunction(Function function) {
        this.map.put( function.getName(), function );
    }
    
    public static void dump(SExpression sExpression, Appendable appendable, MVELClipsContext context) {              
        if ( sExpression instanceof LispAtom ) {
            appendable.append( ( ( LispAtom ) sExpression).getValue() );
        } else {
            LispForm form = (LispForm) sExpression;
            String functionName =  ( (LispAtom) form.getSExpressions()[0]).getValue();
            Function function = FunctionHandlers.getInstance().getFunction( functionName );
            if ( function != null ) {
                function.dump(form, appendable, context );                
            } else {
                // execute as user function
                appendable.append( functionName + "(" );
                for ( int i = 1, length = form.getSExpressions().length; i < length; i++ ) {
                    dump( form.getSExpressions()[i], appendable, context );
                    if ( i < length -1 ) {
                        appendable.append( ", " );
                    }
                }
                appendable.append( ")" );                
            }
        }           
    }
    
    
}
