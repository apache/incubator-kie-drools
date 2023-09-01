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

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.drools.base.util.PropertyReactivityUtil;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.drl.ast.descr.AccumulateDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.ExprConstraintDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.PatternSourceDescr;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.errors.InvalidExpressionErrorResult;
import org.drools.model.codegen.execmodel.generator.AggregateKey;
import org.drools.model.codegen.execmodel.generator.ConstraintUtil;
import org.drools.model.codegen.execmodel.generator.DeclarationSpec;
import org.drools.model.codegen.execmodel.generator.DrlxParseUtil;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.model.codegen.execmodel.generator.WindowReferenceGenerator;
import org.drools.model.codegen.execmodel.generator.drlxparse.ConstraintExpression;
import org.drools.model.codegen.execmodel.generator.drlxparse.ConstraintParser;
import org.drools.model.codegen.execmodel.generator.drlxparse.DrlxParseFail;
import org.drools.model.codegen.execmodel.generator.drlxparse.DrlxParseResult;
import org.drools.model.codegen.execmodel.generator.drlxparse.DrlxParseSuccess;
import org.drools.model.codegen.execmodel.generator.drlxparse.ParseResultVisitor;
import org.drools.model.codegen.execmodel.generator.drlxparse.ParseResultVoidVisitor;
import org.drools.model.codegen.execmodel.generator.drlxparse.SingleDrlxParseSuccess;
import org.drools.model.codegen.execmodel.generator.visitor.DSLNode;
import org.drools.model.codegen.execmodel.generator.visitor.FromVisitor;
import org.kie.api.definition.rule.Watch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.drools.compiler.rule.builder.PatternBuilder.lookAheadFieldsOfIdentifier;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.getPatternListenedProperties;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.validateDuplicateBindings;
import static org.drools.model.impl.NamesGenerator.generateName;
import static org.drools.model.impl.VariableImpl.GENERATED_VARIABLE_PREFIX;
import static org.drools.mvel.parser.printer.PrintUtil.printNode;
import static org.drools.util.StreamUtils.optionalToStream;

public abstract class PatternDSL implements DSLNode {

    protected final RuleContext context;
    protected final PackageModel packageModel;
    protected final PatternDescr pattern;
    private final List<? extends BaseDescr> constraintDescrs;
    protected Class<?> patternType;

    PatternDSL(RuleContext context, PackageModel packageModel, PatternDescr pattern, List<? extends BaseDescr> constraintDescrs, Class<?> patternType) {
        this.context = context;
        this.packageModel = packageModel;
        this.pattern = pattern;
        this.constraintDescrs = constraintDescrs;
        this.patternType = patternType;
    }

    public DeclarationSpec initPattern() {
        generatePatternIdentifierIfMissing();
        context.addPatternBinding(pattern.getIdentifier());
        final Optional<Expression> declarationSource = buildFromDeclaration(pattern);
        return context.addDeclaration(pattern.getIdentifier(), patternType, Optional.of(pattern), declarationSource);
    }

    private static boolean isPositional(BaseDescr constraint) {
        return constraint instanceof ExprConstraintDescr &&
                ((ExprConstraintDescr) constraint).getType() == ExprConstraintDescr.Type.POSITIONAL &&
                !constraint.getText().contains( ":=" );
    }

    private Optional<Expression> buildFromDeclaration(PatternDescr pattern) {
        Optional<PatternSourceDescr> source = Optional.ofNullable(pattern.getSource());
        try {
            patternType = context.getTypeResolver().resolveType( pattern.getObjectType() );
        } catch (ClassNotFoundException e) {
            throw new RuntimeException( e );
        }
        Optional<Expression> declarationSourceFrom = source.flatMap(new FromVisitor(context, patternType)::visit);
        if (declarationSourceFrom.isPresent()) {
            return declarationSourceFrom;
        }
        return source.flatMap(sourceDescr -> new WindowReferenceGenerator(packageModel, context.getTypeResolver()).visit(sourceDescr, context));
    }

