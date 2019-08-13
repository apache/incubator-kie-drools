package org.drools.modelcompiler.builder.generator.visitor.accumulate;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.StaticJavaParser;
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
import com.github.javaparser.ast.type.UnknownType;
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
import org.drools.modelcompiler.builder.generator.expressiontyper.ExpressionTyper;
import org.drools.modelcompiler.builder.generator.visitor.ModelGeneratorVisitor;
import org.drools.mvel.parser.ast.expr.DrlNameExpr;
import org.kie.api.runtime.rule.AccumulateFunction;

import static java.util.stream.Collectors.toList;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.addSemicolon;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getLiteralExpressionType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.validateDuplicateBindings;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.ACCUMULATE_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.ACC_FUNCTION_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.AND_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.BIND_AS_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.VALUE_OF_CALL;
import static org.drools.mvel.parser.printer.PrintUtil.printConstraint;

public abstract class AccumulateVisitor {

    protected final RuleContext context;
    protected final PackageModel packageModel;

    private final ModelGeneratorVisitor modelGeneratorVisitor;
    protected AbstractExpressionBuilder expressionBuilder;

    AccumulateVisitor(RuleContext context, ModelGeneratorVisitor modelGeneratorVisitor, PackageModel packageModel) {
        this.context = context;
        this.modelGeneratorVisitor = modelGeneratorVisitor;
        this.packageModel = packageModel;
    }

    public void visit(AccumulateDescr descr, PatternDescr basePattern) {
        final MethodCallExpr accumulateDSL = new MethodCallExpr(null, ACCUMULATE_CALL);
        context.addExpression(accumulateDSL);
        final MethodCallExpr accumulateExprs = new MethodCallExpr(null, AND_CALL);
        accumulateDSL.addArgument(accumulateExprs);

        context.pushExprPointer(accumulateExprs::addArgument);

        Set<String> externalDeclrs = new HashSet<>( context.getAvailableBindings() );
        BaseDescr input = descr.getInputPattern() == null ? descr.getInput() : descr.getInputPattern();
        input.accept(modelGeneratorVisitor);

        if (accumulateExprs.getArguments().isEmpty()) {
            accumulateDSL.remove(accumulateExprs);
        } else if (accumulateExprs.getArguments().size() == 1) {
            accumulateDSL.setArgument(0, accumulateExprs.getArguments().get(0));
        }

        if (!descr.getFunctions().isEmpty()) {
            final List<String> allBindings = descr
                    .getFunctions()
                    .stream()
                    .map(AccumulateDescr.AccumulateFunctionCallDescr::getBind)
                    .filter(Objects::nonNull)
                    .collect(toList());

            final Optional<InvalidExpressionErrorResult> invalidExpressionErrorResult = validateDuplicateBindings(context.getRuleName(), allBindings);
            invalidExpressionErrorResult.ifPresent(context::addCompilationError);
            if(invalidExpressionErrorResult.isPresent()) {
                return;
            }

            for (AccumulateDescr.AccumulateFunctionCallDescr function : descr.getFunctions()) {
                final Optional<NewBinding> optNewBinding = visit(context, function, accumulateDSL, basePattern);
                processNewBinding(optNewBinding);
            }
        } else if (descr.getFunctions().isEmpty() && descr.getInitCode() != null) {
            // LEGACY: Accumulate with inline custom code
            AccumulateInline accumulateInline = new AccumulateInline(context, packageModel, descr, basePattern);
            if (input instanceof PatternDescr) {
                try {
                    accumulateInline.visitAccInlineCustomCode(accumulateDSL, externalDeclrs, ((PatternDescr) input).getIdentifier());
                } catch (UnsupportedInlineAccumulate e) {
                    new LegacyAccumulate(context, descr, basePattern, accumulateInline.getUsedExternalDeclarations()).build();
                } catch (MissingSemicolonInlineAccumulateException e) {
                    context.addCompilationError(new InvalidExpressionErrorResult(e.getMessage()));
                }
            } else if (input instanceof AndDescr) {
                BlockStmt actionBlock = parseBlockAddSemicolon(descr.getActionCode());
                Collection<String> allNamesInActionBlock = collectNamesInBlock(actionBlock, context);

                final Optional<BaseDescr> bindingUsedInAccumulate =
                        ((AndDescr) input).getDescrs()
                                .stream()
                                .filter(b -> allNamesInActionBlock.contains(((PatternDescr) b).getIdentifier()))
                                .findFirst();

                if(bindingUsedInAccumulate.isPresent()) {
                    BaseDescr binding = bindingUsedInAccumulate.get();
                    try {
                        accumulateInline.visitAccInlineCustomCode(accumulateDSL, externalDeclrs, ((PatternDescr) binding).getIdentifier());
                    } catch (UnsupportedInlineAccumulate e) {
                        new LegacyAccumulate(context, descr, basePattern, accumulateInline.getUsedExternalDeclarations()).build();
                    }
                }
            } else {
                throw new UnsupportedOperationException("I was expecting input to be of type PatternDescr. " + input);
            }
        } else {
            throw new UnsupportedOperationException("Unknown type of Accumulate.");
        }

        context.popExprPointer();
        postVisit();
    }

