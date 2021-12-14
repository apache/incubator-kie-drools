/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.wiring.statics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.internal.assembler.KieAssemblers;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.io.ResourceWithConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.wiring.statics.SimpleInstanceCreator.tryInstance;

public class StaticKieAssemblers implements KieAssemblers {

    private static final Logger log = LoggerFactory.getLogger(StaticKieAssemblers.class);

    private final Map<ResourceType, KieAssemblerService> assemblers = new HashMap<>();

    public StaticKieAssemblers() {
        tryInstance("org.kie.dmn.core.assembler.DMNAssemblerService")
                .ifPresent(i -> assemblers.put(ResourceType.DMN, (KieAssemblerService) i));
        tryInstance("org.kie.pmml.evaluator.assembler.service.PMMLAssemblerService")
                .ifPresent(i -> assemblers.put(ResourceType.PMML, (KieAssemblerService) i));
    }

    @Override
    public void addResourceBeforeRules(Object knowledgeBuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception {
        KieAssemblerService assembler = assemblers.get(type);
        if (assembler != null) {
            assembler.addResourceBeforeRules(knowledgeBuilder,
                    resource,
                    type,
                    configuration);
        } else {
            throw new RuntimeException("Unknown resource type: " + type);
        }

    }

    @Override
    public void addResourceAfterRules(Object knowledgeBuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception {
        KieAssemblerService assembler = assemblers.get(type);
        if (assembler != null) {
            assembler.addResourceAfterRules(knowledgeBuilder,
                    resource,
                    type,
                    configuration);
        } else {
            log.debug("KieAssemblers: ignored {}", type);
        }
    }

    @Override
    public void addResourcesAfterRules(Object knowledgeBuilder, List<ResourceWithConfiguration> resources, ResourceType type) throws Exception {
        KieAssemblerService assembler = assemblers.get(type);
        if (assembler != null) {
            assembler.addResourcesAfterRules(knowledgeBuilder, resources, type);
        } else {
            log.debug("KieAssemblers: ignored {}", type);
        }
    }
}
