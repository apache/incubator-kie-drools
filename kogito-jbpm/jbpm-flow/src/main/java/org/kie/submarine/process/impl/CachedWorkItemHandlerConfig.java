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

package org.kie.submarine.process.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.submarine.process.WorkItemHandlerConfig;

public class CachedWorkItemHandlerConfig implements WorkItemHandlerConfig {

    private final Map<String, WorkItemHandler> workItemHandlers = new HashMap<>();

    public CachedWorkItemHandlerConfig register(String name, WorkItemHandler handler) {
        workItemHandlers.put(name, handler);
        return this;
    }

    @Override
    public WorkItemHandler forName(String name) {
        WorkItemHandler workItemHandler = workItemHandlers.get(name);
        if (workItemHandler == null) {
            throw new NoSuchElementException(name);
        } else {
            return workItemHandler;
        }
    }

    @Override
    public Collection<String> names() {
        return workItemHandlers.keySet();
    }
}
