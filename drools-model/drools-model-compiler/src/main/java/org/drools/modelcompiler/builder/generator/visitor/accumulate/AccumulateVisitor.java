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

package org.drools.modelcompiler.builder.generator.visitor.accumulate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.rule.builder.util.AccumulateUtil;
import org.drools.core.base.accumulators.CollectAccumulator;
import org.drools.core.base.accumulators.CollectListAccumulateFunction;
import org.drools.core.base.accumulators.CollectSetAccumulateFunction;
import org.drools.core.rule.Pattern;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.errors.InvalidExpressionErrorResult;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.TypedExpression;
import org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseFail;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.drlxparse.ParseResultVisitor;
import org.drools.modelcompiler.builder.generator.drlxparse.SingleDrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.expression.AbstractExpressionBuilder;
import org.drools.modelcompiler.builder.generator.expression.PatternExpressionBuilder;
import org.drools.modelcompiler.builder.generator.expressiontyper.ExpressionTyper;
import org.drools.modelcompiler.builder.generator.expressiontyper.ExpressionTyperContext;
import org.drools.modelcompiler.builder.generator.visitor.ModelGeneratorVisitor;
import org.drools.modelcompiler.util.LambdaUtil;
import org.drools.mvel.parser.ast.expr.DrlNameExpr;
import org.kie.api.runtime.rule.AccumulateFunction;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getLiteralExpressionType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.validateDuplicateBindings;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.ACCUMULATE_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.ACC_FUNCTION_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.AND_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.BIND_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.BIND_AS_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.VALUE_OF_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.REACT_ON_CALL;
import static org.drools.modelcompiler.util.lambdareplace.ReplaceTypeInLambda.replaceTypeInExprLambda;
import static org.drools.mvel.parser.printer.PrintUtil.printConstraint;

public class AccumulateVisitor {

    private final RuleContext context;
    private final PackageModel packageModel;

    private final ModelGeneratorVisitor modelGeneratorVisitor;
    private final AbstractExpressionBuilder expressionBuilder;

    public AccumulateVisitor(ModelGeneratorVisitor modelGeneratorVisitor, RuleContext context, PackageModel packageModel) {
        this.context = context;
        this.modelGeneratorVisitor = modelGeneratorVisitor;
        this.packageModel = packageModel;
        this.expressionBuilder = new PatternExpressionBuilder(context);
    }

    public void visit(AccumulateDescr descr, PatternDescr basePattern) {
        final MethodCallExpr accumulateDSL = new MethodCallExpr(null, ACCUMULATE_CALL);
        context.addExpression(accumulateDSL);
        final MethodCallExpr accumulateExprs = new MethodCallExpr(null, AND_CALL);
        accumulateDSL.addArgument(accumulateExprs);

        this.context.pushScope(descr);
        pushAccumulateContext( accumulateExprs );

        try {
            Set<String> externalDeclrs = new HashSet<>( context.getAvailableBindings() );
            BaseDescr input = descr.getInputPattern() == null ? descr.getInput() : descr.getInputPattern();
            input.accept( modelGeneratorVisitor );

            if ( accumulateExprs.getArguments().isEmpty() ) {
                accumulateDSL.remove( accumulateExprs );
            } else if ( accumulateExprs.getArguments().size() == 1 ) {
                accumulateDSL.setArgument( 0, accumulateExprs.getArguments().get( 0 ) );
            }

            if ( !descr.getFunctions().isEmpty() ) {
                if ( validateBindings(descr) ) {
                    return;
                }
                classicAccumulate( descr, basePattern, input, accumulateDSL );
            } else if ( descr.getFunctions().isEmpty() && descr.getInitCode() != null ) {
                new AccumulateInlineVisitor( context, packageModel ).inlineAccumulate( descr, basePattern, accumulateDSL, externalDeclrs, input );
            } else {
                throw new UnsupportedOperationException( "Unknown type of Accumulate." );
            }
        } finally {
            context.popExprPointer();
            this.context.popScope();
        }
    }

