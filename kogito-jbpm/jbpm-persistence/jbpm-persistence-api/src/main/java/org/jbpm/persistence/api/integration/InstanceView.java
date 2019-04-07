/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.persistence.api.integration;

import java.io.Serializable;

/**
 * Generic view of an instance that is triggering an event. This view is to provide simple data access to
 * event data, though it does provide handle (getSource) to get hold of "raw" instance if more information
 * is required. 
 *
 * @param <T> type of the instance this view corresponds to
 */
public interface InstanceView<T> extends Serializable {

    /**
     * Returns "raw" object instance for this view
     * @return source of this view
     */
    T getSource();
    
    /**
     * Triggers to copy data from source to view. Different implementations of the view
     * might have different needs on when this would happen. 
     * PersistentEventManager will invoke it before delivering if it was not done before.
     */
    void copyFromSource();
}
