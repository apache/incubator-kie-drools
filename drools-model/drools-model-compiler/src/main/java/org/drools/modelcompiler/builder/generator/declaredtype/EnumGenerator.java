package org.drools.modelcompiler.builder.generator.declaredtype;

import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.compiler.lang.descr.EnumDeclarationDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.modelcompiler.builder.ModelBuilderImpl;
import org.drools.modelcompiler.builder.PackageModel;

public class EnumGenerator {

    private final ModelBuilderImpl builder;
    private final InternalKnowledgePackage pkg;
    private final PackageModel packageModel;

    public EnumGenerator(ModelBuilderImpl builder, InternalKnowledgePackage pkg, PackageModel packageModel) {
        this.builder = builder;
        this.pkg = pkg;
        this.packageModel = packageModel;
    }

    public ClassOrInterfaceDeclaration generate(List<EnumDeclarationDescr> enumDeclarations) {

        return new ClassOrInterfaceDeclaration();
    }
}
