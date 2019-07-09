package org.drools.modelcompiler.builder.generator.query;

import java.util.stream.IntStream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.BlockComment;
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

import static com.github.javaparser.StaticJavaParser.parse;
import static com.github.javaparser.StaticJavaParser.parseBodyDeclaration;
import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.StaticJavaParser.parseImport;
import static com.github.javaparser.StaticJavaParser.parseType;
import static com.github.javaparser.ast.NodeList.nodeList;

public class QueryDefImplGenerator {

    private final int arity;
    private final String className;

    QueryDefImplGenerator(int arity) {
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
//        copyright(cu);
        firstConstructor(clazz);
        secondConstructor(clazz);
        thirdConstructor(clazz);
        fourthConstructor(clazz);
        callMethod(clazz);
        getArgumentsMethod(clazz);
        getters(clazz);
        equals(clazz);

        return cu;
    }

    private ClassOrInterfaceDeclaration classDeclaration(CompilationUnit cu) {
        ClassOrInterfaceDeclaration clazz = cu.addClass(className, Modifier.Keyword.PUBLIC);

        clazz.addExtendedType("QueryDefImpl");
        clazz.addImplementedType("ModelComponent");

        ClassOrInterfaceType implement = new ClassOrInterfaceType(null, String.format("Query%dDef", arity));
        NodeList<Type> typeArguments = nodeList();

        rangeArity().forEach(i -> {
            String genericTypeName = stringWithIndex("T", i);

            typeArguments.add(parseType(genericTypeName));
            clazz.addTypeParameter(new TypeParameter(genericTypeName));

            clazz.addField(parseType(genericType("Variable", genericTypeName)), stringWithIndex("arg", i), Modifier.Keyword.PRIVATE, Modifier.Keyword.FINAL);
        });

        implement.setTypeArguments(typeArguments);
        clazz.addImplementedType(implement);
        return clazz;
    }

    private void copyright(CompilationUnit cu) {
        cu.addOrphanComment(new BlockComment("\n" +
                                                     " * Copyright 2005 JBoss Inc\n" +
                                                     " *\n" +
                                                     " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                                                     " * you may not use this file except in compliance with the License.\n" +
                                                     " * You may obtain a copy of the License at\n" +
                                                     " *\n" +
                                                     " *      http://www.apache.org/licenses/LICENSE-2.0\n" +
                                                     " *\n" +
                                                     " * Unless required by applicable law or agreed to in writing, software\n" +
                                                     " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                                                     " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                                                     " * See the License for the specific language governing permissions and\n" +
                                                     " * limitations under the License.\n" +
                                                     " "));
    }

    private void firstConstructor(ClassOrInterfaceDeclaration clazz) {
        ConstructorDeclaration constructorDeclaration1 = clazz.addConstructor(Modifier.Keyword.PUBLIC);
        constructorDeclaration1.addParameter("ViewBuilder", "viewBuilder");
        constructorDeclaration1.addParameter("String", "name");
        BlockStmt stmts = new BlockStmt();
        MethodCallExpr ctorDeclaration = new MethodCallExpr(null, "this");
        stmts.addStatement(ctorDeclaration);
        ctorDeclaration.addArgument("viewBuilder");
        ctorDeclaration.addArgument("DEFAULT_PACKAGE");
        ctorDeclaration.addArgument("name");

        rangeArity().forEach(i -> {
            String genericTypeName = stringWithIndex("T", i);
            String typeWithIndex = stringWithIndex("type", i);

            constructorDeclaration1.addParameter(genericType("Class", genericTypeName), typeWithIndex);
            ctorDeclaration.addArgument(typeWithIndex);
        });

        constructorDeclaration1.setBody(stmts);
    }

