package org.kie.dmn.core.compiler.execmodelbased;

import java.util.EnumSet;
import java.util.function.Consumer;

import org.drools.javaparser.ast.CompilationUnit;
import org.drools.javaparser.ast.Modifier;
import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.javaparser.ast.body.ConstructorDeclaration;
import org.drools.javaparser.ast.body.FieldDeclaration;
import org.drools.javaparser.ast.body.VariableDeclarator;

public class JavaParserSourceGenerator {

    private ClassOrInterfaceDeclaration firstClass;
    private CompilationUnit compilationUnit;

    public static EnumSet<Modifier> PUBLIC_STATIC_FINAL = EnumSet.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);

    public JavaParserSourceGenerator(CompilationUnit compilationUnit) {
        firstClass = compilationUnit.findFirst(ClassOrInterfaceDeclaration.class).orElseThrow(() -> new RuntimeException("Cannot find Class"));
        this.compilationUnit = compilationUnit;
    }

    public void insideClass(Consumer<ClassOrInterfaceDeclaration> func) {
        func.accept(firstClass);
    }

    public void addImports(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            compilationUnit.addImport(clazz);
        }
    }

    public ClassOrInterfaceDeclaration addMember(ClassOrInterfaceDeclaration feelExpressionSource) {
        return firstClass.addMember(feelExpressionSource);
    }

    public ClassOrInterfaceDeclaration addMember(FieldDeclaration feelExpressionSource) {
        return firstClass.addMember(feelExpressionSource);
    }

    public void addFieldDeclaration(VariableDeclarator variableDeclarator) {
        firstClass.addMember(new FieldDeclaration(PUBLIC_STATIC_FINAL, variableDeclarator));
    }

    public static void renameFeelExpressionClass(String testClass, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        final String finalTestClass = testClass;
        classOrInterfaceDeclaration
                .setName(finalTestClass);

        classOrInterfaceDeclaration.findAll(ConstructorDeclaration.class)
                .forEach(n -> n.replace(new ConstructorDeclaration(finalTestClass)));
    }
}