    private void classicAccumulate(AccumulateDescr descr, PatternDescr basePattern, BaseDescr input, MethodCallExpr accumulateDSL) {
        for (AccumulateDescr.AccumulateFunctionCallDescr function : descr.getFunctions()) {
            try {
                Optional<NewBinding> optNewBinding = visit(function, basePattern, input, accumulateDSL);
                processNewBinding(optNewBinding, accumulateDSL);
            } catch (AccumulateNonExistingFunction e) {
                addNonExistingFunctionError(context, e.function);
                return;
            } catch (AccumulateParsingFailedException e) {
                context.addCompilationError(new InvalidExpressionErrorResult(e.getMessage(), Optional.of(context.getRuleDescr())));
                return;
            }
        }
    }

    private boolean validateBindings(AccumulateDescr descr) {
        final List<String> allBindings = descr
                .getFunctions()
                .stream()
                .map(AccumulateDescr.AccumulateFunctionCallDescr::getBind)
                .filter(Objects::nonNull)
                .collect(toList());

        final Optional<InvalidExpressionErrorResult> invalidExpressionErrorResult = validateDuplicateBindings(context.getRuleName(), allBindings);
        invalidExpressionErrorResult.ifPresent(context::addCompilationError);
        return invalidExpressionErrorResult.isPresent();
    }

    private Optional<NewBinding> visit(AccumulateDescr.AccumulateFunctionCallDescr function, PatternDescr basePattern, BaseDescr input, MethodCallExpr accumulateDSL) {

        context.pushExprPointer(accumulateDSL::addArgument);

        try {
            final MethodCallExpr functionDSL = new MethodCallExpr( null, ACC_FUNCTION_CALL );

            final String optBindingId = ofNullable( function.getBind() ).orElse( basePattern.getIdentifier() );
            final String bindingId = context.getOutOfScopeVar( ofNullable( optBindingId ).orElse( context.getOrCreateAccumulatorBindingId( function.getFunction() ) ) );

            Optional<NewBinding> optNewBinding = Optional.empty();

            if ( function.getParams().length == 0 ) {
                final AccumulateFunction optAccumulateFunction = getAccumulateFunction( function, Object.class );
                zeroParameterFunction( basePattern, functionDSL, bindingId, optAccumulateFunction );
            } else {
                optNewBinding = parseFirstParameter( basePattern, input, function, functionDSL, bindingId );
            }

            if ( bindingId != null ) {
                final MethodCallExpr asDSL = new MethodCallExpr( functionDSL, BIND_AS_CALL );
                asDSL.addArgument( context.getVarExpr( bindingId, DrlxParseUtil.toVar(bindingId) ) );
                accumulateDSL.addArgument( asDSL );
            }

            return optNewBinding;
        } finally {
            context.popExprPointer();
        }
    }

    private Optional<NewBinding> parseFirstParameter(PatternDescr basePattern, BaseDescr input, AccumulateDescr.AccumulateFunctionCallDescr function, MethodCallExpr functionDSL, String bindingId) {
        final String accumulateFunctionParameterStr = function.getParams()[0];
        final Expression accumulateFunctionParameter = DrlxParseUtil.parseExpression(accumulateFunctionParameterStr).getExpr();

        if (accumulateFunctionParameter instanceof BinaryExpr) {
            return binaryExprParameter(basePattern, function, functionDSL, bindingId, accumulateFunctionParameterStr);
        }

        if (parameterNeedsConvertionToMethodCallExpr(accumulateFunctionParameter)) {
            return methodCallExprParameter(basePattern, input, function, functionDSL, bindingId, accumulateFunctionParameter);
        }

        if (accumulateFunctionParameter instanceof DrlNameExpr) {
            nameExprParameter(basePattern, function, functionDSL, bindingId, accumulateFunctionParameter);
        } else if (accumulateFunctionParameter instanceof LiteralExpr) {
            literalExprParameter(basePattern, function, functionDSL, bindingId, accumulateFunctionParameter);
        } else {
            context.addCompilationError(new InvalidExpressionErrorResult("Invalid expression " + accumulateFunctionParameterStr, Optional.of(context.getRuleDescr())));
            throw new AccumulateParsingFailedException();
        }

        return Optional.empty();
    }

