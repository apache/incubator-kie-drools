/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.validation.dtanalysis.verifier;

import org.kie.dmn.feel.lang.ast.UnaryTestNode;

public class OperatorResolver {

    public static String validatorStringOperatorFromUTOperator(final boolean isNegated,
                                                               final UnaryTestNode.UnaryOperator operator) {
        switch (operator) {
            case EQ:
                return eqOperator(isNegated);
            case GT:
                return gtOperator(isNegated);
            case GTE:
                return gteOperator(isNegated);
            case LT:
                return ltOperator(isNegated);
            case LTE:
                return lteOperator(isNegated);
            case NE:
                return neOperator(isNegated);
            case IN:
            case NOT:
            case TEST:
            default:
                return null;
        }
    }

    private static String lteOperator(final boolean isNegated) {
        if (isNegated) {
            return ">";
        } else {
            return "<=";
        }
    }

    private static String ltOperator(final boolean isNegated) {
        if (isNegated) {
            return ">=";
        } else {
            return "<";
        }
    }

    private static String gteOperator(final boolean isNegated) {
        if (isNegated) {
            return "<";
        } else {
            return ">=";
        }
    }

    private static String gtOperator(final boolean isNegated) {
        if (isNegated) {
            return "<=";
        } else {
            return ">";
        }
    }

    private static String neOperator(boolean isNegated) {
        if (isNegated) {
            return "==";
        } else {
            return "!=";
        }
    }

    private static String eqOperator(final boolean isNegated) {
        if (isNegated) {
            return "!=";
        } else {
            return "==";
        }
    }
}
