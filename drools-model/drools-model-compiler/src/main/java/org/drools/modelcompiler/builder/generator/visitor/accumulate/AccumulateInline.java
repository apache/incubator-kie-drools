package org.drools.modelcompiler.builder.generator.visitor.accumulate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.expressiontyper.ExpressionTyper;
import org.drools.modelcompiler.builder.generator.expressiontyper.TypedExpressionResult;
import org.drools.modelcompiler.util.StringUtil;
import org.drools.mvelcompiler.MvelCompiler;
import org.drools.mvelcompiler.ParsingResult;
import org.drools.mvelcompiler.context.MvelCompilerContext;

import static com.github.javaparser.StaticJavaParser.parseStatement;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.addCurlyBracesToBlock;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.forceCastForName;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.rescopeNamesToNewScope;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.ACC_FUNCTION_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.BIND_AS_CALL;
import static org.drools.modelcompiler.builder.generator.visitor.accumulate.AccumulateVisitor.collectNamesInBlock;

public class AccumulateInline {

    protected final RuleContext context;
    protected final PackageModel packageModel;
    private AccumulateDescr accumulateDescr;
    private PatternDescr basePattern;

    private ClassOrInterfaceDeclaration accumulateInlineClass;
    private ClassOrInterfaceDeclaration contextData;
    private String accumulateInlineClassName;

    private final List<DeclarationSpec> accumulateDeclarations = new ArrayList<>();
    private final List<String> contextFieldNames = new ArrayList<>();
    private Set<String> usedExtDeclrs = new HashSet<>();

    private MvelCompiler mvelCompiler;

    AccumulateInline(RuleContext context,
                     PackageModel packageModel,
                     AccumulateDescr descr,
                     PatternDescr basePattern) {
        this.context = context;
        this.packageModel = packageModel;
        this.accumulateDescr = descr;
        this.basePattern = basePattern;

        MvelCompilerContext mvelCompilerContext = new MvelCompilerContext(context.getTypeResolver());

        for (DeclarationSpec ds : context.getAllDeclarations()) {
            mvelCompilerContext.addDeclaration(ds.getBindingId(), ds.getDeclarationClass());
        }

        mvelCompiler = new MvelCompiler(mvelCompilerContext);
    }

