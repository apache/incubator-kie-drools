package org.drools.modelcompiler.builder;

import java.util.Collection;

import com.github.javaparser.ast.body.TypeDeclaration;

public class GeneratedClassWithPackage {
    private final TypeDeclaration generatedClass;
    private final String packageName;
    private final Collection<String> imports;
    private final Collection<String> staticImports;

    public GeneratedClassWithPackage(TypeDeclaration generatedClass, String packageName, Collection<String> imports, Collection<String> staticImports) {
        this.generatedClass = generatedClass;
        this.packageName = packageName;
        this.imports = imports;
        this.staticImports = staticImports;
    }

    public TypeDeclaration getGeneratedClass() {
        return generatedClass;
    }

    public String getPackageName() {
        return packageName;
    }

    public Collection<String> getImports() {
        return imports;
    }

    public Collection<String> getStaticImports() {
        return staticImports;
    }

    @Override
    public String toString() {
        return "package " + packageName + "\n" + generatedClass;
    }

}