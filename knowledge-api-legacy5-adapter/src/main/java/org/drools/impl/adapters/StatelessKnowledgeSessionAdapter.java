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
        throw new UnsupportedOperationException("org.drools.impl.adapters.StatelessKnowledgeSessionAdapter.execute -> TODO");
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
}
