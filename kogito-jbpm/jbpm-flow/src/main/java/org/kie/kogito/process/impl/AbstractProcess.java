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

import java.util.Collections;

import org.jbpm.process.instance.LightProcessRuntime;
import org.jbpm.process.instance.LightProcessRuntimeContext;
import org.jbpm.process.instance.LightProcessRuntimeServiceProvider;
import org.jbpm.process.instance.ProcessRuntimeServiceProvider;
import org.kie.api.runtime.process.ProcessRuntime;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Signal;

public abstract class AbstractProcess<T extends Model> implements Process<T> {

    private final MapProcessInstances<T> instances;

    private final ProcessRuntimeServiceProvider services;

    protected AbstractProcess(ProcessRuntimeServiceProvider services) {
        this.services = services;
        this.instances = new MapProcessInstances<>();
    }

    @Override
    public T createModel() {
        return null;
    }

    @Override
    public ProcessInstance<T> createInstance(Model m) {
        return createInstance((T) m);
    }

    protected AbstractProcess() {
        this(new LightProcessRuntimeServiceProvider());
    }

    protected AbstractProcess(ProcessConfig config) {
        this(new ConfiguredProcessServices(config));
    }

    @Override
    public final MapProcessInstances<T> instances() {
        return instances;
    }

    @Override
    public final <S> void send(Signal<S> signal) {
        instances().values().forEach(pi -> pi.send(signal));
    }

    protected abstract org.kie.api.definition.process.Process legacyProcess();

    protected ProcessRuntime createLegacyProcessRuntime() {
        return new LightProcessRuntime(
                new LightProcessRuntimeContext(Collections.singletonList(legacyProcess())),
                services);
    }
}
