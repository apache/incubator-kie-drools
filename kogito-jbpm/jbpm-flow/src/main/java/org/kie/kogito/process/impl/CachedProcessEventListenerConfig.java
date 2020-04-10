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

package org.kie.kogito.process.impl;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.event.process.ProcessEventListener;
import org.kie.kogito.process.ProcessEventListenerConfig;

public class CachedProcessEventListenerConfig implements ProcessEventListenerConfig {

    private final List<ProcessEventListener> processEventListeners;

    public CachedProcessEventListenerConfig() {
        this.processEventListeners = new ArrayList<>();
    }

    public CachedProcessEventListenerConfig(List<ProcessEventListener> processEventListeners) {
        this.processEventListeners = processEventListeners;
    }

    public CachedProcessEventListenerConfig register(ProcessEventListener listener) {
        processEventListeners.add(listener);
        return this;
    }

    @Override
    public List<ProcessEventListener> listeners() {
        return processEventListeners;
    }

}