    private void literalExprParameter(PatternDescr basePattern, AccumulateDescr.AccumulateFunctionCallDescr function, MethodCallExpr functionDSL, String bindingId, Expression accumulateFunctionParameter) {
        final Class<?> declarationClass = getLiteralExpressionType((LiteralExpr) accumulateFunctionParameter);

        AccumulateFunction accumulateFunction = getAccumulateFunction(function, declarationClass);

        validateAccFunctionTypeAgainstPatternType(context, basePattern, accumulateFunction);
        functionDSL.addArgument(createAccSupplierExpr(accumulateFunction));
        functionDSL.addArgument(new MethodCallExpr(null, VALUE_OF_CALL, NodeList.nodeList(accumulateFunctionParameter)));

        addBindingAsDeclaration(context, bindingId, accumulateFunction);
    }

    private void nameExprParameter(PatternDescr basePattern, AccumulateDescr.AccumulateFunctionCallDescr function, MethodCallExpr functionDSL, String bindingId, Expression accumulateFunctionParameter) {
        String nameExpr = ((DrlNameExpr) accumulateFunctionParameter).getName().asString();
        Optional<DeclarationSpec> declaration = context.getDeclarationById(nameExpr);
        if ( !declaration.isPresent() ) {
            String name = nameExpr;
            declaration = context.getAllDeclarations().stream().filter( d -> d.getVariableName().map( n -> n.equals( name ) ).orElse( false ) ).findFirst();
            if ( declaration.isPresent() ) {
                nameExpr = declaration.get().getBindingId();
            } else {
                throw new RuntimeException("Unknown parameter in accumulate: " + accumulateFunctionParameter);
            }
        }

        AccumulateFunction accumulateFunction = getAccumulateFunction(function, declaration.get().getDeclarationClass());

        validateAccFunctionTypeAgainstPatternType(context, basePattern, accumulateFunction);
        functionDSL.addArgument(createAccSupplierExpr(accumulateFunction));
        functionDSL.addArgument(context.getVarExpr(nameExpr));

        addBindingAsDeclaration(context, bindingId, accumulateFunction);
    }

