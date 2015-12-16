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


import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.LiteralRestrictionDescr;
import org.drools.compiler.lang.descr.OperatorDescr;
import org.drools.compiler.lang.descr.PredicateDescr;
import org.drools.compiler.lang.descr.RelationalExprDescr;
import org.drools.core.base.EvaluatorWrapper;
import org.drools.core.base.ValueType;
import org.drools.core.base.evaluators.EvaluatorDefinition;
import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.Constraint;
import org.drools.core.spi.Evaluator;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.util.index.IndexUtil;

import java.util.Collection;
import java.util.Map;


public interface ConstraintBuilder {

    public boolean isMvelOperator(String operator);

    public Constraint buildVariableConstraint(RuleBuildContext context,
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

    public Constraint buildLiteralConstraint(RuleBuildContext context,
                                             Pattern pattern,
                                             ValueType vtype,
                                             FieldValue field,
                                             String expression,
                                             String leftValue,
                                             String operator,
                                             String rightValue,
                                             InternalReadAccessor extractor,
                                             LiteralRestrictionDescr restrictionDescr,
                                             Map<String, OperatorDescr> aliases);


    public Evaluator buildLiteralEvaluator( RuleBuildContext context,
                                            InternalReadAccessor extractor,
                                            LiteralRestrictionDescr literalRestrictionDescr,
                                            ValueType vtype );

    public EvaluatorDefinition.Target getRightTarget( final InternalReadAccessor extractor );

    public Evaluator getEvaluator( RuleBuildContext context,
                                   BaseDescr descr,
                                   ValueType valueType,
                                   String evaluatorString,
                                   boolean isNegated,
                                   String parameters,
                                   EvaluatorDefinition.Target left,
                                   EvaluatorDefinition.Target right );
    
    public EvaluatorWrapper wrapEvaluator( Evaluator evaluator,
                                           Declaration left,
                                           Declaration right );

    public MVELCompilationUnit buildCompilationUnit(RuleBuildContext context,
                                                    Pattern pattern,
                                                    String expression,
                                                    Map<String, OperatorDescr> aliases);

    public MVELCompilationUnit buildCompilationUnit( RuleBuildContext context,
                                                     Declaration[] previousDeclarations,
                                                     Declaration[] localDeclarations,
                                                     PredicateDescr predicateDescr,
                                                     AnalysisResult analysis );

    public Constraint buildMvelConstraint( Collection<String> packageNames,
                                           String expression, 
                                           Declaration[] declarations,
                                           EvaluatorWrapper[] operators,
                                           MVELCompilationUnit compilationUnit,
                                           IndexUtil.ConstraintType constraintType,
                                           Declaration indexingDeclaration,
                                           InternalReadAccessor extractor,
                                           boolean isUnification );

    public Constraint buildMvelConstraint( String packageName,
                                           String expression,
                                           Declaration[] declarations,
                                           EvaluatorWrapper[] operators,
                                           MVELCompilationUnit compilationUnit,
                                           boolean isIndexable );
}
