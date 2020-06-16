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
import org.drools.compiler.lang.descr.DeclarativeInvokerDescr;
import org.drools.compiler.lang.descr.FromDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.PatternSourceDescr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.util.StringUtil;
import org.drools.mvelcompiler.MvelCompiler;
import org.drools.mvelcompiler.ParsingResult;
import org.drools.mvelcompiler.context.MvelCompilerContext;

import static com.github.javaparser.StaticJavaParser.parseStatement;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.addCurlyBracesToBlock;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.addSemicolon;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.forceCastForName;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.rescopeNamesToNewScope;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.ACC_FUNCTION_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.BIND_AS_CALL;
import static org.drools.modelcompiler.builder.generator.visitor.accumulate.AccumulateVisitor.collectNamesInBlock;

public class AccumulateInline {

    protected final RuleContext context;
    protected final PackageModel packageModel;
    private final String REVERSE = "reverse";
    private AccumulateDescr accumulateDescr;
    private PatternDescr basePattern;

    private ClassOrInterfaceDeclaration accumulateInlineClass;
    private ClassOrInterfaceDeclaration contextData;
    private String accumulateInlineClassName;

    private final List<DeclarationSpec> accumulateDeclarations = new ArrayList<>();
    private final List<String> contextFieldNames = new ArrayList<>();
    private Set<String> usedExternalDeclarations = new HashSet<>();
    private Type singleAccumulateType;

