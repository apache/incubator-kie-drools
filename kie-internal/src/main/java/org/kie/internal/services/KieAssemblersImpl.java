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
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.internal.assembler.KieAssemblers;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.io.ResourceWithConfiguration;

public class KieAssemblersImpl implements KieAssemblers, Consumer<KieAssemblerService> {
    private Map<ResourceType, KieAssemblerService> assemblers;

    public KieAssemblersImpl() {
        assemblers = new HashMap<>();
    }

    public Map<ResourceType, KieAssemblerService> getAssemblers() {
        return assemblers;
    }

    @Override
    public void addResource(Object knowledgeBuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception {
        KieAssemblerService assembler = assemblers.get(type);
        if (assembler != null) {
            assembler.addResource(knowledgeBuilder,
                                  resource,
                                  type,
                                  configuration);
        } else {
            throw new RuntimeException("Unknown resource type: " + type);
        }

    }

    @Override
    public void addResources(Object knowledgeBuilder, List<ResourceWithConfiguration> resources, ResourceType type) throws Exception {
        KieAssemblerService assembler = assemblers.get(type);
        if (assembler != null) {
            assembler.addResources(knowledgeBuilder, resources, type);
        } else {
            throw new RuntimeException("Unknown resource type: " + type);
        }
    }

    @Override
    public void accept( KieAssemblerService kieAssemblerService ) {
        assemblers.put( kieAssemblerService.getResourceType(), kieAssemblerService );
    }
}
