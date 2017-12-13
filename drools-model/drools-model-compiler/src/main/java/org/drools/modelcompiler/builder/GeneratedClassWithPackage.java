package org.drools.modelcompiler.builder;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class GeneratedClassWithPackage {
    private final ClassOrInterfaceDeclaration generatedClass;
    private final String packageName;
    private final Collection<String> imports = new ArrayList<>();

    public GeneratedClassWithPackage(ClassOrInterfaceDeclaration generatedClass, String packageName) {
        this.generatedClass = generatedClass;
        this.packageName = packageName;
    }

    public GeneratedClassWithPackage(ClassOrInterfaceDeclaration generatedClass, String packageName, Collection<String> imports) {
        this(generatedClass, packageName);
        this.imports.addAll(imports);
    }

    public ClassOrInterfaceDeclaration getGeneratedClass() {
        return generatedClass;
    }

    public String getPackageName() {
        return packageName;
    }

    public void addImport(String importClass) {
        imports.add(importClass);
    }

    public Collection<String> getImports() {
        return imports;
    }

    @Override
    public String toString() {
        return "package " + packageName + "\n" + generatedClass;
    }

}