    private Optional<NewBinding> methodCallExprParameter(PatternDescr basePattern, BaseDescr input, AccumulateDescr.AccumulateFunctionCallDescr function, MethodCallExpr functionDSL, String bindingId, Expression accumulateFunctionParameter) {
        final Expression parameterConverted = convertParameter(accumulateFunctionParameter);
        final DrlxParseUtil.RemoveRootNodeResult methodCallWithoutRootNode = DrlxParseUtil.removeRootNode(parameterConverted);

        String rootNodeName = getRootNodeName(methodCallWithoutRootNode);
        Optional<DeclarationSpec> decl = context.getDeclarationById(rootNodeName);

        Class<?> clazz = decl.map(DeclarationSpec::getDeclarationClass)
                .orElseGet( () -> {
                    try {
                        return context.getTypeResolver().resolveType(rootNodeName);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException( e );
                    }
                } );

        final ExpressionTyperContext expressionTyperContext = new ExpressionTyperContext();
        final ExpressionTyper expressionTyper = new ExpressionTyper(context, clazz, bindingId, false, expressionTyperContext);

        TypedExpression typedExpression =
                expressionTyper
                        .toTypedExpression(methodCallWithoutRootNode.getWithoutRootNode())
                        .typedExpressionOrException();

        final Class<?> methodCallExprType = typedExpression.getRawClass();

        final AccumulateFunction accumulateFunction = getAccumulateFunction(function, methodCallExprType);

        validateAccFunctionTypeAgainstPatternType(context, basePattern, accumulateFunction);
        functionDSL.addArgument(createAccSupplierExpr(accumulateFunction));

        // Every expression in an accumulate function gets transformed in a bind expression with a generated id
        // Then the accumulate function will have that binding expression as a source
        final Class accumulateFunctionResultType = accumulateFunction.getResultType();
        final String bindExpressionVariable = context.getExprId(accumulateFunctionResultType, typedExpression.toString());

        String paramExprBindingId = rootNodeName;
        Class<?> patternType = clazz;
        PatternDescr inputPattern = decl.isPresent() ? null : findInputPattern(input);
        if (inputPattern != null) {
            String inputId = inputPattern.getIdentifier();
            Optional<DeclarationSpec> accumulateClassDeclOpt = context.getDeclarationById(inputId);
            if (accumulateClassDeclOpt.isPresent()) {
                // when static method is used in accumulate function, "_this" is a pattern input
                // Note that DrlxParseUtil.generateLambdaWithoutParameters() takes the patternType as a class of "_this"
                paramExprBindingId = inputId;
                patternType = accumulateClassDeclOpt.get().getDeclarationClass();
            }
        }

        SingleDrlxParseSuccess drlxParseResult = (SingleDrlxParseSuccess) new ConstraintParser(context, context.getPackageModel())
                .drlxParse(patternType, paramExprBindingId, printConstraint(parameterConverted));

        if (inputPattern != null) {
            drlxParseResult.setAccumulateBinding( inputPattern.getIdentifier() );
        }

        return drlxParseResult.acceptWithReturnValue(new ReplaceBindingVisitor(functionDSL, bindingId, methodCallExprType, accumulateFunctionResultType, bindExpressionVariable, drlxParseResult));
    }

    private PatternDescr findInputPattern(BaseDescr input) {
        if ( input instanceof PatternDescr ) {
            return (PatternDescr) input;
        }
        if ( input instanceof AndDescr ) {
            List<BaseDescr> childDescrs = (( AndDescr ) input).getDescrs();
            for (int i = childDescrs.size()-1; i >= 0; i--) {
                if ( childDescrs.get(i) instanceof PatternDescr ) {
                    return (( PatternDescr ) childDescrs.get( i ));
                }
            }
        }
        return null;
    }

    private class ReplaceBindingVisitor implements  ParseResultVisitor<Optional<NewBinding>> {

        private final MethodCallExpr functionDSL;
        private final String bindingId;
        private final Class<?> methodCallExprType;
        private final Class accumulateFunctionResultType;
        private final String bindExpressionVariable;
        private final SingleDrlxParseSuccess drlxParseResult;

        ReplaceBindingVisitor(MethodCallExpr functionDSL, String bindingId, Class<?> methodCallExprType, Class accumulateFunctionResultType, String bindExpressionVariable, SingleDrlxParseSuccess drlxParseResult) {
            this.functionDSL = functionDSL;
            this.bindingId = bindingId;
            this.methodCallExprType = methodCallExprType;
            this.accumulateFunctionResultType = accumulateFunctionResultType;
            this.bindExpressionVariable = bindExpressionVariable;
            this.drlxParseResult = drlxParseResult;
        }

        @Override
        public Optional<NewBinding> onSuccess(DrlxParseSuccess result) {
            SingleDrlxParseSuccess singleResult = drlxParseResult;
            singleResult.setExprBinding(bindExpressionVariable);
            final MethodCallExpr binding = expressionBuilder.buildBinding(singleResult);
            context.addDeclarationReplacing(new DeclarationSpec(bindExpressionVariable, methodCallExprType));
            functionDSL.addArgument(context.getVarExpr(bindExpressionVariable));

            context.addDeclarationReplacing(new DeclarationSpec(bindingId, accumulateFunctionResultType));

            context.getExpressions().forEach(expression -> replaceTypeInExprLambda(bindingId, accumulateFunctionResultType, expression));

            List<String> ids = new ArrayList<>();
            if (singleResult.getPatternBinding() != null) {
                 ids.add( singleResult.getPatternBinding() );
            }
            return Optional.of(new NewBinding(ids, binding));
        }

