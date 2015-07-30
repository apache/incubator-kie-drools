/*
 * Copyright 2015 JBoss Inc
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

package org.drools.builder;

import org.drools.io.Resource;

/**
 * A KnowledgeBuilder with a fluent interface allowing to add multiple Resources
 * at the same time, without worrying about cross dependencies among them.
 */
public interface CompositeKnowledgeBuilder {

    /**
     * Set the default resource type of all the subsequently added Resources.
     *
     * @param type the resource type
     * @return
     */
    CompositeKnowledgeBuilder type(ResourceType type);

    /**
     * Add a resource of the given ResourceType, using the default type and resource configuration.
     *
     * @param resource the Resource to add
     * @return
     */
    CompositeKnowledgeBuilder add(Resource resource);
    /**

     * Add a resource of the given ResourceType, using the default resource configuration.
     *
     * @param resource the Resource to add
     * @param type the resource type
     * @return
     */
    CompositeKnowledgeBuilder add(Resource resource, ResourceType type);

    /**
     * Add a resource of the given ResourceType, using the provided ResourceConfiguration.
     * Resources can be created by calling any of the "newX" factory methods of
     * ResourceFactory. The kind of resource (DRL,  XDRL, DSL,... CHANGE_SET) must be
     * indicated by the second argument.
     *
     * @param resource the Resource to add
     * @param type the resource type
     * @param configuration the resource configuration
     * @return
     */
    CompositeKnowledgeBuilder add(Resource resource, ResourceType type, ResourceConfiguration configuration);

    /**
     * Build all the Resources added during this batch
     */
    void build();
}
