package org.kie.dmn.feel.codegen.feel11;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.util.EvalHelper;

public class CompiledFEELUtils {
    
    /**
     * FEEL spec Table 38
     * Taken from {@link InfixOpNode} except evaluationcontext
     */
    public static Object and(Object left, Object right) {
        Boolean l = EvalHelper.getBooleanOrNull( left );
        Boolean r = EvalHelper.getBooleanOrNull( right );
        // have to check for all nulls first to avoid NPE
        if ( (l == null && r == null) || (l == null && r == true) || (r == null && l == true) ) {
            return null;
        } else if ( l == null || r == null ) {
            return false;
        }
        return l && r;
    }
    
    public static Object and(boolean left, Object right) {
        if ( left == true ) {
            return EvalHelper.getBooleanOrNull( right );
        } else {
            return false;
        }
    }
    
    public static Object and(boolean left, boolean right) {
        return left && right;
    }

    /**
     * FEEL spec Table 38
     * Taken from {@link InfixOpNode} except evaluationcontext
     */
    public static Object or(Object left, Object right) {
        Boolean l = EvalHelper.getBooleanOrNull( left );
        Boolean r = EvalHelper.getBooleanOrNull( right );
        // have to check for all nulls first to avoid NPE
        if ( (l == null && r == null) || (l == null && r == false) || (r == null && l == false) ) {
            return null;
        } else if ( l == null || r == null ) {
            return true;
        }
        return l || r;
    }
    
    public static Object or(Object left, boolean right) {
        if ( right == true ) {
            return true;
        } else {
            return EvalHelper.getBooleanOrNull( left );
        }
    }
    
    public static Object or(boolean left, boolean right) {
        return left || right;
    }
    
    public static Object conditionWasNotBoolean(EvaluationContext ctx) {
        return null;
    }
}
