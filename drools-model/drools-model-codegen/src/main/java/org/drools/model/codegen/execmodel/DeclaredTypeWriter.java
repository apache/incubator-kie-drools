package org.drools.model.codegen.execmodel;

import com.github.javaparser.ast.body.TypeDeclaration;

public class DeclaredTypeWriter {

    protected final TypeDeclaration generatedPojo;
    protected final PackageModel pkgModel;
    private final String name;

    public DeclaredTypeWriter(TypeDeclaration generatedPojo, PackageModel pkgModel) {
        this.generatedPojo = generatedPojo;
        this.name = generatedPojo.getNameAsString();
        this.pkgModel = pkgModel;
    }

    public String getSource() {
        return JavaParserCompiler.toPojoSource(
                pkgModel.getName(),
                pkgModel.getImports(),
                pkgModel.getStaticImports(),
                generatedPojo);
    }

    public String getName() {
        return pkgModel.getPathName() + "/" + name + ".java";
    }

    public String getClassName() {
        return pkgModel.getName() + "." + name;
    }
}