    private void generatePatternIdentifierIfMissing() {
        // the PatternDescr can be shared by multiple rules in case of rules inheritance, so its identifier has to
        // be set atomically when rule generation is performed in parallel
        synchronized (pattern) {
            if (pattern.getIdentifier() == null) {
                final String generatedName = generateName("pattern_" + patternType.getSimpleName());
                final String patternNameAggregated = findFirstInnerBinding(constraintDescrs, patternType)
                        .map(ib -> context.getAggregatePatternMap().putIfAbsent(new AggregateKey(ib, patternType), generatedName))
                        .orElse(generatedName);
                pattern.setIdentifier(GENERATED_VARIABLE_PREFIX + patternNameAggregated);
            }
        }
    }

    private Optional<String> findFirstInnerBinding(List<? extends BaseDescr> constraintDescrs, Class<?> patternType) {
        return constraintDescrs.stream()
                .map( constraint -> ConstraintExpression.createConstraintExpression( context, patternType, constraint, isPositional(constraint) ).getExpression() )
                .map( DrlxParseUtil::parseExpression )
                .filter( drlx -> drlx.getBind() != null )
                .map( drlx -> drlx.getBind().asString() )
                .findFirst();
    }

    private List<PatternConstraintParseResult> findAllConstraint(PatternDescr pattern, List<? extends BaseDescr> constraintDescrs, Class<?> patternType) {
        ConstraintParser constraintParser = ConstraintParser.defaultConstraintParser(context, packageModel);
        List<PatternConstraintParseResult> patternConstraintParseResults = new ArrayList<>();

        for (BaseDescr constraint : constraintDescrs) {
            List<PatternConstraintParseResult> patternConstraintParseResultsPerConstraintDescr = new ArrayList<>();
            String patternIdentifier = pattern.getIdentifier();

            boolean isPositional = isPositional(constraint);

            ConstraintExpression constraintExpression = ConstraintExpression.createConstraintExpression(context, patternType, constraint, isPositional);
            if (constraintExpression == null) {
                continue;
            }

            DrlxParseResult drlxParseResult;
            try {
                context.setCurrentConstraintDescr(Optional.of(constraint));
                drlxParseResult = constraintParser.drlxParse(patternType, patternIdentifier, constraintExpression, isPositional);
            } finally {
                context.resetCurrentConstraintDescr();
            }

            String expression = constraintExpression.getExpression();
            if (drlxParseResult.isSuccess() && (( DrlxParseSuccess ) drlxParseResult).isRequiresSplit() && (( DrlxParseSuccess ) drlxParseResult).getExpr().isBinaryExpr()) {
                String leftExpression = printNode(((SingleDrlxParseSuccess) drlxParseResult).getLeft().getExpression());
                DrlxParseResult leftExpressionReparsed = constraintParser.drlxParse(patternType, patternIdentifier, leftExpression, isPositional);
                patternConstraintParseResultsPerConstraintDescr.add(new PatternConstraintParseResult(leftExpression, patternIdentifier, leftExpressionReparsed));

                String rightExpression = printNode(((SingleDrlxParseSuccess) drlxParseResult).getRight().getExpression());
                DrlxParseResult rightExpressionReparsed = constraintParser.drlxParse(patternType, patternIdentifier, rightExpression, isPositional);
                DrlxParseResult normalizedParseResult = ConstraintUtil.normalizeConstraint(rightExpressionReparsed);
                patternConstraintParseResultsPerConstraintDescr.add(new PatternConstraintParseResult(rightExpression, patternIdentifier, normalizedParseResult));
            } else {
                DrlxParseResult normalizedParseResult = ConstraintUtil.normalizeConstraint(drlxParseResult);
                patternConstraintParseResultsPerConstraintDescr.add(new PatternConstraintParseResult(expression, patternIdentifier, normalizedParseResult));
            }

            // Cast-check should be placed earlier than Null-check (calling the add method later means pushing the constraint earlier)
            addNullSafeExpr(constraintParser, pattern.getIdentifier(), patternConstraintParseResultsPerConstraintDescr);
            addImplicitCastExpr(constraintParser, pattern.getIdentifier(), patternConstraintParseResultsPerConstraintDescr);

            patternConstraintParseResults.addAll(patternConstraintParseResultsPerConstraintDescr);
        }

        return patternConstraintParseResults;
    }

