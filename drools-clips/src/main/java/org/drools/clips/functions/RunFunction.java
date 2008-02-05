package org.drools.clips.functions;

import org.drools.clips.Appendable;
import org.drools.clips.Function;
import org.drools.clips.FunctionHandlers;
import org.drools.clips.LispAtom;
import org.drools.clips.LispForm;

public class RunFunction implements Function {
    private static final String name = "run";

    public String getName() {
        return name;
    }
    
    public void dump(LispForm lispForm, Appendable appendable) {
        if ( lispForm.getSExpressions().length == 3 ) {
            appendable.append( "run(" + ((LispAtom)lispForm.getSExpressions()[2]).getValue() + ");\n " );
        } else {
            appendable.append( "run();\n " );
        }
    }
}
