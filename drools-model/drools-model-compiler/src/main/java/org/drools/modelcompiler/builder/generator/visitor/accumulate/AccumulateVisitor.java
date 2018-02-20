package org.drools.modelcompiler.builder.generator.visitor.accumulate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.rule.builder.util.AccumulateUtil;
import org.drools.core.util.IoUtils;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.CompilationUnit;
import org.drools.javaparser.ast.Modifier;
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
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.TypedExpression;
import org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseFail;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.drlxparse.ParseResultVisitor;
import org.drools.modelcompiler.builder.generator.expression.AbstractExpressionBuilder;
import org.drools.modelcompiler.builder.generator.visitor.ModelGeneratorVisitor;
import org.drools.modelcompiler.util.StringUtil;
import org.kie.api.runtime.rule.AccumulateFunction;

import static java.util.stream.Collectors.toList;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.forceCastForName;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.rescopeNamesToNewScope;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;

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
        final MethodCallExpr accumulateDSL = new MethodCallExpr(null, "accumulate");
        context.addExpression(accumulateDSL);
        final MethodCallExpr accumulateExprs = new MethodCallExpr(null, "and");
        accumulateDSL.addArgument(accumulateExprs);

        context.pushExprPointer(accumulateExprs::addArgument);

        BaseDescr input = descr.getInputPattern() == null ? descr.getInput() : descr.getInputPattern();
        boolean inputPatternHasConstraints = (input instanceof PatternDescr) && (!((PatternDescr) input).getConstraint().getDescrs().isEmpty());
        input.accept(modelGeneratorVisitor);

        if (accumulateExprs.getArguments().isEmpty()) {
            accumulateDSL.remove(accumulateExprs);
        } else if (accumulateExprs.getArguments().size() == 1) {
            accumulateDSL.setArgument(0, accumulateExprs.getArguments().get(0));
        }

        if (!descr.getFunctions().isEmpty()) {
            for (AccumulateDescr.AccumulateFunctionCallDescr function : descr.getFunctions()) {
                final Optional<NewBinding> optNewBinding = visit(context, function, accumulateDSL, basePattern, inputPatternHasConstraints);
                processNewBinding(optNewBinding);
            }
        } else if (descr.getFunctions().isEmpty() && descr.getInitCode() != null) {
            // LEGACY: Accumulate with inline custom code
            if (input instanceof PatternDescr) {
                visitAccInlineCustomCode(context, descr, accumulateDSL, basePattern, (PatternDescr) input);
            } else {
                throw new UnsupportedOperationException("I was expecting input to be of type PatternDescr. " + input);
            }
        } else {
            throw new UnsupportedOperationException("Unknown type of Accumulate.");
        }

        postVisit();
    }

    protected Optional<AccumulateVisitorPatternDSL.NewBinding> visit(RuleContext context, AccumulateDescr.AccumulateFunctionCallDescr function, MethodCallExpr accumulateDSL, PatternDescr basePattern, boolean inputPatternHasConstraints) {

        context.pushExprPointer(accumulateDSL::addArgument);

        final MethodCallExpr functionDSL = new MethodCallExpr(null, "accFunction");

        final String expression = function.getParams()[0];
        final Expression expr = DrlxParseUtil.parseExpression(expression).getExpr();
        final String bindingId = Optional.ofNullable(function.getBind()).orElse(basePattern.getIdentifier());

        Optional<AccumulateVisitorPatternDSL.NewBinding> newBinding = Optional.empty();

        if (expr instanceof BinaryExpr) {

            final DrlxParseResult parseResult = new ConstraintParser(context, packageModel).drlxParse(Object.class, bindingId, expression);

            newBinding = parseResult.acceptWithReturnValue(new ParseResultVisitor<Optional<AccumulateVisitorPatternDSL.NewBinding>>() {
                @Override
                public Optional<AccumulateVisitorPatternDSL.NewBinding> onSuccess(DrlxParseSuccess drlxParseResult) {
                    final AccumulateFunction accumulateFunction = AccumulateVisitor.this.getAccumulateFunction(function, drlxParseResult.getExprType());

                    final String bindExpressionVariable = context.getExprId(accumulateFunction.getResultType(), drlxParseResult.getLeft().toString());

                    drlxParseResult.setExprBinding(bindExpressionVariable);

                    context.addDeclarationReplacing(new DeclarationSpec(drlxParseResult.getPatternBinding(), drlxParseResult.getExprType()));

                    functionDSL.addArgument(new ClassExpr(toType(accumulateFunction.getClass())));
                    final MethodCallExpr newBindingFromBinary = AccumulateVisitor.this.buildBinding(bindExpressionVariable, drlxParseResult.getUsedDeclarations(), drlxParseResult.getExpr());
                    context.addDeclarationReplacing(new DeclarationSpec(bindExpressionVariable, drlxParseResult.getExprType()));
                    functionDSL.addArgument(new NameExpr(toVar(bindExpressionVariable)));
                    return Optional.of(new AccumulateVisitorPatternDSL.NewBinding(Optional.empty(), newBindingFromBinary));
                }

                @Override
                public Optional<AccumulateVisitorPatternDSL.NewBinding> onFail(DrlxParseFail failure) {
                    return Optional.empty();
                }
            });
        } else if (expr instanceof MethodCallExpr) {

            final DrlxParseUtil.RemoveRootNodeResult methodCallWithoutRootNode = DrlxParseUtil.removeRootNode(expr);

            final String rootNodeName = getRootNodeName(methodCallWithoutRootNode);

            final TypedExpression typedExpression = parseMethodCallType(context, rootNodeName, methodCallWithoutRootNode.getWithoutRootNode());
            final Class<?> methodCallExprType = typedExpression.getType();

            final AccumulateFunction accumulateFunction = getAccumulateFunction(function, methodCallExprType);

            final Class accumulateFunctionResultType = accumulateFunction.getResultType();
            functionDSL.addArgument(new ClassExpr(toType(accumulateFunction.getClass())));

            // Every expression in an accumulate function gets transformed in a bind expression with a generated id
            // Then the accumulate function will have that binding expression as a source
            final String bindExpressionVariable = context.getExprId(accumulateFunctionResultType, typedExpression.toString());
            Expression withThis = DrlxParseUtil.prepend(DrlxParseUtil._THIS_EXPR, typedExpression.getExpression());
            DrlxParseSuccess result = new DrlxParseSuccess(accumulateFunctionResultType, "", rootNodeName, withThis, accumulateFunctionResultType)
                    .setLeft(typedExpression)
                    .setExprBinding(bindExpressionVariable);
            final MethodCallExpr binding = expressionBuilder.buildBinding(result);
            newBinding = Optional.of(new AccumulateVisitorPatternDSL.NewBinding(Optional.of(result.getPatternBinding()), binding));
            context.addDeclarationReplacing(new DeclarationSpec(bindExpressionVariable, methodCallExprType));
            functionDSL.addArgument(new NameExpr(toVar(bindExpressionVariable)));

            context.addDeclarationReplacing(new DeclarationSpec(bindingId, accumulateFunctionResultType));
        } else if (expr instanceof NameExpr) {
            final Class<?> declarationClass = context
                    .getDeclarationById(expr.toString())
                    .orElseThrow(RuntimeException::new)
                    .getDeclarationClass();

            final String nameExpr = ((NameExpr) expr).getName().asString();
            final AccumulateFunction accumulateFunction = getAccumulateFunction(function, declarationClass);
            functionDSL.addArgument(new ClassExpr(toType(accumulateFunction.getClass())));
            functionDSL.addArgument(new NameExpr(toVar(nameExpr)));

            Class accumulateFunctionResultType = accumulateFunction.getResultType();
            if (accumulateFunctionResultType == Comparable.class && (Comparable.class.isAssignableFrom(declarationClass) || declarationClass.isPrimitive())) {
                accumulateFunctionResultType = declarationClass;
            }
            context.addDeclarationReplacing(new DeclarationSpec(bindingId, accumulateFunctionResultType));
        } else {
            throw new UnsupportedOperationException("Unsupported expression " + expr);
        }

        final MethodCallExpr asDSL = new MethodCallExpr(functionDSL, "as");
        asDSL.addArgument(new NameExpr(toVar(bindingId)));
        accumulateDSL.addArgument(asDSL);

        context.popExprPointer();
        return newBinding;
    }

    protected AccumulateFunction getAccumulateFunction(AccumulateDescr.AccumulateFunctionCallDescr function, Class<?> methodCallExprType) {
        final String accumulateFunctionName = AccumulateUtil.getFunctionName(() -> methodCallExprType, function.getFunction());
        final Optional<AccumulateFunction> bundledAccumulateFunction = Optional.ofNullable(packageModel.getConfiguration().getAccumulateFunction(accumulateFunctionName));
        final Optional<AccumulateFunction> importedAccumulateFunction = Optional.ofNullable(packageModel.getAccumulateFunctions().get(accumulateFunctionName));

        return bundledAccumulateFunction
                .map(Optional::of)
                .orElse(importedAccumulateFunction)
                .orElseThrow(RuntimeException::new);
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
    protected void visitAccInlineCustomCode(RuleContext context2, AccumulateDescr descr, MethodCallExpr accumulateDSL, PatternDescr basePattern, PatternDescr inputDescr) {
        context.pushExprPointer(accumulateDSL::addArgument);
        final MethodCallExpr functionDSL = new MethodCallExpr(null, "accFunction");

        String code = null;
        try {
            code = new String(IoUtils.readBytesFromInputStream(this.getClass().getResourceAsStream("/AccumulateInlineFunction.java")));
        } catch (IOException e1) {
            e1.printStackTrace();
            throw new RuntimeException("Unable to locate template.");
        }
        String targetClassName = StringUtil.toId(context2.getRuleDescr().getName()) + "Accumulate" + descr.getLine();
        code = code.replaceAll("AccumulateInlineFunction", targetClassName);
        CompilationUnit templateCU = JavaParser.parse(code);
        ClassOrInterfaceDeclaration templateClass = templateCU.getClassByName(targetClassName).orElseThrow(() -> new RuntimeException("Template did not contain expected type definition."));
        ClassOrInterfaceDeclaration templateContextClass = templateClass.getMembers().stream().filter(m -> m instanceof ClassOrInterfaceDeclaration && ((ClassOrInterfaceDeclaration) m).getNameAsString().equals("ContextData")).map(ClassOrInterfaceDeclaration.class::cast).findFirst().orElseThrow(() -> new RuntimeException("Template did not contain expected type definition."));

        List<String> contextFieldNames = new ArrayList<>();
        MethodDeclaration initMethod = templateClass.getMethodsByName("init").get(0);
        BlockStmt initBlock = JavaParser.parseBlock("{" + descr.getInitCode() + "}");
        for (Statement stmt : initBlock.getStatements()) {
            if (stmt instanceof ExpressionStmt && ((ExpressionStmt) stmt).getExpression() instanceof VariableDeclarationExpr) {
                VariableDeclarationExpr vdExpr = (VariableDeclarationExpr) ((ExpressionStmt) stmt).getExpression();
                for (VariableDeclarator vd : vdExpr.getVariables()) {
                    contextFieldNames.add(vd.getNameAsString());
                    templateContextClass.addField(vd.getType(), vd.getNameAsString(), Modifier.PUBLIC);
                    if (vd.getInitializer().isPresent()) {
                        Expression initializer = vd.getInitializer().get();
                        Expression target = new FieldAccessExpr(new NameExpr("data"), vd.getNameAsString());
                        Statement initStmt = new ExpressionStmt(new AssignExpr(target, initializer, AssignExpr.Operator.ASSIGN));
                        initMethod.getBody().get().addStatement(initStmt);
                    }
                }
            } else {
                initMethod.getBody().get().addStatement(stmt); // add as-is.
            }
        }

        Type singleAccumulateType = JavaParser.parseType("java.lang.Object");

        MethodDeclaration accumulateMethod = templateClass.getMethodsByName("accumulate").get(0);
        BlockStmt actionBlock = JavaParser.parseBlock("{" + descr.getActionCode() + "}");
        Collection<String> allNamesInActionBlock = collectNamesInBlock(context2, actionBlock);
        if (allNamesInActionBlock.size() == 1) {
            String nameExpr = allNamesInActionBlock.iterator().next();
            accumulateMethod.getParameter(1).setName(nameExpr);
            singleAccumulateType = context2.getDeclarationById(nameExpr).get().getType();
        } else {
            throw new UnsupportedOperationException("By design this legacy accumulate (with inline custome code) visitor supports only with 1-and-only binding");
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

        if (descr.getReverseCode() != null) {
            MethodDeclaration supportsReverseMethod = templateClass.getMethodsByName("supportsReverse").get(0);
            supportsReverseMethod.getBody().get().addStatement(JavaParser.parseStatement("return true;"));

            MethodDeclaration reverseMethod = templateClass.getMethodsByName("reverse").get(0);
            BlockStmt reverseBlock = JavaParser.parseBlock("{" + descr.getReverseCode() + "}");
            Collection<String> allNamesInReverseBlock = collectNamesInBlock(context2, reverseBlock);
            if (allNamesInReverseBlock.size() == 1) {
                reverseMethod.getParameter(1).setName(allNamesInReverseBlock.iterator().next());
            } else {
                throw new UnsupportedOperationException("By design this legacy accumulate (with inline custome code) visitor supports only with 1-and-only binding");
            }
            writeAccumulateMethod(contextFieldNames, singleAccumulateType, reverseMethod, reverseBlock);
        } else {
            MethodDeclaration supportsReverseMethod = templateClass.getMethodsByName("supportsReverse").get(0);
            supportsReverseMethod.getBody().get().addStatement(JavaParser.parseStatement("return false;"));

            MethodDeclaration reverseMethod = templateClass.getMethodsByName("reverse").get(0);
            reverseMethod.getBody().get().addStatement(JavaParser.parseStatement("throw new UnsupportedOperationException(\"This function does not support reverse.\");"));
        }

        // add resulting accumulator class into the package model
        this.packageModel.addGeneratedPOJO(templateClass);
        functionDSL.addArgument(new ClassExpr(JavaParser.parseType(targetClassName)));
        functionDSL.addArgument(new NameExpr(toVar(inputDescr.getIdentifier())));

        final String bindingId = basePattern.getIdentifier();
        final MethodCallExpr asDSL = new MethodCallExpr(functionDSL, "as");
        asDSL.addArgument(new NameExpr(toVar(bindingId)));
        accumulateDSL.addArgument(asDSL);

        context.popExprPointer();
    }

    void writeAccumulateMethod(List<String> contextFieldNames, Type singleAccumulateType, MethodDeclaration accumulateMethod, BlockStmt actionBlock) {
        for (Statement stmt : actionBlock.getStatements()) {
            for (ExpressionStmt eStmt : stmt.findAll(ExpressionStmt.class)) {
                forceCastForName(accumulateMethod.getParameter(1).getNameAsString(), singleAccumulateType, eStmt.getExpression());
                rescopeNamesToNewScope(new NameExpr("data"), contextFieldNames, eStmt.getExpression());
            }
            accumulateMethod.getBody().get().addStatement(stmt);
        }
    }

    List<String> collectNamesInBlock(RuleContext context2, BlockStmt block) {
        return block.findAll(NameExpr.class, n -> context2.getAvailableBindings().contains(n.getNameAsString())).stream().map(NameExpr::getNameAsString).distinct().collect(toList());
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

    class NewBinding {

        Optional<String> patternBinding;
        Expression bindExpression;

        public NewBinding(Optional<String> patternBinding, Expression bindExpression) {
            this.patternBinding = patternBinding;
            this.bindExpression = bindExpression;
        }
    }
}
