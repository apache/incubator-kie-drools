package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.ast.descr.PackageDescr;

public class ImportProcessor extends AbstractPackageProcessor {
    public ImportProcessor(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        super(pkgRegistry, packageDescr);
    }

    @Override
    public void process() {
        for (final ImportDescr importDescr : packageDescr.getImports()) {
            pkgRegistry.addImport(importDescr);
        }
    }
}
