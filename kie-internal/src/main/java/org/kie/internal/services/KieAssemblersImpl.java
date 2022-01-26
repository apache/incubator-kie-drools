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
import java.util.ServiceLoader;

import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.internal.assembler.KieAssemblers;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.io.ResourceWithConfiguration;

public class KieAssemblersImpl implements KieAssemblers {
    private Map<ResourceType, KieAssemblerService> assemblers;

    @Override
    public void addResourceBeforeRules(Object knowledgeBuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception {
        KieAssemblerService assembler = getAssembler(type);
        if (assembler != null) {
            assembler.addResourceBeforeRules(knowledgeBuilder,
                                             resource,
                                             type,
                                             configuration);
        } else {
            throw new RuntimeException("Unknown resource type: " + type);
        }
    }

    private KieAssemblerService getAssembler(ResourceType type) {
        if (assemblers == null) {
            assemblers = new HashMap<>();
            ServiceLoader<KieAssemblerService> loader = ServiceLoader.load(KieAssemblerService.class);
            for (KieAssemblerService kieAssemblerService : loader) {
                assemblers.put( kieAssemblerService.getResourceType(), kieAssemblerService );
            }
        }
        return assemblers.get(type);
    }

    @Override
    public void addResourceAfterRules(Object knowledgeBuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception {
        KieAssemblerService assembler = getAssembler(type);
        if (assembler != null) {
            assembler.addResourceAfterRules(knowledgeBuilder,
                                            resource,
                                            type,
                                            configuration);
        } else {
            throw new RuntimeException("Unknown resource type: " + type);
        }

    }

    @Override
    public void addResourcesAfterRules(Object knowledgeBuilder, List<ResourceWithConfiguration> resources, ResourceType type) throws Exception {
        KieAssemblerService assembler = getAssembler(type);
        if (assembler != null) {
            assembler.addResourcesAfterRules(knowledgeBuilder, resources, type);
        } else {
            throw new RuntimeException("Unknown resource type: " + type);
        }
    }
}
