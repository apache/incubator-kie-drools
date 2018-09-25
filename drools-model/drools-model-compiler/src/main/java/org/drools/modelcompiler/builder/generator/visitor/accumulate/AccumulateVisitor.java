package org.drools.modelcompiler.builder.generator.visitor.accumulate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.rule.builder.util.AccumulateUtil;
import org.drools.core.base.accumulators.CollectAccumulator;
import org.drools.core.base.accumulators.CollectListAccumulateFunction;
import org.drools.core.base.accumulators.CollectSetAccumulateFunction;
import org.drools.core.rule.Pattern;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.CompilationUnit;
import org.drools.javaparser.ast.Modifier;
import org.drools.javaparser.ast.NodeList;
import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.javaparser.ast.body.MethodDeclaration;
import org.drools.javaparser.ast.body.Parameter;
import org.drools.javaparser.ast.body.VariableDeclarator;
import org.drools.javaparser.ast.expr.AssignExpr;
import org.drools.javaparser.ast.expr.BinaryExpr;
import org.drools.javaparser.ast.expr.ClassExpr;
import org.drools.javaparser.ast.expr.EnclosedExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.FieldAccessExpr;
import org.drools.javaparser.ast.expr.LambdaExpr;
import org.drools.javaparser.ast.expr.LiteralExpr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.VariableDeclarationExpr;
import org.drools.javaparser.ast.stmt.BlockStmt;
import org.drools.javaparser.ast.stmt.ExpressionStmt;
import org.drools.javaparser.ast.stmt.ReturnStmt;
import org.drools.javaparser.ast.stmt.Statement;
import org.drools.javaparser.ast.type.Type;
import org.drools.javaparser.ast.type.UnknownType;
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
import org.drools.modelcompiler.builder.generator.expressiontyper.TypedExpressionResult;
import org.drools.modelcompiler.builder.generator.visitor.ModelGeneratorVisitor;
import org.drools.modelcompiler.util.StringUtil;
import org.kie.api.runtime.rule.AccumulateFunction;

import static java.util.stream.Collectors.toList;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.forceCastForName;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getLiteralExpressionType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.rescopeNamesToNewScope;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.validateDuplicateBindings;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.ACCUMULATE_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.ACC_FUNCTION_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.AND_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.BIND_AS_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.VALUE_OF_CALL;

public abstract class AccumulateVisitor {

    protected final RuleContext context;
    protected final PackageModel packageModel;
    protected final ModelGeneratorVisitor modelGeneratorVisitor;
    protected AbstractExpressionBuilder expressionBuilder;

    public AccumulateVisitor(RuleContext context, ModelGeneratorVisitor modelGeneratorVisitor, PackageModel packageModel) {
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
            boolean inputPatternHasConstraints = (input instanceof PatternDescr) && (!((PatternDescr) input).getConstraint().getDescrs().isEmpty());

            final List<String> allBindings = descr
                    .getFunctions()
                    .stream()
                    .map(f -> f.getBind())
                    .filter(Objects::nonNull)
                    .collect(toList());

            final Optional<InvalidExpressionErrorResult> invalidExpressionErrorResult = validateDuplicateBindings(context.getRuleName(), allBindings);
            invalidExpressionErrorResult.ifPresent(context::addCompilationError);
            if(invalidExpressionErrorResult.isPresent()) {
                return;
            }

            for (AccumulateDescr.AccumulateFunctionCallDescr function : descr.getFunctions()) {
                final Optional<NewBinding> optNewBinding = visit(context, function, accumulateDSL, basePattern, inputPatternHasConstraints);
                processNewBinding(optNewBinding);
            }
        } else if (descr.getFunctions().isEmpty() && descr.getInitCode() != null) {
            // LEGACY: Accumulate with inline custom code
            if (input instanceof PatternDescr) {
                visitAccInlineCustomCode(context, descr, accumulateDSL, basePattern, (PatternDescr) input, externalDeclrs);
            } else if (input instanceof AndDescr) {

                BlockStmt actionBlock = parseBlock(descr.getActionCode());
                Collection<String> allNamesInActionBlock = collectNamesInBlock(context, actionBlock);

                final Optional<BaseDescr> bindingUsedInAccumulate = ((AndDescr) input).getDescrs().stream().filter(b -> {
                    return allNamesInActionBlock.contains(((PatternDescr) b).getIdentifier());
                }).findFirst();

                bindingUsedInAccumulate.ifPresent(b -> visitAccInlineCustomCode(context, descr, accumulateDSL, basePattern, (PatternDescr) b, externalDeclrs));
            } else {
                throw new UnsupportedOperationException("I was expecting input to be of type PatternDescr. " + input);
            }
        } else {
            throw new UnsupportedOperationException("Unknown type of Accumulate.");
        }

