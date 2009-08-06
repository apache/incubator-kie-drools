package org.drools.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.RuleBaseConfiguration;
import org.drools.SessionConfiguration;
import org.drools.agent.KnowledgeAgent;
import org.drools.base.MapGlobalResolver;
import org.drools.command.Command;
import org.drools.command.impl.ContextImpl;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.command.runtime.rule.FireAllRulesCommand;
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
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.Environment;
import org.drools.runtime.Globals;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSessionResults;
import org.drools.runtime.impl.BatchExecutionImpl;
import org.drools.runtime.rule.AgendaFilter;

public class StatelessKnowledgeSessionImpl
    implements
    StatelessKnowledgeSession {

    private InternalRuleBase                                                  ruleBase;
    private KnowledgeAgent                                                    kagent;
    private AgendaFilter                                                      agendaFilter;
    private MapGlobalResolver                                                 sessionGlobals            = new MapGlobalResolver();

    /** The event mapping */
    public Map<WorkingMemoryEventListener, WorkingMemoryEventListenerWrapper> mappedWorkingMemoryListeners;
    public Map<AgendaEventListener, AgendaEventListenerWrapper>               mappedAgendaListeners;
    public Map<ProcessEventListener, ProcessEventListenerWrapper>             mappedProcessListeners;

    /** The event support */
    public WorkingMemoryEventSupport                                          workingMemoryEventSupport = new WorkingMemoryEventSupport();
    public AgendaEventSupport                                                 agendaEventSupport        = new AgendaEventSupport();
    public RuleFlowEventSupport                                               ruleFlowEventSupport      = new RuleFlowEventSupport();

    private KnowledgeSessionConfiguration                                     conf;
    private Environment                                                       environment;

    public StatelessKnowledgeSessionImpl() {
    }

    public StatelessKnowledgeSessionImpl(final InternalRuleBase ruleBase,
                                         final KnowledgeAgent kagent,
                                         final KnowledgeSessionConfiguration conf) {
        this.ruleBase = ruleBase;
        this.kagent = kagent;
        this.conf = (conf != null) ? conf : new SessionConfiguration();
        this.environment = EnvironmentFactory.newEnvironment();

        if ( this.ruleBase != null ) {
            synchronized ( this.ruleBase.getPackagesMap() ) {
                if ( ruleBase.getConfiguration().isSequential() ) {
                    this.ruleBase.getReteooBuilder().order();
                }
            }
        }
    }

    public InternalRuleBase getRuleBase() {
        if ( this.kagent != null ) {
            // if we have an agent always get the rulebase from there
            this.ruleBase = (InternalRuleBase) ((KnowledgeBaseImpl) this.kagent.getKnowledgeBase()).ruleBase;
        }
        return this.ruleBase;
    }

    public StatefulKnowledgeSession newWorkingMemory() {
        if ( this.kagent != null ) {
            // if we have an agent always get the rulebase from there
            this.ruleBase = (InternalRuleBase) ((KnowledgeBaseImpl) this.kagent.getKnowledgeBase()).ruleBase;
        }
        synchronized ( this.ruleBase.getPackagesMap() ) {
            ReteooWorkingMemory wm = new ReteooWorkingMemory( this.ruleBase.nextWorkingMemoryCounter(),
                                                              this.ruleBase,
                                                              (SessionConfiguration) this.conf,
                                                              this.environment );

            // we don't pass the mapped listener wrappers to the session constructor anymore,
            // because they would be ignored anyway, since the wm already contains those listeners
            StatefulKnowledgeSessionImpl ksession = new StatefulKnowledgeSessionImpl( wm,
                                                                                      new KnowledgeBaseImpl( this.ruleBase ) );

            ((Globals) wm.getGlobalResolver()).setDelegate( this.sessionGlobals );
            wm.setKnowledgeRuntime( ksession );
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
            return ksession;
        }
    }

    public void addEventListener(WorkingMemoryEventListener listener) {
        if ( this.mappedWorkingMemoryListeners == null ) {
            this.mappedWorkingMemoryListeners = new IdentityHashMap<WorkingMemoryEventListener, WorkingMemoryEventListenerWrapper>();
        }

        WorkingMemoryEventListenerWrapper wrapper = new WorkingMemoryEventListenerWrapper( listener );
        this.mappedWorkingMemoryListeners.put( listener,
                                               wrapper );
        this.workingMemoryEventSupport.addEventListener( wrapper );
    }

    public void removeEventListener(WorkingMemoryEventListener listener) {
        if ( this.mappedWorkingMemoryListeners == null ) {
            this.mappedWorkingMemoryListeners = new IdentityHashMap<WorkingMemoryEventListener, WorkingMemoryEventListenerWrapper>();
        }

        WorkingMemoryEventListenerWrapper wrapper = this.mappedWorkingMemoryListeners.remove( listener );
        this.workingMemoryEventSupport.removeEventListener( wrapper );
    }

    public Collection<WorkingMemoryEventListener> getWorkingMemoryEventListeners() {
        if ( this.mappedWorkingMemoryListeners == null ) {
            this.mappedWorkingMemoryListeners = new IdentityHashMap<WorkingMemoryEventListener, WorkingMemoryEventListenerWrapper>();
        }

        return Collections.unmodifiableCollection( this.mappedWorkingMemoryListeners.keySet() );
    }

    public void addEventListener(AgendaEventListener listener) {
        if ( this.mappedAgendaListeners == null ) {
            this.mappedAgendaListeners = new IdentityHashMap<AgendaEventListener, AgendaEventListenerWrapper>();
        }

        AgendaEventListenerWrapper wrapper = new AgendaEventListenerWrapper( listener );
        this.mappedAgendaListeners.put( listener,
                                        wrapper );
        this.agendaEventSupport.addEventListener( wrapper );
    }

    public Collection<AgendaEventListener> getAgendaEventListeners() {
        if ( this.mappedAgendaListeners == null ) {
            this.mappedAgendaListeners = new IdentityHashMap<AgendaEventListener, AgendaEventListenerWrapper>();
        }

        return Collections.unmodifiableCollection( this.mappedAgendaListeners.keySet() );
    }

    public void removeEventListener(AgendaEventListener listener) {
        if ( this.mappedAgendaListeners == null ) {
            this.mappedAgendaListeners = new IdentityHashMap<AgendaEventListener, AgendaEventListenerWrapper>();
        }

        AgendaEventListenerWrapper wrapper = this.mappedAgendaListeners.remove( listener );
        this.agendaEventSupport.removeEventListener( wrapper );
    }

    public void addEventListener(ProcessEventListener listener) {
        if ( this.mappedProcessListeners == null ) {
            this.mappedProcessListeners = new IdentityHashMap<ProcessEventListener, ProcessEventListenerWrapper>();
        }

        ProcessEventListenerWrapper wrapper = new ProcessEventListenerWrapper( listener );
        this.mappedProcessListeners.put( listener,
                                         wrapper );
        this.ruleFlowEventSupport.addEventListener( wrapper );
    }

    public Collection<ProcessEventListener> getProcessEventListeners() {
        if ( this.mappedProcessListeners == null ) {
            this.mappedProcessListeners = new IdentityHashMap<ProcessEventListener, ProcessEventListenerWrapper>();
        }

        return Collections.unmodifiableCollection( this.mappedProcessListeners.keySet() );
    }

    public void removeEventListener(ProcessEventListener listener) {
        if ( this.mappedProcessListeners == null ) {
            this.mappedProcessListeners = new IdentityHashMap<ProcessEventListener, ProcessEventListenerWrapper>();
        }

        ProcessEventListenerWrapper wrapper = this.mappedProcessListeners.get( listener );
        this.ruleFlowEventSupport.removeEventListener( wrapper );
    }

    public void setGlobal(String identifier,
                          Object value) {
        this.sessionGlobals.setGlobal( identifier,
                                       value );
    }

    public Globals getGlobals() {
        return this.sessionGlobals;
    }    

    public ExecutionResults execute(Command command) {
        StatefulKnowledgeSession ksession = newWorkingMemory();

        KnowledgeCommandContext context = new KnowledgeCommandContext( new ContextImpl( "ksession",
                                                                                        null ),
                                                                       null,
                                                                       null,
                                                                       ksession );

        try {
            ((StatefulKnowledgeSessionImpl) ksession).session.startBatchExecution();

            ((GenericCommand) command).execute( context );
            // did the user take control of fireAllRules, if not we will auto execute
            boolean autoFireAllRules = true;
            if ( command instanceof FireAllRulesCommand ) {
                autoFireAllRules = false;
            } else if ( command instanceof BatchExecutionImpl ) {
                for ( Command nestedCmd : ((BatchExecutionImpl) command).getCommands() ) {
                    if ( nestedCmd instanceof FireAllRulesCommand ) {
                        autoFireAllRules = false;
                        break;
                    }
                }
            }
            if ( autoFireAllRules ) {
                ksession.fireAllRules( );
            }
            ExecutionResults result = ((StatefulKnowledgeSessionImpl) ksession).session.getExecutionResult();
            return result;
        } finally {
            ((StatefulKnowledgeSessionImpl) ksession).session.endBatchExecution();
        }
    }

    public void execute(Object object) {
        StatefulKnowledgeSession ksession = newWorkingMemory();

        ksession.insert( object );
        ksession.fireAllRules( );
    }

    public void execute(Iterable objects) {
        StatefulKnowledgeSession ksession = newWorkingMemory();

        for ( Object object : objects ) {
            ksession.insert( object );
        }
        ksession.fireAllRules( );
    }

}
