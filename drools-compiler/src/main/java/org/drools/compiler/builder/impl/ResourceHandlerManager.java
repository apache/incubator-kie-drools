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
package org.drools.compiler.builder.impl;

import java.util.List;
import java.util.function.Supplier;

import org.drools.compiler.builder.impl.resources.DecisionTableResourceHandler;
import org.drools.compiler.builder.impl.resources.DrlResourceHandler;
import org.drools.compiler.builder.impl.resources.DslrResourceHandler;
import org.drools.compiler.builder.impl.resources.ResourceHandler;
import org.drools.compiler.builder.impl.resources.TemplateResourceHandler;
import org.drools.compiler.builder.impl.resources.YamlResourceHandler;
import org.drools.drl.parser.lang.dsl.DefaultExpander;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.ResourceType;

import static java.util.Arrays.asList;

public class ResourceHandlerManager {
    private final List<ResourceHandler> mappers;
    private final List<ResourceType> orderedResourceTypes;

    public ResourceHandlerManager(KnowledgeBuilderConfigurationImpl configuration, ReleaseId releaseId, Supplier<DefaultExpander> dslExpander){
        this.mappers = asList(
                new DrlResourceHandler(configuration),
                new YamlResourceHandler(configuration),
                new TemplateResourceHandler(configuration, releaseId, dslExpander),
                new DslrResourceHandler(configuration, dslExpander) ,
                new DecisionTableResourceHandler(configuration, releaseId));

        this.orderedResourceTypes = asList(
                ResourceType.DRL,
                ResourceType.YAML,
                ResourceType.GDRL,
                ResourceType.RDRL,
                ResourceType.DESCR,
                ResourceType.DSLR,
                ResourceType.RDSLR,
                ResourceType.DTABLE,
                ResourceType.TDRL,
                ResourceType.TEMPLATE);
    }

    public List<ResourceType> getOrderedResourceTypes(){
        return this.orderedResourceTypes;
    }

    public ResourceHandler handlerForType(ResourceType type) {
        for (ResourceHandler mapper : this.mappers) {
            if (mapper.handles(type)) {
                return mapper;
            }
        }
        throw new IllegalArgumentException("No registered mapper for type " + type);
    }

    public boolean handles(ResourceType type){
        boolean handlesType=false;
        for (ResourceHandler mapper : this.mappers) {
            if (mapper.handles(type)) {
                handlesType = true;
            }
        }
        return handlesType;
    }
}