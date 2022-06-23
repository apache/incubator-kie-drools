package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.impl.AssetFilter;
import org.drools.compiler.builder.impl.GlobalVariableContext;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.internal.builder.ResourceChange;

public class GlobalCompilationPhase extends ImmutableGlobalCompilationPhase {

    private final InternalKnowledgeBase kBase;
    private final AssetFilter assetFilter;

    public GlobalCompilationPhase(PackageRegistry pkgRegistry, PackageDescr packageDescr, InternalKnowledgeBase kBase, GlobalVariableContext globalVariableContext, AssetFilter filterAcceptsRemoval) {
        super(pkgRegistry, packageDescr, globalVariableContext);
        this.kBase = kBase;
        this.assetFilter = filterAcceptsRemoval;
    }

    @Override
    protected void addGlobal(InternalKnowledgePackage pkg, String identifier, Class<?> clazz) {
        super.addGlobal(pkg, identifier, clazz);
        if (kBase != null) {
            kBase.addGlobal(identifier, clazz);
        }
    }

    protected void removeGlobal(InternalKnowledgePackage pkg, String toBeRemoved) {
        if (assetFilter != null && AssetFilter.Action.REMOVE.equals(assetFilter.accept(ResourceChange.Type.GLOBAL, pkg.getName(), toBeRemoved))) {
            pkg.removeGlobal(toBeRemoved);
            if (kBase != null) {
                kBase.removeGlobal(toBeRemoved);
            }
        }
    }

}
