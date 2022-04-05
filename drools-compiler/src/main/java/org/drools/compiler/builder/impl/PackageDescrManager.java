package org.drools.compiler.builder.impl;

import org.drools.drl.ast.descr.PackageDescr;

import java.util.Collection;
import java.util.List;

/**
 * Manages handling of {@link PackageDescr} instances.
 *
 */
public interface PackageDescrManager {
    void registerPackage(PackageDescr packageDescr);

    List<PackageDescr> getPackageDescrs(String packageName);

    Collection<List<PackageDescr>> getPackageDescrs();

    Collection<String> getPackageNames();

}
