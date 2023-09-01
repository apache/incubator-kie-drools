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
package org.drools.model.codegen.execmodel.generator.query;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import org.drools.model.view.QueryCallViewItem;
import org.drools.model.view.QueryCallViewItemImpl;

import static com.github.javaparser.StaticJavaParser.parseBodyDeclaration;
import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.StaticJavaParser.parseImport;
import static com.github.javaparser.StaticJavaParser.parseType;
import static com.github.javaparser.ast.NodeList.nodeList;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toClassOrInterfaceType;

public class QueryDefImplGenerator extends Generator {

    private final int arity;
    private final String className;

    QueryDefImplGenerator(int arity) {
        super(arity);
        this.arity = arity;
        className = String.format("Query%dDefImpl", arity);
    }

    public String getClassName() {
        return className;
    }

    public CompilationUnit generate() {

        CompilationUnit cu = new CompilationUnit("org.drools.model.impl");

        cu.setImports(nodeList(
                parseImport("import org.drools.model.Argument;"),
                parseImport(String.format("import org.drools.model.Query%dDef;", arity)),
                parseImport("import org.drools.model.Variable;"),
                parseImport("import org.drools.model.view.QueryCallViewItem;"),
                parseImport("import org.drools.model.view.QueryCallViewItemImpl;"),
                parseImport("import static org.drools.model.FlowDSL.declarationOf;"),
                parseImport("import static org.drools.model.impl.RuleBuilder.DEFAULT_PACKAGE;")
        ));

        ClassOrInterfaceDeclaration clazz = classDeclaration(cu);
        nameClassConstructor(clazz);
        packageNameClassConstructor(clazz);
        nameClassArgConstructor(clazz);
        pkgNameClassArgConstructor(clazz);

        callMethod(clazz);
        getArgumentsMethod(clazz);
        getters(clazz);
        generateEquals(clazz);

        return cu;
    }

    private ClassOrInterfaceDeclaration classDeclaration(CompilationUnit cu) {
        ClassOrInterfaceDeclaration clazz = cu.addClass(className, Modifier.Keyword.PUBLIC);

        clazz.addExtendedType("QueryDefImpl");
        clazz.addImplementedType("ModelComponent");

        ClassOrInterfaceType implement = new ClassOrInterfaceType(null, String.format("Query%dDef", arity));
        NodeList<Type> typeArguments = nodeList();

        rangeArity().forEach(i -> {
            String genericTypeName = genericTypeName(i);

            typeArguments.add(parseType(genericTypeName));
            clazz.addTypeParameter(new TypeParameter(genericTypeName));

            clazz.addField(parseType(genericType("Variable", genericTypeName)), argIndex(i), Modifier.Keyword.PRIVATE, Modifier.Keyword.FINAL);
        });

        implement.setTypeArguments(typeArguments);
        clazz.addImplementedType(implement);
        return clazz;
    }

    private void nameClassConstructor(ClassOrInterfaceDeclaration clazz) {
        ConstructorDeclaration constructorDeclaration1 = clazz.addConstructor(Modifier.Keyword.PUBLIC);
        addViewBuilderParameter(constructorDeclaration1);
        addNameParameter(constructorDeclaration1);
        BlockStmt statements = new BlockStmt();
        MethodCallExpr constructor = new MethodCallExpr(null, "this");
        statements.addStatement(constructor);
        addViewBuilderValue(constructor);
        addDefaultPackageValue(constructor);
        addNameValue(constructor);

        rangeArity().forEach(i -> {
            String genericTypeName = genericTypeName(i);
            String typeWithIndex = typeWithIndex(i);

            constructorDeclaration1.addParameter(classGenericParameter(genericTypeName), typeWithIndex);
            constructor.addArgument(typeWithIndex);
        });

        constructorDeclaration1.setBody(statements);
    }

    private void packageNameClassConstructor(ClassOrInterfaceDeclaration clazz) {
        ConstructorDeclaration declaration = clazz.addConstructor(Modifier.Keyword.PUBLIC);
        addViewBuilderParameter(declaration);
        addPackageParameter(declaration);
        addNameParameter(declaration);
        BlockStmt statements = new BlockStmt();
        MethodCallExpr body = new MethodCallExpr(null, "super");
        statements.addStatement(body);
        addViewBuilderValue(body);
        addPkgValue(body);
        addNameValue(body);

        rangeArity().forEach(i -> {
            String typeWithIndex = typeWithIndex(i);
            String genericTypeName = genericTypeName(i);


            declaration.addParameter(classGenericParameter(genericTypeName), typeWithIndex);

            AssignExpr assignExpr = new AssignExpr();
            assignExpr.setTarget(new FieldAccessExpr(new NameExpr("this"), argIndex(i)));
            assignExpr.setValue(new MethodCallExpr(null, "declarationOf", nodeList(new NameExpr(typeWithIndex))));

            statements.addStatement(assignExpr);
        });

        declaration.setBody(statements);
    }

    private void nameClassArgConstructor(ClassOrInterfaceDeclaration clazz) {
        ConstructorDeclaration constructor = clazz.addConstructor(Modifier.Keyword.PUBLIC);
        addViewBuilderParameter(constructor);
        addNameParameter(constructor);
        BlockStmt constructorStatements = new BlockStmt();
        MethodCallExpr constructorBody = new MethodCallExpr(null, "this");
        constructorStatements.addStatement(constructorBody);
        addViewBuilderValue(constructorBody);
        addDefaultPackageValue(constructorBody);
        addNameValue(constructorBody);

        rangeArity().forEach(i -> {
            String argWithIndex = argNameWithIndex(i);

            constructor.addParameter(classGenericName(i), typeWithIndex(i));
            constructor.addParameter(STRINGLITERAL, argWithIndex);
            constructorBody.addArgument(typeWithIndex(i));
            constructorBody.addArgument(argWithIndex);
        });

        constructor.setBody(constructorStatements);
    }

