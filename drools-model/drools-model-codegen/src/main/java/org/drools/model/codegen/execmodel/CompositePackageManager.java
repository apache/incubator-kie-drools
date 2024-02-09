/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
