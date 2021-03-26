/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.lang.ast.visitor;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jsinterop.base.Js;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.FEELProfile;
import org.kie.dmn.feel.lang.ast.ASTNode;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.BooleanNode;
import org.kie.dmn.feel.lang.ast.FunctionInvocationNode;
import org.kie.dmn.feel.lang.ast.NameRefNode;
import org.kie.dmn.feel.lang.ast.NumberNode;
import org.kie.dmn.feel.lang.ast.SignedUnaryNode;
import org.kie.dmn.feel.lang.ast.StringNode;
import org.kie.dmn.feel.lang.impl.FEELImpl;
import org.kie.dmn.feel.util.EvalHelper;

import static org.kie.dmn.feel.runtime.functions.FEELConversionFunctionNames.DATE;
import static org.kie.dmn.feel.runtime.functions.FEELConversionFunctionNames.DATE_AND_TIME;
import static org.kie.dmn.feel.runtime.functions.FEELConversionFunctionNames.DURATION;
import static org.kie.dmn.feel.runtime.functions.FEELConversionFunctionNames.NUMBER;
import static org.kie.dmn.feel.runtime.functions.FEELConversionFunctionNames.STRING;
import static org.kie.dmn.feel.runtime.functions.FEELConversionFunctionNames.TIME;
import static org.kie.dmn.feel.runtime.functions.FEELConversionFunctionNames.YEARS_AND_MONTHS_DURATION;

public class DMNDTAnalyserValueFromNodeVisitor extends DefaultedVisitor<Comparable<?>> {

    private final FEELImpl FEEL;

    public DMNDTAnalyserValueFromNodeVisitor(final List<FEELProfile> feelProfiles) {
        FEEL = Js.uncheckedCast(org.kie.dmn.feel.FEEL.newInstance(feelProfiles));
    }

    @Override
    public Comparable<?> visit(final ASTNode n) {
        return super.visit(n);
    }

    @Override
    public Comparable<?> visit(final SignedUnaryNode n) {
        return super.visit(n);
    }

    @Override
    public Comparable<?> defaultVisit(final ASTNode n) {
        final List<FEELEventListener> listeners = Collections.emptyList();
        final Map<String, Object> inputVariables = Collections.emptyMap();
        final EvaluationContext ctx = FEEL.newEvaluationContext(listeners, inputVariables);
        final Object evaluate = n.evaluate(ctx);
        return (Comparable<?>) evaluate;
    }

    @Override
    public Comparable<?> visit(final FunctionInvocationNode n) {
        return defaultVisit(n);
    }

    @Override
    public Boolean visit(final BooleanNode n) {
        return n.getValue();
    }

    @Override
    public BigDecimal visit(final NumberNode n) {
        return n.getValue();
    }

    @Override
    public String visit(final StringNode n) {
        return EvalHelper.unescapeString(n.getText());
    }

    public static class DMNDTAnalyserOutputClauseVisitor extends DMNDTAnalyserValueFromNodeVisitor {

        public DMNDTAnalyserOutputClauseVisitor(final List<FEELProfile> feelProfiles) {
            super(feelProfiles);
        }

        @Override
        public Comparable<?> defaultVisit(final ASTNode n) {
            return n.getText();
        }
    }

    private static class SupportedConstantValueVisitor extends DefaultedVisitor<Boolean> {

        public boolean areAllSupported(final List<BaseNode> nodes) {
            return nodes.stream().allMatch(n -> n.accept(this));
        }

        @Override
        public Boolean defaultVisit(final ASTNode n) {
            return false;
        }

        @Override
        public Boolean visit(final BooleanNode n) {
            return true;
        }

        @Override
        public Boolean visit(final NumberNode n) {
            return true;
        }

        @Override
        public Boolean visit(final StringNode n) {
            return true;
        }

        @Override
        public Boolean visit(final SignedUnaryNode n) {
            final BaseNode signedExpr = n.getExpression();
            if (signedExpr instanceof NumberNode) {
                return true;
            } else {
                return defaultVisit(n);
            }
        }

        @Override
        public Boolean visit(final FunctionInvocationNode n) {
            final String fnName;
            if (n.getName() instanceof NameRefNode) {
                fnName = n.getName().getText();
            } else {
                throw new IllegalStateException("Name of function is not instance of NameRefNode: " + n.toString());
            }
            final List<BaseNode> params = n.getParams().getElements();
            switch (fnName) {
                case DATE:
                    if (params.size() == 1 || params.size() == 3) {
                        return areAllSupported(params);
                    }
                    break;
                case DATE_AND_TIME:
                    if (params.size() == 2 || params.size() == 1) {
                        return areAllSupported(params);
                    }
                    break;
                case TIME:
                    if (params.size() == 1 || params.size() == 4) {
                        return areAllSupported(params);
                    }
                    break;
                case NUMBER:
                    if (params.size() == 3) {
                        return areAllSupported(params);
                    }
                    break;
                case STRING:
                    if (params.size() == 1) {
                        return areAllSupported(params);
                    }
                    break;
                case DURATION:
                    if (params.size() == 1) {
                        return areAllSupported(params);
                    }
                case YEARS_AND_MONTHS_DURATION:
                    if (params.size() == 2) {
                        return areAllSupported(params);
                    }
                default:
                    return false;
            }
            return false;
        }
    }
}
