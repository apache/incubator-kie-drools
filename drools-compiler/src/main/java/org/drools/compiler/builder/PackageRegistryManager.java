package org.drools.compiler.builder;

import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface PackageRegistryManager {

    PackageRegistry getPackageRegistry(String packageName);
    PackageRegistry getOrCreatePackageRegistry(PackageDescr packageDescr);
    Map<String, PackageRegistry> getPackageRegistry();
    List<PackageDescr> getPackageDescrs(String namespace);

}
