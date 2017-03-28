/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.weaver;

import org.drools.core.definitions.InternalKnowledgePackage;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.ResourceType;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNPackage;
import org.kie.dmn.core.impl.DMNPackageImpl;
import org.kie.internal.io.ResourceTypePackage;
import org.kie.internal.weaver.KieWeaverService;

import java.util.Map;

public class DMNWeaverService implements KieWeaverService<DMNPackage> {

    @Override
    public ResourceType getResourceType() {
        return ResourceType.DMN;
    }

    @Override
    public void merge(KieBase kieBase, KiePackage kiePkg, DMNPackage dmnpkg) {
        Map<ResourceType, ResourceTypePackage> map = ((InternalKnowledgePackage)kiePkg).getResourceTypePackages();
        DMNPackageImpl existing  = (DMNPackageImpl) map.get( ResourceType.DMN );
        if ( existing == null ) {
            existing = new DMNPackageImpl( dmnpkg.getNamespace() );
            map.put(ResourceType.DMN, existing);
        }

        for ( Map.Entry<String, DMNModel> entry : dmnpkg.getAllModels().entrySet() ) {
            existing.addModel( entry.getKey(), entry.getValue() );
        }
    }

    @Override
    public void weave(KieBase kieBase, KiePackage kiePkg, DMNPackage rtPkg) {
        // nothing to do for now
    }

    @Override
    public Class getServiceInterface() {
        return KieWeaverService.class;
    }
}