        @Override
        public Optional<NewBinding> onFail(DrlxParseFail failure) {
            return Optional.empty();
        }
    }

    private Expression convertParameter(Expression accumulateFunctionParameter) {
        final Expression parameterConverted;
        if (accumulateFunctionParameter.isArrayAccessExpr()) {
            parameterConverted = new ExpressionTyper(context, Object.class, null, false)
                    .toTypedExpression(accumulateFunctionParameter)
                    .getTypedExpression().orElseThrow(() -> new RuntimeException("Cannot convert expression to method call" + accumulateFunctionParameter))
                    .getExpression();
        } else if (accumulateFunctionParameter.isFieldAccessExpr()) {
            parameterConverted = new ExpressionTyper(context, Object.class, null, false)
                    .toTypedExpression(accumulateFunctionParameter)
                    .getTypedExpression().orElseThrow(() -> new RuntimeException("Cannot convert expression to method call" + accumulateFunctionParameter))
                    .getExpression();
        } else {
            parameterConverted = accumulateFunctionParameter;
        }
        return parameterConverted;
    }

    private boolean parameterNeedsConvertionToMethodCallExpr(Expression accumulateFunctionParameter) {
        return accumulateFunctionParameter.isMethodCallExpr() || accumulateFunctionParameter.isArrayAccessExpr() || accumulateFunctionParameter.isFieldAccessExpr();
    }

    private Optional<NewBinding> binaryExprParameter(PatternDescr basePattern, AccumulateDescr.AccumulateFunctionCallDescr function, MethodCallExpr functionDSL, String bindingId, String accumulateFunctionParameterStr) {
        final DrlxParseResult parseResult = new ConstraintParser(context, packageModel).drlxParse(Object.class, bindingId, accumulateFunctionParameterStr);

        Optional<NewBinding> optNewBinding = parseResult.acceptWithReturnValue(new ParseResultVisitor<Optional<NewBinding>>() {
            @Override
            public Optional<NewBinding> onSuccess(DrlxParseSuccess drlxParseResult) {
                SingleDrlxParseSuccess singleResult = (SingleDrlxParseSuccess) drlxParseResult;
                Class<?> exprRawClass = singleResult.getExprRawClass();
                AccumulateFunction accumulateFunction = getAccumulateFunction(function, exprRawClass);

                validateAccFunctionTypeAgainstPatternType(context, basePattern, accumulateFunction);
                final String bindExpressionVariable = context.getExprId(accumulateFunction.getResultType(), singleResult.getLeft().toString());

                singleResult.setExprBinding(bindExpressionVariable);

                context.addDeclarationReplacing(new DeclarationSpec(singleResult.getPatternBinding(), exprRawClass));
                context.getExpressions().forEach(expression -> replaceTypeInExprLambda(bindingId, exprRawClass, expression));

                functionDSL.addArgument(createAccSupplierExpr(accumulateFunction));
                final MethodCallExpr newBindingFromBinary = AccumulateVisitor.this.buildBinding(bindExpressionVariable, singleResult.getUsedDeclarations(), singleResult.getExpr());
                context.addDeclarationReplacing(new DeclarationSpec(bindExpressionVariable, exprRawClass));
                functionDSL.addArgument(context.getVarExpr(bindExpressionVariable));
                return Optional.of(new NewBinding(Collections.emptyList(), newBindingFromBinary));
            }

            @Override
            public Optional<NewBinding> onFail(DrlxParseFail failure) {
                return Optional.empty();
            }
        });
        return optNewBinding;
    }

