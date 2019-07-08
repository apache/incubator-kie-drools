package org.drools.modelcompiler.builder.generator.query;

import java.util.stream.IntStream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static com.github.javaparser.StaticJavaParser.parseType;
import static com.github.javaparser.ast.NodeList.nodeList;

public class PatternDSLQueryGenerator {

    private final ClassOrInterfaceDeclaration clazz;
    private final int arity;

    public PatternDSLQueryGenerator(ClassOrInterfaceDeclaration clazz, int arity) {
        this.clazz = clazz;
        this.arity = arity;
    }

    public ClassOrInterfaceDeclaration generate() {

        queryFirstMethodOnlyClass(clazz);
        querySecondMethodOnlyClass(clazz);

        queryFirstMethod(clazz);
        querySecondMethod(clazz);

        return clazz;
    }

    private void queryFirstMethodOnlyClass(ClassOrInterfaceDeclaration clazz) {
        MethodDeclaration query1 = clazz.addMethod("query", Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);


        query1.addParameter("String", "name");

        BlockStmt stmts = new BlockStmt();
        NodeList<Type> typeArguments = nodeList();
        NodeList<TypeParameter> typeParameters = nodeList();

        NodeList<Expression> arguments = nodeList();
        ClassOrInterfaceType queryCallViewItemImpl = parseClassOrInterfaceType("Query" + arity + "DefImpl<>");
        ObjectCreationExpr objCreationExpr = new ObjectCreationExpr(null, queryCallViewItemImpl, arguments);
        objCreationExpr.setDiamondOperator();
        stmts.addStatement(new ReturnStmt(objCreationExpr));
        objCreationExpr.addArgument(new NameExpr("VIEW_BUILDER"));
        objCreationExpr.addArgument("name");

        rangeArity().forEach(i -> {
            String genericTypeName = stringWithIndex("T", i);
            String type = stringWithIndex("type", i);

            typeArguments.add(parseType(genericTypeName));

            arguments.add(new NameExpr(type));

            String classGenericType = genericType("Class", genericTypeName);
            Type genericType = parseType(classGenericType);
            query1.addParameter(genericType, type);

            typeParameters.add(new TypeParameter(genericTypeName));

        });

        query1.setTypeParameters(typeParameters);


        query1.setBody(stmts);
        ClassOrInterfaceType type = parseClassOrInterfaceType("Query" + arity + "Def");
        type.setTypeArguments(typeArguments);
        query1.setType(type);

    }

    private void querySecondMethodOnlyClass(ClassOrInterfaceDeclaration clazz) {
        MethodDeclaration query1 = clazz.addMethod("query", Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);

        query1.addParameter("String", "pkg");
        query1.addParameter("String", "name");


        BlockStmt stmts = new BlockStmt();
        NodeList<Type> typeArguments = nodeList();
        NodeList<TypeParameter> typeParameters = nodeList();
        NodeList<Expression> arguments = nodeList();
        ClassOrInterfaceType queryCallViewItemImpl = parseClassOrInterfaceType("Query" + arity + "DefImpl<>");
        ObjectCreationExpr objCreationExpr = new ObjectCreationExpr(null, queryCallViewItemImpl, arguments);
        objCreationExpr.setDiamondOperator();
        stmts.addStatement(new ReturnStmt(objCreationExpr));
        objCreationExpr.addArgument(new NameExpr("VIEW_BUILDER"));
        objCreationExpr.addArgument("pkg");
        objCreationExpr.addArgument("name");

        rangeArity().forEach(i -> {
            String genericTypeName = stringWithIndex("T", i);
            String type = stringWithIndex("type", i);

            typeArguments.add(parseType(genericTypeName));

            arguments.add(new NameExpr(type));

            String classGenericType = genericType("Class", genericTypeName);
            Type genericType = parseType(classGenericType);
            query1.addParameter(genericType, type);

            typeParameters.add(new TypeParameter(genericTypeName));
        });

        query1.setTypeParameters(typeParameters);

        query1.setBody(stmts);
        ClassOrInterfaceType type = parseClassOrInterfaceType("Query" + arity + "Def");
        type.setTypeArguments(typeArguments);
        query1.setType(type);
    }