    private void pkgNameClassArgConstructor(ClassOrInterfaceDeclaration clazz) {
        ConstructorDeclaration constructorDeclaration2 = clazz.addConstructor(Modifier.Keyword.PUBLIC);
        addViewBuilderParameter(constructorDeclaration2);
        addPackageParameter(constructorDeclaration2);
        addNameParameter(constructorDeclaration2);
        BlockStmt statements = new BlockStmt();
        MethodCallExpr constructorBody = new MethodCallExpr(null, "super");
        statements.addStatement(constructorBody);
        addViewBuilderValue(constructorBody);
        addPkgValue(constructorBody);
        addNameValue(constructorBody);

        rangeArity().forEach(i -> {
            String argWithIndex = argNameWithIndex(i);

            constructorDeclaration2.addParameter(classGenericName(i), typeWithIndex(i));
            constructorDeclaration2.addParameter(STRINGLITERAL, argWithIndex);

            AssignExpr assignExpr = new AssignExpr();
            assignExpr.setTarget(new FieldAccessExpr(new NameExpr("this"), argIndex(i)));
            assignExpr.setValue(new MethodCallExpr(null, "declarationOf", nodeList(new NameExpr(typeWithIndex(i)), new NameExpr(argWithIndex))));

            statements.addStatement(assignExpr);
        });

        constructorDeclaration2.setBody(statements);
    }

    private void callMethod(ClassOrInterfaceDeclaration clazz) {
        MethodDeclaration method = clazz.addMethod("call", Modifier.Keyword.PUBLIC);
        addOverride(method);
        method.addParameter("boolean", "open");
        method.setType(toClassOrInterfaceType(QueryCallViewItem.class));

        BlockStmt statements = new BlockStmt();
        NodeList<Expression> arguments = nodeList();
        ClassOrInterfaceType queryCallViewItemImpl = toClassOrInterfaceType(QueryCallViewItemImpl.class);
        ObjectCreationExpr objCreationExpr = new ObjectCreationExpr(null, queryCallViewItemImpl, arguments);
        statements.addStatement(new ReturnStmt(objCreationExpr));
        objCreationExpr.addArgument("this");
        objCreationExpr.addArgument("open");

        rangeArity().forEach(i -> {
            String varWithIndex = stringWithIndex("var", i);
            String genericTypeName = genericTypeName(i);

            method.addParameter(genericType("Argument", genericTypeName), varWithIndex);

            objCreationExpr.addArgument(varWithIndex);
        });

        method.setBody(statements);
    }

    private void getArgumentsMethod(ClassOrInterfaceDeclaration clazz) {
        MethodDeclaration method = clazz.addMethod("getArguments", Modifier.Keyword.PUBLIC);
        addOverride(method);
        Type variableArrayType = parseType("Variable<?>[]");
        method.setType(variableArrayType);

        BlockStmt statements = new BlockStmt();
        ArrayInitializerExpr arrayInitializerExpr = new ArrayInitializerExpr();
        ArrayCreationExpr arrayCreation = new ArrayCreationExpr(variableArrayType, nodeList(), arrayInitializerExpr);
        statements.addStatement(new ReturnStmt(arrayCreation));

        NodeList<Expression> values = nodeList();
        rangeArity().forEach(i -> {
            String argIndex = argIndex(i);

            values.add(new NameExpr(argIndex));
        });

        arrayInitializerExpr.setValues(values);

        method.setBody(statements);
    }

    private void getters(ClassOrInterfaceDeclaration clazz) {
        rangeArity().forEach(i -> {
            String genericTypeName = genericTypeName(i);
            String methodName = getArgIndex(i);
            String argWithIndex = argIndex(i);

            MethodDeclaration methodDeclaration = clazz.addMethod(methodName, Modifier.Keyword.PUBLIC);
            addOverride(methodDeclaration);
            methodDeclaration.setType(genericType("Variable", genericTypeName));
            methodDeclaration.setBody(new BlockStmt(nodeList(new ReturnStmt(new NameExpr(argWithIndex)))));
        });
    }

    private void generateEquals(ClassOrInterfaceDeclaration clazz) {
        String template = "   @Override\n" +
                "    public boolean isEqualTo( ModelComponent other ) {\n" +
                "        if ( this == other ) return true;\n" +
                "        if ( !(other instanceof DEF_IMPL_TYPE) ) return false;\n" +
                "\n" +
                "        DEF_IMPL_TYPE that = (DEF_IMPL_TYPE) other;\n" +
                "\n" +
                "        return EQUALS_CALL;\n" +
                "    }";

        BodyDeclaration<?> parse = parseBodyDeclaration(template);


        Expression andExpr = new BooleanLiteralExpr(true);


        for(int i : rangeArity().toArray()) {
            String argWithIndex = argIndex(i);
            Expression e = parseExpression(String.format("ModelComponent.areEqualInModel( %s, that.%s )", argWithIndex, argWithIndex));
            andExpr = new BinaryExpr(andExpr, e, BinaryExpr.Operator.AND);
        }

        final Expression finalAndExpr = andExpr;

        parse.findAll(ClassOrInterfaceType.class, n -> n.toString().equals("DEF_IMPL_TYPE"))
                .forEach(s -> s.replace(parseType("Query" + arity + "DefImpl")));

        parse.findAll(NameExpr.class, n -> n.toString().equals("EQUALS_CALL")).forEach(s -> s.replace(finalAndExpr));


        clazz.addMember(parse);
    }

}