    private void zeroParameterFunction(PatternDescr basePattern, MethodCallExpr functionDSL, String bindingId, AccumulateFunction accumulateFunction) {
        validateAccFunctionTypeAgainstPatternType(context, basePattern, accumulateFunction);
        functionDSL.addArgument(createAccSupplierExpr(accumulateFunction));
        Class accumulateFunctionResultType = accumulateFunction.getResultType();
        context.addDeclarationReplacing(new DeclarationSpec(bindingId, accumulateFunctionResultType));
        context.getExpressions().forEach(expression -> replaceTypeInExprLambda(bindingId, accumulateFunctionResultType, expression));
    }

    private static MethodReferenceExpr createAccSupplierExpr(AccumulateFunction accumulateFunction) {
        NameExpr nameExpr = new NameExpr(accumulateFunction.getClass().getCanonicalName());
        return new MethodReferenceExpr(nameExpr, new NodeList<>(), "new");
    }

    private void validateAccFunctionTypeAgainstPatternType(RuleContext context, PatternDescr basePattern, AccumulateFunction accumulateFunction) {
        final String expectedResultTypeString = basePattern.getObjectType();
        final Class<?> expectedResultType = DrlxParseUtil.getClassFromType(context.getTypeResolver(), DrlxParseUtil.toClassOrInterfaceType(expectedResultTypeString));
        final Class actualResultType = accumulateFunction.getResultType();

        final boolean isJavaDialect = context.getRuleDialect().equals(RuleContext.RuleDialect.JAVA);
        final boolean isQuery = context.isQuery();
        final boolean isCollectFunction = isCollectFunction(accumulateFunction);
        final boolean isInsideAccumulate = ((AccumulateDescr) basePattern.getSource()).getInput() instanceof AndDescr;
        final boolean checkCollect = !isCollectFunction || isInsideAccumulate;

        if (!isQuery && checkCollect && isJavaDialect && !Pattern.isCompatibleWithAccumulateReturnType(expectedResultType, actualResultType)) {
            context.addCompilationError(new InvalidExpressionErrorResult(
                    String.format(
                            "Pattern of type: '[ClassObjectType class=%s]' " +
                                    "on rule '%s' " +
                                    "is not compatible with type %s returned by accumulate function."
                            , expectedResultType.getCanonicalName(), context.getRuleName(), actualResultType.getCanonicalName()), Optional.of(context.getRuleDescr())));
        }
    }

    private boolean isCollectFunction(AccumulateFunction accumulateFunction) {
        return accumulateFunction instanceof CollectListAccumulateFunction ||
                accumulateFunction instanceof CollectSetAccumulateFunction ||
                accumulateFunction instanceof CollectAccumulator;
    }

    private void addNonExistingFunctionError(RuleContext context, AccumulateDescr.AccumulateFunctionCallDescr function) {
        context.addCompilationError(new InvalidExpressionErrorResult(String.format("Unknown accumulate function: '%s' on rule '%s'.", function.getFunction(), context.getRuleDescr().getName()), Optional.of(context.getRuleDescr())));
    }

    private void addBindingAsDeclaration(RuleContext context, String bindingId, AccumulateFunction accumulateFunction) {
        if (bindingId != null) {
            Class accumulateFunctionResultType = accumulateFunction.getResultType();
            context.addDeclarationReplacing(new DeclarationSpec(bindingId, accumulateFunctionResultType));
            context.getExpressions().forEach(expression -> replaceTypeInExprLambda(bindingId, accumulateFunctionResultType, expression));
        }
    }

    private AccumulateFunction getAccumulateFunction(AccumulateDescr.AccumulateFunctionCallDescr function, Class<?> methodCallExprType) {
        final String accumulateFunctionName = AccumulateUtil.getFunctionName(() -> methodCallExprType, function.getFunction());
        final Optional<AccumulateFunction> bundledAccumulateFunction = ofNullable(packageModel.getConfiguration().getAccumulateFunction(accumulateFunctionName));
        final Optional<AccumulateFunction> importedAccumulateFunction = ofNullable(packageModel.getAccumulateFunctions().get(accumulateFunctionName));

        return bundledAccumulateFunction
                .map(Optional::of)
                .orElse(importedAccumulateFunction)
                .orElseThrow(() -> new AccumulateNonExistingFunction(function));
    }