    private void queryFirstMethod(ClassOrInterfaceDeclaration clazz) {
        MethodDeclaration query1 = clazz.addMethod("query", Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);


        query1.addParameter("String", "name");

        BlockStmt stmts = new BlockStmt();
        NodeList<Type> typeArguments = nodeList();
        NodeList<TypeParameter> typeParameters = nodeList();

        NodeList<Expression> arguments = nodeList();
        ClassOrInterfaceType queryCallViewItemImpl = parseClassOrInterfaceType("Query" + arity + "DefImpl<>");
        ObjectCreationExpr objCreationExpr = new ObjectCreationExpr(null, queryCallViewItemImpl, arguments);
        objCreationExpr.setDiamondOperator();
        stmts.addStatement(new ReturnStmt(objCreationExpr));
        objCreationExpr.addArgument(new NameExpr("VIEW_BUILDER"));
        objCreationExpr.addArgument("name");

        rangeArity().forEach(i -> {
            String genericTypeName = stringWithIndex("T", i);
            String type = stringWithIndex("type", i);
            String name = stringWithIndexInside(i, "arg", "name");

            typeArguments.add(parseType(genericTypeName));

            arguments.add(new NameExpr(type));
            arguments.add(new NameExpr(name));

            String classGenericType = genericType("Class", genericTypeName);
            Type genericType = parseType(classGenericType);
            query1.addParameter(genericType, type);
            query1.addParameter(parseType("String"), name);

            typeParameters.add(new TypeParameter(genericTypeName));

        });

        query1.setTypeParameters(typeParameters);


        query1.setBody(stmts);
        ClassOrInterfaceType type = parseClassOrInterfaceType("Query" + arity + "Def");
        type.setTypeArguments(typeArguments);
        query1.setType(type);

    }

    private void querySecondMethod(ClassOrInterfaceDeclaration clazz) {
        MethodDeclaration query1 = clazz.addMethod("query", Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);

        query1.addParameter("String", "pkg");
        query1.addParameter("String", "name");


        BlockStmt stmts = new BlockStmt();
        NodeList<Type> typeArguments = nodeList();
        NodeList<TypeParameter> typeParameters = nodeList();
        NodeList<Expression> arguments = nodeList();
        ClassOrInterfaceType queryCallViewItemImpl = parseClassOrInterfaceType("Query" + arity + "DefImpl<>");
        ObjectCreationExpr objCreationExpr = new ObjectCreationExpr(null, queryCallViewItemImpl, arguments);
        objCreationExpr.setDiamondOperator();
        stmts.addStatement(new ReturnStmt(objCreationExpr));
        objCreationExpr.addArgument(new NameExpr("VIEW_BUILDER"));
        objCreationExpr.addArgument("pkg");
        objCreationExpr.addArgument("name");

        rangeArity().forEach(i -> {
            String genericTypeName = stringWithIndex("T", i);
            String type = stringWithIndex("type", i);
            String name = stringWithIndexInside(i, "arg", "name");

            typeArguments.add(parseType(genericTypeName));

            arguments.add(new NameExpr(type));
            arguments.add(new NameExpr(name));

            String classGenericType = genericType("Class", genericTypeName);
            Type genericType = parseType(classGenericType);
            query1.addParameter(genericType, type);
            query1.addParameter(parseType("String"), name);

            typeParameters.add(new TypeParameter(genericTypeName));
        });

        query1.setTypeParameters(typeParameters);

        query1.setBody(stmts);
        ClassOrInterfaceType type = parseClassOrInterfaceType("Query" + arity + "Def");
        type.setTypeArguments(typeArguments);
        query1.setType(type);
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
