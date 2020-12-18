/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.event;

import java.util.Collection;

/**
 * Batch of events to be considered as single item to be processed.
 * New events can be appended at any time unless the events have 
 * already been consumed - by calling <code>events</code> method.
 *
 */
public interface EventBatch {

    /**
     * Appends new event in its raw format - meaning as it was generated
     * @param rawEvent event to be appended to the batch
     */
    void append(Object rawEvent);
    
    /**
     * Returns all events appended to this batch already converted to 
     * <code>DataEvents</code>
     * @return converted events
     */
    Collection<DataEvent<?>> events();
}