    protected Optional<AccumulateVisitorPatternDSL.NewBinding> visit(RuleContext context, AccumulateDescr.AccumulateFunctionCallDescr function, MethodCallExpr accumulateDSL, PatternDescr basePattern) {

        context.pushExprPointer(accumulateDSL::addArgument);

        final MethodCallExpr functionDSL = new MethodCallExpr(null, ACC_FUNCTION_CALL);

        final String optBindingId = Optional.ofNullable(function.getBind()).orElse(basePattern.getIdentifier());
        final String bindingId = Optional.ofNullable(optBindingId).orElse(context.getOrCreateAccumulatorBindingId(function.getFunction()));

        Optional<AccumulateVisitorPatternDSL.NewBinding> newBinding = Optional.empty();

        if(function.getParams().length == 0) {
            final Optional<AccumulateFunction> optAccumulateFunction = AccumulateVisitor.this.getAccumulateFunction(function, Object.class);
            if(!optAccumulateFunction.isPresent()) {
                addNonExistingFunctionError(context, function);
                return Optional.empty();
            }
            final AccumulateFunction accumulateFunction = optAccumulateFunction.get();
            validateAccFunctionTypeAgainstPatternType(context, basePattern, accumulateFunction);
            functionDSL.addArgument( createAccSupplierExpr( accumulateFunction ) );
            Class accumulateFunctionResultType = accumulateFunction.getResultType();
            context.addDeclarationReplacing(new DeclarationSpec(bindingId, accumulateFunctionResultType));

        } else {

            final String accumulateFunctionParameterStr = function.getParams()[0];
            final Expression accumulateFunctionParameter = DrlxParseUtil.parseExpression(accumulateFunctionParameterStr).getExpr();


            if (accumulateFunctionParameter instanceof BinaryExpr) {

                final DrlxParseResult parseResult = new ConstraintParser(context, packageModel).drlxParse(Object.class, bindingId, accumulateFunctionParameterStr);

                newBinding = parseResult.acceptWithReturnValue(new ParseResultVisitor<Optional<AccumulateVisitorPatternDSL.NewBinding>>() {
                    @Override
                    public Optional<AccumulateVisitorPatternDSL.NewBinding> onSuccess(DrlxParseSuccess drlxParseResult) {
                        SingleDrlxParseSuccess singleResult = (SingleDrlxParseSuccess) drlxParseResult;
                        Class<?> exprRawClass = singleResult.getExprRawClass();
                        final Optional<AccumulateFunction> optAccumulateFunction = AccumulateVisitor.this.getAccumulateFunction(function, exprRawClass);
                        if(!optAccumulateFunction.isPresent()) {
                            addNonExistingFunctionError(context, function);
                            return Optional.empty();
                        }

                        final AccumulateFunction accumulateFunction = optAccumulateFunction.get();
                        validateAccFunctionTypeAgainstPatternType(context, basePattern, accumulateFunction);
                        final String bindExpressionVariable = context.getExprId(accumulateFunction.getResultType(), singleResult.getLeft().toString());

                        singleResult.setExprBinding(bindExpressionVariable);

                        context.addDeclarationReplacing(new DeclarationSpec(singleResult.getPatternBinding(), exprRawClass));

                        functionDSL.addArgument( createAccSupplierExpr( accumulateFunction ) );
                        final MethodCallExpr newBindingFromBinary = AccumulateVisitor.this.buildBinding(bindExpressionVariable, singleResult.getUsedDeclarations(), singleResult.getExpr());
                        context.addDeclarationReplacing(new DeclarationSpec(bindExpressionVariable, exprRawClass));
                        functionDSL.addArgument(context.getVarExpr(bindExpressionVariable));
                        return Optional.of(new AccumulateVisitorPatternDSL.NewBinding(Optional.empty(), newBindingFromBinary));
                    }

                    @Override
                    public Optional<AccumulateVisitorPatternDSL.NewBinding> onFail(DrlxParseFail failure) {
                        return Optional.empty();
                    }
                });
            } else if (accumulateFunctionParameter.isMethodCallExpr() || accumulateFunctionParameter.isArrayAccessExpr() || accumulateFunctionParameter.isFieldAccessExpr()) {

                final Expression parameterConverted;
                if(accumulateFunctionParameter.isArrayAccessExpr()) {
                    parameterConverted = new ExpressionTyper(context, Object.class, null, false)
                            .toTypedExpression(accumulateFunctionParameter)
                    .getTypedExpression().orElseThrow(() -> new RuntimeException("Cannot convert expression to method call"  + accumulateFunctionParameter))
                    .getExpression();
                } else if(accumulateFunctionParameter.isMethodCallExpr()){
                    parameterConverted = DrlxParseUtil.sanitizeDrlNameExpr((MethodCallExpr) accumulateFunctionParameter);
                } else if(accumulateFunctionParameter.isFieldAccessExpr()){
                    parameterConverted = new ExpressionTyper(context, Object.class, null, false)
                            .toTypedExpression(accumulateFunctionParameter)
                            .getTypedExpression().orElseThrow(() -> new RuntimeException("Cannot convert expression to method call"  + accumulateFunctionParameter))
                            .getExpression();
                } else {
                    parameterConverted = accumulateFunctionParameter;
                }

                final DrlxParseUtil.RemoveRootNodeResult methodCallWithoutRootNode = DrlxParseUtil.removeRootNode(parameterConverted);

                final String rootNodeName = getRootNodeName(methodCallWithoutRootNode);

                final TypedExpression typedExpression = parseMethodCallType(context, rootNodeName, methodCallWithoutRootNode.getWithoutRootNode());
                final Class<?> methodCallExprType = typedExpression.getRawClass();

                final Optional<AccumulateFunction> optAccumulateFunction = getAccumulateFunction(function, methodCallExprType);
                if(!optAccumulateFunction.isPresent()) {
                    addNonExistingFunctionError(context, function);
                    return Optional.empty();
                }

                final AccumulateFunction accumulateFunction = optAccumulateFunction.get();
                validateAccFunctionTypeAgainstPatternType(context, basePattern, accumulateFunction);
                functionDSL.addArgument( createAccSupplierExpr( accumulateFunction ) );

                // Every expression in an accumulate function gets transformed in a bind expression with a generated id
                // Then the accumulate function will have that binding expression as a source
                final Class accumulateFunctionResultType = accumulateFunction.getResultType();
                final String bindExpressionVariable = context.getExprId(accumulateFunctionResultType, typedExpression.toString());

                final DrlxParseResult drlxParseResult = new ConstraintParser(context, context.getPackageModel()).drlxParse(Object.class, rootNodeName, printConstraint(parameterConverted));

                newBinding = drlxParseResult.acceptWithReturnValue(new ParseResultVisitor<Optional<NewBinding>>() {
                       @Override
                       public Optional<NewBinding> onSuccess(DrlxParseSuccess result) {
                           SingleDrlxParseSuccess singleResult = (SingleDrlxParseSuccess) drlxParseResult;
                           singleResult.setExprBinding(bindExpressionVariable);
                           final MethodCallExpr binding = expressionBuilder.buildBinding(singleResult);
                           context.addDeclarationReplacing(new DeclarationSpec(bindExpressionVariable, methodCallExprType));
                           functionDSL.addArgument(context.getVarExpr(bindExpressionVariable));

                           context.addDeclarationReplacing(new DeclarationSpec(bindingId, accumulateFunctionResultType));
                           return Optional.of(new NewBinding(Optional.of(singleResult.getPatternBinding()), binding));
                       }

                       @Override
                       public Optional<NewBinding> onFail(DrlxParseFail failure) {
                           return Optional.empty();
                       }
                   }
                );

            } else if (accumulateFunctionParameter instanceof DrlNameExpr) {
                final Class<?> declarationClass = context
                        .getDeclarationById(printConstraint(accumulateFunctionParameter))
                        .orElseThrow(RuntimeException::new)
                        .getDeclarationClass();

                final String nameExpr = ((DrlNameExpr) accumulateFunctionParameter).getName().asString();
                final Optional<AccumulateFunction> optAccumulateFunction = getAccumulateFunction(function, declarationClass);
                if(!optAccumulateFunction.isPresent()) {
                    addNonExistingFunctionError(context, function);
                    return Optional.empty();
                }

                final AccumulateFunction accumulateFunction = optAccumulateFunction.get();
                validateAccFunctionTypeAgainstPatternType(context, basePattern, accumulateFunction);
                functionDSL.addArgument( createAccSupplierExpr( accumulateFunction ) );
                functionDSL.addArgument(context.getVarExpr(nameExpr));

                addBindingAsDeclaration(context, bindingId, accumulateFunction);
            } else if (accumulateFunctionParameter instanceof LiteralExpr) {
                final Class<?> declarationClass = getLiteralExpressionType((LiteralExpr) accumulateFunctionParameter);

                final Optional<AccumulateFunction> optAccumulateFunction = getAccumulateFunction(function, declarationClass);
                if(!optAccumulateFunction.isPresent()) {
                    addNonExistingFunctionError(context, function);
                    return Optional.empty();
                }

                final AccumulateFunction accumulateFunction = optAccumulateFunction.get();
                validateAccFunctionTypeAgainstPatternType(context, basePattern, accumulateFunction);
                functionDSL.addArgument( createAccSupplierExpr( accumulateFunction ) );
                functionDSL.addArgument(new MethodCallExpr(null, VALUE_OF_CALL, NodeList.nodeList(accumulateFunctionParameter)));

                addBindingAsDeclaration(context, bindingId, accumulateFunction);
            } else {
                context.addCompilationError(new InvalidExpressionErrorResult("Invalid expression " + accumulateFunctionParameterStr));
                return Optional.empty();
            }
        }

        if (bindingId != null) {
            final MethodCallExpr asDSL = new MethodCallExpr( functionDSL, BIND_AS_CALL );
            asDSL.addArgument( context.getVarExpr( bindingId ) );
            accumulateDSL.addArgument( asDSL );
        }

        context.popExprPointer();
        return newBinding;
    }

