/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler;

import java.util.Collection;
import java.util.Map;

import org.kie.api.definition.KiePackage;

public class CanonicalKiePackages {
    private final Map<String, KiePackage> packages;

    public CanonicalKiePackages( Map<String, KiePackage> packages ) {
        this.packages = packages;
    }

    public Collection<KiePackage> getKiePackages() {
        return packages.values();
    }

    public KiePackage getKiePackage( String pkgName ) {
        return packages.get(pkgName);
    }
}
