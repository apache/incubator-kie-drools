package org.drools.model.codegen.execmodel;

import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.drl.ast.descr.PackageDescr;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;

/**
 * A container for CompositePackageDescrs used in {@link ModelBuilderImpl}
 */
public class CompositePackageManager {
    private Map<String, CompositePackageDescr> compositePackagesMap;

    public void register(PackageDescr packageDescr) {
        if (compositePackagesMap == null) {
            compositePackagesMap = new HashMap<>();
        }

        CompositePackageDescr pkgDescr = compositePackagesMap.get(packageDescr.getNamespace());
        if (pkgDescr == null) {
            compositePackagesMap.put(packageDescr.getNamespace(), new CompositePackageDescr( packageDescr.getResource(), packageDescr) );
        } else {
            pkgDescr.addPackageDescr( packageDescr.getResource(), packageDescr );
        }
    }

    public Collection<CompositePackageDescr> findPackages(Collection<CompositePackageDescr> compositePackages ) {
        if (compositePackages != null && !compositePackages.isEmpty()) {
            if (compositePackagesMap != null) {
                compositePackages = new HashSet<>(compositePackages);
                for (Map.Entry<String, CompositePackageDescr> entry : compositePackagesMap.entrySet()) {
                    Optional<CompositePackageDescr> optPkg = compositePackages.stream().filter(pkg -> pkg.getNamespace().equals(entry.getKey()) ).findFirst();
                    if (optPkg.isPresent()) {
                        optPkg.get().addPackageDescr(entry.getValue().getResource(), entry.getValue());
                    } else {
                        compositePackages.add(entry.getValue());
                    }
                }
            }
            return compositePackages;
        }
        if (compositePackagesMap != null) {
            return compositePackagesMap.values();
        }
        return emptyList();
    }
}
