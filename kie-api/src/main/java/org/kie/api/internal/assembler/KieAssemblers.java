/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.api.internal.assembler;

import java.util.List;

import org.kie.api.internal.utils.KieService;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.io.ResourceWithConfiguration;

public interface KieAssemblers extends KieService {

    void addResourceBeforeRules(
            Object knowledgeBuilder,
            Resource resource,
            ResourceType type,
            ResourceConfiguration configuration) throws Exception;

    void addResourceAfterRules(
            Object knowledgeBuilder,
            Resource resource,
            ResourceType type,
            ResourceConfiguration configuration) throws Exception;

    void addResourcesAfterRules(
            Object knowledgeBuilder,
            List<ResourceWithConfiguration> resources,
            ResourceType type) throws Exception;

    /**
     * @deprecated As of version 7.51.0 replaced by {@link #addResourceAfterRules(Object, Resource, ResourceType, ResourceConfiguration)}
     */
    @Deprecated
    default void addResource(
            Object knowledgeBuilder,
            Resource resource,
            ResourceType type,
            ResourceConfiguration configuration) throws Exception {
        addResourceAfterRules(knowledgeBuilder, resource, type, configuration);
    }

    /**
     * @deprecated As of version 7.51.0 replaced by {@link #addResourcesAfterRules(Object, List, ResourceType)}
     */
    @Deprecated
    default void addResources(
            Object knowledgeBuilder,
            List<ResourceWithConfiguration> resources,
            ResourceType type) throws Exception {
        addResourcesAfterRules(knowledgeBuilder, resources, type);
    }

}
