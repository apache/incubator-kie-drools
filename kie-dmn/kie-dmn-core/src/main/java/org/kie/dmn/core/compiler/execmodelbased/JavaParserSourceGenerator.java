package org.kie.dmn.core.compiler.execmodelbased;

import java.util.EnumSet;

import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.ArrayCreationLevel;
import org.drools.javaparser.ast.CompilationUnit;
import org.drools.javaparser.ast.Modifier;
import org.drools.javaparser.ast.NodeList;
import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.javaparser.ast.body.ConstructorDeclaration;
import org.drools.javaparser.ast.body.FieldDeclaration;
import org.drools.javaparser.ast.body.VariableDeclarator;
import org.drools.javaparser.ast.expr.ArrayCreationExpr;
import org.drools.javaparser.ast.expr.ArrayInitializerExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.ObjectCreationExpr;
import org.drools.javaparser.ast.type.ArrayType;
import org.drools.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.javaparser.ast.type.Type;

public class JavaParserSourceGenerator {

    private ClassOrInterfaceDeclaration firstClass;
    private CompilationUnit compilationUnit;
    private String className;

    public static EnumSet<Modifier> PUBLIC_STATIC_FINAL = EnumSet.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);

    public JavaParserSourceGenerator(CompilationUnit compilationUnit, String className) {
        firstClass = compilationUnit.findFirst(ClassOrInterfaceDeclaration.class).orElseThrow(() -> new RuntimeException("Cannot find Class"));
        this.compilationUnit = compilationUnit;
        this.className = className;
    }

    public void addImports(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            compilationUnit.addImport(clazz);
        }
    }

    public void addInnerClassWithName(ClassOrInterfaceDeclaration feelExpressionSource, String name) {
        renameFeelExpressionClass(name, feelExpressionSource);
        firstClass.addMember(feelExpressionSource);
    }

    public void addMember(FieldDeclaration feelExpressionSource) {
        firstClass.addMember(feelExpressionSource);
    }

    public void addField(String testClass, Type type, String instanceName) {
        ClassOrInterfaceType innerClassType = JavaParser.parseClassOrInterfaceType(testClass);
        ObjectCreationExpr newInstanceOfInnerClass = new ObjectCreationExpr(null, innerClassType, NodeList.nodeList());
        VariableDeclarator variableDeclarator = new VariableDeclarator(type, instanceName, newInstanceOfInnerClass);
        firstClass.addMember(new FieldDeclaration(PUBLIC_STATIC_FINAL, variableDeclarator));
    }

    public void addTwoDimensionalArray(NodeList<Expression> arrayInitializer, String arrayName, Type type) {
        NodeList<ArrayCreationLevel> arrayCreationLevels = NodeList.nodeList(new ArrayCreationLevel(), new ArrayCreationLevel());
        ArrayInitializerExpr initializerMainArray = new ArrayInitializerExpr(arrayInitializer);
        ArrayCreationExpr arrayCreationExpr = new ArrayCreationExpr(type, arrayCreationLevels, initializerMainArray);
        VariableDeclarator variable = new VariableDeclarator(new ArrayType(new ArrayType(type)), arrayName, arrayCreationExpr);
        addMember(new FieldDeclaration(PUBLIC_STATIC_FINAL, variable));
    }

    private void renameFeelExpressionClass(String testClass, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        final String finalTestClass = testClass;
        classOrInterfaceDeclaration
                .setName(finalTestClass);

        classOrInterfaceDeclaration.findAll(ConstructorDeclaration.class)
                .forEach(n -> n.replace(new ConstructorDeclaration(finalTestClass)));
    }
}
