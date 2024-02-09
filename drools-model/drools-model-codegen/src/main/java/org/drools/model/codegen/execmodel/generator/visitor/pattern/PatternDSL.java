/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel.generator.visitor.pattern;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.expr.MethodCallExpr;
import org.drools.drl.ast.descr.AccumulateDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.ExprConstraintDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.generator.DeclarationSpec;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.model.codegen.execmodel.generator.drlxparse.DrlxParseSuccess;
import org.drools.model.codegen.execmodel.generator.visitor.DSLNode;

import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.validateDuplicateBindings;

public abstract class PatternDSL implements DSLNode {

    protected final RuleContext context;
    protected final PackageModel packageModel;
    protected final PatternDescr pattern;
    protected final List<? extends BaseDescr> constraintDescrs;

    PatternDSL(RuleContext context, PackageModel packageModel, PatternDescr pattern, List<? extends BaseDescr> constraintDescrs) {
        this.context = context;
        this.packageModel = packageModel;
        this.pattern = pattern;
        this.constraintDescrs = constraintDescrs;
    }

    @Override
    public void buildPattern() {
        try {
            context.setCurrentPatternDescr(Optional.of(pattern));
            DeclarationSpec declarationSpec = initPattern();

            if (constraintDescrs.isEmpty() && !(pattern.getSource() instanceof AccumulateDescr)) {
                context.addExpression(input(declarationSpec));
            } else {
                final List<PatternConstraintParseResult> patternConstraintParseResults = findAllConstraint(pattern, constraintDescrs);
                final List<String> allBindings = patternConstraintParseResults
                        .stream()
                        .map(p -> p.drlxParseResult().acceptWithReturnValue(DrlxParseSuccess::getExprBinding))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                validateDuplicateBindings(context.getRuleName(), allBindings).ifPresent(context::addCompilationError);

                if (!context.hasErrors()) {
                    buildPattern(declarationSpec, patternConstraintParseResults);
                }
            }
        } finally {
            context.resetCurrentPatternDescr();
        }
    }


    protected static boolean isPositional(BaseDescr constraint) {
        return constraint instanceof ExprConstraintDescr &&
                ((ExprConstraintDescr) constraint).getType() == ExprConstraintDescr.Type.POSITIONAL &&
                !constraint.getText().contains(":=");
    }

    public abstract DeclarationSpec initPattern();

    protected abstract List<PatternConstraintParseResult> findAllConstraint(PatternDescr pattern, List<? extends BaseDescr> constraintDescrs);

    protected abstract void buildPattern(DeclarationSpec declarationSpec, List<PatternConstraintParseResult> patternConstraintParseResults);

    protected abstract String getPatternTypeName();

    protected abstract Class<?> getPatternType();

    protected abstract MethodCallExpr input(DeclarationSpec declarationSpec);
}