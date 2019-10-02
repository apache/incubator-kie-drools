package org.drools.modelcompiler.builder.generator.visitor.accumulate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
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
    AbstractExpressionBuilder expressionBuilder;

    AccumulateVisitor(RuleContext context, ModelGeneratorVisitor modelGeneratorVisitor, PackageModel packageModel) {
        this.context = context;
        this.modelGeneratorVisitor = modelGeneratorVisitor;
        this.packageModel = packageModel;
    }

    protected BaseDescr input;
    private PatternDescr basePattern;
    private AccumulateDescr descr;

    Optional<NewBinding> optNewBinding;

    public void visit(AccumulateDescr descr, PatternDescr basePattern) {
        this.basePattern = basePattern;
        this.descr = descr;

        final MethodCallExpr accumulateDSL = new MethodCallExpr(null, ACCUMULATE_CALL);
        context.addExpression(accumulateDSL);
        final MethodCallExpr accumulateExprs = new MethodCallExpr(null, AND_CALL);
        accumulateDSL.addArgument(accumulateExprs);

        context.pushExprPointer(accumulateExprs::addArgument);

        Set<String> externalDeclrs = new HashSet<>(context.getAvailableBindings());
        input = descr.getInputPattern() == null ? descr.getInput() : descr.getInputPattern();
        input.accept(modelGeneratorVisitor);

        if (accumulateExprs.getArguments().isEmpty()) {
            accumulateDSL.remove(accumulateExprs);
        } else if (accumulateExprs.getArguments().size() == 1) {
            accumulateDSL.setArgument(0, accumulateExprs.getArguments().get(0));
        }

        if (!descr.getFunctions().isEmpty()) {
            if (validateBindings()) {
                return;
            }
            classicAccumulate(accumulateDSL);
        } else if (descr.getFunctions().isEmpty() && descr.getInitCode() != null) {
            new AccumulateInlineVisitor(context, packageModel).inlineAccumulate(descr, basePattern, accumulateDSL, externalDeclrs, input);
        } else {
            throw new UnsupportedOperationException("Unknown type of Accumulate.");
        }

        context.popExprPointer();
        postVisit();
    }

    private void classicAccumulate(MethodCallExpr accumulateDSL) {
        for (AccumulateDescr.AccumulateFunctionCallDescr function : descr.getFunctions()) {
            try {
                visit(function, accumulateDSL);
            } catch (AccumulateNonExistingFunction e) {
                addNonExistingFunctionError(context, e.function);
                return;
            } catch (AccumulateParsingFailedException e) {
                context.addCompilationError(new InvalidExpressionErrorResult(e.getMessage()));
                return;
            }
            processNewBinding(accumulateDSL);
        }
    }

    private boolean validateBindings() {
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

    protected void visit(AccumulateDescr.AccumulateFunctionCallDescr function, MethodCallExpr accumulateDSL) {

        context.pushExprPointer(accumulateDSL::addArgument);

        final MethodCallExpr functionDSL = new MethodCallExpr(null, ACC_FUNCTION_CALL);

        final String optBindingId = ofNullable(function.getBind()).orElse(basePattern.getIdentifier());
        final String bindingId = ofNullable(optBindingId).orElse(context.getOrCreateAccumulatorBindingId(function.getFunction()));

        optNewBinding = Optional.empty();

        if (function.getParams().length == 0) {
            final AccumulateFunction optAccumulateFunction = getAccumulateFunction(function, Object.class);
            zeroParameterFunction(functionDSL, bindingId, optAccumulateFunction);
        } else {
            parseFirstParameter(function, functionDSL, bindingId);
        }

        if (bindingId != null) {
            final MethodCallExpr asDSL = new MethodCallExpr(functionDSL, BIND_AS_CALL);
            asDSL.addArgument(context.getVarExpr(bindingId));
            accumulateDSL.addArgument(asDSL);
        }

        context.popExprPointer();
    }

    private void parseFirstParameter(AccumulateDescr.AccumulateFunctionCallDescr function, MethodCallExpr functionDSL, String bindingId) {
        final String accumulateFunctionParameterStr = function.getParams()[0];
        final Expression accumulateFunctionParameter = DrlxParseUtil.parseExpression(accumulateFunctionParameterStr).getExpr();

        if (accumulateFunctionParameter instanceof BinaryExpr) {
            optNewBinding = binaryExprParameter(function, functionDSL, bindingId, accumulateFunctionParameterStr);
        } else if (parameterNeedsConvertionToMethodCallExpr(accumulateFunctionParameter)) {
            methodCallExprParameter(function, functionDSL, bindingId, accumulateFunctionParameter);
        } else if (accumulateFunctionParameter instanceof DrlNameExpr) {
            nameExprParameter(function, functionDSL, bindingId, accumulateFunctionParameter);
        } else if (accumulateFunctionParameter instanceof LiteralExpr) {
            literalExprParameter(function, functionDSL, bindingId, accumulateFunctionParameter);
        } else {
            context.addCompilationError(new InvalidExpressionErrorResult("Invalid expression " + accumulateFunctionParameterStr));
            throw new AccumulateParsingFailedException();
        }
    }

    private void literalExprParameter(AccumulateDescr.AccumulateFunctionCallDescr function, MethodCallExpr functionDSL, String bindingId, Expression accumulateFunctionParameter) {
        final Class<?> declarationClass = getLiteralExpressionType((LiteralExpr) accumulateFunctionParameter);

        AccumulateFunction accumulateFunction = getAccumulateFunction(function, declarationClass);

        validateAccFunctionTypeAgainstPatternType(context, basePattern, accumulateFunction);
        functionDSL.addArgument(createAccSupplierExpr(accumulateFunction));
        functionDSL.addArgument(new MethodCallExpr(null, VALUE_OF_CALL, NodeList.nodeList(accumulateFunctionParameter)));

        addBindingAsDeclaration(context, bindingId, accumulateFunction);
    }

    private void nameExprParameter(AccumulateDescr.AccumulateFunctionCallDescr function, MethodCallExpr functionDSL, String bindingId, Expression accumulateFunctionParameter) {
        final Class<?> declarationClass = context
                .getDeclarationById(printConstraint(accumulateFunctionParameter))
                .orElseThrow(RuntimeException::new)
                .getDeclarationClass();

        final String nameExpr = ((DrlNameExpr) accumulateFunctionParameter).getName().asString();
        AccumulateFunction accumulateFunction = getAccumulateFunction(function, declarationClass);

        validateAccFunctionTypeAgainstPatternType(context, basePattern, accumulateFunction);
        functionDSL.addArgument(createAccSupplierExpr(accumulateFunction));
        functionDSL.addArgument(context.getVarExpr(nameExpr));

        addBindingAsDeclaration(context, bindingId, accumulateFunction);
    }

    private void methodCallExprParameter(AccumulateDescr.AccumulateFunctionCallDescr function, MethodCallExpr functionDSL, String bindingId, Expression accumulateFunctionParameter) {
        final Expression parameterConverted = convertParameter(accumulateFunctionParameter);
        final DrlxParseUtil.RemoveRootNodeResult methodCallWithoutRootNode = DrlxParseUtil.removeRootNode(parameterConverted);
        final String rootNodeName = getRootNodeName(methodCallWithoutRootNode);

        final TypedExpression typedExpression = parseMethodCallType(context, rootNodeName, methodCallWithoutRootNode.getWithoutRootNode());
        final Class<?> methodCallExprType = typedExpression.getRawClass();

        final AccumulateFunction accumulateFunction = getAccumulateFunction(function, methodCallExprType);

        validateAccFunctionTypeAgainstPatternType(context, basePattern, accumulateFunction);
        functionDSL.addArgument(createAccSupplierExpr(accumulateFunction));

        // Every expression in an accumulate function gets transformed in a bind expression with a generated id
        // Then the accumulate function will have that binding expression as a source
        final Class accumulateFunctionResultType = accumulateFunction.getResultType();
        final String bindExpressionVariable = context.getExprId(accumulateFunctionResultType, typedExpression.toString());

        final DrlxParseResult drlxParseResult = new ConstraintParser(context, context.getPackageModel()).drlxParse(Object.class, rootNodeName, printConstraint(parameterConverted));

        optNewBinding = drlxParseResult.acceptWithReturnValue(new ParseResultVisitor<Optional<NewBinding>>() {
                                                                  @Override
                                                                  public Optional<NewBinding> onSuccess(DrlxParseSuccess result) {
                                                                      SingleDrlxParseSuccess singleResult = (SingleDrlxParseSuccess) drlxParseResult;
                                                                      singleResult.setExprBinding(bindExpressionVariable);
                                                                      final MethodCallExpr binding = expressionBuilder.buildBinding(singleResult);
                                                                      context.addDeclarationReplacing(new DeclarationSpec(bindExpressionVariable, methodCallExprType));
                                                                      functionDSL.addArgument(context.getVarExpr(bindExpressionVariable));

                                                                      context.addDeclarationReplacing(new DeclarationSpec(bindingId, accumulateFunctionResultType));
                                                                      List<String> ids = new ArrayList<>();
                                                                      ids.add(singleResult.getPatternBinding());
                                                                      if (input instanceof PatternDescr) {
                                                                          ids.add(((PatternDescr) input).getIdentifier());
                                                                      }
                                                                      return Optional.of(new NewBinding(ids, binding));
                                                                  }

                                                                  @Override
                                                                  public Optional<NewBinding> onFail(DrlxParseFail failure) {
                                                                      return Optional.empty();
                                                                  }
                                                              }
        );
    }

    private Expression convertParameter(Expression accumulateFunctionParameter) {
        final Expression parameterConverted;
        if (accumulateFunctionParameter.isArrayAccessExpr()) {
            parameterConverted = new ExpressionTyper(context, Object.class, null, false)
                    .toTypedExpression(accumulateFunctionParameter)
                    .getTypedExpression().orElseThrow(() -> new RuntimeException("Cannot convert expression to method call" + accumulateFunctionParameter))
                    .getExpression();
        } else if (accumulateFunctionParameter.isMethodCallExpr()) {
            parameterConverted = DrlxParseUtil.sanitizeDrlNameExpr((MethodCallExpr) accumulateFunctionParameter);
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

    private Optional<NewBinding> binaryExprParameter(AccumulateDescr.AccumulateFunctionCallDescr function, MethodCallExpr functionDSL, String bindingId, String accumulateFunctionParameterStr) {
        final DrlxParseResult parseResult = new ConstraintParser(context, packageModel).drlxParse(Object.class, bindingId, accumulateFunctionParameterStr);

        optNewBinding = parseResult.acceptWithReturnValue(new ParseResultVisitor<Optional<NewBinding>>() {
            @Override
            public Optional<NewBinding> onSuccess(DrlxParseSuccess drlxParseResult) {
                SingleDrlxParseSuccess singleResult = (SingleDrlxParseSuccess) drlxParseResult;
                Class<?> exprRawClass = singleResult.getExprRawClass();
                AccumulateFunction accumulateFunction = getAccumulateFunction(function, exprRawClass);

                validateAccFunctionTypeAgainstPatternType(context, basePattern, accumulateFunction);
                final String bindExpressionVariable = context.getExprId(accumulateFunction.getResultType(), singleResult.getLeft().toString());

                singleResult.setExprBinding(bindExpressionVariable);

                context.addDeclarationReplacing(new DeclarationSpec(singleResult.getPatternBinding(), exprRawClass));

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

    private void zeroParameterFunction(MethodCallExpr functionDSL, String bindingId, AccumulateFunction accumulateFunction) {
        validateAccFunctionTypeAgainstPatternType(context, basePattern, accumulateFunction);
        functionDSL.addArgument(createAccSupplierExpr(accumulateFunction));
        Class accumulateFunctionResultType = accumulateFunction.getResultType();
        context.addDeclarationReplacing(new DeclarationSpec(bindingId, accumulateFunctionResultType));
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

    private TypedExpression parseMethodCallType(RuleContext context, String variableName, Expression methodCallWithoutRoot) {
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

    abstract void processNewBinding(MethodCallExpr accumulateDSL);

    protected abstract void postVisit();

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
