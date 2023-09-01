package org.drools.model.codegen.execmodel;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.model.codegen.execmodel.generator.DRLIdGenerator;
import org.kie.api.builder.ReleaseId;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A delegate/container for {@link PackageModel}s, used in {@link ModelBuilderImpl}
 */
public class PackageModelManager {
    private final Map<String, PackageModel> packageModels;
    private final KnowledgeBuilderConfigurationImpl builderConfiguration;
    private final ReleaseId releaseId;
    private final DRLIdGenerator exprIdGenerator;

    public PackageModelManager(KnowledgeBuilderConfigurationImpl builderConfiguration, ReleaseId releaseId, DRLIdGenerator exprIdGenerator) {
        this.packageModels = new HashMap<>();
        this.builderConfiguration = builderConfiguration;
        this.releaseId = releaseId;
        this.exprIdGenerator = exprIdGenerator;
    }

    public PackageModel getPackageModel(PackageDescr packageDescr, PackageRegistry pkgRegistry, String pkgName) {
        return packageModels.computeIfAbsent(pkgName, s -> PackageModel.createPackageModel(builderConfiguration, packageDescr, pkgRegistry, pkgName, releaseId, exprIdGenerator));
    }

    public PackageModel remove(String name) {
        return packageModels.remove(name);
    }

    public Collection<PackageModel> values() {
        return packageModels.values();
    }
}