    private String getRootNodeName(DrlxParseUtil.RemoveRootNodeResult methodCallWithoutRootNode) {
        final Expression rootNode = methodCallWithoutRootNode.getRootNode().orElseThrow(UnsupportedOperationException::new);

        final String rootNodeName;
        if (rootNode instanceof NameExpr) {
            rootNodeName = ((NameExpr) rootNode).getName().asString();
        } else {
            throw new AccumulateParsingFailedException("Root node of expression should be a declaration");
        }
        return rootNodeName;
    }

    Expression buildConstraintExpression(Expression expr, Collection<String> usedDeclarations) {
        LambdaExpr lambdaExpr = new LambdaExpr();
        lambdaExpr.setEnclosingParameters(true);
        usedDeclarations.stream().map(s -> new Parameter(context.getDelarationType(s), s)).forEach(lambdaExpr::addParameter);
        lambdaExpr.setBody(new ExpressionStmt(expr));
        return lambdaExpr;
    }

    static List<String> collectNamesInBlock(BlockStmt block, RuleContext context) {
        return block.findAll(NameExpr.class, n -> {
            Optional<DeclarationSpec> optD = context.getDeclarationById(n.getNameAsString());
            return optD.filter(d -> !d.isGlobal()).isPresent(); // Global aren't supported
        })
                .stream()
                .map(NameExpr::getNameAsString)
                .distinct()
                .collect(toList());
    }

    private void pushAccumulateContext( MethodCallExpr accumulateExprs ) {
        context.pushExprPointer(accumulateExprs::addArgument);
    }

    private MethodCallExpr buildBinding(String bindingName, Collection<String> usedDeclaration, Expression expression) {
        MethodCallExpr bindDSL = new MethodCallExpr(null, BIND_CALL);
        bindDSL.addArgument(context.getVar(bindingName));
        usedDeclaration.stream().map(context::getVarExpr).forEach(bindDSL::addArgument);
        bindDSL.addArgument(buildConstraintExpression(expression, usedDeclaration));
        return bindDSL;
    }

    private void processNewBinding(Optional<NewBinding> optNewBinding, MethodCallExpr accumulateDSL) {
        optNewBinding.ifPresent(newBinding -> {
            final List<Expression> allExpressions = context.getExpressions();
            final MethodCallExpr newBindingExpression = newBinding.bindExpression;

            if (newBinding.patternBinding.size() == 1) {
                new PatternToReplace(context, newBinding.patternBinding).findFromPattern()
                        .ifPresent(pattern -> addBindAsLastChainCall(newBindingExpression, pattern));

                String binding = newBinding.patternBinding.iterator().next();
                composeTwoBindings(binding, newBindingExpression);

            } else if (newBinding.patternBinding.size() == 2) {
                String binding = newBinding.patternBinding.iterator().next();
                composeTwoBindings(binding, newBindingExpression);

            } else {
                final MethodCallExpr lastPattern = DrlxParseUtil.findLastPattern(allExpressions)
                        .orElseThrow(() -> new RuntimeException("Need the last pattern to add the binding"));
                final MethodCallExpr replacedBinding = replaceBindingWithPatternBinding(newBindingExpression, lastPattern);
                addBindAsLastChainCall(replacedBinding, lastPattern);
            }
        });
    }

