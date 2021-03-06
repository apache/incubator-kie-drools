/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.internal.process.runtime;

import java.util.Collection;
import java.util.function.Predicate;

import org.kie.api.runtime.process.NodeInstanceContainer;

public interface KogitoNodeInstanceContainer extends NodeInstanceContainer {

    /**
     * Returns all node instances that are currently active
     * within this container.
     *
     * @return the list of node instances currently active
     */
    default Collection<KogitoNodeInstance> getKogitoNodeInstances() {
        return (Collection<KogitoNodeInstance>) (Object) getNodeInstances();
    }

    /**
     * Returns the node instance with the given id, or <code>null</code>
     * if the node instance cannot be found.
     *
     * @param nodeInstanceId
     * @return the node instance with the given id
     */
    KogitoNodeInstance getNodeInstance(String nodeInstanceId);

    /**
     * Return nodes that matches a filter
     * 
     * @param filter condition to be fulfilled by node
     * @param recursive if should process child nodes
     * @return nodes fullfilling the filter
     */
    Collection<KogitoNodeInstance> getKogitoNodeInstances(Predicate<KogitoNodeInstance> filter, boolean recursive);
}
