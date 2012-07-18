package org.drools.rule.builder;


import org.drools.base.EvaluatorWrapper;
import org.drools.base.ValueType;
import org.drools.base.evaluators.EvaluatorDefinition;
import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.compiler.AnalysisResult;
import org.drools.core.util.index.IndexUtil;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.lang.descr.OperatorDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.RelationalExprDescr;
import org.drools.rule.Declaration;
import org.drools.rule.Pattern;
import org.drools.spi.Constraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;



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
                                                     RelationalExprDescr relDescr);

    public Constraint buildLiteralConstraint(RuleBuildContext context,
                                                    Pattern pattern,
                                                    ValueType vtype,
                                                    FieldValue field,
                                                    String expression,
                                                    String leftValue,
                                                    String operator,
                                                    String rightValue,
                                                    InternalReadAccessor extractor,
                                                    LiteralRestrictionDescr restrictionDescr);


    public Evaluator buildLiteralEvaluator( RuleBuildContext context,
                                            InternalReadAccessor extractor,
                                            LiteralRestrictionDescr literalRestrictionDescr,
                                            ValueType vtype );

    public EvaluatorDefinition.Target getRightTarget( final InternalReadAccessor extractor );

    public Evaluator getEvaluator( final RuleBuildContext context,
                                    final BaseDescr descr,
                                    final ValueType valueType,
                                    final String evaluatorString,
                                    final boolean isNegated,
                                    final String parameters,
                                    final EvaluatorDefinition.Target left,
                                    final EvaluatorDefinition.Target right );
    
    public EvaluatorWrapper wrapEvaluator( Evaluator evaluator, Declaration left, Declaration right );

    public MVELCompilationUnit buildCompilationUnit(RuleBuildContext context, Pattern pattern, String expression);

    public MVELCompilationUnit buildCompilationUnit( final RuleBuildContext context,
                                                            final Declaration[] previousDeclarations,
                                                            final Declaration[] localDeclarations,
                                                            final PredicateDescr predicateDescr,
                                                            final AnalysisResult analysis );

    public Constraint buildMvelConstraint( String packageName, 
                                           String expression, 
                                           Declaration[] declarations, 
                                           MVELCompilationUnit compilationUnit,
                                           IndexUtil.ConstraintType constraintType,
                                           Declaration indexingDeclaration,
                                           InternalReadAccessor extractor,
                                           boolean isUnification );

    public Constraint buildMvelConstraint( String packageName,
                                           String expression,
                                           Declaration[] declarations,
                                           MVELCompilationUnit compilationUnit,
                                           boolean isIndexable );

}
