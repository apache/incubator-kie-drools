package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.impl.TypeDeclarationBuilder;
import org.drools.compiler.builder.impl.TypeDefinition;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.drl.ast.descr.AbstractClassTypeDeclarationDescr;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeDeclarationCompositeCompilationPhase implements CompilationPhase {
    private Collection<CompositePackageDescr> packages;
    private final TypeDeclarationBuilder typeBuilder;

    public TypeDeclarationCompositeCompilationPhase(Collection<CompositePackageDescr> packages, TypeDeclarationBuilder typeBuilder) {
        this.packages = packages;
        this.typeBuilder = typeBuilder;
    }

    public void process() {
        Map<String,AbstractClassTypeDeclarationDescr> unprocesseableDescrs = new HashMap<>();
        List<TypeDefinition> unresolvedTypes = new ArrayList<>();
        List<AbstractClassTypeDeclarationDescr> unsortedDescrs = new ArrayList<>();
        for (CompositePackageDescr packageDescr : packages) {
            unsortedDescrs.addAll(packageDescr.getTypeDeclarations());
            unsortedDescrs.addAll(packageDescr.getEnumDeclarations());
        }

        typeBuilder.processTypeDeclarations( packages, unsortedDescrs, unresolvedTypes, unprocesseableDescrs );
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return Collections.emptyList();
    }
}