    private void addImplicitCastExpr(ConstraintParser constraintParser, String patternIdentifier, List<PatternConstraintParseResult> patternConstraintParseResults) {
        final boolean hasInstanceOfExpr = patternConstraintParseResults.stream()
                .anyMatch(r -> r.getDrlxParseResult().acceptWithReturnValue(new ParseResultVisitor<>() {
                    @Override
                    public Boolean onSuccess(DrlxParseSuccess t) {
                        Expression expr = t.getExpr();
                        return expr != null && expr.isInstanceOfExpr();
                    }

                    @Override
                    public Boolean onFail(DrlxParseFail failure) {
                        return false;
                    }
                }));

        final Optional<Expression> implicitCastExpression =
                patternConstraintParseResults.stream()
                .flatMap(r -> optionalToStream(r.getDrlxParseResult().acceptWithReturnValue(new ParseResultVisitor<Optional<Expression>>() {
                    @Override
                    public Optional<Expression> onSuccess(DrlxParseSuccess t) {
                        return t.getImplicitCastExpression();
                    }

                    @Override
                    public Optional<Expression> onFail(DrlxParseFail failure) {
                        return Optional.empty();
                    }
                })))
                .findFirst();

        implicitCastExpression.ifPresent(ce -> {
            if(!hasInstanceOfExpr) {
                String instanceOfExpression = printNode(ce);
                DrlxParseResult instanceOfExpressionParsed = constraintParser.drlxParse(patternType, patternIdentifier, instanceOfExpression, false);
                patternConstraintParseResults.add(0, new PatternConstraintParseResult(instanceOfExpression, patternIdentifier, instanceOfExpressionParsed));
            }
        });
    }

    private void addNullSafeExpr(ConstraintParser constraintParser, String patternIdentifier, List<PatternConstraintParseResult> patternConstraintParseResults) {
        final List<Expression> nullSafeExpressions =
                patternConstraintParseResults.stream()
                                             .flatMap(r -> r.getDrlxParseResult().acceptWithReturnValue(new ParseResultVisitor<List<Expression>>() {

                                                 @Override
                                                 public List<Expression> onSuccess(DrlxParseSuccess t) {
                                                     return t.getNullSafeExpressions();
                                                 }

                                                 @Override
                                                 public List<Expression> onFail(DrlxParseFail failure) {
                                                     return Collections.emptyList();
                                                 }
                                             }).stream())
                                             .collect(Collectors.toList());

        List<Expression> newNullSafeExpressions = reverseDistinct(nullSafeExpressions);

        newNullSafeExpressions.forEach(expr -> {
            String nullSafeExpression = printNode(expr);
            DrlxParseResult nullSafeExpressionParsed = constraintParser.drlxParse(patternType, patternIdentifier, nullSafeExpression, false);
            patternConstraintParseResults.add(0, new PatternConstraintParseResult(nullSafeExpression, patternIdentifier, nullSafeExpressionParsed));
        });
    }

    private List<Expression> reverseDistinct(List<Expression> nullSafeExpressions) {
        // distinct from the end of the list
        Collections.reverse(nullSafeExpressions);
        List<Expression> newNullSafeExpressions = nullSafeExpressions.stream().distinct().collect(Collectors.toList());
        Collections.reverse(newNullSafeExpressions);
        return newNullSafeExpressions;
    }

