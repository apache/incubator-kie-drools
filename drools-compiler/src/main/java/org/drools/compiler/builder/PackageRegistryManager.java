package org.drools.compiler.builder;

import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;

import java.util.List;
import java.util.Map;

public interface PackageRegistryManager {

    PackageRegistry getPackageRegistry(String packageName);
    PackageRegistry getOrCreatePackageRegistry(PackageDescr packageDescr);
    Map<String, PackageRegistry> getPackageRegistry();
    List<PackageDescr> getPackageDescrs(String namespace);
}