        context.popExprPointer();
        postVisit();
    }

    protected Optional<AccumulateVisitorPatternDSL.NewBinding> visit(RuleContext context, AccumulateDescr.AccumulateFunctionCallDescr function, MethodCallExpr accumulateDSL, PatternDescr basePattern, boolean inputPatternHasConstraints) {

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
            functionDSL.addArgument(new ClassExpr(toType(accumulateFunction.getClass())));
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

                        functionDSL.addArgument(new ClassExpr(toType(accumulateFunction.getClass())));
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
            } else if (accumulateFunctionParameter.isMethodCallExpr() || accumulateFunctionParameter.isArrayAccessExpr()) {

                final Expression parameterConverted;
                if(accumulateFunctionParameter.isArrayAccessExpr()) {
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
                final Class accumulateFunctionResultType = accumulateFunction.getResultType();
                functionDSL.addArgument(new ClassExpr(toType(accumulateFunction.getClass())));

                // Every expression in an accumulate function gets transformed in a bind expression with a generated id
                // Then the accumulate function will have that binding expression as a source
                final String bindExpressionVariable = context.getExprId(accumulateFunctionResultType, typedExpression.toString());

                final DrlxParseResult drlxParseResult = new ConstraintParser(context, context.getPackageModel()).drlxParse(Object.class, rootNodeName, accumulateFunctionParameterStr);

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

            } else if (accumulateFunctionParameter instanceof NameExpr ) {
                final Class<?> declarationClass = context
                        .getDeclarationById(accumulateFunctionParameter.toString())
                        .orElseThrow(RuntimeException::new)
                        .getDeclarationClass();

                final String nameExpr = ((NameExpr) accumulateFunctionParameter).getName().asString();
                final Optional<AccumulateFunction> optAccumulateFunction = getAccumulateFunction(function, declarationClass);
                if(!optAccumulateFunction.isPresent()) {
                    addNonExistingFunctionError(context, function);
                    return Optional.empty();
                }
                final AccumulateFunction accumulateFunction = optAccumulateFunction.get();
                validateAccFunctionTypeAgainstPatternType(context, basePattern, accumulateFunction);
                functionDSL.addArgument(new ClassExpr(toType(accumulateFunction.getClass())));
                functionDSL.addArgument(context.getVarExpr(nameExpr));

                addBindingAsDeclaration(context, bindingId, declarationClass, accumulateFunction);
            } else if (accumulateFunctionParameter instanceof LiteralExpr) {
                final Class<?> declarationClass = getLiteralExpressionType((LiteralExpr) accumulateFunctionParameter);

                final Optional<AccumulateFunction> optAccumulateFunction = getAccumulateFunction(function, declarationClass);
                if(!optAccumulateFunction.isPresent()) {
                    addNonExistingFunctionError(context, function);
                    return Optional.empty();
                }

                final AccumulateFunction accumulateFunction = optAccumulateFunction.get();
                validateAccFunctionTypeAgainstPatternType(context, basePattern, accumulateFunction);
                functionDSL.addArgument(new ClassExpr(toType(accumulateFunction.getClass())));
                functionDSL.addArgument(new MethodCallExpr(null, VALUE_OF_CALL, NodeList.nodeList(accumulateFunctionParameter)));

                addBindingAsDeclaration(context, bindingId, declarationClass, accumulateFunction);
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

    private void addBindingAsDeclaration(RuleContext context, String bindingId, Class<?> declarationClass, AccumulateFunction accumulateFunction) {
        if (bindingId != null) {
            Class accumulateFunctionResultType = accumulateFunction.getResultType();
            if ((accumulateFunctionResultType == Comparable.class || accumulateFunctionResultType == Number.class) &&
                    (Comparable.class.isAssignableFrom(declarationClass) || declarationClass.isPrimitive())) {
                accumulateFunctionResultType = declarationClass;
            }
            context.addDeclarationReplacing(new DeclarationSpec(bindingId, accumulateFunctionResultType));
        }
    }

    protected Optional<AccumulateFunction> getAccumulateFunction(AccumulateDescr.AccumulateFunctionCallDescr function, Class<?> methodCallExprType) {
        final String accumulateFunctionName = AccumulateUtil.getFunctionName(() -> methodCallExprType, function.getFunction());
        final Optional<AccumulateFunction> bundledAccumulateFunction = Optional.ofNullable(packageModel.getConfiguration().getAccumulateFunction(accumulateFunctionName));
        final Optional<AccumulateFunction> importedAccumulateFunction = Optional.ofNullable(packageModel.getAccumulateFunctions().get(accumulateFunctionName));

        return bundledAccumulateFunction
                .map(Optional::of)
                .orElse(importedAccumulateFunction);
    }

    protected String getRootNodeName(DrlxParseUtil.RemoveRootNodeResult methodCallWithoutRootNode) {
        final Expression rootNode = methodCallWithoutRootNode.getRootNode().orElseThrow(UnsupportedOperationException::new);

        final String rootNodeName;
        if (rootNode instanceof NameExpr) {
            rootNodeName = ((NameExpr) rootNode).getName().asString();
        } else {
            throw new RuntimeException("Root node of expression should be a declaration");
        }
        return rootNodeName;
    }

    protected TypedExpression parseMethodCallType(RuleContext context, String variableName, Expression methodCallWithoutRoot) {
        final Class clazz = context.getDeclarationById(variableName)
                .map(DeclarationSpec::getDeclarationClass)
                .orElseThrow(RuntimeException::new);

        return DrlxParseUtil.toMethodCallWithClassCheck(context, methodCallWithoutRoot, null, clazz, context.getTypeResolver());
    }

    protected Expression buildConstraintExpression(Expression expr, Collection<String> usedDeclarations) {
        LambdaExpr lambdaExpr = new LambdaExpr();
        lambdaExpr.setEnclosingParameters(true);
        usedDeclarations.stream().map(s -> new Parameter(new UnknownType(), s)).forEach(lambdaExpr::addParameter);
        lambdaExpr.setBody(new ExpressionStmt(expr));
        return lambdaExpr;
    }

    /**
     * By design this legacy accumulate (with inline custome code) visitor supports only with 1-and-only binding in the accumulate code/expressions.
     */
    protected void visitAccInlineCustomCode(RuleContext context2, AccumulateDescr descr, MethodCallExpr accumulateDSL, PatternDescr basePattern, PatternDescr inputDescr, Set<String> externalDeclrs) {
        context.pushExprPointer(accumulateDSL::addArgument);

        String targetClassName = StringUtil.toId(context2.getRuleDescr().getName()) + "Accumulate" + descr.getLine();
        String code = ACCUMULATE_INLINE_FUNCTION.replaceAll("AccumulateInlineFunction", targetClassName);

        CompilationUnit templateCU = JavaParser.parse(code);
        ClassOrInterfaceDeclaration templateClass = templateCU.getClassByName(targetClassName).orElseThrow(() -> new RuntimeException("Template did not contain expected type definition."));
        ClassOrInterfaceDeclaration templateContextClass = templateClass.getMembers().stream().filter(m -> m instanceof ClassOrInterfaceDeclaration && ((ClassOrInterfaceDeclaration) m).getNameAsString().equals("ContextData")).map(ClassOrInterfaceDeclaration.class::cast).findFirst().orElseThrow(() -> new RuntimeException("Template did not contain expected type definition."));

        List<String> contextFieldNames = new ArrayList<>();
        MethodDeclaration initMethod = templateClass.getMethodsByName("init").get(0);
        BlockStmt initBlock = JavaParser.parseBlock("{" + descr.getInitCode() + "}");
        List<DeclarationSpec> accumulateDeclarations = new ArrayList<>();
        Set<String> usedExtDeclrs = parseInitBlock( context2, descr, basePattern, templateContextClass, contextFieldNames, initMethod, initBlock, accumulateDeclarations );

        boolean useLegacyAccumulate = false;
        Type singleAccumulateType = null;
        MethodDeclaration accumulateMethod = templateClass.getMethodsByName("accumulate").get(0);
        BlockStmt actionBlock = parseBlock(descr.getActionCode());
        Collection<String> allNamesInActionBlock = collectNamesInBlock(context2, actionBlock);
        if (allNamesInActionBlock.size() == 1) {
            String nameExpr = allNamesInActionBlock.iterator().next();
            accumulateMethod.getParameter(1).setName(nameExpr);
            singleAccumulateType = context2.getDeclarationById(nameExpr).get().getType();
        } else {
            allNamesInActionBlock.removeIf( name -> !externalDeclrs.contains( name ) );
            usedExtDeclrs.addAll( allNamesInActionBlock );
            useLegacyAccumulate = true;
        }

        Optional<MethodDeclaration> optReverseMethod = Optional.empty();
        if(descr.getReverseCode() != null) {
            BlockStmt reverseBlock = parseBlock(descr.getReverseCode());
            Collection<String> allNamesInReverseBlock = collectNamesInBlock(context2, reverseBlock);
            if (allNamesInReverseBlock.size() == 1) {
                MethodDeclaration reverseMethod = templateClass.getMethodsByName("reverse").get(0);
                reverseMethod.getParameter(1).setName(allNamesInReverseBlock.iterator().next());
                optReverseMethod = Optional.of(reverseMethod);
            } else {
                allNamesInActionBlock.removeIf( name -> !externalDeclrs.contains( name ) );
                usedExtDeclrs.addAll( allNamesInActionBlock );
                useLegacyAccumulate = true;
            }
        }

        if ( useLegacyAccumulate || !usedExtDeclrs.isEmpty() ) {
            new LegacyAccumulate(context, descr, basePattern, usedExtDeclrs).build();
            return;
        }

        for(DeclarationSpec d : accumulateDeclarations) {
            context2.addDeclaration(d);
        }

        writeAccumulateMethod(contextFieldNames, singleAccumulateType, accumulateMethod, actionBlock);

        // <result expression>: this is a semantic expression in the selected dialect that is executed after all source objects are iterated.
        MethodDeclaration resultMethod = templateClass.getMethodsByName("getResult").get(0);
        Type returnExpressionType = JavaParser.parseType("java.lang.Object");
        Expression returnExpression = JavaParser.parseExpression(descr.getResultCode());
        if (returnExpression instanceof NameExpr) {
            returnExpression = new EnclosedExpr(returnExpression);
        }
        rescopeNamesToNewScope(new NameExpr("data"), contextFieldNames, returnExpression);
        resultMethod.getBody().get().addStatement(new ReturnStmt(returnExpression));
        MethodDeclaration getResultTypeMethod = templateClass.getMethodsByName("getResultType").get(0);
        getResultTypeMethod.getBody().get().addStatement(new ReturnStmt(new ClassExpr(returnExpressionType)));

        if (optReverseMethod.isPresent()) {
            MethodDeclaration supportsReverseMethod = templateClass.getMethodsByName("supportsReverse").get(0);
            supportsReverseMethod.getBody().get().addStatement(JavaParser.parseStatement("return true;"));

            BlockStmt reverseBlock = parseBlock(descr.getReverseCode());
            writeAccumulateMethod(contextFieldNames, singleAccumulateType, optReverseMethod.get(), reverseBlock);
        } else {
            MethodDeclaration supportsReverseMethod = templateClass.getMethodsByName("supportsReverse").get(0);
            supportsReverseMethod.getBody().get().addStatement(JavaParser.parseStatement("return false;"));

            MethodDeclaration reverseMethod = templateClass.getMethodsByName("reverse").get(0);
            reverseMethod.getBody().get().addStatement(JavaParser.parseStatement("throw new UnsupportedOperationException(\"This function does not support reverse.\");"));
        }

        // add resulting accumulator class into the package model
        this.packageModel.addGeneratedPOJO(templateClass);

        final MethodCallExpr functionDSL = new MethodCallExpr(null, ACC_FUNCTION_CALL);
        functionDSL.addArgument(new ClassExpr(JavaParser.parseType(targetClassName)));
        functionDSL.addArgument(context.getVarExpr(inputDescr.getIdentifier()));

        final String bindingId = basePattern.getIdentifier();
        final MethodCallExpr asDSL = new MethodCallExpr(functionDSL, BIND_AS_CALL);
        asDSL.addArgument( context.getVarExpr( bindingId ) );
        accumulateDSL.addArgument(asDSL);

        context.popExprPointer();
    }

    private Set<String> parseInitBlock( RuleContext context2, AccumulateDescr descr, PatternDescr basePattern, ClassOrInterfaceDeclaration templateContextClass, List<String> contextFieldNames, MethodDeclaration initMethod, BlockStmt initBlock, List<DeclarationSpec> accumulateDeclarations ) {
        Set<String> externalDeclrs = new HashSet<>();

        for (Statement stmt : initBlock.getStatements()) {
            final BlockStmt initMethodBody = initMethod.getBody().get();
            if (stmt instanceof ExpressionStmt && ((ExpressionStmt) stmt).getExpression() instanceof VariableDeclarationExpr ) {
                VariableDeclarationExpr vdExpr = (VariableDeclarationExpr) ((ExpressionStmt) stmt).getExpression();
                for (VariableDeclarator vd : vdExpr.getVariables()) {
                    final String variableName = vd.getNameAsString();
                    contextFieldNames.add(variableName);
                    templateContextClass.addField(vd.getType(), variableName, Modifier.PUBLIC);
                    createInitializer(variableName, vd.getInitializer()).ifPresent(statement -> {
                        initMethodBody.addStatement(statement);
                        statement.findAll(NameExpr.class).stream().map( n -> n.toString()).filter( context2::hasDeclaration ).forEach( externalDeclrs::add );
                    }
                    );
                    accumulateDeclarations.add(new DeclarationSpec(variableName, DrlxParseUtil.getClassFromContext(context2.getTypeResolver(), vd.getType().asString()) ));
                }
            } else {
                if(stmt.isExpressionStmt()) {
                    final Expression statementExpression = stmt.asExpressionStmt().getExpression();
                    if(statementExpression.isAssignExpr()) {
                        final AssignExpr assignExpr = statementExpression.asAssignExpr();
                        final String targetName = assignExpr.getTarget().asNameExpr().toString();
                        // Mvel allows using a field without declaration
                        if(!contextFieldNames.contains(targetName)) {
                            contextFieldNames.add(targetName);
                            final String variableName = assignExpr.getTarget().toString();
                            final Expression initCreationExpression = assignExpr.getValue();

                            final ExpressionTyper expressionTyper = new ExpressionTyper(context2, Object.class, "", false);
                            final TypedExpressionResult typedExpressionResult = expressionTyper.toTypedExpression(initCreationExpression);

                            final Type type = typedExpressionResult
                                    .getTypedExpression()
                                    .map(t -> DrlxParseUtil.classToReferenceType(t.getRawClass()))
                                    .orElseThrow(() -> new RuntimeException("Unknown type: " + initCreationExpression));

                            templateContextClass.addField(type, variableName, Modifier.PUBLIC);
                            final Optional<Statement> initializer = createInitializer(variableName, Optional.of(initCreationExpression));
                            initializer.ifPresent(initMethodBody::addStatement);
                            accumulateDeclarations.add(new DeclarationSpec(variableName, DrlxParseUtil.getClassFromContext(context2.getTypeResolver(), type.asString())));
                        }

                    }
                } else {
                    initMethodBody.addStatement(stmt); // add as-is.
                }
            }
        }

        return externalDeclrs;
    }

    private BlockStmt parseBlock(String block) {
        final String withTerminator = block.endsWith(";") ? block : block + ";";
        return JavaParser.parseBlock("{" + withTerminator + "}");
    }

    private Optional<Statement> createInitializer(String variableName, Optional<Expression> optInitializer) {
        if (optInitializer.isPresent()) {
            Expression initializer = optInitializer.get();
            Expression target = new FieldAccessExpr(new NameExpr("data"), variableName);
            Statement initStmt = new ExpressionStmt(new AssignExpr(target, initializer, AssignExpr.Operator.ASSIGN));
            return Optional.of(initStmt);
        }
        return Optional.empty();
    }

    void writeAccumulateMethod(List<String> contextFieldNames, Type singleAccumulateType, MethodDeclaration accumulateMethod, BlockStmt actionBlock) {
        for (Statement stmt : actionBlock.getStatements()) {
            final ExpressionStmt convertedExpressionStatement = new ExpressionStmt();
            for (ExpressionStmt eStmt : stmt.findAll(ExpressionStmt.class)) {
                final Expression expressionUntyped = eStmt.getExpression();
                final String parameterName = accumulateMethod.getParameter(1).getNameAsString();

                final ExpressionTyper expressionTyper = new ExpressionTyper(context, Object.class, "", false);
                final TypedExpressionResult typedExpression = expressionTyper.toTypedExpression(expressionUntyped);

                final Expression expression = typedExpression.getTypedExpression().get().getExpression();

                forceCastForName(parameterName, singleAccumulateType, expression);
                rescopeNamesToNewScope(new NameExpr("data"), contextFieldNames, expression);
                convertedExpressionStatement.setExpression(expression);
            }
            accumulateMethod.getBody().get().addStatement(convertedExpressionStatement);
        }
    }

    List<String> collectNamesInBlock(RuleContext context2, BlockStmt block) {
        return block.findAll(NameExpr.class, n -> {
            Optional<DeclarationSpec> optD = context2.getDeclarationById(n.getNameAsString());
            return optD.filter(d -> !d.isGlobal()).isPresent(); // Global aren't supported
        })
                .stream()
                .map(NameExpr::getNameAsString)
                .distinct()
                .collect(toList());
    }
    /*
        Since accumulate are always relative to the Pattern, it may happen that the declaration inside the accumulate
        was already set in the relative Pattern.
        Here though the type is more precise as it checks the result type Accumulate Function, so we use
        addDeclarationReplacing instead of addDeclaration to overwrite the previous declaration.
     */

    protected abstract MethodCallExpr buildBinding(String bindingName, Collection<String> usedDeclaration, Expression expression);

    protected abstract void processNewBinding(Optional<NewBinding> optNewBinding);

    protected abstract void postVisit();

    public static class NewBinding {

        Optional<String> patternBinding;
        MethodCallExpr bindExpression;

        public NewBinding(Optional<String> patternBinding, MethodCallExpr bindExpression) {
            this.patternBinding = patternBinding;
            this.bindExpression = bindExpression;
        }
    }

    private static final String ACCUMULATE_INLINE_FUNCTION =
            "public class AccumulateInlineFunction implements org.kie.api.runtime.rule.AccumulateFunction<AccumulateInlineFunction.ContextData> {\n" +
                    "\n" +
                    "    public static class ContextData implements java.io.Serializable {\n" +
                    "        // context fields will go here.\n" +
                    "    }\n" +
                    "\n" +
                    "    public void readExternal(java.io.ObjectInput in) throws java.io.IOException, ClassNotFoundException {\n" +
                    "        // functions are stateless, so nothing to serialize\n" +
                    "    }\n" +
                    "\n" +
                    "    public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException {\n" +
                    "        // functions are stateless, so nothing to serialize\n" +
                    "    }\n" +
                    "\n" +
                    "    public ContextData createContext() {\n" +
                    "        return new ContextData();\n" +
                    "    }\n" +
                    "\n" +
                    "    public void init(ContextData data) {\n" +
                    "    }\n" +
                    "\n" +
                    "    public void accumulate(ContextData data, Object $single) {\n" +
                    "    }\n" +
                    "\n" +
                    "    public void reverse(ContextData data, Object $single) {\n" +
                    "    }\n" +
                    "\n" +
                    "    public Object getResult(ContextData data) {\n" +
                    "    }\n" +
                    "\n" +
                    "    public boolean supportsReverse() {\n" +
                    "    }\n" +
                    "\n" +
                    "    public Class<?> getResultType() {\n" +
                    "    }\n" +
                    "}";
}
