package org.drools.compiler.builder;

import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;

import java.util.Collection;
import java.util.Map;

/**
 * Manages handling and creation of {@link PackageRegistry} instances.
 */
public interface PackageRegistryManager {

    PackageRegistry getPackageRegistry(String packageName);

    PackageRegistry getOrCreatePackageRegistry(PackageDescr packageDescr);

    Map<String, PackageRegistry> getPackageRegistry();

    Collection<String> getPackageNames();
}
