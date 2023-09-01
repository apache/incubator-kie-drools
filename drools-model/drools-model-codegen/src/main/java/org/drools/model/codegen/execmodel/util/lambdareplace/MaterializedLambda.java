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
package org.drools.model.codegen.execmodel.util.lambdareplace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithMembers;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.UnknownType;
import org.drools.model.functions.HashedExpression;

import static org.drools.util.StringUtils.md5Hash;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.createSimpleAnnotation;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.model.codegen.execmodel.util.lambdareplace.ExecModelLambdaPostProcessor.MATERIALIZED_LAMBDA_PRETTY_PRINTER;

abstract class MaterializedLambda {

    private static final String CLASS_NAME_TEMPLATE_NAME = "{CLASS_NAME}";

    final List<LambdaParameter> lambdaParameters = new ArrayList<>();

    protected final String packageName;
    protected String temporaryClassHash;

    LambdaExpr lambdaExpr;
    private String ruleClassName;

    MaterializedLambda(String packageName, String ruleClassName) {
        this.packageName = packageName;
        this.ruleClassName = ruleClassName;
    }

    public CreatedClass create(String expressionString, Collection<String> imports, Collection<String> staticImports) {
        Expression expression = StaticJavaParser.parseExpression(expressionString);

        if (!expression.isLambdaExpr()) {
            throw new NotLambdaException();
        }

        return create(expression.asLambdaExpr(), imports, staticImports);
    }

    public CreatedClass create(LambdaExpr lambdaExpr, Collection<String> imports, Collection<String> staticImports) {
        this.lambdaExpr = lambdaExpr;
        this.temporaryClassHash = classHash(lambdaExpr.toString());

        parseParameters();

        CompilationUnit compilationUnit = new CompilationUnit();
        addImports(imports, staticImports, compilationUnit);

        EnumDeclaration classDeclaration = create(compilationUnit);

        classDeclaration.setName(CLASS_NAME_TEMPLATE_NAME);
        createMethodsDeclaration(classDeclaration);

        String contents = MATERIALIZED_LAMBDA_PRETTY_PRINTER.print(compilationUnit);
        String classHash = classHash(contents);
        String isolatedPackageName = getIsolatedPackageName(classHash);
        String className = getPrefix() + classHash;

        String renamedContents = contents.replace(CLASS_NAME_TEMPLATE_NAME, className);
        String packageDeclaration = new PackageDeclaration(new Name(isolatedPackageName)).toString();

        String fullContents = packageDeclaration + "\n" + renamedContents;

        return new CreatedClass(fullContents, className, isolatedPackageName);
    }

    /*
        Externalised Lambda need to be isolated in a separate packages because putting too many classes
        in the same package as the rule might break Java 8 compiler while importing *
        We aggregate the Lambda classes based on the first two letters of the hash
     */
    private String getIsolatedPackageName(String className) {
        return packageName + ".P" + className.substring(0, 2);
    }

    private void addImports(Collection<String> imports, Collection<String> staticImports, CompilationUnit compilationUnit) {
        compilationUnit.getImports().add(new ImportDeclaration(new Name(ruleClassName), true, true));
        for (String i : imports) {
            compilationUnit.getImports().add( new ImportDeclaration(new Name(i), false, false ) );
        }
        for (String si : staticImports) {
            String replace = si;
            if (si.endsWith(".*")) { // JP doesn't want the * in the import
                replace = si.replace(".*", "");
                compilationUnit.getImports().add(new ImportDeclaration(new Name(replace), true, true));
            } else {
                compilationUnit.getImports().add(new ImportDeclaration(new Name(replace), true, false));
            }
        }
        compilationUnit.getImports().add(new ImportDeclaration(new Name("org.drools.modelcompiler.dsl.pattern.D"), false, false));
    }

    private void parseParameters() {
        NodeList<Parameter> parameters = lambdaExpr.getParameters();
        for (Parameter p : parameters) {
            Type c = p.getType();
            if (c instanceof UnknownType) {
                throw new LambdaTypeNeededException(lambdaExpr.toString());
            }
            lambdaParameters.add(new LambdaParameter(p.getNameAsString(), c));
        }
    }

    void setMethodParameter(MethodDeclaration methodDeclaration) {
        for (LambdaParameter parameter : lambdaParameters) {
            methodDeclaration.addParameter(new Parameter(parameter.type, parameter.name));
        }
    }

