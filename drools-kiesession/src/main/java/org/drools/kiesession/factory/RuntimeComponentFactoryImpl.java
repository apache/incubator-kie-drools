package org.drools.kiesession.factory;

import java.io.Serializable;

import org.drools.core.SessionConfiguration;
import org.drools.core.base.MapGlobalResolver;
import org.drools.core.common.AgendaFactory;
import org.drools.core.common.AgendaGroupFactory;
import org.drools.core.common.EntryPointFactory;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PhreakPropagationContextFactory;
import org.drools.core.common.PriorityQueueAgendaGroupFactory;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.common.ReteEvaluator;
import org.drools.base.factmodel.traits.TraitFactory;
import org.drools.core.management.DroolsManagementAgent;
import org.drools.core.reteoo.ReteooFactHandleFactory;
import org.drools.core.reteoo.RuntimeComponentFactory;
import org.drools.core.rule.accessor.FactHandleFactory;
import org.drools.base.rule.accessor.GlobalResolver;
import org.drools.core.rule.consequence.KnowledgeHelper;
import org.drools.core.time.TimerService;
import org.drools.kiesession.agenda.DefaultAgendaFactory;
import org.drools.kiesession.entrypoints.NamedEntryPointFactory;
import org.drools.kiesession.management.KieSessionMonitoringImpl;
import org.drools.kiesession.management.StatelessKieSessionMonitoringImpl;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.session.KieSessionsPoolImpl;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.drools.kiesession.session.StatelessKnowledgeSessionImpl;
import org.drools.base.RuleBase;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.KieSessionsPool;
import org.kie.api.runtime.StatelessKieSession;

public class RuntimeComponentFactoryImpl implements Serializable, RuntimeComponentFactory {

    public static final RuntimeComponentFactoryImpl DEFAULT = new RuntimeComponentFactoryImpl();

    private final FactHandleFactory handleFactory = new ReteooFactHandleFactory();
    private final PropagationContextFactory propagationFactory = PhreakPropagationContextFactory.getInstance();
    private final WorkingMemoryFactory wmFactory = PhreakWorkingMemoryFactory.getInstance();
    private final AgendaFactory agendaFactory = DefaultAgendaFactory.getInstance();
    private final AgendaGroupFactory agendaGroupFactory = PriorityQueueAgendaGroupFactory.getInstance();

    public FactHandleFactory getFactHandleFactoryService() {
        return handleFactory;
    }

    public EntryPointFactory getEntryPointFactory() {
        return new NamedEntryPointFactory();
    }

    public PropagationContextFactory getPropagationContextFactory() {
        return propagationFactory;
    }

    public AgendaFactory getAgendaFactory(SessionConfiguration config) {
        return agendaFactory;
    }

    public AgendaGroupFactory getAgendaGroupFactory() {
        return agendaGroupFactory;
    }

    public TraitFactory getTraitFactory(RuleBase knowledgeBase) {
        return null;
    }

    public final KnowledgeHelper createKnowledgeHelper(ReteEvaluator reteEvaluator) {
        return KnowledgeHelperFactory.get().createKnowledgeHelper(reteEvaluator);
    }

    public InternalWorkingMemory createStatefulSession(RuleBase ruleBase, Environment environment, SessionConfiguration sessionConfig, boolean fromPool) {
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) ruleBase;
        if (fromPool || kbase.getSessionPool() == null) {
            StatefulKnowledgeSessionImpl session = ( StatefulKnowledgeSessionImpl ) getWorkingMemoryFactory()
                    .createWorkingMemory( kbase.nextWorkingMemoryCounter(), kbase, sessionConfig, environment );
            return internalInitSession( kbase, sessionConfig, session );
        }
        return (InternalWorkingMemory) kbase.getSessionPool().newKieSession( sessionConfig );
    }

    public GlobalResolver createGlobalResolver(ReteEvaluator reteEvaluator, Environment environment) {
        Globals globals = (Globals) environment.get( EnvironmentName.GLOBALS );
        if (globals != null) {
            return globals instanceof GlobalResolver ? (GlobalResolver) globals : new StatefulKnowledgeSessionImpl.GlobalsAdapter( globals );
        }
        return new MapGlobalResolver();
    }

    public TimerService createTimerService(ReteEvaluator reteEvaluator) {
        return reteEvaluator.getSessionConfiguration().createTimerService();
    }

    private StatefulKnowledgeSessionImpl internalInitSession( InternalKnowledgeBase kbase, SessionConfiguration sessionConfig, StatefulKnowledgeSessionImpl session ) {
        if ( sessionConfig.isKeepReference() ) {
            kbase.addStatefulSession(session);
        }
        return session;
    }

    public StatelessKieSession createStatelessSession(RuleBase ruleBase, KieSessionConfiguration conf) {
        return new StatelessKnowledgeSessionImpl((InternalKnowledgeBase) ruleBase, conf );
    }

    public KieSessionsPool createSessionsPool(RuleBase ruleBase, int initialSize) {
        return new KieSessionsPoolImpl((InternalKnowledgeBase) ruleBase, initialSize);
    }

    public KieSessionMonitoringImpl createStatefulSessionMonitor(DroolsManagementAgent.CBSKey cbsKey) {
        return new KieSessionMonitoringImpl( cbsKey.getKcontainerId(), cbsKey.getKbaseId(), cbsKey.getKsessionName() );
    }

    public StatelessKieSessionMonitoringImpl createStatelessSessionMonitor(DroolsManagementAgent.CBSKey cbsKey) {
        return new StatelessKieSessionMonitoringImpl( cbsKey.getKcontainerId(), cbsKey.getKbaseId(), cbsKey.getKsessionName() );
    }

    protected WorkingMemoryFactory getWorkingMemoryFactory() {
        return wmFactory;
    }
}
