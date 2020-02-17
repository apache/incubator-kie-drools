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

package org.drools.modelcompiler.util.lambdareplace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithMembers;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.UnknownType;
import org.drools.modelcompiler.builder.JavaParserCompiler;

import static org.drools.modelcompiler.util.StringUtil.md5Hash;

import static com.github.javaparser.StaticJavaParser.parseType;

abstract class MaterializedLambda {

    final List<LambdaParameter> lambdaParameters = new ArrayList<>();

    protected final String packageName;
    protected String temporaryClassName;

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

        lambdaExpr = expression.asLambdaExpr();
        temporaryClassName = className(expressionString);

        parseParameters();

        CompilationUnit compilationUnit = new CompilationUnit(packageName);
        addImports(imports, staticImports, compilationUnit);

        EnumDeclaration classDeclaration = create(compilationUnit);

        createMethodDeclaration(classDeclaration);

        String className = className(JavaParserCompiler.getPrettyPrinter().print(compilationUnit));
        classDeclaration.setName(className);

        return new CreatedClass(compilationUnit, className, packageName);
    }

    private void addImports(Collection<String> imports, Collection<String> staticImports, CompilationUnit compilationUnit) {
        compilationUnit.addImport(ruleClassName, true, true);
        for (String i : imports) {
            compilationUnit.addImport(i);
        }
        for (String si : staticImports) {
            String replace = si;
            if (si.endsWith(".*")) { // JP doesn't want the * in the import
                replace = si.replace(".*", "");
                compilationUnit.addImport(replace, true, true);
            } else {
                compilationUnit.addImport(replace, true, false);
            }
        }
        compilationUnit.addImport("org.drools.modelcompiler.dsl.pattern.D");
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
        EnumDeclaration lambdaClass = compilationUnit.addEnum(temporaryClassName);
        lambdaClass.addAnnotation(org.drools.compiler.kie.builder.MaterializedLambda.class.getCanonicalName());
        lambdaClass.setImplementedTypes(createImplementedType());
        lambdaClass.addEntry(new EnumConstantDeclaration("INSTANCE"));

        lambdaClass.addFieldWithInitializer(String.class, "EXPRESSION_HASH", StaticJavaParser.parseExpression("\"" + (md5Hash(lambdaExpr.toString())) + "\""),
                                            Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC, Modifier.Keyword.FINAL);
        return lambdaClass;
    }

    protected NodeList<ClassOrInterfaceType> createImplementedType() {
        ClassOrInterfaceType functionType = functionType();

        List<Type> typeArguments = lambdaParametersToType();
        if (!typeArguments.isEmpty()) {
            functionType.setTypeArguments(NodeList.nodeList(typeArguments));
        }
        return NodeList.nodeList(functionType);
    }

    List<Type> lambdaParametersToType() {
        return lambdaParameters.stream()
                .map(p -> p.type)
                .collect(Collectors.toList());
    }

    abstract String className(String sourceCode);

    abstract ClassOrInterfaceType functionType();

    abstract void createMethodDeclaration(EnumDeclaration classDeclaration);

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
            Type bitMaskType = parseType(bitMaskString);

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
            Type bitMaskType = parseType(bitMaskString);

            NodeList<Expression> args = new NodeList<>();
            args.add(new NameExpr(domainClassMetadata));
            args.addAll(fields.stream().map(NameExpr::new).collect(Collectors.toList()));
            MethodCallExpr methodCallExpr = new MethodCallExpr(new NameExpr(bitMaskString), "getPatternMask", args);
            clazz.addFieldWithInitializer(bitMaskType, maskName, methodCallExpr, Modifier.Keyword.PRIVATE, Modifier.Keyword.FINAL);
        }
    }
}
