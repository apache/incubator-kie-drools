package org.drools.compiler.builder;

import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;

public interface PackageRegistryManager {

    PackageRegistry getPackageRegistry(String packageName);
    PackageRegistry getOrCreatePackageRegistry(PackageDescr packageDescr);

}
