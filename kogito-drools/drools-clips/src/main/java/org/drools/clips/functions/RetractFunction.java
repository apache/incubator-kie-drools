package org.drools.clips.functions;

import org.drools.clips.Appendable;
import org.drools.clips.Function;
import org.drools.clips.FunctionHandlers;
import org.drools.clips.LispAtom;
import org.drools.clips.LispForm;
import org.drools.clips.SExpression;

public class RetractFunction implements Function {
    private static final String name = "retract";

    public String getName() {
        return name;
    }
    
    public void dump(LispForm lispForm, Appendable appendable) {
        LispForm form = ( LispForm) lispForm.getSExpressions()[1];
        
        String var = ((LispAtom) form.getSExpressions()[0]).getValue();
        
        appendable.append("retract( " + var + " );\n");
    }
}
