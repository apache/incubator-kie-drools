package org.kie.drl.engine.compilation.model;

import java.util.Set;

import org.drools.drl.ast.descr.PackageDescr;
import org.kie.drl.api.identifiers.LocalComponentIdDrl;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoSetResource;

public class DrlPackageDescrSetResource extends EfestoSetResource<PackageDescr> implements EfestoCompilationOutput {

    public DrlPackageDescrSetResource(Set<PackageDescr> packageDescrs, String basePath) {
        super(packageDescrs, new LocalComponentIdDrl(basePath));
    }


}
