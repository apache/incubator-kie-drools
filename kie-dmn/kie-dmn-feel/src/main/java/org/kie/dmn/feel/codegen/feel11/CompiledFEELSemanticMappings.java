/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.codegen.feel11;

import java.math.BigDecimal;

import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.runtime.UnaryTest;
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

    // to ground to null if right = 0
    public static Object div(Object left, BigDecimal right) {
        return right.signum() == 0 ? null : InfixOpNode.div(left, right, null);
    }

    /**
     * FEEL spec Table 42 and derivations
     * Delegates to {@link EvalHelper} except evaluationcontext
     */
    public static Boolean lte(Object left, Object right) {
        return EvalHelper.compare(left, right, null, (l, r) -> l.compareTo(r) <= 0);
    }

    /**
     * FEEL spec Table 42 and derivations
     * Delegates to {@link EvalHelper} except evaluationcontext
     */
    public static Boolean lt(Object left, Object right) {
        return EvalHelper.compare(left, right, null, (l, r) -> l.compareTo(r) < 0);
    }

    /**
     * FEEL spec Table 42 and derivations
     * Delegates to {@link EvalHelper} except evaluationcontext
     */
    public static Boolean gte(Object left, Object right) {
        return EvalHelper.compare(left, right, null, (l, r) -> l.compareTo(r) >= 0);
    }

    /**
     * FEEL spec Table 42 and derivations
     * Delegates to {@link EvalHelper} except evaluationcontext
     */
    public static Boolean gt(Object left, Object right) {
        return EvalHelper.compare(left, right, null, (l, r) -> l.compareTo(r) > 0);
    }

    /**
     * FEEL spec Table 41: Specific semantics of equality
     * Delegates to {@link EvalHelper} except evaluationcontext
     */
    public static Boolean eq(Object left, Object right) {
        return EvalHelper.isEqual(left, right, null);
    }

    public static Boolean between(Object val, Object s, Object e) {
        if (!val.getClass().isAssignableFrom(s.getClass())) {
            // TODO here in the original code "interpreted" there was an error reporting utility.
            return null;
        }

        if (!val.getClass().isAssignableFrom(e.getClass())) {
            // TODO here in the original code "interpreted" there was an error reporting utility.
            return null;
        }

        return gte(val, s) && lte(val, e);
    }

    /**
     * FEEL spec Table 39
     */
    public static Boolean ne(Object left, Object right) {
        return not(EvalHelper.isEqual(left, right, null));
    }

    public static Boolean not(Object arg, UnaryTest test) {
        return not(test.apply(null, arg));
    }

    private static Boolean not(Object arg) {
        if (Boolean.TRUE.equals(arg)) {
            return Boolean.FALSE;
        }
        if (Boolean.FALSE.equals(arg)) {
            return Boolean.TRUE;
        }
        return null;
    }
}
