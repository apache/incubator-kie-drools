package org.kie.dmn.feel.codegen.feel11;

import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.util.EvalHelper;

/**
 * The purpose of this class is to offer import .* methods to compiled FEEL classes compiling expressions.
 * Implementing DMN FEEL spec chapter 10.3.2.12 Semantic mappings
 */
public class CompiledFEELSemanticMappings {
    
    /**
     * FEEL spec Table 38
     * Delegates to {@link InfixOpNode} except evaluationcontext
     */
    public static Object and(Object left, Object right) {
        return InfixOpNode.and(left, right, null);
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
     * Delegates to {@link InfixOpNode} except evaluationcontext
     */
    public static Object or(Object left, Object right) {
        return InfixOpNode.or(left, right, null);
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

    /**
     * FEEL spec Table 45
     * Delegates to {@link InfixOpNode} except evaluationcontext
     */
    public static Object add(Object left, Object right) {
        return InfixOpNode.add(left, right, null);
    }

    /**
     * FEEL spec Table 45
     * Delegates to {@link InfixOpNode} except evaluationcontext
     */
    public static Object sub(Object left, Object right) {
        return InfixOpNode.sub(left, right, null);
    }

    /**
     * FEEL spec Table 45
     * Delegates to {@link InfixOpNode} except evaluationcontext
     */
    public static Object mult(Object left, Object right) {
        return InfixOpNode.mult(left, right, null);
    }

    /**
     * FEEL spec Table 45
     * Delegates to {@link InfixOpNode} except evaluationcontext
     */
    public static Object div(Object left, Object right) {
        return InfixOpNode.div(left, right, null);
    }

}
