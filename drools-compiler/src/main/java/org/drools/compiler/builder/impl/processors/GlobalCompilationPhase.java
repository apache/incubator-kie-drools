package org.drools.compiler.builder.impl.processors;

import java.lang.reflect.Type;

import org.drools.compiler.builder.impl.AssetFilter;
import org.drools.compiler.builder.impl.GlobalVariableContext;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.internal.builder.ResourceChange;

public class GlobalCompilationPhase extends ImmutableGlobalCompilationPhase {

    public static CompilationPhase of(PackageRegistry pkgRegistry, PackageDescr packageDescr, InternalKnowledgeBase kBase, GlobalVariableContext globalVariableContext, AssetFilter filterAcceptsRemoval) {
        if (kBase == null) {
            return new ImmutableGlobalCompilationPhase(pkgRegistry, packageDescr, globalVariableContext);
        } else {
            return new GlobalCompilationPhase(pkgRegistry, packageDescr, kBase, globalVariableContext, filterAcceptsRemoval);
        }
    }

    private final InternalKnowledgeBase kBase;
    private final AssetFilter assetFilter;

    private GlobalCompilationPhase(PackageRegistry pkgRegistry, PackageDescr packageDescr, InternalKnowledgeBase kBase, GlobalVariableContext globalVariableContext, AssetFilter filterAcceptsRemoval) {
        super(pkgRegistry, packageDescr, globalVariableContext);
        this.kBase = kBase;
        this.assetFilter = filterAcceptsRemoval;
    }

    @Override
    protected void addGlobal(InternalKnowledgePackage pkg, String identifier, Type type) {
        super.addGlobal(pkg, identifier, type);
        kBase.addGlobal(identifier, type);
    }

    protected void removeGlobal(InternalKnowledgePackage pkg, String toBeRemoved) {
        if (assetFilter != null && AssetFilter.Action.REMOVE.equals(assetFilter.accept(ResourceChange.Type.GLOBAL, pkg.getName(), toBeRemoved))) {
            pkg.removeGlobal(toBeRemoved);
            kBase.removeGlobal(toBeRemoved);
        }
    }

}
