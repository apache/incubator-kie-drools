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

import java.util.Collection;

import org.kie.api.internal.utils.KieService;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.io.ResourceWithConfiguration;

public interface KieAssemblerService extends KieService {

    ResourceType getResourceType();

    default void addResourceBeforeRules(Object kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception { }

    default void addResourcesBeforeRules(Object kbuilder, Collection<ResourceWithConfiguration> resources, ResourceType type) throws Exception {
        for (ResourceWithConfiguration rd : resources) {
            rd.getBeforeAdd().accept(kbuilder);
            addResourceBeforeRules(kbuilder, rd.getResource(), type, rd.getResourceConfiguration());
            rd.getAfterAdd().accept(kbuilder);
        }
    }

    default void addResourceAfterRules(Object kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception {
        addResource(kbuilder, resource, type, configuration);
    }

    default void addResourcesAfterRules(Object kbuilder, Collection<ResourceWithConfiguration> resources, ResourceType type) throws Exception {
        for (ResourceWithConfiguration rd : resources) {
            rd.getBeforeAdd().accept(kbuilder);
            addResourceAfterRules(kbuilder, rd.getResource(), type, rd.getResourceConfiguration());
            rd.getAfterAdd().accept(kbuilder);
        }
    }

    /**
     * @deprecated As of version 7.51.0 replaced by {@link #addResourceAfterRules(Object, Resource, ResourceType, ResourceConfiguration)}
     */
    @Deprecated
    default void addResource(Object kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception {

    }

    /**
     * @deprecated As of version 7.51.0 replaced by {@link #addResourcesAfterRules(Object, Collection, ResourceType)}
     */
    @Deprecated
    default void addResources(Object kbuilder, Collection<ResourceWithConfiguration> resources, ResourceType type) throws Exception {
        for (ResourceWithConfiguration rd : resources) {
            rd.getBeforeAdd().accept(kbuilder);
            addResource(kbuilder, rd.getResource(), type, rd.getResourceConfiguration());
            rd.getAfterAdd().accept(kbuilder);
        }
    }
}