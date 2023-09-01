package org.drools.model.codegen.execmodel;

import com.github.javaparser.ast.body.TypeDeclaration;

public class AccumulateClassWriter {

    protected final TypeDeclaration generatedPojo;
    protected final PackageModel pkgModel;
    private final GeneratedClassWithPackage generatedClassWithPackage;
    private final String name;

    public AccumulateClassWriter(GeneratedClassWithPackage pojo, PackageModel packageModel) {
        TypeDeclaration genClass = pojo.getGeneratedClass();
        this.generatedPojo = genClass;
        this.name = genClass.getNameAsString();
        this.pkgModel = packageModel;
        this.generatedClassWithPackage = pojo;
    }

    public String getSource() {
        return JavaParserCompiler.toPojoSource(
                pkgModel.getName(),
                generatedClassWithPackage.getImports(),
                pkgModel.getStaticImports(),
                generatedClassWithPackage.getGeneratedClass());
    }

    public String getName() {
        return pkgModel.getPathName() + "/" + name + ".java";
    }
}
