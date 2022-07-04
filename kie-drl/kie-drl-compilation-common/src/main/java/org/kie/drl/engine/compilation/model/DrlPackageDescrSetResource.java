package org.kie.drl.engine.compilation.model;

import org.drools.drl.ast.descr.PackageDescr;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.model.EfestoSetResource;

import java.util.Set;

public class DrlPackageDescrSetResource extends EfestoSetResource<PackageDescr> implements EfestoResource<Set<PackageDescr>> {

    public DrlPackageDescrSetResource(Set<PackageDescr> packageDescrs, String basePath) {
        super(packageDescrs, "drl", basePath);
    }


}