    /**
     * By design this legacy accumulate (with inline custome code) visitor supports only with 1-and-only binding in the accumulate code/expressions.
     */
    void visitAccInlineCustomCode(MethodCallExpr accumulateDSL, Set<String> externalDeclrs, String identifier) {

        initInlineAccumulateTemplate();

        context.pushExprPointer(accumulateDSL::addArgument);

        parseInitBlock();

        boolean useLegacyAccumulate = false;
        Type singleAccumulateType = null;
        MethodDeclaration accumulateMethod = accumulateInlineClass.getMethodsByName("accumulate").get(0);

        ParsingResult actionBlockCompilationResult = mvelCompiler.compile(addCurlyBracesToBlock(accumulateDescr.getActionCode()));

        BlockStmt actionBlock = actionBlockCompilationResult.statementResults();

        Collection<String> allNamesInActionBlock = collectNamesInBlock(actionBlock, context);
        if (allNamesInActionBlock.size() == 1) {
            String nameExpr = allNamesInActionBlock.iterator().next();
            accumulateMethod.getParameter(1).setName(nameExpr);
            singleAccumulateType =
                    context.getDeclarationById(nameExpr)
                            .orElseThrow(() -> new IllegalStateException("Cannot find declaration by name " + nameExpr + "!"))
                            .getBoxedType();
        } else {
            allNamesInActionBlock.removeIf(name -> !externalDeclrs.contains(name));
            usedExtDeclrs.addAll(allNamesInActionBlock);
            useLegacyAccumulate = true;
        }

        Optional<MethodDeclaration> optReverseMethod = Optional.empty();
        ParsingResult reverseBlockCompilationResult = mvelCompiler.compile(addCurlyBracesToBlock(accumulateDescr.getReverseCode()));

        BlockStmt reverseBlock = reverseBlockCompilationResult.statementResults();

        if (accumulateDescr.getReverseCode() != null) {
            Collection<String> allNamesInReverseBlock = collectNamesInBlock(reverseBlock, context);
            if (allNamesInReverseBlock.size() == 1) {
                MethodDeclaration reverseMethod = accumulateInlineClass.getMethodsByName("reverse").get(0);
                reverseMethod.getParameter(1).setName(allNamesInReverseBlock.iterator().next());
                optReverseMethod = Optional.of(reverseMethod);
            } else {
                allNamesInActionBlock.removeIf(name -> !externalDeclrs.contains(name));
                usedExtDeclrs.addAll(allNamesInActionBlock);
                useLegacyAccumulate = true;
            }
        }

        if (useLegacyAccumulate || !usedExtDeclrs.isEmpty()) {
            new LegacyAccumulate(context, accumulateDescr, this.basePattern, usedExtDeclrs).build();
            return;
        }

        for (DeclarationSpec d : accumulateDeclarations) {
            context.addDeclaration(d);
        }

        writeAccumulateMethod(contextFieldNames, singleAccumulateType, accumulateMethod, actionBlock);

        // <result expression>: this is a semantic expression in the selected dialect that is executed after all source objects are iterated.
        MethodDeclaration resultMethod = accumulateInlineClass.getMethodsByName("getResult").get(0);
        Type returnExpressionType = StaticJavaParser.parseType("java.lang.Object");
        Expression returnExpression = StaticJavaParser.parseExpression(accumulateDescr.getResultCode());
        if (returnExpression instanceof NameExpr) {
            returnExpression = new EnclosedExpr(returnExpression);
        }
        rescopeNamesToNewScope(new NameExpr("data"), contextFieldNames, returnExpression);
        resultMethod
                .getBody()
                .orElseThrow(InvalidInlineTemplateException::new)
                .addStatement(new ReturnStmt(returnExpression));
        MethodDeclaration getResultTypeMethod = accumulateInlineClass.getMethodsByName("getResultType").get(0);
        getResultTypeMethod
                .getBody()
                .orElseThrow(InvalidInlineTemplateException::new)
                .addStatement(new ReturnStmt(new ClassExpr(returnExpressionType)));

        if (optReverseMethod.isPresent()) {
            MethodDeclaration supportsReverseMethod = accumulateInlineClass.getMethodsByName("supportsReverse").get(0);
            supportsReverseMethod
                    .getBody()
                    .orElseThrow(InvalidInlineTemplateException::new)
                    .addStatement(parseStatement("return true;"));

            writeAccumulateMethod(contextFieldNames, singleAccumulateType, optReverseMethod.get(), reverseBlock);
        } else {
            MethodDeclaration supportsReverseMethod = accumulateInlineClass.getMethodsByName("supportsReverse").get(0);
            supportsReverseMethod
                    .getBody()
                    .orElseThrow(InvalidInlineTemplateException::new)
                    .addStatement(parseStatement("return false;"));

            MethodDeclaration reverseMethod = accumulateInlineClass.getMethodsByName("reverse").get(0);
            reverseMethod
                    .getBody()
                    .orElseThrow(InvalidInlineTemplateException::new)
                    .addStatement(parseStatement("throw new UnsupportedOperationException(\"This function does not support reverse.\");"));
        }

        // add resulting accumulator class into the package model
        this.packageModel.addGeneratedPOJO(accumulateInlineClass);

        final MethodCallExpr functionDSL = new MethodCallExpr(null, ACC_FUNCTION_CALL);
        functionDSL.addArgument(new MethodReferenceExpr(new NameExpr(accumulateInlineClassName), new NodeList<>(), "new"));
        functionDSL.addArgument(context.getVarExpr(identifier));

        final String bindingId = this.basePattern.getIdentifier();
        final MethodCallExpr asDSL = new MethodCallExpr(functionDSL, BIND_AS_CALL);
        asDSL.addArgument(context.getVarExpr(bindingId));
        accumulateDSL.addArgument(asDSL);

        context.popExprPointer();
    }

    private void initInlineAccumulateTemplate() {
        accumulateInlineClassName = StringUtil.toId(context.getRuleDescr().getName()) + "Accumulate" + accumulateDescr.getLine();

        CompilationUnit templateCU;
        try {
            templateCU = StaticJavaParser.parseResource("AccumulateInlineTemplate.java");
        } catch (IOException e) {
            throw new InvalidInlineTemplateException(e);
        }

        ClassOrInterfaceDeclaration parsedClass =
                templateCU
                        .getClassByName("AccumulateInlineFunction")
                        .orElseThrow(InvalidInlineTemplateException::new);

        parsedClass.setName(accumulateInlineClassName);
        parsedClass.findAll(ClassOrInterfaceType.class, c -> "CONTEXT_DATA_GENERIC".equals(c.asString()))
                .forEach(c -> c.setName(accumulateInlineClassName + ".ContextData"));

        this.accumulateInlineClass = parsedClass;

        contextData = this.accumulateInlineClass.findFirst(ClassOrInterfaceDeclaration.class
                , c -> "ContextData".equals(c.getNameAsString()))
                .orElseThrow(InvalidInlineTemplateException::new);
    }

