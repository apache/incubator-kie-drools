/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.impl.adapters;

import org.drools.command.Command;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.runtime.Globals;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.internal.runtime.StatelessKnowledgeSession;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class StatelessKnowledgeSessionAdapter implements org.drools.runtime.StatelessKnowledgeSession {

    private final StatelessKnowledgeSession delegate;

    private final Map<WorkingMemoryEventListener, RuleRuntimeEventListener> wimListeners = new HashMap<WorkingMemoryEventListener, RuleRuntimeEventListener>();
    private final Map<ProcessEventListener, org.kie.api.event.process.ProcessEventListener> processListeners = new HashMap<ProcessEventListener, org.kie.api.event.process.ProcessEventListener>();
    private final Map<AgendaEventListener, org.kie.api.event.rule.AgendaEventListener> agendaListeners = new HashMap<AgendaEventListener, org.kie.api.event.rule.AgendaEventListener>();

    public StatelessKnowledgeSessionAdapter(StatelessKnowledgeSession delegate) {
        this.delegate = delegate;
    }

    public Globals getGlobals() {
        return new GlobalsAdapter(delegate.getGlobals());
    }

    public void setGlobal(String identifer, Object value) {
        delegate.setGlobal(identifer, value);
    }

    public <T> T execute(Command<T> command) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    public void addEventListener(ProcessEventListener listener) {
        org.kie.api.event.process.ProcessEventListener adapted = new ProcessEventListenerAdapter(listener);
        processListeners.put(listener, adapted);
        delegate.addEventListener(adapted);
    }

    public void removeEventListener(ProcessEventListener listener) {
        delegate.removeEventListener(processListeners.remove(listener));
    }

    public Collection<ProcessEventListener> getProcessEventListeners() {
        return processListeners.keySet();
    }

    public void execute(Object object) {
        delegate.execute(object);
    }

    public void execute(Iterable objects) {
        delegate.execute(objects);
    }

    public void addEventListener(WorkingMemoryEventListener listener) {
        RuleRuntimeEventListener adapted = new WorkingMemoryEventListenerAdapter(listener);
        wimListeners.put(listener, adapted);
        delegate.addEventListener(adapted);
    }

    public void removeEventListener(WorkingMemoryEventListener listener) {
        delegate.removeEventListener(wimListeners.remove(listener));
    }

    public Collection<WorkingMemoryEventListener> getWorkingMemoryEventListeners() {
        return wimListeners.keySet();
    }

    public void addEventListener(AgendaEventListener listener) {
        org.kie.api.event.rule.AgendaEventListener adapted = new AgendaEventListenerAdapter(listener);
        agendaListeners.put(listener, adapted);
        delegate.addEventListener(adapted);
    }

    public void removeEventListener(AgendaEventListener listener) {
        delegate.removeEventListener(agendaListeners.remove(listener));
    }

    public Collection<AgendaEventListener> getAgendaEventListeners() {
        return agendaListeners.keySet();
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof StatelessKnowledgeSessionAdapter && delegate.equals(((StatelessKnowledgeSessionAdapter)obj).delegate);
    }
}
