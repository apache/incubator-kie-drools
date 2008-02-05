package org.drools.clips.functions;

import org.drools.clips.Appendable;
import org.drools.clips.Function;
import org.drools.clips.FunctionHandlers;
import org.drools.clips.LispAtom;
import org.drools.clips.LispForm;
import org.drools.clips.SExpression;

public class BindFunction implements Function {
    private static final String name = "bind";

    public String getName() {
        return name;
    }
    
    public void dump(LispForm lispForm, Appendable appendable) {
        SExpression[] sExpressions = lispForm.getSExpressions();
        
        appendable.append(( (LispAtom) lispForm.getSExpressions()[1]).getValue() + " = " );
        FunctionHandlers.dump( lispForm.getSExpressions()[2], appendable);
        appendable.append(";\n");
    }
}
