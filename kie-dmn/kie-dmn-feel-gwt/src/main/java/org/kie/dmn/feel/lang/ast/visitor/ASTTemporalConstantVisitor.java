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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Symbol;
import org.kie.dmn.feel.lang.ast.ASTNode;
import org.kie.dmn.feel.lang.ast.ContextEntryNode;
import org.kie.dmn.feel.lang.ast.ContextNode;
import org.kie.dmn.feel.lang.ast.ForExpressionNode;
import org.kie.dmn.feel.lang.ast.FormalParameterNode;
import org.kie.dmn.feel.lang.ast.FunctionDefNode;
import org.kie.dmn.feel.lang.ast.FunctionInvocationNode;
import org.kie.dmn.feel.lang.ast.IterationContextNode;
import org.kie.dmn.feel.lang.ast.NameRefNode;
import org.kie.dmn.feel.lang.ast.QualifiedNameNode;
import org.kie.dmn.feel.lang.ast.QuantifiedExpressionNode;
import org.kie.dmn.feel.lang.ast.TemporalConstantNode;
import org.kie.dmn.feel.parser.feel11.ScopeHelper;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.functions.BuiltInFunctions;
import org.kie.dmn.feel.runtime.functions.DateAndTimeFunction;
import org.kie.dmn.feel.runtime.functions.DateFunction;
import org.kie.dmn.feel.runtime.functions.DurationFunction;
import org.kie.dmn.feel.runtime.functions.TimeFunction;
import org.kie.dmn.feel.util.EvalHelper;

import static org.kie.dmn.feel.runtime.functions.FEELConversionFunctionNames.DATE;
import static org.kie.dmn.feel.runtime.functions.FEELConversionFunctionNames.DATE_AND_TIME;
import static org.kie.dmn.feel.runtime.functions.FEELConversionFunctionNames.DURATION;
import static org.kie.dmn.feel.runtime.functions.FEELConversionFunctionNames.TIME;

public class ASTTemporalConstantVisitor extends DefaultedVisitor<ASTNode> {

    private final ScopeHelper<FEELFunction> scopeHelper = new ScopeHelper<>();

    private static final FEELFunction MASKED = new DUMMY();

    public static final List<FEELFunction> TEMPORAL_FNS = Arrays.asList(DateFunction.INSTANCE,
                                                                        TimeFunction.INSTANCE,
                                                                        DateAndTimeFunction.INSTANCE,
                                                                        DurationFunction.INSTANCE);
    public static final Set<String> TEMPORAL_FNS_NAMES = TEMPORAL_FNS.stream().map(FEELFunction::getName).collect(Collectors.toSet());

    public ASTTemporalConstantVisitor(final CompilerContext ctx) {

        Stream.of(BuiltInFunctions.getFunctions()).forEach(f -> scopeHelper.addInScope(f.getName(), f));

        for (final FEELFunction f : ctx.getFEELFunctions()) {
            scopeHelper.addInScope(f.getName(), f);
        }

        ctx.getInputVariables().keySet().forEach(this::processNameInScope);
        ctx.getInputVariableTypes().keySet().forEach(this::processNameInScope);
    }

    private void processNameInScope(final String n) {
        if (TEMPORAL_FNS_NAMES.contains(n)) {
            scopeHelper.addInScope(n, MASKED);
        }
    }

    @Override
    public ASTNode defaultVisit(final ASTNode n) {
        for (final ASTNode children : n.getChildrenNode()) {
            if (children != null) {
                children.accept(this);
            }
        }
        return n;
    }

    @Override
    public ASTNode visit(final ASTNode n) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ASTNode visit(final ForExpressionNode n) {
        scopeHelper.pushScope();

        for (final IterationContextNode ic : n.getIterationContexts()) {
            ic.accept(this);
            scopeHelper.addInScope(EvalHelper.normalizeVariableName(ic.getName().getText()), MASKED);
        }

        n.getExpression().accept(this);
        scopeHelper.popScope();
        return n;
    }

    @Override
    public ASTNode visit(final ContextNode n) {
        scopeHelper.pushScope();
        for (final ContextEntryNode ce : n.getEntries()) {
            ce.accept(this);
            scopeHelper.addInScope(ce.getName().getText(), MASKED);
        }
        scopeHelper.popScope();
        return n;
    }

    @Override
    public ASTNode visit(final QuantifiedExpressionNode n) {
        scopeHelper.pushScope();
        for (final IterationContextNode ic : n.getIterationContexts()) {
            ic.accept(this);
            scopeHelper.addInScope(EvalHelper.normalizeVariableName(ic.getName().getText()), MASKED);
        }
        n.getExpression().accept(this);
        scopeHelper.popScope();
        return n;
    }

    @Override
    public ASTNode visit(final FunctionDefNode n) {
        scopeHelper.pushScope();
        for (final FormalParameterNode fp : n.getFormalParameters()) {
            scopeHelper.addInScope(EvalHelper.normalizeVariableName(fp.getName().getText()), MASKED);
        }
        n.getBody().accept(this);
        scopeHelper.popScope();
        return n;
    }

    @Override
    public ASTNode visit(final FunctionInvocationNode n) {
        final Optional<FEELFunction> fnOpt;
        if (n.getName() instanceof NameRefNode) {
            fnOpt = scopeHelper.resolve(n.getName().getText());
        } else if (n.getName() instanceof QualifiedNameNode) {
            QualifiedNameNode qnn = (QualifiedNameNode) n.getName();
            String[] qns = qnn.getPartsAsStringArray();
            String qn = Stream.of(qns).collect(Collectors.joining(" "));
            fnOpt = scopeHelper.resolve(qn);
        } else {
            fnOpt = Optional.empty();
        }
        if (fnOpt.isPresent()) {
            FEELFunction fn = fnOpt.get();
            if (TEMPORAL_FNS.contains(fn)) {
                try {
                    TemporalConstantNode tcNode = buildTemporalConstantNode(n, fn);
                    n.setTcFolded(tcNode);
                } catch (final Exception e) {
                    // Temporal constant inlining failed - not an issue, simply not memoized.
                }
            }
        }
        return super.visit(n);
    }

    private TemporalConstantNode buildTemporalConstantNode(final FunctionInvocationNode n,
                                                           final FEELFunction fn) {
        switch (fn.getName()) {
            case DATE:
                return buildTCNodeForDate(n, fn);
            case DATE_AND_TIME:
                return buildTCNodeForDateAndTime(n, fn);
            case TIME:
                return buildTCNodeForTime(n, fn);
            case DURATION:
                return buildTCNodeForDuration(n, fn);
            default:
                return null;
        }
    }

    private TemporalConstantNode buildTCNodeForDuration(final FunctionInvocationNode n,
                                                        final FEELFunction fn) {
        return null;
    }

    private TemporalConstantNode buildTCNodeForTime(final FunctionInvocationNode n,
                                                    final FEELFunction fn) {
        return null;
    }

    private TemporalConstantNode buildTCNodeForDateAndTime(final FunctionInvocationNode n,
                                                           final FEELFunction fn) {
        return null;
    }

    private TemporalConstantNode buildTCNodeForDate(final FunctionInvocationNode n,
                                                    final FEELFunction fn) {
        return null;
    }

    private static final class DUMMY implements FEELFunction {

        @Override
        public String getName() {
            return "DUMMY";
        }

        @Override
        public Symbol getSymbol() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<List<Param>> getParameters() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object invokeReflectively(final EvaluationContext ctx,
                                         final Object[] params) {
            throw new UnsupportedOperationException();
        }
    }
}
