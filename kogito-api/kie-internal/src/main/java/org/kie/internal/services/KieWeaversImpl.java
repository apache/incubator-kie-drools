/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.services;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.kie.api.internal.weaver.KieWeaverService;
import org.kie.api.internal.weaver.KieWeavers;
import org.kie.api.io.ResourceType;

public class KieWeaversImpl implements KieWeavers, Consumer<KieWeaverService> {
    private Map<ResourceType, KieWeaverService> weavers;

    public KieWeaversImpl() {
        weavers = new HashMap<ResourceType, KieWeaverService>();
    }

    public Map<ResourceType, KieWeaverService> getWeavers() {
        return this.weavers;
    }

    @Override
    public void accept( KieWeaverService weaver ) {
        weavers.put( weaver.getResourceType(), weaver );
    }
}
