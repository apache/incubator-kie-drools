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
    
    public void dump(SExpression sExpression, Appendable appendable) {              
        if ( sExpression instanceof LispAtom2 ) {
            appendable.append( ( ( LispAtom2 ) sExpression).getValue() );
        } else {
            LispForm2 form = (LispForm2) sExpression;
            Function function = FunctionHandlers.getInstance().getFunction( ( (LispAtom2) form.getSExpressions()[0]).getValue() );
            function.dump(form, appendable );
            
        }           
    }
    
    
}
