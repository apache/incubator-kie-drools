package org.drools.modelcompiler.builder;

import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class GeneratedClassWithPackage {
    private final ClassOrInterfaceDeclaration generatedClass;
    private final String packageName;

    public GeneratedClassWithPackage(ClassOrInterfaceDeclaration generatedClass, String packageName) {
        this.generatedClass = generatedClass;
        this.packageName = packageName;
    }

    public ClassOrInterfaceDeclaration getGeneratedClass() {
        return generatedClass;
    }

    public String getPackageName() {
        return packageName;
    }
}