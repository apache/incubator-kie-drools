package org.drools.clips.functions;

import org.drools.clips.Appendable;
import org.drools.clips.Function;
import org.drools.clips.FunctionHandlers;
import org.drools.clips.LispAtom;
import org.drools.clips.LispForm;
import org.drools.clips.SExpression;

public class SetFunction
    implements
    Function {
    private static final String name = "set";

    public String getName() {
        return name;
    }

    public void dump(LispForm lispForm,
                     Appendable appendable) {
        SExpression[] sExpressions = lispForm.getSExpressions();

        String name = ((LispAtom) sExpressions[1]).getValue();

        String field = ((LispAtom) sExpressions[2]).getValue().trim();
        field = field.substring( 1, field.length() -1  );                

        appendable.append( name );
        appendable.append( "." );
        appendable.append( field );
        appendable.append( " = " );

        FunctionHandlers.dump( sExpressions[3],
                               appendable );

        appendable.append( ";\n" );
    }
}
