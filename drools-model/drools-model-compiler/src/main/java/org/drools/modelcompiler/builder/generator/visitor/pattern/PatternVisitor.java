/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.generator.visitor.pattern;

import java.util.List;

import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.errors.InvalidExpressionErrorResult;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.visitor.DSLNode;
import org.drools.modelcompiler.util.PatternUtil;

import static org.drools.modelcompiler.builder.generator.QueryGenerator.QUERY_METHOD_PREFIX;
import static org.drools.modelcompiler.builder.generator.QueryGenerator.toQueryDef;

public class PatternVisitor {

    private final RuleContext context;
    private final PackageModel packageModel;

    public PatternVisitor(RuleContext context, PackageModel packageModel) {
        this.context = context;
        this.packageModel = packageModel;
    }

    public DSLNode visit(PatternDescr pattern) {
        String className = pattern.getObjectType();

        if (className != null) {
            DSLNode constraintDescrs = parsePatternWithClass(pattern, className);
            if (constraintDescrs != null) {
                return constraintDescrs;
            }
        } else {
            pattern = PatternUtil.normalizeOOPathPattern(pattern, context);
            className = pattern.getObjectType();
        }

        List<? extends BaseDescr> constraintDescrs = pattern.getConstraint().getDescrs();

        Class<?> patternType;
        try {
            patternType = context.getTypeResolver().resolveType(className);
        } catch (ClassNotFoundException e) {
            context.addCompilationError( new InvalidExpressionErrorResult( "Unable to find class: " + className ) );
            return () -> { };
        }

        final boolean allConstraintsPositional = areAllConstraintsPositional(constraintDescrs);
        return new PatternDSLPattern(context, packageModel, pattern, constraintDescrs, patternType);
    }

    private DSLNode parsePatternWithClass(PatternDescr pattern, String className) {
        List<? extends BaseDescr> constraintDescrs = pattern.getConstraint().getDescrs();

        String queryName = QUERY_METHOD_PREFIX + className;
        String queryDef = toQueryDef( className );

        // Expression is a query, get bindings from query parameter type
        if ( packageModel.hasQuery(className) && !context.isRecurisveQuery(queryDef) ) {
            return new Query(context, packageModel, pattern, constraintDescrs, queryName );
        }

        if ( packageModel.getQueryDefWithType().containsKey( queryDef ) ) {
            return new QueryCall(context, packageModel, pattern, queryDef );
        }

        if ( pattern.getIdentifier() == null && className.equals( "Object" ) && pattern.getSource() instanceof AccumulateDescr) {
            return new PatternAccumulateConstraint(context, packageModel, pattern, (( AccumulateDescr ) pattern.getSource()), constraintDescrs );
        }
        return null;
    }

    private boolean areAllConstraintsPositional(List<? extends BaseDescr> constraintDescrs) {
        return !constraintDescrs.isEmpty() && constraintDescrs.stream()
                .allMatch(c -> c instanceof ExprConstraintDescr
                        && ((ExprConstraintDescr) c).getType().equals(ExprConstraintDescr.Type.POSITIONAL));
    }
}