    private static MethodReferenceExpr createAccSupplierExpr( AccumulateFunction accumulateFunction ) {
        NameExpr nameExpr = new NameExpr( accumulateFunction.getClass().getCanonicalName() );
        return new MethodReferenceExpr(nameExpr, new NodeList<>(), "new" );
    }

    private void validateAccFunctionTypeAgainstPatternType(RuleContext context, PatternDescr basePattern, AccumulateFunction accumulateFunction) {
        final String expectedResultTypeString = basePattern.getObjectType();
        final Class<?> expectedResultType = DrlxParseUtil.getClassFromType(context.getTypeResolver(), DrlxParseUtil.toClassOrInterfaceType(expectedResultTypeString));
        final Class actualResultType = accumulateFunction.getResultType();

        final boolean isJavaDialect = context.getRuleDialect().equals(RuleContext.RuleDialect.JAVA);
        final boolean isQuery = context.isQuery();
        final boolean isCollectFunction = isCollectFunction(accumulateFunction);
        final boolean isInsideAccumulate = ((AccumulateDescr)basePattern.getSource()).getInput() instanceof AndDescr;
        final boolean checkCollect = !isCollectFunction || isInsideAccumulate;

        if( !isQuery && checkCollect && isJavaDialect && !Pattern.isCompatibleWithAccumulateReturnType(expectedResultType, actualResultType)) {
            context.addCompilationError(new InvalidExpressionErrorResult(
                    String.format(
                            "Pattern of type: '[ClassObjectType class=%s]' " +
                                    "on rule '%s' " +
                                    "is not compatible with type %s returned by accumulate function."
                                    , expectedResultType.getCanonicalName(), context.getRuleName(), actualResultType.getCanonicalName())));
        }
    }

