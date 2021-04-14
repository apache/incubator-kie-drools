/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.rule.builder;


import java.util.List;
import java.util.Map;

import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.DialectConfiguration;
import org.drools.compiler.compiler.JavaDialectConfiguration;
import org.drools.compiler.kie.util.BeanCreator;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.LiteralRestrictionDescr;
import org.drools.compiler.lang.descr.OperatorDescr;
import org.drools.compiler.lang.descr.PredicateDescr;
import org.drools.compiler.lang.descr.RelationalExprDescr;
import org.drools.core.base.EvaluatorWrapper;
import org.drools.core.base.ValueType;
import org.drools.core.base.evaluators.EvaluatorDefinition;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.QueryArgument;
import org.drools.core.spi.Constraint;
import org.drools.core.spi.Evaluator;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.ObjectType;
import org.drools.core.time.TimerExpression;
import org.kie.api.internal.utils.ServiceRegistry;


public interface ConstraintBuilder {

    class Holder {
        private static final ConstraintBuilder cBuilder = getConstraintBuilder();

        private static ConstraintBuilder getConstraintBuilder() {
            ConstraintBuilder builder = ServiceRegistry.getService(ConstraintBuilder.class);
            return builder != null ? builder : DummyConstraintBuilder.INSTANCE;
        }
    }

    static ConstraintBuilder get() {
        return Holder.cBuilder;
    }

    DialectConfiguration createJavaDialectConfiguration();

    DialectConfiguration createMVELDialectConfiguration();

    boolean isMvelOperator(String operator);

    Constraint buildVariableConstraint(RuleBuildContext context,
                                       Pattern pattern,
                                       String expression,
                                       Declaration[] declarations,
                                       String leftValue,
                                       OperatorDescr operator,
                                       String rightValue,
                                       InternalReadAccessor extractor,
                                       Declaration requiredDeclaration,
                                       RelationalExprDescr relDescr,
                                       Map<String, OperatorDescr> aliases);

    Constraint buildLiteralConstraint(RuleBuildContext context,
                                      Pattern pattern,
                                      ValueType vtype,
                                      FieldValue field,
                                      String expression,
                                      String leftValue,
                                      String operator,
                                      boolean negated,
                                      String rightValue,
                                      InternalReadAccessor extractor,
                                      LiteralRestrictionDescr restrictionDescr,
                                      Map<String, OperatorDescr> aliases);


    Evaluator getEvaluator( RuleBuildContext context,
                            BaseDescr descr,
                            ValueType valueType,
                            String evaluatorString,
                            boolean isNegated,
                            String parameters,
                            EvaluatorDefinition.Target left,
                            EvaluatorDefinition.Target right );
    
    EvaluatorWrapper wrapEvaluator( Evaluator evaluator,
                                    Declaration left,
                                    Declaration right );

    Constraint buildMvelConstraint( String packageName,
                                    String expression,
                                    Declaration[] declarations,
                                    EvaluatorWrapper[] operators,
                                    RuleBuildContext context,
                                    Declaration[] previousDeclarations,
                                    Declaration[] localDeclarations,
                                    PredicateDescr predicateDescr,
                                    AnalysisResult analysis,
                                    boolean isIndexable );

    TimerExpression buildTimerExpression( String expression, RuleBuildContext context );

    AnalysisResult analyzeExpression(Class<?> thisClass, String expr);

    InternalReadAccessor buildMvelFieldReadAccessor( RuleBuildContext context, BaseDescr descr, Pattern pattern,
                                                     ObjectType objectType, String fieldName, boolean reportError);

    void setExprInputs(RuleBuildContext context, PatternBuilder.ExprBindings descrBranch,
                       Class<?> thisClass, String expr);

    FieldValue getMvelFieldValue(RuleBuildContext context, ValueType vtype, String value);

    QueryArgument buildExpressionQueryArgument(RuleBuildContext context, List<Declaration> declarations, String expression);

    BeanCreator createMVELBeanCreator(Map<String, Object> parameters);

    enum DummyConstraintBuilder implements ConstraintBuilder {
        INSTANCE;

        @Override
        public DialectConfiguration createJavaDialectConfiguration() {
            return new JavaDialectConfiguration();
        }

        @Override
        public DialectConfiguration createMVELDialectConfiguration() {
            return null;
        }

        @Override
        public AnalysisResult analyzeExpression( Class<?> thisClass, String expr ) {
            return null;
        }

        @Override
        public boolean isMvelOperator( String operator ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Constraint buildVariableConstraint( RuleBuildContext context, Pattern pattern, String expression, Declaration[] declarations, String leftValue, OperatorDescr operator, String rightValue, InternalReadAccessor extractor, Declaration requiredDeclaration, RelationalExprDescr relDescr, Map<String, OperatorDescr> aliases ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Constraint buildLiteralConstraint( RuleBuildContext context, Pattern pattern, ValueType vtype, FieldValue field, String expression, String leftValue, String operator, boolean negated, String rightValue, InternalReadAccessor extractor, LiteralRestrictionDescr restrictionDescr, Map<String, OperatorDescr> aliases ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Evaluator getEvaluator( RuleBuildContext context, BaseDescr descr, ValueType valueType, String evaluatorString, boolean isNegated, String parameters, EvaluatorDefinition.Target left, EvaluatorDefinition.Target right ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public EvaluatorWrapper wrapEvaluator( Evaluator evaluator, Declaration left, Declaration right ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Constraint buildMvelConstraint( String packageName, String expression, Declaration[] declarations, EvaluatorWrapper[] operators, RuleBuildContext context, Declaration[] previousDeclarations, Declaration[] localDeclarations, PredicateDescr predicateDescr, AnalysisResult analysis, boolean isIndexable ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public TimerExpression buildTimerExpression( String expression, RuleBuildContext context ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public InternalReadAccessor buildMvelFieldReadAccessor( RuleBuildContext context, BaseDescr descr, Pattern pattern, ObjectType objectType, String fieldName, boolean reportError ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setExprInputs( RuleBuildContext context, PatternBuilder.ExprBindings descrBranch, Class<?> thisClass, String expr ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public FieldValue getMvelFieldValue( RuleBuildContext context, ValueType vtype, String value ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public QueryArgument buildExpressionQueryArgument(RuleBuildContext context, List<Declaration> declarations, String expression) {
            throw new UnsupportedOperationException();
        }

        @Override
        public BeanCreator createMVELBeanCreator( Map<String, Object> parameters) {
            throw new UnsupportedOperationException();
        }
    }
}
