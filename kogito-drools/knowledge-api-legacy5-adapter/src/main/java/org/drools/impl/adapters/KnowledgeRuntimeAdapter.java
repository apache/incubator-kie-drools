package org.drools.impl.adapters;

import org.drools.KnowledgeBase;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.runtime.Calendars;
import org.drools.runtime.Channel;
import org.drools.runtime.Environment;
import org.drools.runtime.ExitPoint;
import org.drools.runtime.Globals;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.rule.Agenda;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.LiveQuery;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.Variable;
import org.drools.runtime.rule.ViewChangedEventListener;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.time.SessionClock;
import org.drools.time.SessionPseudoClock;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.Row;
import org.kie.internal.runtime.KnowledgeRuntime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.drools.impl.adapters.FactHandleAdapter.adaptFactHandles;
import static org.drools.impl.adapters.ProcessInstanceAdapter.adaptProcessInstances;

public class KnowledgeRuntimeAdapter implements org.drools.runtime.KnowledgeRuntime {

    public final KnowledgeRuntime delegate;

    private final Map<WorkingMemoryEventListener, RuleRuntimeEventListener> wimListeners = new HashMap<WorkingMemoryEventListener, RuleRuntimeEventListener>();
    private final Map<ProcessEventListener, org.kie.api.event.process.ProcessEventListener> processListeners = new HashMap<ProcessEventListener, org.kie.api.event.process.ProcessEventListener>();
    private final Map<AgendaEventListener, org.kie.api.event.rule.AgendaEventListener> agendaListeners = new HashMap<AgendaEventListener, org.kie.api.event.rule.AgendaEventListener>();

    public KnowledgeRuntimeAdapter(KnowledgeRuntime delegate) {
        this.delegate = delegate;
    }

    public <T extends SessionClock> T getSessionClock() {
        final org.kie.api.time.SessionClock delegateClock = delegate.getSessionClock();
        if (delegateClock instanceof org.drools.core.time.SessionPseudoClock) {
            return (T) new SessionPseudoClock() {
                @Override
                public long advanceTime(long amount, TimeUnit unit) {
                    return ((org.drools.core.time.SessionPseudoClock)delegateClock).advanceTime(amount, unit);
                }

                @Override
                public long getCurrentTime() {
                    return delegateClock.getCurrentTime();
                }
            };
        }
        return (T)new SessionClock() {
            @Override
            public long getCurrentTime() {
                return delegateClock.getCurrentTime();
            }
        };
    }

    public void setGlobal(String identifier, Object value) {
        delegate.setGlobal(identifier, value);
    }

    public Object getGlobal(String identifier) {
        return delegate.getGlobal(identifier);
    }

    public Globals getGlobals() {
        return new GlobalsAdapter(delegate.getGlobals());
    }

    public Calendars getCalendars() {
        return new CalendarsAdapter(delegate.getCalendars());
    }

    public Environment getEnvironment() {
        return new EnvironmentAdapter(delegate.getEnvironment());
    }

    public KnowledgeBase getKnowledgeBase() {
        return new KnowledgeBaseAdapter((org.kie.internal.KnowledgeBase)delegate.getKieBase());
    }

    public void registerExitPoint(String name, ExitPoint exitPoint) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    public void unregisterExitPoint(String name) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    public void registerChannel(String name, Channel channel) {
        delegate.registerChannel(name, new ChannelAdapter(channel));
    }

    @Override
    public void unregisterChannel(String name) {
        delegate.unregisterChannel(name);
    }

    @Override
    public Map<String, Channel> getChannels() {
        Map<String, Channel> channels = new HashMap<String, Channel>();
        for (final Map.Entry<String, org.kie.api.runtime.Channel> entry : delegate.getChannels().entrySet()) {
            channels.put(entry.getKey(), new Channel() {
                @Override
                public void send(Object object) {
                    entry.getValue().send(object);
                }
            });
        }
        return channels;
    }

