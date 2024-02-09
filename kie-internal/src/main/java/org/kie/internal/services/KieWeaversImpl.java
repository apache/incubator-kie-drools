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
package org.kie.internal.services;

import org.kie.api.definition.KiePackage;
import org.kie.api.internal.io.ResourceTypePackage;
import org.kie.api.internal.weaver.KieWeaverService;
import org.kie.api.internal.weaver.KieWeavers;
import org.kie.api.io.ResourceType;

public class KieWeaversImpl extends AbstractMultiService<ResourceType, KieWeaverService> implements KieWeavers {

    @Override
    public void weave(KiePackage newPkg, ResourceTypePackage rtkKpg) {
        KieWeaverService svc = getWeaver(rtkKpg);
        if (svc != null) {
            svc.weave(newPkg, rtkKpg);
        }
    }

    private KieWeaverService getWeaver(ResourceTypePackage rtkKpg) {
        return getService(rtkKpg.getResourceType());
    }

    @Override
    public void merge(KiePackage pkg, ResourceTypePackage rtkKpg) {
        KieWeaverService svc = getWeaver(rtkKpg);
        if (svc != null) {
            svc.merge(pkg, rtkKpg);
        }
    }

    @Override
    protected Class<KieWeaverService> serviceClass() {
        return KieWeaverService.class;
    }

    @Override
    protected ResourceType serviceKey(KieWeaverService service) {
        return service.getResourceType();
    }
}
