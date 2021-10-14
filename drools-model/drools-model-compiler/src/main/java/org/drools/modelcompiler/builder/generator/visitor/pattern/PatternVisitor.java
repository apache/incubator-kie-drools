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
import java.util.Optional;

import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.drools.compiler.lang.descr.FromDescr;
import org.drools.compiler.lang.descr.MVELExprDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.rule.builder.XpathAnalysis;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.errors.InvalidExpressionErrorResult;
import org.drools.modelcompiler.builder.generator.QueryParameter;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.visitor.DSLNode;

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
            pattern = normalizeOOPathPattern( pattern );
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

    private PatternDescr normalizeOOPathPattern(PatternDescr pattern) {
        String oopathExpr = pattern.getDescrs().get(0).getText();
        XpathAnalysis xpathAnalysis = XpathAnalysis.analyze(oopathExpr);
        XpathAnalysis.XpathPart firstPart = xpathAnalysis.getPart( 0 );

        PatternDescr normalizedPattern = new PatternDescr();
        normalizedPattern.setObjectType( findPatternType(firstPart) );
        firstPart.getConstraints().stream().map( ExprConstraintDescr::new ).forEach( normalizedPattern::addConstraint );

        if (xpathAnalysis.getParts().size() == 1) {
            normalizedPattern.setIdentifier( pattern.getIdentifier() );
        } else {
            StringBuilder sb = new StringBuilder();
            if (pattern.getIdentifier() != null) {
                sb.append( pattern.getIdentifier() ).append( ": " );
            }
            for (int i = 1; i < xpathAnalysis.getParts().size(); i++) {
                sb.append( "/" ).append( xpathAnalysis.getPart( i ) );
            }
            normalizedPattern.addConstraint( new ExprConstraintDescr( sb.toString() ) );
        }

        FromDescr source = new FromDescr();
        source.setDataSource(new MVELExprDescr( firstPart.getField() ));
        normalizedPattern.setSource( source );
        return normalizedPattern;
    }

    private String findPatternType(XpathAnalysis.XpathPart firstPart) {
        if (firstPart.getInlineCast() != null) {
            return firstPart.getInlineCast();
        }

        Optional<QueryParameter> queryParameter = context.getQueryParameterByName(firstPart.getField());
        if (queryParameter.isPresent()) {
            return queryParameter.get().getType().getCanonicalName();
        }

        Class<?> ruleUnitVarType = context.getRuleUnitVarType(firstPart.getField());
        if (ruleUnitVarType == null) {
            throw new IllegalArgumentException("Unknown declaration: " + firstPart.getField());
        }
        return ruleUnitVarType.getCanonicalName();
    }

    private boolean areAllConstraintsPositional(List<? extends BaseDescr> constraintDescrs) {
        return !constraintDescrs.isEmpty() && constraintDescrs.stream()
                .allMatch(c -> c instanceof ExprConstraintDescr
                        && ((ExprConstraintDescr) c).getType().equals(ExprConstraintDescr.Type.POSITIONAL));
    }
}
