package org.drools.clips.functions;

import org.drools.clips.Appendable;
import org.drools.clips.Function;
import org.drools.clips.FunctionHandlers;
import org.drools.clips.LispAtom;
import org.drools.clips.LispForm;
import org.drools.clips.SExpression;

public class IfFunction implements Function {
    private static final String name = "if";       

    public String getName() {
        return name;
    }
    
    public void dump(LispForm lispForm, Appendable appendable) {
        SExpression[] sExpressions = lispForm.getSExpressions();

        appendable.append( "if " );
        
        FunctionHandlers.dump( sExpressions[1], appendable );
        
        appendable.append( "{" );
        int i = 3;
        for ( int length = sExpressions.length; i < length; i++ ) {
            SExpression sExpr = ( SExpression ) sExpressions[i];
            if ( ( sExpr instanceof LispAtom ) && "\"else\"".equals( ((LispAtom)sExpr).getValue() ) ) {
                i++;
                break;
            }
            FunctionHandlers.dump( sExpressions[i], appendable );
        }  
        appendable.append( "}" );
        
        
        while ( i < sExpressions.length ) {        
            appendable.append( " else {" );
            for ( int length = sExpressions.length; i < length; i++ ) {
                SExpression sExpr = ( SExpression ) sExpressions[i];
                if ( ( sExpr instanceof LispAtom ) && "\"else\"".equals( ((LispAtom)sExpr).getValue() ) ) {
                    i++;
                    break;
                }
                FunctionHandlers.dump( sExpressions[i], appendable );
            }        
            appendable.append( "}" );  
        }             
    }
}
