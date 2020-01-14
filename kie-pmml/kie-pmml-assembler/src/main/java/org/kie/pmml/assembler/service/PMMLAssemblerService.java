/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.assembler.service;

import java.io.IOException;
import java.util.Collection;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.io.ResourceWithConfiguration;
import org.kie.pmml.assembler.executor.PMMLAssemblerExecutor;

public class PMMLAssemblerService implements KieAssemblerService {

    private PMMLAssemblerExecutor pmmlAssemblerExecutor;

    @Override
    public ResourceType getResourceType() {
        return ResourceType.PMML;
    }

    @Override
    public synchronized void addResource(Object kbuilder, Resource resource, ResourceType type,
                                         ResourceConfiguration configuration) throws Exception {
        addPackage((KnowledgeBuilderImpl) kbuilder, resource);
    }

    @Override
    public synchronized void addResources(Object kbuilder, Collection<ResourceWithConfiguration> resources,
                                          ResourceType type) throws Exception {
        for (ResourceWithConfiguration rd : resources) {
            if (rd.getBeforeAdd() != null) {
                rd.getBeforeAdd().accept(kbuilder);
            }
            addResource(kbuilder, rd.getResource(), type, rd.getResourceConfiguration());
            if (rd.getAfterAdd() != null) {
                rd.getAfterAdd().accept(kbuilder);
            }
        }
    }

    /**
     * This method does the work of calling the PMML compiler and then assembling the results
     * into packages that are added to the KnowledgeBuilder
     *
     * @param kbuilder
     * @param resource
     * @throws IOException
     */
    private void addPackage(KnowledgeBuilderImpl kbuilder, Resource resource) throws IOException {
        pmmlAssemblerExecutor.getResults(resource).forEach(kbuilder::addBuilderResult);
    }
}
