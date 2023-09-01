package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.impl.TypeDeclarationBuilder;
import org.drools.compiler.builder.impl.TypeDefinition;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.TypeDeclarationError;
import org.drools.drl.ast.descr.AbstractClassTypeDeclarationDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.api.io.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeDeclarationCompilationPhase extends AbstractPackageCompilationPhase {
    private final TypeDeclarationBuilder typeBuilder;
    private final Resource currentResource;

    public TypeDeclarationCompilationPhase(PackageDescr packageDescr, TypeDeclarationBuilder typeBuilder, PackageRegistry pkgRegistry, Resource resource) {
        super(pkgRegistry, packageDescr);
        this.typeBuilder = typeBuilder;
        this.currentResource = resource;
    }

    public void process() {
        Map<String, AbstractClassTypeDeclarationDescr> unprocesseableDescrs = new HashMap<>();
        List<TypeDefinition> unresolvedTypes = new ArrayList<>();
        List<AbstractClassTypeDeclarationDescr> unsortedDescrs = new ArrayList<>();
        unsortedDescrs.addAll(packageDescr.getTypeDeclarations());
        unsortedDescrs.addAll(packageDescr.getEnumDeclarations());
        typeBuilder.processTypeDeclarations(packageDescr, pkgRegistry, currentResource, unsortedDescrs, unresolvedTypes, unprocesseableDescrs);
        for (AbstractClassTypeDeclarationDescr descr : unprocesseableDescrs.values()) {
            this.results.add(new TypeDeclarationError(descr, "Unable to process type " + descr.getTypeName()));
        }
    }
}