    private void secondConstructor(ClassOrInterfaceDeclaration clazz) {
        ConstructorDeclaration declaration = clazz.addConstructor(Modifier.Keyword.PUBLIC);
        declaration.addParameter("ViewBuilder", "viewBuilder");
        declaration.addParameter("String", "name");
        declaration.addParameter("String", "pkg");
        BlockStmt stmts = new BlockStmt();
        MethodCallExpr body = new MethodCallExpr(null, "super");
        stmts.addStatement(body);
        body.addArgument("viewBuilder");
        body.addArgument("pkg");
        body.addArgument("name");

        rangeArity().forEach(i -> {
            String typeWithIndex = stringWithIndex("type", i);
            String genericTypeName = stringWithIndex("T", i);


            declaration.addParameter(genericType("Class", genericTypeName), typeWithIndex);

            AssignExpr assignExpr = new AssignExpr();
            assignExpr.setTarget(new FieldAccessExpr(new NameExpr("this"), stringWithIndex("arg", i)));
            assignExpr.setValue(new MethodCallExpr(null, "declarationOf", nodeList(new NameExpr(typeWithIndex))));

            stmts.addStatement(assignExpr);
        });

        declaration.setBody(stmts);
    }

    private void thirdConstructor(ClassOrInterfaceDeclaration clazz) {
        ConstructorDeclaration ctorDeclaration = clazz.addConstructor(Modifier.Keyword.PUBLIC);
        ctorDeclaration.addParameter("ViewBuilder", "viewBuilder");
        ctorDeclaration.addParameter("String", "name");
        BlockStmt ctor2Stmt = new BlockStmt();
        MethodCallExpr ctorDeclarationBody = new MethodCallExpr(null, "this");
        ctor2Stmt.addStatement(ctorDeclarationBody);
        ctorDeclarationBody.addArgument("viewBuilder");
        ctorDeclarationBody.addArgument("DEFAULT_PACKAGE");
        ctorDeclarationBody.addArgument("name");

        rangeArity().forEach(i -> {
            String genericTypeName = stringWithIndex("T", i);
            String typeWithIndex = stringWithIndex("type", i);
            String argWithIndex = stringWithIndexInside(i, "arg", "name");

            ctorDeclaration.addParameter(genericType("Class", genericTypeName), typeWithIndex);
            ctorDeclaration.addParameter("String", argWithIndex);
            ctorDeclarationBody.addArgument(typeWithIndex);
            ctorDeclarationBody.addArgument(argWithIndex);
        });

        ctorDeclaration.setBody(ctor2Stmt);
    }

    private void fourthConstructor(ClassOrInterfaceDeclaration clazz) {
        ConstructorDeclaration constructorDeclaration2 = clazz.addConstructor(Modifier.Keyword.PUBLIC);
        constructorDeclaration2.addParameter("ViewBuilder", "viewBuilder");
        constructorDeclaration2.addParameter("String", "pkg");
        constructorDeclaration2.addParameter("String", "name");
        BlockStmt stmts = new BlockStmt();
        MethodCallExpr ctorDeclaration = new MethodCallExpr(null, "super");
        stmts.addStatement(ctorDeclaration);
        ctorDeclaration.addArgument("viewBuilder");
        ctorDeclaration.addArgument("pkg");
        ctorDeclaration.addArgument("name");

        rangeArity().forEach(i -> {
            String typeWithIndex = stringWithIndex("type", i);
            String genericTypeName = stringWithIndex("T", i);
            String argWithIndex = stringWithIndexInside(i, "arg", "name");

            constructorDeclaration2.addParameter(genericType("Class", genericTypeName), typeWithIndex);
            constructorDeclaration2.addParameter("String", argWithIndex);

            AssignExpr assignExpr = new AssignExpr();
            assignExpr.setTarget(new FieldAccessExpr(new NameExpr("this"), stringWithIndex("arg", i)));
            assignExpr.setValue(new MethodCallExpr(null, "declarationOf", nodeList(new NameExpr(typeWithIndex), new NameExpr(argWithIndex))));

            stmts.addStatement(assignExpr);
        });

        constructorDeclaration2.setBody(stmts);
    }

