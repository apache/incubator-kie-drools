package org.drools.drl.ast.dsl;

import org.drools.drl.ast.dsl.impl.PackageDescrBuilderImpl;
import org.kie.api.io.Resource;

/**
 * A factory API for the *Descr classes 
 */
public class DescrFactory {

    public static PackageDescrBuilder newPackage() {
        return PackageDescrBuilderImpl.newPackage();
    }

    public static PackageDescrBuilder newPackage(Resource resource) {
        return PackageDescrBuilderImpl.newPackage(resource);
    }
}
