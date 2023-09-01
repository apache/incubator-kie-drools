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
package org.kie.dmn.core.weaver;

import java.util.Map;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.ResourceTypePackageRegistry;
import org.kie.api.definition.KiePackage;
import org.kie.api.internal.weaver.KieWeaverService;
import org.kie.api.io.ResourceType;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNPackage;
import org.kie.dmn.core.impl.DMNPackageImpl;

public class DMNWeaverService implements KieWeaverService<DMNPackage> {

    @Override
    public ResourceType getResourceType() {
        return ResourceType.DMN;
    }

    @Override
    public void merge(KiePackage kiePkg, DMNPackage dmnpkg) {
        ResourceTypePackageRegistry registry = ((InternalKnowledgePackage)kiePkg).getResourceTypePackages();
        DMNPackageImpl existing = registry.computeIfAbsent(ResourceType.DMN, rt -> new DMNPackageImpl(dmnpkg.getNamespace()));

        for ( Map.Entry<String, DMNModel> entry : dmnpkg.getAllModels().entrySet() ) {
            existing.addModel( entry.getKey(), entry.getValue() );
        }

        existing.addProfiles(((DMNPackageImpl) dmnpkg).getProfiles());
    }

    @Override
    public void weave(KiePackage kiePkg, DMNPackage rtPkg) {
        // nothing to do for now
    }
}