    private boolean isCollectFunction(AccumulateFunction accumulateFunction) {
        return accumulateFunction instanceof CollectListAccumulateFunction ||
                accumulateFunction instanceof CollectSetAccumulateFunction ||
                accumulateFunction instanceof CollectAccumulator;
    }

    private void addNonExistingFunctionError(RuleContext context, AccumulateDescr.AccumulateFunctionCallDescr function) {
        context.addCompilationError(new InvalidExpressionErrorResult(String.format("Unknown accumulate function: '%s' on rule '%s'.", function.getFunction(), context.getRuleDescr().getName())));
    }

    private void addBindingAsDeclaration(RuleContext context, String bindingId, AccumulateFunction accumulateFunction) {
        if (bindingId != null) {
            Class accumulateFunctionResultType = accumulateFunction.getResultType();
            context.addDeclarationReplacing(new DeclarationSpec(bindingId, accumulateFunctionResultType));
        }
    }

    Optional<AccumulateFunction> getAccumulateFunction(AccumulateDescr.AccumulateFunctionCallDescr function, Class<?> methodCallExprType) {
        final String accumulateFunctionName = AccumulateUtil.getFunctionName(() -> methodCallExprType, function.getFunction());
        final Optional<AccumulateFunction> bundledAccumulateFunction = Optional.ofNullable(packageModel.getConfiguration().getAccumulateFunction(accumulateFunctionName));
        final Optional<AccumulateFunction> importedAccumulateFunction = Optional.ofNullable(packageModel.getAccumulateFunctions().get(accumulateFunctionName));

        return bundledAccumulateFunction
                .map(Optional::of)
                .orElse(importedAccumulateFunction);
    }

