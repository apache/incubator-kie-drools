package org.drools.modelcompiler.builder.generator.query;

import java.util.stream.IntStream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.TypeParameter;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static com.github.javaparser.StaticJavaParser.parseImport;
import static com.github.javaparser.ast.NodeList.nodeList;

public class QueryDefGenerator {

    private final int arity;

    QueryDefGenerator(int arity) {
        this.arity = arity;
    }

    public CompilationUnit generate() {

        CompilationUnit cu = new CompilationUnit("org.drools.model");

        cu.setImports(nodeList(
                parseImport("import org.drools.model.view.QueryCallViewItem;")
        ));

        ClassOrInterfaceDeclaration clazz = classDeclaration(cu);
        callMethod(clazz);
        callMethodInterface(clazz);
        getters(clazz);

        return cu;
    }

    private ClassOrInterfaceDeclaration classDeclaration(CompilationUnit cu) {
        ClassOrInterfaceDeclaration clazz = cu.addInterface(String.format("Query%dDef", arity), Modifier.Keyword.PUBLIC);

        clazz.addExtendedType("QueryDef");


        rangeArity().forEach(i -> {
            String genericTypeName = stringWithIndex("T", i);

            clazz.addTypeParameter(new TypeParameter(genericTypeName));
        });

        return clazz;
    }

    private void callMethod(ClassOrInterfaceDeclaration clazz) {
        MethodDeclaration method = clazz.addMethod("call", Modifier.Keyword.DEFAULT);
        method.addParameter("boolean", "open");
        method.setType(parseClassOrInterfaceType("QueryCallViewItem"));

        BlockStmt stmts = new BlockStmt();
        NodeList<Expression> arguments = nodeList();
        MethodCallExpr objCreationExpr = new MethodCallExpr(null, "call", arguments);
        stmts.addStatement(new ReturnStmt(objCreationExpr));
        objCreationExpr.addArgument(new BooleanLiteralExpr(true));

        rangeArity().forEach(i -> {
            String varWithIndex = stringWithIndex("var", i);
            String genericTypeName = stringWithIndex("T", i);

            method.addParameter(genericType("Argument", genericTypeName), varWithIndex);

            objCreationExpr.addArgument(varWithIndex);
        });

        method.setBody(stmts);
    }


    private void callMethodInterface(ClassOrInterfaceDeclaration clazz) {
        MethodDeclaration method = new MethodDeclaration(nodeList(), parseClassOrInterfaceType("QueryCallViewItem"), "call");
        method.setBody(null);

        rangeArity().forEach(i -> {
            String varWithIndex = stringWithIndex("var", i);
            String genericTypeName = stringWithIndex("T", i);

            method.addParameter(genericType("Argument", genericTypeName), varWithIndex);

        });

        clazz.addMember(method);

    }

    private void getters(ClassOrInterfaceDeclaration clazz) {
        rangeArity().forEach(i -> {
            String genericTypeName = stringWithIndex("T", i);
            String methodName = stringWithIndex("getArg", i);

            MethodDeclaration methodDeclaration = clazz.addMethod(methodName, Modifier.Keyword.PUBLIC);
            methodDeclaration.setBody(null);
            methodDeclaration.setType(genericType("Variable", genericTypeName));
        });
    }

    private String stringWithIndex(String pre, int i) {
        return pre + i;
    }

    private IntStream rangeArity() {
        return IntStream.range(1, arity + 1);
    }

    private String genericType(String typeName, String genericTypeName) {
        return typeName + "<" + genericTypeName + ">";
    }
}
