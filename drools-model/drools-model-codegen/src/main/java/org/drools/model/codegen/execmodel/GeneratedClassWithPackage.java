package org.drools.model.codegen.execmodel;

import java.util.Collection;
import java.util.Objects;

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

    public String getClassName() {
        return generatedClass.getNameAsString();
    }

    public String getFullyQualifiedName() {
        return getPackageName() + "." + generatedClass.getNameAsString();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneratedClassWithPackage that = (GeneratedClassWithPackage) o;
        return getPackageName().equals(that.getPackageName()) && getClassName().equals(that.getClassName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPackageName(), getClassName());
    }
}