    protected EnumDeclaration create(CompilationUnit compilationUnit) {
        EnumDeclaration lambdaClass = compilationUnit.addEnum(temporaryClassHash);
        lambdaClass.addAnnotation(createSimpleAnnotation(org.drools.compiler.kie.builder.MaterializedLambda.class));
        lambdaClass.setImplementedTypes(createImplementedTypes());
        lambdaClass.addEntry(new EnumConstantDeclaration("INSTANCE"));

        String expressionHash = md5Hash(MATERIALIZED_LAMBDA_PRETTY_PRINTER.print(lambdaExpr));
        String expressionHashFieldName = "EXPRESSION_HASH";
        lambdaClass.addFieldWithInitializer(String.class, expressionHashFieldName, new StringLiteralExpr(expressionHash),
                Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC, Modifier.Keyword.FINAL);

        createGetterForExpressionHashField(lambdaClass, expressionHashFieldName);

        return lambdaClass;
    }

    private void createGetterForExpressionHashField(EnumDeclaration clazz, String expressionHashFieldName) {
        final MethodDeclaration getter;
        getter = clazz.addMethod("getExpressionHash", Modifier.Keyword.PUBLIC);
        getter.setType(toClassOrInterfaceType(String.class));
        BlockStmt blockStmt = new BlockStmt();
        getter.setBody(blockStmt);
        blockStmt.addStatement(new ReturnStmt(new NameExpr(expressionHashFieldName)));
    }

    protected NodeList<ClassOrInterfaceType> createImplementedTypes() {
        ClassOrInterfaceType functionType = functionType();

        if (!lambdaParameters.isEmpty()) {
            functionType.setTypeArguments(lambdaParametersToTypeArguments());
        }

        return NodeList.nodeList(functionType, lambdaExtractorType());
    }

    protected ClassOrInterfaceType lambdaExtractorType() {
        return toClassOrInterfaceType(HashedExpression.class);
    }

    NodeList<Type> lambdaParametersToTypeArguments() {
        NodeList<Type> typeArguments = new NodeList<>();
        for (LambdaParameter lp : lambdaParameters) {
            typeArguments.add(lp.type);
        }
        return typeArguments;
    }

    protected String classHash(String sourceCode) {
        return md5Hash(sourceCode);
    }

    abstract String getPrefix();

    abstract ClassOrInterfaceType functionType();

    abstract void createMethodsDeclaration(EnumDeclaration classDeclaration);

    static class LambdaParameter {

        String name;
        Type type;

        LambdaParameter(String name, Type type) {
            this.name = name;
            this.type = type;
        }
    }

    interface BitMaskVariable {

        void generateBitMaskField(NodeWithMembers<EnumDeclaration> clazz);
    }

    static class AllSetButLastBitMask implements BitMaskVariable {

        String maskName;
        String bitMaskString = "org.drools.model.bitmask.AllSetButLastBitMask";

        public AllSetButLastBitMask(String maskName) {
            this.maskName = maskName;
        }

        @Override
        public void generateBitMaskField(NodeWithMembers<EnumDeclaration> clazz) {
            Type bitMaskType = toClassOrInterfaceType(bitMaskString);

            MethodCallExpr methodCallExpr = new MethodCallExpr(new NameExpr(bitMaskString), "get");
            clazz.addFieldWithInitializer(bitMaskType, maskName, methodCallExpr, Modifier.Keyword.PRIVATE, Modifier.Keyword.FINAL);
        }
    }

    static class BitMaskVariableWithFields implements BitMaskVariable {

        String bitMaskString = "org.drools.model.BitMask";

        String domainClassMetadata;
        List<String> fields;
        String maskName;

        public BitMaskVariableWithFields(String domainClassMetadata, List<String> fields, String maskName) {
            this.domainClassMetadata = domainClassMetadata;
            this.fields = fields;
            this.maskName = maskName;
        }

        @Override
        public void generateBitMaskField(NodeWithMembers<EnumDeclaration> clazz) {
            Type bitMaskType = toClassOrInterfaceType(bitMaskString);

            NodeList<Expression> args = new NodeList<>();
            args.add(new NameExpr(domainClassMetadata));
            args.addAll(fields.stream().map(NameExpr::new).collect(Collectors.toList()));
            MethodCallExpr methodCallExpr = new MethodCallExpr(new NameExpr(bitMaskString), "getPatternMask", args);
            clazz.addFieldWithInitializer(bitMaskType, maskName, methodCallExpr, Modifier.Keyword.PRIVATE, Modifier.Keyword.FINAL);
        }
    }
}