    String getRootNodeName(DrlxParseUtil.RemoveRootNodeResult methodCallWithoutRootNode) {
        final Expression rootNode = methodCallWithoutRootNode.getRootNode().orElseThrow(UnsupportedOperationException::new);

        final String rootNodeName;
        if (rootNode instanceof NameExpr) {
            rootNodeName = ((NameExpr) rootNode).getName().asString();
        } else {
            throw new RuntimeException("Root node of expression should be a declaration");
        }
        return rootNodeName;
    }

    TypedExpression parseMethodCallType(RuleContext context, String variableName, Expression methodCallWithoutRoot) {
        final Class clazz = context.getDeclarationById(variableName)
                .map(DeclarationSpec::getDeclarationClass)
                .orElseThrow(RuntimeException::new);

        return DrlxParseUtil.toMethodCallWithClassCheck(context, methodCallWithoutRoot, null, clazz, context.getTypeResolver());
    }

    Expression buildConstraintExpression(Expression expr, Collection<String> usedDeclarations) {
        LambdaExpr lambdaExpr = new LambdaExpr();
        lambdaExpr.setEnclosingParameters(true);
        usedDeclarations.stream().map(s -> new Parameter(new UnknownType(), s)).forEach(lambdaExpr::addParameter);
        lambdaExpr.setBody(new ExpressionStmt(expr));
        return lambdaExpr;
    }


    private BlockStmt parseBlockAddSemicolon(String block) {
        return StaticJavaParser.parseBlock(String.format("{%s}", addSemicolon(block)));
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
    /*
        Since accumulate are always relative to the Pattern, it may happen that the declaration inside the accumulate
        was already se  t in the relative Pattern.
        Here though the type is more precise as it checks the result type Accumulate Function, so we use
        addDeclarationReplacing instead of addDeclaration to overwrite the previous declaration.
     */

    protected abstract MethodCallExpr buildBinding(String bindingName, Collection<String> usedDeclaration, Expression expression);

    protected abstract void processNewBinding(Optional<NewBinding> optNewBinding);

    protected abstract void postVisit();

    static class NewBinding {

        Optional<String> patternBinding;
        MethodCallExpr bindExpression;

        NewBinding(Optional<String> patternBinding, MethodCallExpr bindExpression) {
            this.patternBinding = patternBinding;
            this.bindExpression = bindExpression;
        }
    }
}
