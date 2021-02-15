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
package org.kie.kogito.event.process;

import java.util.function.Consumer;

import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;

public class ContextAwareEventListener extends DefaultProcessEventListener {

    private final Consumer<ContextAwareEventListener> action;

    private ContextAwareEventListener(Consumer<ContextAwareEventListener> action) {
        this.action = action;
    }

    @Override
    public void afterNodeLeft( ProcessNodeLeftEvent event) {
        action.accept(this);
    }

    @Override
    public void afterVariableChanged( ProcessVariableChangedEvent event) {
        action.accept(this);
    }

    public static ProcessEventListener using( Consumer<ContextAwareEventListener> action) {
        return new ContextAwareEventListener(action);
    }
}