    void buildConstraint(PatternDescr pattern, Class<?> patternType, PatternConstraintParseResult patternConstraintParseResult) {
        DrlxParseResult drlxParseResult = patternConstraintParseResult.getDrlxParseResult();

        DrlxParseResult withBindingCheck = drlxParseResult.acceptWithReturnValue(new ParseResultVisitor<>() {
            @Override
            public DrlxParseResult onSuccess(DrlxParseSuccess drlxParseResult) {

                String exprBinding = drlxParseResult.getExprBinding();
                if (exprBinding == null && !drlxParseResult.isPredicate() && drlxParseResult.getImplicitCastExpression().isEmpty()) {
                    return new DrlxParseFail(new DescrBuildError(context.getRuleDescr(), context.getRuleDescr(), "",
                                                                 String.format("Predicate '%s' must be a Boolean expression", drlxParseResult.getOriginalDrlConstraint())));
                } else {
                    return drlxParseResult;
                }
            }

            @Override
            public DrlxParseResult onFail(DrlxParseFail failure) {
                return failure;
            }
        });

        withBindingCheck.accept(
                new ParseResultVoidVisitor() {
                    @Override
                    public void onSuccess( DrlxParseSuccess drlxParseResult ) {
                        DSLNode constraint = drlxParseResult.isOOPath() ?
                            new ConstraintOOPath( context, packageModel, pattern, patternType, patternConstraintParseResult, drlxParseResult ) :
                            createSimpleConstraint( drlxParseResult, pattern );
                        constraint.buildPattern();
                        registerUsedBindingInOr(drlxParseResult.getExprBinding());
                    }

                    @Override
                    public void onFail( DrlxParseFail failure ) {
                        if (failure.getError() != null) {
                            context.addCompilationError( failure.getError() );
                        }
                    }
                } );
    }

    private void registerUsedBindingInOr(String exprBinding) {
        if(context.isNestedInsideOr()) {
            context.getBindingOr().add(exprBinding);
        }
    }

    @Override
    public void buildPattern() {
        try {
            context.setCurrentPatternDescr(Optional.of(pattern));
            DeclarationSpec declarationSpec = initPattern();

            if (constraintDescrs.isEmpty() && !(pattern.getSource() instanceof AccumulateDescr)) {
                context.addExpression(input(declarationSpec));
            } else {
                final List<PatternConstraintParseResult> patternConstraintParseResults = findAllConstraint(pattern, constraintDescrs, patternType);
                final List<String> allBindings = patternConstraintParseResults
                        .stream()
                        .map(p -> p.getDrlxParseResult().acceptWithReturnValue( DrlxParseSuccess::getExprBinding ))
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

    Set<String> getSettableWatchedProps() {
        Set<String> settableWatchedProps = new HashSet<>();
        Collection<String> settableProps = PropertyReactivityUtil.getAccessibleProperties(patternType);

        List<String> propertiesInWatch = getPatternListenedProperties(pattern);
        propertiesInWatch.forEach(prop -> populateSettableWatchedProps(prop, settableProps, settableWatchedProps, true));

        if (context.isPropertyReactive(patternType)) {
            Collection<String> lookAheadProps = lookAheadFieldsOfIdentifier(context.getRuleDescr(), pattern);
            lookAheadProps.forEach(prop -> populateSettableWatchedProps(prop, settableProps, settableWatchedProps, false)); // okay to have non-settable prop in lookAhead
        }
        
        return settableWatchedProps;
    }

    private void populateSettableWatchedProps(String property, Collection<String> settableProps, Set<String> settableWatchedProps, boolean raiseErrorForNonSettableOrDuplicatedProp) {
        String trimmedProperty = property.trim();
        String actualProperty = trimmedProperty;
        if (trimmedProperty.startsWith("!")) {
            actualProperty = property.substring(1).trim();
            trimmedProperty = "!" + actualProperty;
        }
        if (actualProperty.equals("*") || settableProps.contains(actualProperty)) {
            if (raiseErrorForNonSettableOrDuplicatedProp && (settableWatchedProps.contains(actualProperty) || settableWatchedProps.contains("!" + actualProperty))) {
                context.addCompilationError(new InvalidExpressionErrorResult("Duplicate property " + actualProperty + " in @" + Watch.class.getSimpleName() + " annotation"));
                return;
            }
            settableWatchedProps.add(trimmedProperty);
        } else if (raiseErrorForNonSettableOrDuplicatedProp) {
            context.addCompilationError(new InvalidExpressionErrorResult("Unknown property " + actualProperty + " in @watch annotation"));
        }
    }

    protected abstract void buildPattern(DeclarationSpec declarationSpec, List<PatternConstraintParseResult> patternConstraintParseResults);

    protected abstract MethodCallExpr input(DeclarationSpec declarationSpec);

    protected abstract DSLNode createSimpleConstraint( DrlxParseSuccess drlxParseResult, PatternDescr pattern );
}
