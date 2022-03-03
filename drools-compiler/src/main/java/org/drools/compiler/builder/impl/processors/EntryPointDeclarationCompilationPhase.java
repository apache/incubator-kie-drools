package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.EntryPointDeclarationDescr;
import org.drools.drl.ast.descr.PackageDescr;

public class EntryPointDeclarationCompilationPhase extends AbstractPackageCompilationPhase {
    public EntryPointDeclarationCompilationPhase(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        super(pkgRegistry, packageDescr);
    }

    public void process() {
        for (EntryPointDeclarationDescr epDescr : packageDescr.getEntryPointDeclarations()) {
            pkgRegistry.getPackage().addEntryPointId(epDescr.getEntryPointId());
        }
    }

}
