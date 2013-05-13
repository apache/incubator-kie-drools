package org.drools.impl.adapters;

import org.drools.command.Command;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.runtime.Globals;
import org.kie.internal.runtime.StatelessKnowledgeSession;

import java.util.Collection;

public class StatelessKnowledgeSessionAdapter implements org.drools.runtime.StatelessKnowledgeSession {

    private final StatelessKnowledgeSession delegate;

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
        throw new UnsupportedOperationException("org.drools.impl.adapters.StatelessKnowledgeSessionAdapter.addEventListener -> TODO");
    }

    public void removeEventListener(ProcessEventListener listener) {
        throw new UnsupportedOperationException("org.drools.impl.adapters.StatelessKnowledgeSessionAdapter.removeEventListener -> TODO");
    }

    public Collection<ProcessEventListener> getProcessEventListeners() {
        throw new UnsupportedOperationException("org.drools.impl.adapters.StatelessKnowledgeSessionAdapter.getProcessEventListeners -> TODO");
    }

    public void execute(Object object) {
        delegate.execute(object);
    }

    public void execute(Iterable objects) {
        delegate.execute(objects);
    }

    public void addEventListener(WorkingMemoryEventListener listener) {
        throw new UnsupportedOperationException("org.drools.impl.adapters.StatelessKnowledgeSessionAdapter.addEventListener -> TODO");
    }

    public void removeEventListener(WorkingMemoryEventListener listener) {
        throw new UnsupportedOperationException("org.drools.impl.adapters.StatelessKnowledgeSessionAdapter.removeEventListener -> TODO");
    }

    public Collection<WorkingMemoryEventListener> getWorkingMemoryEventListeners() {
        throw new UnsupportedOperationException("org.drools.impl.adapters.StatelessKnowledgeSessionAdapter.getWorkingMemoryEventListeners -> TODO");
    }

    public void addEventListener(AgendaEventListener listener) {
        throw new UnsupportedOperationException("org.drools.impl.adapters.StatelessKnowledgeSessionAdapter.addEventListener -> TODO");
    }

    public void removeEventListener(AgendaEventListener listener) {
        throw new UnsupportedOperationException("org.drools.impl.adapters.StatelessKnowledgeSessionAdapter.removeEventListener -> TODO");
    }

    public Collection<AgendaEventListener> getAgendaEventListeners() {
        throw new UnsupportedOperationException("org.drools.impl.adapters.StatelessKnowledgeSessionAdapter.getAgendaEventListeners -> TODO");
    }
}