    private void composeTwoBindings(String binding, MethodCallExpr newBindingExpression) {
        context.findBindingExpression(binding).ifPresent(oldBind -> {

            // compose newComposedBinding using oldBind and newBindingExpression. But still keep oldBind.

            LambdaExpr oldBindLambda = oldBind.findFirst(LambdaExpr.class).orElseThrow(RuntimeException::new);
            LambdaExpr newBindLambda = newBindingExpression.findFirst(LambdaExpr.class).orElseThrow(RuntimeException::new);

            LambdaExpr tmpOldBindLambda = oldBindLambda.clone();
            Expression newComposedLambda = LambdaUtil.appendNewLambdaToOld(tmpOldBindLambda, newBindLambda);

            MethodCallExpr newComposedBinding = new MethodCallExpr(BIND_CALL, newBindingExpression.getArgument(0), newComposedLambda);
            newComposedBinding.setScope(oldBind.getScope().orElseThrow(RuntimeException::new));

            Optional<MethodCallExpr> optReactOn = oldBind.getArguments().stream()
                    .filter(MethodCallExpr.class::isInstance)
                    .map(MethodCallExpr.class::cast)
                    .filter(exp -> exp.getName().asString().equals(REACT_ON_CALL))
                    .findFirst();
            if (optReactOn.isPresent()) {
                newComposedBinding.addArgument(optReactOn.get().clone());
            }
            oldBind.setScope(newComposedBinding); // insert newComposedBinding at the first in the chain
        });
    }

    private void addBindAsLastChainCall(MethodCallExpr newBindingExpression, MethodCallExpr pattern) {
        final Optional<Node> optParent = pattern.getParentNode();
        newBindingExpression.setScope(pattern);
        optParent.ifPresent(parent -> {
            parent.replace(pattern, newBindingExpression);
            pattern.setParentNode( newBindingExpression );
        });
    }

    private MethodCallExpr replaceBindingWithPatternBinding(MethodCallExpr bindExpression, MethodCallExpr lastPattern) {
        // This method links a binding expression, used to evaluate the accumulated value,
        // to the last pattern in a multi-pattern accumulate like the following
        //
        // accumulate( $c : Child( age < 10 ) and $a : Adult( name == $c.parent ) and $s : String( this == $a.name ),
        //             $sum : sum($a.getAge() + $c.getAge() + $s.length()) )
        //
        // In the case the bindExpression, that will have to be linked to the $s pattern, is originally generated as
        //
        // bind(var_$sum, var_$a, var_$c, var_$s, (Adult $a, Child $c, String $s) -> $a.getAge() + $c.getAge() + $s.length())

        final Expression bindingId = lastPattern.getArgument(0);

        bindExpression.findFirst(NameExpr.class, e -> e.equals(bindingId)).ifPresent( name -> {

            // since the bind has to be linked to $s, the corresponding variable should be removed from the arguments list so it becomes
            // bind(var_$sum, var_$a, var_$c, (Adult $a, Child $c, String $s) -> $a.getAge() + $c.getAge() + $s.length())
            bindExpression.remove(name);

            // also the first formal parameter in the binding lambda has to be $s so it becomes
            // bind(var_$sum, var_$a, var_$c, (String $s, Adult $a, Child $c) -> $a.getAge() + $c.getAge() + $s.length())
            LambdaExpr lambda = (LambdaExpr)bindExpression.getArgument( bindExpression.getArguments().size()-1 );
            if (lambda.getParameters().size() > 1) {
                String formalArg = context.fromVar( name.getNameAsString() );
                for (Parameter param : lambda.getParameters()) {
                    if (param.getNameAsString().equals( formalArg )) {
                        lambda.getParameters().remove( param );
                        lambda.getParameters().add( 0, param );
                        break;
                    }
                }
            }
        } );

        return bindExpression;
    }

    static class NewBinding {

        List<String> patternBinding;
        MethodCallExpr bindExpression;

        NewBinding(List<String> patternBinding, MethodCallExpr bindExpression) {
            this.patternBinding = patternBinding;
            this.bindExpression = bindExpression;
        }
    }

    private class AccumulateParsingFailedException extends RuntimeException {

        AccumulateParsingFailedException() {
        }

        AccumulateParsingFailedException(String message) {
            super(message);
        }
    }

    private class AccumulateNonExistingFunction extends RuntimeException {

        private final AccumulateDescr.AccumulateFunctionCallDescr function;

        AccumulateNonExistingFunction(AccumulateDescr.AccumulateFunctionCallDescr function) {
            this.function = function;
        }
    }
}