    Set<String> getUsedExternalDeclarations() {
        return usedExternalDeclarations;
    }

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
        singleAccumulateType = null;
    }

    /**
     * By design this legacy accumulate (with inline custome code) visitor supports only with 1-and-only binding in the accumulate code/expressions.
     */
    void visitAccInlineCustomCode(MethodCallExpr accumulateDSL, Set<String> externalDeclarations, String identifier) {
        initInlineAccumulateTemplate();

        parseInitBlock();
        try {
            Collection<String> allNamesInActionBlock = parseActionBlock( externalDeclarations );
            parseReverseBlock( externalDeclarations, allNamesInActionBlock );
        } catch (UnsupportedInlineAccumulate e) {
            parseAccumulatePattern();
            throw e;
        }
        parseResultMethod();

        if (!usedExternalDeclarations.isEmpty()) {
            parseAccumulatePattern();
            throw new UnsupportedInlineAccumulate();
        }

        for (DeclarationSpec d : accumulateDeclarations) {
            context.addDeclaration(d);
        }

        addAccumulateClassInitializationToMethod(accumulateDSL, identifier);
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

    void parseAccumulatePattern() {
        PatternDescr pattern = accumulateDescr.getInputPattern();
        if ( pattern == null || pattern.getSource() == null ) {
            return;
        }

        PatternSourceDescr sourceDescr = pattern.getSource();
        if ( sourceDescr instanceof FromDescr ) {
            DeclarativeInvokerDescr invokerDescr = (( FromDescr ) sourceDescr).getDataSource();
            String mvelBlock = addCurlyBracesToBlock( addSemicolon( invokerDescr.getText() ) );
            ParsingResult fromCodeCompilationResult = mvelCompiler.compile( mvelBlock );
            BlockStmt fromBlock = fromCodeCompilationResult.statementResults();
            for (Statement stmt : fromBlock.getStatements()) {
                stmt.findAll(NameExpr.class).stream().map(Node::toString).filter(context::hasDeclaration).forEach(usedExternalDeclarations::add);
            }
        }
    }

    private void parseInitBlock() {
        MethodDeclaration initMethod = getMethodFromTemplateClass("init");
        String mvelBlock = addCurlyBracesToBlock(addSemicolon(accumulateDescr.getInitCode()));
        ParsingResult initCodeCompilationResult = mvelCompiler.compile(mvelBlock);
        BlockStmt initBlock = initCodeCompilationResult.statementResults();

        for (Statement stmt : initBlock.getStatements()) {
            final BlockStmt initMethodBody = initMethod.getBody().orElseThrow(InvalidInlineTemplateException::new);
            if (stmt.isExpressionStmt() && stmt.asExpressionStmt().getExpression().isVariableDeclarationExpr()) {
                VariableDeclarationExpr vdExpr = stmt.asExpressionStmt().getExpression().asVariableDeclarationExpr();
                for (VariableDeclarator vd : vdExpr.getVariables()) {
                    final String variableName = vd.getNameAsString();
                    contextFieldNames.add(variableName);
                    contextData.addField(vd.getType(), variableName, Modifier.publicModifier().getKeyword());
                    Optional<Expression> optInitializer = vd.getInitializer();
                    optInitializer.ifPresent(initializer -> {
                        Expression target = new FieldAccessExpr(getDataNameExpr(), variableName);
                        Statement initStmt = new ExpressionStmt(new AssignExpr(target, initializer, AssignExpr.Operator.ASSIGN));
                        initMethodBody.addStatement(initStmt);
                        initStmt.findAll(NameExpr.class).stream().map(Node::toString).filter(context::hasDeclaration).forEach(usedExternalDeclarations::add);
                    });
                    accumulateDeclarations.add(new DeclarationSpec(variableName, DrlxParseUtil.getClassFromContext(context.getTypeResolver(), vd.getType().asString())));
                }
            }
        }
    }

    private void writeAccumulateMethod(List<String> contextFieldNames, MethodDeclaration accumulateMethod, BlockStmt actionBlock) {
        for (Statement stmt : actionBlock.getStatements()) {
            final ExpressionStmt convertedExpressionStatement = new ExpressionStmt();
            for (ExpressionStmt eStmt : stmt.findAll(ExpressionStmt.class)) {
                final Expression expressionUntyped = eStmt.getExpression();
                final String parameterName = accumulateMethod.getParameter(1).getNameAsString();

                forceCastForName(parameterName, singleAccumulateType, expressionUntyped);
                rescopeNamesToNewScope(getDataNameExpr(), contextFieldNames, expressionUntyped);
                convertedExpressionStatement.setExpression(expressionUntyped);
            }
            accumulateMethod.getBody().orElseThrow(InvalidInlineTemplateException::new)
                    .addStatement(convertedExpressionStatement);
        }
    }

    private Collection<String> parseActionBlock(Set<String> externalDeclarations) {
        MethodDeclaration accumulateMethod = getMethodFromTemplateClass("accumulate");

        String actionCode = accumulateDescr.getActionCode();
        if(blockIsNonEmptyWithoutSemicolon(actionCode)) {
            throw new MissingSemicolonInlineAccumulateException("action");
        }

        ParsingResult actionBlockCompilationResult = mvelCompiler.compile(addCurlyBracesToBlock(actionCode));

        BlockStmt actionBlock = actionBlockCompilationResult.statementResults();

        Collection<String> allNamesInActionBlock = collectNamesInBlock(actionBlock, context);
        if (allNamesInActionBlock.size() == 1) {
            String nameExpr = allNamesInActionBlock.iterator().next();
            accumulateMethod.getParameter(1).setName(nameExpr);
            singleAccumulateType =
                    context.getDeclarationById(nameExpr)
                            .orElseThrow(() -> new IllegalStateException("Cannot find declaration by name " + nameExpr + "!"))
                            .getBoxedType();

            writeAccumulateMethod(contextFieldNames, accumulateMethod, actionBlock);

        } else {
            allNamesInActionBlock.removeIf(name -> !externalDeclarations.contains(name));
            usedExternalDeclarations.addAll(allNamesInActionBlock);
            throw new UnsupportedInlineAccumulate();
        }
        return allNamesInActionBlock;
    }

    private void parseReverseBlock(Set<String> externalDeclarations, Collection<String> allNamesInActionBlock) {
        String reverseCode = accumulateDescr.getReverseCode();
        ParsingResult reverseBlockCompilationResult = mvelCompiler.compile(addCurlyBracesToBlock(reverseCode));

        BlockStmt reverseBlock = reverseBlockCompilationResult.statementResults();

        if (reverseCode != null) {
            if(blockIsNonEmptyWithoutSemicolon(reverseCode)) {
                throw new MissingSemicolonInlineAccumulateException(REVERSE);
            }

            Collection<String> allNamesInReverseBlock = collectNamesInBlock(reverseBlock, context);
            if (allNamesInReverseBlock.size() == 1) {
                MethodDeclaration reverseMethod = getMethodFromTemplateClass(REVERSE);
                reverseMethod.getParameter(1).setName(allNamesInReverseBlock.iterator().next());
                writeAccumulateMethod(contextFieldNames, reverseMethod, reverseBlock);

                MethodDeclaration supportsReverseMethod = getMethodFromTemplateClass("supportsReverse");
                supportsReverseMethod
                        .getBody()
                        .orElseThrow(InvalidInlineTemplateException::new)
                        .addStatement(parseStatement("return true;"));
            } else {
                allNamesInActionBlock.removeIf(name -> !externalDeclarations.contains(name));
                usedExternalDeclarations.addAll(allNamesInActionBlock);
                throw new UnsupportedInlineAccumulate();
            }
        } else {
            MethodDeclaration supportsReverseMethod = getMethodFromTemplateClass("supportsReverse");
            supportsReverseMethod
                    .getBody()
                    .orElseThrow(InvalidInlineTemplateException::new)
                    .addStatement(parseStatement("return false;"));

            MethodDeclaration reverseMethod = getMethodFromTemplateClass(REVERSE);
            reverseMethod
                    .getBody()
                    .orElseThrow(InvalidInlineTemplateException::new)
                    .addStatement(parseStatement("throw new UnsupportedOperationException(\"This function does not support reverse.\");"));
        }
    }

    private void parseResultMethod() {
        // <result expression>: this is a semantic expression in the selected dialect that is executed after all source objects are iterated.
        MethodDeclaration resultMethod = getMethodFromTemplateClass("getResult");
        Type returnExpressionType = StaticJavaParser.parseType("java.lang.Object");
        Expression returnExpression = StaticJavaParser.parseExpression(accumulateDescr.getResultCode());
        if (returnExpression instanceof NameExpr) {
            returnExpression = new EnclosedExpr(returnExpression);
        }
        rescopeNamesToNewScope(getDataNameExpr(), contextFieldNames, returnExpression);

        resultMethod
                .getBody()
                .orElseThrow(InvalidInlineTemplateException::new)
                .addStatement(new ReturnStmt(returnExpression));
        MethodDeclaration getResultTypeMethod = getMethodFromTemplateClass("getResultType");
        getResultTypeMethod
                .getBody()
                .orElseThrow(InvalidInlineTemplateException::new)
                .addStatement(new ReturnStmt(new ClassExpr(returnExpressionType)));
    }

    private void addAccumulateClassInitializationToMethod(MethodCallExpr accumulateDSL, String identifier) {
        this.packageModel.addGeneratedPOJO(accumulateInlineClass);

        final MethodCallExpr functionDSL = new MethodCallExpr(null, ACC_FUNCTION_CALL);
        functionDSL.addArgument(new MethodReferenceExpr(new NameExpr(accumulateInlineClassName), new NodeList<>(), "new"));
        functionDSL.addArgument(context.getVarExpr(identifier));

        final String bindingId = this.basePattern.getIdentifier();
        final MethodCallExpr asDSL = new MethodCallExpr(functionDSL, BIND_AS_CALL);
        asDSL.addArgument(context.getVarExpr(bindingId));
        accumulateDSL.addArgument(asDSL);
    }

    private NameExpr getDataNameExpr() {
        return new NameExpr("data");
    }

    private MethodDeclaration getMethodFromTemplateClass(String init) {
        return accumulateInlineClass.getMethodsByName(init).get(0);
    }

    private boolean blockIsNonEmptyWithoutSemicolon(String block) {
        return !"".equals(block) && !block.endsWith(";");
    }
}