    @Override
    public KnowledgeSessionConfiguration getSessionConfiguration() {
        return new KnowledgeSessionConfigurationAdapter(delegate.getSessionConfiguration());
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

    public ProcessInstance startProcess(String processId) {
        return new ProcessInstanceAdapter(delegate.startProcess(processId));
    }

    public ProcessInstance startProcess(String processId, Map<String, Object> parameters) {
        return new ProcessInstanceAdapter(delegate.startProcess(processId, parameters));
    }

    public ProcessInstance createProcessInstance(String processId, Map<String, Object> parameters) {
        return new ProcessInstanceAdapter(delegate.createProcessInstance(processId, parameters));
    }

    public ProcessInstance startProcessInstance(long processInstanceId) {
        return new ProcessInstanceAdapter(delegate.startProcessInstance(processInstanceId));
    }

    public void signalEvent(String type, Object event) {
        delegate.signalEvent(type, event);
    }

    public void signalEvent(String type, Object event, long processInstanceId) {
        delegate.signalEvent(type, event, processInstanceId);
    }

    public Collection<ProcessInstance> getProcessInstances() {
        return adaptProcessInstances(delegate.getProcessInstances());
    }

    public ProcessInstance getProcessInstance(long processInstanceId) {
        return new ProcessInstanceAdapter(delegate.getProcessInstance(processInstanceId));
    }

    public ProcessInstance getProcessInstance(long processInstanceId, boolean readonly) {
        return new ProcessInstanceAdapter(delegate.getProcessInstance(processInstanceId, readonly));
    }

    public void abortProcessInstance(long processInstanceId) {
        delegate.abortProcessInstance(processInstanceId);
    }

    public WorkItemManager getWorkItemManager() {
    	return new WorkItemManagerAdapter(delegate.getWorkItemManager());
    }

    public void halt() {
        delegate.halt();
    }

    public Agenda getAgenda() {
        return new AgendaAdapter(delegate.getAgenda());
    }

    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String name) {
        EntryPoint entryPoint = delegate.getEntryPoint(name);
        return entryPoint == null ? null : new WorkingMemoryEntryPointAdapter(entryPoint);
    }

    public Collection<? extends WorkingMemoryEntryPoint> getWorkingMemoryEntryPoints() {
        Collection<WorkingMemoryEntryPoint> result = new ArrayList<WorkingMemoryEntryPoint>();
        for (EntryPoint ep : delegate.getEntryPoints()) {
            result.add(new WorkingMemoryEntryPointAdapter(ep));
        }
        return result;
    }

    public QueryResults getQueryResults(String query, Object... arguments) {
        if (arguments != null) {
            for (int i = 0; i < arguments.length; i++) {
                if (arguments[i] instanceof Variable) {
                    arguments[i] = org.kie.api.runtime.rule.Variable.v;
                }
            }
        }
        return new QueryResultsAdapter(delegate.getQueryResults(query, arguments));
    }

    public LiveQuery openLiveQuery(String query, Object[] arguments, final ViewChangedEventListener listener) {
        return new LiveQueryAdapter(delegate.openLiveQuery(query, arguments, new org.kie.api.runtime.rule.ViewChangedEventListener() {
            @Override
            public void rowInserted(Row row) {
                listener.rowAdded(new RowAdapter(row));
            }

            @Override
            public void rowDeleted(Row row) {
                listener.rowRemoved(new RowAdapter(row));
            }

            @Override
            public void rowUpdated(Row row) {
                listener.rowUpdated(new RowAdapter(row));
            }
        }));
    }

    public String getEntryPointId() {
        return delegate.getEntryPointId();
    }

    public FactHandle insert(Object object) {
        return new FactHandleAdapter(delegate.insert(object));
    }

    public void retract(FactHandle handle) {
        delegate.retract(((FactHandleAdapter) handle).getDelegate());
    }

    public void update(FactHandle handle, Object object) {
        delegate.update(((FactHandleAdapter) handle).getDelegate(), object);
    }

    public FactHandle getFactHandle(Object object) {
        return new FactHandleAdapter(delegate.getFactHandle(object));
    }

    public Object getObject(FactHandle factHandle) {
        return delegate.getObject(((FactHandleAdapter)factHandle).getDelegate());
    }

    public Collection<Object> getObjects() {
        return (Collection<Object>)delegate.getObjects();
    }

    public Collection<Object> getObjects(final ObjectFilter filter) {
        return (Collection<Object>)delegate.getObjects(new org.kie.api.runtime.ObjectFilter() {
            public boolean accept(Object object) {
                return filter.accept(object);
            }
        });
    }

    public <T extends FactHandle> Collection<T> getFactHandles() {
        return (Collection<T>)adaptFactHandles(delegate.getFactHandles());
    }

    public <T extends FactHandle> Collection<T> getFactHandles(final ObjectFilter filter) {
        return (Collection<T>)adaptFactHandles(delegate.getFactHandles(new org.kie.api.runtime.ObjectFilter() {
            public boolean accept(Object object) {
                return filter.accept(object);
            }
        }));
    }

    public long getFactCount() {
        return delegate.getFactCount();
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
        return obj instanceof KnowledgeRuntimeAdapter && delegate.equals(((KnowledgeRuntimeAdapter)obj).delegate);
    }
}
