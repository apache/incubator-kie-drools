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
package org.jbpm.process.instance;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.process.instance.event.KogitoProcessEventListenerAdapter;
import org.jbpm.process.instance.event.KogitoProcessEventSupportImpl;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.kogito.Application;
import org.kie.kogito.internal.process.event.KogitoProcessEventListener;
import org.kie.kogito.internal.process.event.KogitoProcessEventSupport;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;

public abstract class AbstractProcessRuntime implements InternalProcessRuntime {

    protected KogitoProcessEventSupport processEventSupport;
    protected KogitoProcessRuntimeImpl kogitoProcessRuntime = new KogitoProcessRuntimeImpl(this);
    private final Application application;

    private final Map<ProcessEventListener, KogitoProcessEventListener> listenersMap = new IdentityHashMap<>();

    protected AbstractProcessRuntime(Application application) {
        this.application = application;
    }

    @Override
    public KogitoProcessRuntime getKogitoProcessRuntime() {
        return kogitoProcessRuntime;
    }

    @Override
    public KogitoProcessEventSupport getProcessEventSupport() {
        return processEventSupport;
    }

    @Override
    public Application getApplication() {
        return application;
    }

    @Override
    public void addEventListener(final ProcessEventListener listener) {
        ((KogitoProcessEventSupportImpl) this.processEventSupport).addEventListener(asKogitoProcessEventListener(listener));
    }

    @Override
    public void removeEventListener(final ProcessEventListener listener) {
        ((KogitoProcessEventSupportImpl) this.processEventSupport).removeEventListener(removeKogitoProcessEventListener(listener));
    }

    @Override
    public List<ProcessEventListener> getProcessEventListeners() {
        return (List<ProcessEventListener>) (Object) ((KogitoProcessEventSupportImpl) this.processEventSupport).getEventListeners();
    }

    private KogitoProcessEventListener asKogitoProcessEventListener(ProcessEventListener processEventListener) {
        if (processEventListener instanceof KogitoProcessEventListener) {
            return ((KogitoProcessEventListener) processEventListener);
        }
        return listenersMap.computeIfAbsent(processEventListener, KogitoProcessEventListenerAdapter::new);
    }

    private KogitoProcessEventListener removeKogitoProcessEventListener(ProcessEventListener processEventListener) {
        if (processEventListener instanceof KogitoProcessEventListener) {
            return ((KogitoProcessEventListener) processEventListener);
        }
        return listenersMap.remove(processEventListener);
    }
}
