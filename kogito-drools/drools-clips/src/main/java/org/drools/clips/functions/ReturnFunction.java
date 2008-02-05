package org.drools.clips.functions;

import org.drools.clips.Appendable;
import org.drools.clips.Function;
import org.drools.clips.FunctionHandlers;
import org.drools.clips.LispForm;

public class ReturnFunction implements Function {
    private static final String name = "return";

    public String getName() {
        return name;
    }
    
    public void dump(LispForm lispForm, Appendable appendable) {
        appendable.append( "return " );
        FunctionHandlers.dump( lispForm.getSExpressions()[1], appendable );
        appendable.append( ";\n" );
    }
}
