package org.kie.dmn.core.compiler.execmodelbased;

import java.util.function.Consumer;

import org.drools.javaparser.ast.CompilationUnit;
import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.javaparser.ast.body.ConstructorDeclaration;

public class JavaParserSourceGenerator {

    ClassOrInterfaceDeclaration coid;

    public JavaParserSourceGenerator(CompilationUnit parse) {
        coid = parse.findFirst(ClassOrInterfaceDeclaration.class).orElseThrow(() -> new RuntimeException("Cannot find Class"));
    }

    public void insideClass(Consumer<ClassOrInterfaceDeclaration> func) {
        func.accept(coid);
    }

    public static void renameFeelExpressionClass(String testClass, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        final String finalTestClass = testClass;
        classOrInterfaceDeclaration
                .setName(finalTestClass);

        classOrInterfaceDeclaration.findAll(ConstructorDeclaration.class)
                .forEach(n -> n.replace(new ConstructorDeclaration(finalTestClass)));
    }
}
