package org.drools.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.SessionConfiguration;
import org.drools.base.MapGlobalResolver;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.event.AgendaEventSupport;
import org.drools.event.RuleFlowEventSupport;
import org.drools.event.WorkingMemoryEventSupport;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.impl.StatefulKnowledgeSessionImpl.AgendaEventListenerWrapper;
import org.drools.impl.StatefulKnowledgeSessionImpl.ProcessEventListenerWrapper;
import org.drools.impl.StatefulKnowledgeSessionImpl.WorkingMemoryEventListenerWrapper;
import org.drools.reteoo.InitialFactHandle;
import org.drools.reteoo.InitialFactHandleDummyObject;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.reteoo.ReteooWorkingMemory.WorkingMemoryReteAssertAction;
import org.drools.rule.EntryPoint;
import org.drools.runtime.Parameters;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSessionResults;
import org.drools.spi.AgendaFilter;
import org.drools.spi.GlobalResolver;

public class StatelessKnowledgeSessionImpl
    implements
    StatelessKnowledgeSession {

    private InternalRuleBase                                                  ruleBase;
    private AgendaFilter                                                      agendaFilter;
    private GlobalResolver                                                    sessionGlobals            = new MapGlobalResolver();

    /** The event mapping */
    public Map<WorkingMemoryEventListener, WorkingMemoryEventListenerWrapper> mappedWorkingMemoryListeners;
    public Map<AgendaEventListener, AgendaEventListenerWrapper>               mappedAgendaListeners;
    public Map<ProcessEventListener, ProcessEventListenerWrapper>             mappedProcessListeners;

    /** The event support */
    public WorkingMemoryEventSupport                                          workingMemoryEventSupport = new WorkingMemoryEventSupport();
    public AgendaEventSupport                                                 agendaEventSupport        = new AgendaEventSupport();
    public RuleFlowEventSupport                                               ruleFlowEventSupport      = new RuleFlowEventSupport();

    public StatelessKnowledgeSessionImpl() {
    }

    public StatelessKnowledgeSessionImpl(final InternalRuleBase ruleBase) {
        this.ruleBase = ruleBase;
    }

    public InternalRuleBase getRuleBase() {
        return this.ruleBase;
    }

    public InternalWorkingMemory newWorkingMemory() {
        synchronized ( this.ruleBase.getPackagesMap() ) {
            InternalWorkingMemory wm = new ReteooWorkingMemory( this.ruleBase.nextWorkingMemoryCounter(),
                                                                this.ruleBase,
                                                                new SessionConfiguration() );

            DelegatingGlobalResolver resolver = new DelegatingGlobalResolver();
            resolver.setDelegate( this.sessionGlobals );

            wm.setGlobalResolver( resolver );
            wm.setWorkingMemoryEventSupport( this.workingMemoryEventSupport );
            wm.setAgendaEventSupport( this.agendaEventSupport );
            wm.setRuleFlowEventSupport( this.ruleFlowEventSupport );

            final InitialFactHandleDummyObject initialFact = new InitialFactHandleDummyObject();
            final InitialFactHandle handle = new InitialFactHandle( wm.getFactHandleFactory().newFactHandle( initialFact,
                                                                                                             wm.getObjectTypeConfigurationRegistry().getObjectTypeConf( EntryPoint.DEFAULT,
                                                                                                                                                                        initialFact ),
                                                                                                             wm ) );

            wm.queueWorkingMemoryAction( new WorkingMemoryReteAssertAction( handle,
                                                                            false,
                                                                            true,
                                                                            null,
                                                                            null ) );
            return wm;
        }
    }

    public void addEventListener(WorkingMemoryEventListener listener) {
        WorkingMemoryEventListenerWrapper wrapper = new WorkingMemoryEventListenerWrapper( listener );
        this.mappedWorkingMemoryListeners.put( listener,
                                               wrapper );
        this.workingMemoryEventSupport.addEventListener( wrapper );
    }

    public void removeEventListener(WorkingMemoryEventListener listener) {
        WorkingMemoryEventListenerWrapper wrapper = this.mappedWorkingMemoryListeners.remove( listener );
        this.workingMemoryEventSupport.removeEventListener( wrapper );
    }

    public Collection<WorkingMemoryEventListener> getWorkingMemoryEventListeners() {
        return Collections.unmodifiableCollection( this.mappedWorkingMemoryListeners.keySet() );
    }

    public void addEventListener(AgendaEventListener listener) {
        AgendaEventListenerWrapper wrapper = new AgendaEventListenerWrapper( listener );
        this.mappedAgendaListeners.put( listener,
                                        wrapper );
        this.agendaEventSupport.addEventListener( wrapper );
    }

    public Collection<AgendaEventListener> getAgendaEventListeners() {
        return Collections.unmodifiableCollection( this.mappedAgendaListeners.keySet() );
    }

    public void removeEventListener(AgendaEventListener listener) {
        AgendaEventListenerWrapper wrapper = this.mappedAgendaListeners.remove( listener );
        this.agendaEventSupport.removeEventListener( wrapper );
    }

    public void addEventListener(ProcessEventListener listener) {
        ProcessEventListenerWrapper wrapper = new ProcessEventListenerWrapper( listener );
        this.mappedProcessListeners.put( listener,
                                         wrapper );
        this.ruleFlowEventSupport.addEventListener( wrapper );
    }

    public Collection<ProcessEventListener> getProcessEventListeners() {
        return Collections.unmodifiableCollection( this.mappedProcessListeners.keySet() );
    }

    public void removeEventListener(ProcessEventListener listener) {
        ProcessEventListenerWrapper wrapper = this.mappedProcessListeners.get( listener );
        this.ruleFlowEventSupport.removeEventListener( wrapper );
    }

    public void setGlobal(String identifier,
                          Object value) {
        this.sessionGlobals.setGlobal( identifier,
                                       value );
    }

    public void setGlobalResolver(org.drools.runtime.GlobalResolver globalResolver) {
        this.sessionGlobals = (GlobalResolver) globalResolver;

    }

    public void executeObject(Object object) {
        InternalWorkingMemory wm = newWorkingMemory();

        wm.insert( object );
        wm.fireAllRules( this.agendaFilter );
    }

    public void executeIterable(Iterable< ? > objects) {
        InternalWorkingMemory wm = newWorkingMemory();

        for ( Object object : objects ) {
            wm.insert( object );
        }
        wm.fireAllRules( this.agendaFilter );
    }

    public StatelessKnowledgeSessionResults executeObjectWithParameters(Object object,
                                                                        Parameters parameters) {
        InternalWorkingMemory wm = newWorkingMemory();

        wm.insert( object );

        Map<String, Object> results = new HashMap<String, Object>();

        executeInParams( wm,
                         parameters,
                         results );

        wm.fireAllRules( this.agendaFilter );

        getOutParams( wm,
                      parameters,
                      results );

        return new StatelessKnowledgeSessionResultsImpl( results );

    }

    public StatelessKnowledgeSessionResults executeIterableWithParameters(Iterable< ? > objects,
                                                                          Parameters parameters) {
        InternalWorkingMemory wm = newWorkingMemory();

        for ( Object object : objects ) {
            wm.insert( object );
        }

        Map<String, Object> results = new HashMap<String, Object>();

        executeInParams( wm,
                         parameters,
                         results );

        wm.fireAllRules( this.agendaFilter );

        getOutParams( wm,
                      parameters,
                      results );

        return new StatelessKnowledgeSessionResultsImpl( results );

    }

    private void executeInParams(InternalWorkingMemory wm,
                                 Parameters parameters,
                                 Map<String, Object> results) {
        Map<String, ? > map = ((FactParamsImpl) parameters.getFactParams()).getIn();
        if ( map != null && !map.isEmpty() ) {
            for ( Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
                Entry<String, Object> entry = (Entry<String, Object>) it.next();
                sessionGlobals.setGlobal( entry.getKey(),
                                          entry.getValue() );
                wm.insert( entry.getValue() );
            }
        }

        map = ((FactParamsImpl) parameters.getFactParams()).getInOut();
        if ( map != null && !map.isEmpty() ) {
            for ( Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
                Entry<String, Object> entry = (Entry<String, Object>) it.next();
                sessionGlobals.setGlobal( entry.getKey(),
                                          entry.getValue() );
                results.put( entry.getKey(),
                             entry.getValue() );
                wm.insert( entry.getValue() );
            }
        }

        map = ((GlobalParamsImpl) parameters.getGlobalParams()).getIn();
        if ( map != null && !map.isEmpty() ) {
            for ( Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
                Entry<String, Object> entry = (Entry<String, Object>) it.next();
                sessionGlobals.setGlobal( entry.getKey(),
                                          entry.getValue() );
            }
        }

        map = ((GlobalParamsImpl) parameters.getGlobalParams()).getInOut();
        if ( map != null && !map.isEmpty() ) {
            for ( Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
                Entry<String, Object> entry = (Entry<String, Object>) it.next();
                results.put( entry.getKey(),
                             entry.getValue() );
                sessionGlobals.setGlobal( entry.getKey(),
                                          entry.getValue() );
            }
        }
    }

    private void getOutParams(InternalWorkingMemory wm,
                              Parameters parameters,
                              Map<String, Object> results) {
        Collection<String> col = ((FactParamsImpl) parameters.getFactParams()).getOut();
        if ( col != null && !col.isEmpty() ) {
            for ( String identifer : col ) {
                results.put( identifer,
                             wm.getGlobal( identifer ) );
            }
        }

        col = ((GlobalParamsImpl) parameters.getGlobalParams()).getOut();
        if ( col != null && !col.isEmpty() ) {
            for ( String identifer : col ) {
                results.put( identifer,
                             wm.getGlobal( identifer ) );
            }
        }
    }
    
    public Parameters newParameters() {
        return new ParametersImpl();
    }

    public static class DelegatingGlobalResolver
        implements
        GlobalResolver {
        MapGlobalResolver resolver;
        GlobalResolver    delegate;

        public DelegatingGlobalResolver() {
            this.resolver = new MapGlobalResolver();
        }

        public void setDelegate(GlobalResolver delegate) {
            this.delegate = delegate;
        }

        public Object resolveGlobal(String identifier) {
            Object object = this.resolver.resolveGlobal( identifier );
            if ( object == null ) {
                return this.delegate.resolveGlobal( identifier );
            } else {
                return object;
            }
        }

        public void setGlobal(String identifier,
                              Object value) {
            resolver.setGlobal( identifier,
                                value );
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( this.resolver );
            out.writeObject( this.delegate );
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            this.resolver = (MapGlobalResolver) in.readObject();
            this.delegate = (GlobalResolver) in.readObject();
        }
    }

}
