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
package org.drools.compiler.rule.builder;


import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.base.base.ObjectType;
import org.drools.base.base.ValueType;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.QueryArgument;
import org.drools.base.rule.accessor.Evaluator;
import org.drools.base.rule.accessor.FieldValue;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.base.rule.constraint.Constraint;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.DialectConfiguration;
import org.drools.compiler.compiler.JavaDialectConfiguration;
import org.drools.compiler.kie.util.BeanCreator;
import org.drools.core.time.TimerExpression;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.LiteralRestrictionDescr;
import org.drools.drl.ast.descr.OperatorDescr;
import org.drools.drl.ast.descr.PredicateDescr;
import org.drools.drl.ast.descr.RelationalExprDescr;
import org.kie.api.internal.utils.KieService;

public interface ConstraintBuilder extends KieService {

    class Holder {
        private static final ConstraintBuilder cBuilder = getConstraintBuilder();

        private static ConstraintBuilder getConstraintBuilder() {
            ConstraintBuilder builder = KieService.load(ConstraintBuilder.class);
            return builder != null ? builder : DummyConstraintBuilder.INSTANCE;
        }
    }

    static ConstraintBuilder get() {
        return Holder.cBuilder;
    }

    static boolean present() {
        return Holder.cBuilder != null;
    }

    static List<EvaluatorDefinition> loadEvaluatorDefinitions() {
        return present() ? get().getEvaluatorDefinitions() : Collections.emptyList();
    }

    List<EvaluatorDefinition> getEvaluatorDefinitions();

    DialectConfiguration createJavaDialectConfiguration(KnowledgeBuilderConfigurationImpl conf);

    DialectConfiguration createMVELDialectConfiguration(KnowledgeBuilderConfigurationImpl conf);

    boolean isMvelOperator(String operator);

    Constraint buildVariableConstraint(RuleBuildContext context,
                                       Pattern pattern,
                                       String expression,
                                       Declaration[] declarations,
                                       String leftValue,
                                       OperatorDescr operator,
                                       String rightValue,
                                       ReadAccessor extractor,
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
                                      ReadAccessor extractor,
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

    ReadAccessor buildMvelFieldReadAccessor( RuleBuildContext context, BaseDescr descr, Pattern pattern,
                                                     ObjectType objectType, String fieldName, boolean reportError);

    void setExprInputs(RuleBuildContext context, PatternBuilder.ExprBindings descrBranch,
                       Class<?> thisClass, String expr);

    FieldValue getMvelFieldValue(RuleBuildContext context, ValueType vtype, String value);

    QueryArgument buildExpressionQueryArgument(RuleBuildContext context, List<Declaration> declarations, String expression);

    BeanCreator createMVELBeanCreator(Map<String, Object> parameters);

    class DummyConstraintBuilder implements ConstraintBuilder {
        public static final ConstraintBuilder INSTANCE = new DummyConstraintBuilder();

        @Override
        public List<EvaluatorDefinition> getEvaluatorDefinitions() {
            return Collections.emptyList();
        }

        @Override
        public DialectConfiguration createJavaDialectConfiguration(KnowledgeBuilderConfigurationImpl conf) {
            return new JavaDialectConfiguration();
        }

        @Override
        public DialectConfiguration createMVELDialectConfiguration(KnowledgeBuilderConfigurationImpl conf) {
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
        public Constraint buildVariableConstraint( RuleBuildContext context, Pattern pattern, String expression, Declaration[] declarations, String leftValue, OperatorDescr operator, String rightValue, ReadAccessor extractor, Declaration requiredDeclaration, RelationalExprDescr relDescr, Map<String, OperatorDescr> aliases ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Constraint buildLiteralConstraint( RuleBuildContext context, Pattern pattern, ValueType vtype, FieldValue field, String expression, String leftValue, String operator, boolean negated, String rightValue, ReadAccessor extractor, LiteralRestrictionDescr restrictionDescr, Map<String, OperatorDescr> aliases ) {
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
        public ReadAccessor buildMvelFieldReadAccessor( RuleBuildContext context, BaseDescr descr, Pattern pattern, ObjectType objectType, String fieldName, boolean reportError ) {
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