    private void parseInitBlock() {
        MethodDeclaration initMethod = accumulateInlineClass.getMethodsByName("init").get(0);
        ParsingResult initCodeCompilationResult = mvelCompiler.compile(addCurlyBracesToBlock(accumulateDescr.getInitCode()));
        BlockStmt initBlock = initCodeCompilationResult.statementResults();

        for (Statement stmt : initBlock.getStatements()) {
            final BlockStmt initMethodBody = initMethod.getBody().orElseThrow(() -> new IllegalStateException("Method declaration doesn't contain body!"));
            if (stmt instanceof ExpressionStmt && ((ExpressionStmt) stmt).getExpression() instanceof VariableDeclarationExpr) {
                VariableDeclarationExpr vdExpr = (VariableDeclarationExpr) ((ExpressionStmt) stmt).getExpression();
                for (VariableDeclarator vd : vdExpr.getVariables()) {
                    final String variableName = vd.getNameAsString();
                    contextFieldNames.add(variableName);
                    contextData.addField(vd.getType(), variableName, Modifier.publicModifier().getKeyword());
                    createInitializer(variableName, vd.getInitializer()).ifPresent(statement -> {
                                                                                       initMethodBody.addStatement(statement);
                                                                                       statement.findAll(NameExpr.class).stream().map(Node::toString).filter(context::hasDeclaration).forEach(usedExtDeclrs::add);
                                                                                   }
                    );
                    accumulateDeclarations.add(new DeclarationSpec(variableName, DrlxParseUtil.getClassFromContext(context.getTypeResolver(), vd.getType().asString())));
                }
            } else {
                if (stmt.isExpressionStmt()) {
                    final Expression statementExpression = stmt.asExpressionStmt().getExpression();
                    if (statementExpression.isAssignExpr()) {
                        final AssignExpr assignExpr = statementExpression.asAssignExpr();
                        final String targetName = assignExpr.getTarget().asNameExpr().toString();
                        // Mvel allows using a field without declaration
                        if (!contextFieldNames.contains(targetName)) {
                            contextFieldNames.add(targetName);
                            final String variableName = assignExpr.getTarget().toString();
                            final Expression initCreationExpression = assignExpr.getValue();

                            final Type type =
                                    initCodeCompilationResult.lastExpressionType()
                                            .map(t -> DrlxParseUtil.classToReferenceType((Class<?>) t))
                                            .orElseThrow(() -> new RuntimeException("Unknown type: " + initCreationExpression));

                            contextData.addField(type, variableName, Modifier.publicModifier().getKeyword());
                            final Optional<Statement> initializer = createInitializer(variableName, Optional.of(initCreationExpression));
                            initializer.ifPresent(initMethodBody::addStatement);
                            accumulateDeclarations.add(new DeclarationSpec(variableName, DrlxParseUtil.getClassFromContext(context.getTypeResolver(), type.asString())));
                        }
                    }
                } else {
                    initMethodBody.addStatement(stmt); // add as-is.
                }
            }
        }
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

    private void writeAccumulateMethod(List<String> contextFieldNames, Type singleAccumulateType, MethodDeclaration accumulateMethod, BlockStmt actionBlock) {
        for (Statement stmt : actionBlock.getStatements()) {
            final ExpressionStmt convertedExpressionStatement = new ExpressionStmt();
            for (ExpressionStmt eStmt : stmt.findAll(ExpressionStmt.class)) {
                final Expression expressionUntyped = eStmt.getExpression();
                final String parameterName = accumulateMethod.getParameter(1).getNameAsString();

                final ExpressionTyper expressionTyper = new ExpressionTyper(context, Object.class, "", false);
                final TypedExpressionResult typedExpression = expressionTyper.toTypedExpression(expressionUntyped);

                final Expression expression =
                        typedExpression
                                .getTypedExpression()
                                .orElseThrow(() -> new IllegalStateException("Typed expression is not present!"))
                                .getExpression();

                forceCastForName(parameterName, singleAccumulateType, expression);
                rescopeNamesToNewScope(new NameExpr("data"), contextFieldNames, expression);
                convertedExpressionStatement.setExpression(expression);
            }
            accumulateMethod.getBody().orElseThrow(() -> new IllegalStateException("Method declaration doesn't contain body!")).addStatement(convertedExpressionStatement);
        }
    }
}