    private void callMethod(ClassOrInterfaceDeclaration clazz) {
        MethodDeclaration method = clazz.addMethod("call", Modifier.Keyword.PUBLIC);
        addOverride(method);
        method.addParameter("boolean", "open");
        method.setType(parseClassOrInterfaceType("QueryCallViewItem"));

        BlockStmt stmts = new BlockStmt();
        NodeList<Expression> arguments = nodeList();
        ClassOrInterfaceType queryCallViewItemImpl = parseClassOrInterfaceType("QueryCallViewItemImpl");
        ObjectCreationExpr objCreationExpr = new ObjectCreationExpr(null, queryCallViewItemImpl, arguments);
        stmts.addStatement(new ReturnStmt(objCreationExpr));
        objCreationExpr.addArgument("this");
        objCreationExpr.addArgument("open");

        rangeArity().forEach(i -> {
            String varWithIndex = stringWithIndex("var", i);
            String genericTypeName = stringWithIndex("T", i);

            method.addParameter(genericType("Argument", genericTypeName), varWithIndex);

            objCreationExpr.addArgument(varWithIndex);
        });

        method.setBody(stmts);
    }

    private MethodDeclaration addOverride(MethodDeclaration method) {
        return method.addAnnotation("Override");
    }

    private void getArgumentsMethod(ClassOrInterfaceDeclaration clazz) {
        MethodDeclaration method = clazz.addMethod("getArguments", Modifier.Keyword.PUBLIC);
        addOverride(method);
        Type variableArrayType = parseType("Variable<?>[]");
        method.setType(variableArrayType);

        BlockStmt stmts = new BlockStmt();
        ArrayInitializerExpr arrayInitializerExpr = new ArrayInitializerExpr();
        ArrayCreationExpr arrayCreation = new ArrayCreationExpr(variableArrayType, nodeList(), arrayInitializerExpr);
        stmts.addStatement(new ReturnStmt(arrayCreation));

        NodeList<Expression> values = nodeList();
        rangeArity().forEach(i -> {
            String argIndex = stringWithIndex("arg", i);

            values.add(new NameExpr(argIndex));
        });

        arrayInitializerExpr.setValues(values);

        method.setBody(stmts);
    }

    private void getters(ClassOrInterfaceDeclaration clazz) {
        rangeArity().forEach(i -> {
            String genericTypeName = stringWithIndex("T", i);
            String methodName = stringWithIndex("getArg", i);
            String argWithIndex = stringWithIndex("arg", i);

            MethodDeclaration methodDeclaration = clazz.addMethod(methodName, Modifier.Keyword.PUBLIC);
            addOverride(methodDeclaration);
            methodDeclaration.setType(genericType("Variable", genericTypeName));
            methodDeclaration.setBody(new BlockStmt(nodeList(new ReturnStmt(new NameExpr(argWithIndex)))));
        });
    }

    private void equals(ClassOrInterfaceDeclaration clazz) {


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
            String argWithIndex = stringWithIndex("arg", i);

            Expression e = parseExpression(String.format("ModelComponent.areEqualInModel( %s, that.%s )", argWithIndex, argWithIndex));

            andExpr = new BinaryExpr(andExpr, e, BinaryExpr.Operator.AND);
        }

        final Expression finalAndExpr = andExpr;

        parse.findAll(ClassOrInterfaceType.class, n -> n.toString().equals("DEF_IMPL_TYPE"))
                .forEach(s -> s.replace(parseType("Query" + arity + "DefImpl")));

        parse.findAll(NameExpr.class, n -> n.toString().equals("EQUALS_CALL")).forEach(s -> s.replace(finalAndExpr));


        clazz.addMember(parse);
    }

    private String stringWithIndex(String pre, int i) {
        return pre + i;
    }

    private String stringWithIndexInside(int i, String pre, String post) {
        return pre + i + post;
    }

    private IntStream rangeArity() {
        return IntStream.range(1, arity + 1);
    }

    private String genericType(String typeName, String genericTypeName) {
        return typeName + "<" + genericTypeName + ">";
    }
}
