/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.persistence;

import org.drools.core.SessionConfiguration;
import org.drools.core.command.SingleSessionCommandService;
import org.drools.core.command.impl.AbstractInterceptor;
import org.drools.core.common.EndOperationListener;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.fluent.impl.InternalExecutable;
import org.drools.core.fluent.impl.PseudoClockRunner;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.marshalling.impl.KieSessionInitializer;
import org.drools.core.marshalling.impl.MarshallingConfigurationImpl;
import org.drools.core.runtime.ChainableRunner;
import org.drools.core.runtime.process.InternalProcessRuntime;
import org.drools.core.time.impl.CommandServiceTimerJobFactoryManager;
import org.drools.core.time.impl.TimerJobFactoryManager;
import org.drools.persistence.api.OrderedTransactionSynchronization;
import org.drools.persistence.api.PersistenceContext;
import org.drools.persistence.api.PersistenceContextManager;
import org.drools.persistence.api.SessionMarshallingHelper;
import org.drools.persistence.api.SessionNotFoundException;
import org.drools.persistence.api.TransactionAware;
import org.drools.persistence.api.TransactionManager;
import org.drools.persistence.api.TransactionManagerFactory;
import org.drools.persistence.api.TransactionManagerHelper;
import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.jpa.JpaPersistenceContextManager;
import org.drools.persistence.jpa.processinstance.JPAWorkItemManager;
import org.kie.api.KieBase;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.Executable;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

public class PersistableRunner implements SingleSessionCommandService {

    private static Logger              logger           = LoggerFactory.getLogger( PersistableRunner.class );

    private SessionInfo                sessionInfo;
    private SessionMarshallingHelper   marshallingHelper;

    private KieSession                 ksession;
    private Environment                env;
    private RequestContext             sessionContext;
    private ChainableRunner            runner;

    private TransactionManager         txm;
    private PersistenceContextManager  jpm;

    private volatile boolean           doRollback;

    private LinkedList<ChainableRunner> interceptors = new LinkedList<ChainableRunner>();

    public void checkEnvironment(Environment env) {
        if ( env.get( EnvironmentName.ENTITY_MANAGER_FACTORY ) == null &&
             env.get( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER ) == null ) {
            throw new IllegalArgumentException( "Environment must have an EntityManagerFactory " +
                                                "or a PersistenceContextManager instance" );
        }
    }

    public PersistableRunner( KieBase kbase,
                              KieSessionConfiguration conf,
                              Environment env ) {
        if ( conf == null ) {
            conf = SessionConfiguration.newInstance();
        }
        this.env = env;

        checkEnvironment( this.env );

        initTransactionManager( this.env );
        
        initNewKnowledgeSession(kbase, conf);

        // Use the App scoped EntityManager if the user has provided it, and it is open.
        // - open the entity manager before the transaction begins. 
        PersistenceContext persistenceContext = jpm.getApplicationScopedPersistenceContext();
        boolean transactionOwner = false;
        try {
            transactionOwner = txm.begin();
            registerRollbackSync();

            persistenceContext.joinTransaction();
            this.sessionInfo = (SessionInfo) persistenceContext.persist( this.sessionInfo );
            registerUpdateSync();
            txm.commit( transactionOwner );
        } catch ( RuntimeException re ) {
            rollbackTransaction( re,
                                 transactionOwner );
            throw re;
        } catch ( Exception t1 ) {
            rollbackTransaction( t1,
                                 transactionOwner );
            throw new RuntimeException( "Wrapped exception see cause",
                                        t1 );
        }

        // update the session id to be the same as the session info id
        ((InternalKnowledgeRuntime) ksession).setIdentifier( this.sessionInfo.getId());
    }

    protected void initNewKnowledgeSession(KieBase kbase, KieSessionConfiguration conf) { 
        this.sessionInfo = new SessionInfo();

        // create session but bypass command service
        this.ksession = kbase.newKieSession( conf,
                                             this.env );
        
        initKieSessionMBeans(this.ksession);
        
        this.marshallingHelper = new SessionMarshallingHelper( this.ksession, conf );
        
        MarshallingConfigurationImpl config = (MarshallingConfigurationImpl) this.marshallingHelper.getMarshaller().getMarshallingConfiguration();
        config.setMarshallProcessInstances( false );
        config.setMarshallWorkItems( false );

        this.sessionInfo.setJPASessionMashallingHelper( this.marshallingHelper );

        ((InternalKnowledgeRuntime) this.ksession).setEndOperationListener( new EndOperationListenerImpl(this.txm, this.sessionInfo ) );

        this.sessionContext = RequestContext.create(ksession.getClass().getClassLoader()).with(this.ksession);

        this.runner = new TransactionInterceptor();

        TimerJobFactoryManager timerJobFactoryManager = ((InternalKnowledgeRuntime) ksession ).getTimerService().getTimerJobFactoryManager();
        if (timerJobFactoryManager instanceof CommandServiceTimerJobFactoryManager) {
           ( (CommandServiceTimerJobFactoryManager) timerJobFactoryManager ).setRunner( this );
        }
    }

    private void initKieSessionMBeans(KieSession ksession) {
        InternalKnowledgeBase internalKnowledgeBase = (InternalKnowledgeBase) ksession.getKieBase();
        StatefulKnowledgeSessionImpl statefulKnowledgeSessionImpl = (StatefulKnowledgeSessionImpl) ksession;
        // DROOLS-1322
        statefulKnowledgeSessionImpl.initMBeans(internalKnowledgeBase.getContainerId(), internalKnowledgeBase.getId(), "persistent");
    }
    
    public PersistableRunner( Long sessionId,
                              KieBase kbase,
                              KieSessionConfiguration conf,
                              Environment env ) {
        if ( conf == null ) {
            conf = SessionConfiguration.newInstance();
        }

        this.env = env;

        checkEnvironment( this.env );

        initTransactionManager( this.env );

        // Open the entity manager before the transaction begins. 
        PersistenceContext persistenceContext = jpm.getApplicationScopedPersistenceContext();

        boolean transactionOwner = false;
        try {
            transactionOwner = txm.begin();
            registerRollbackSync();

            persistenceContext.joinTransaction();
            initExistingKnowledgeSession( sessionId,
                          kbase,
                          conf,
                          persistenceContext );
            registerUpdateSync();
            txm.commit( transactionOwner );
        } catch (SessionNotFoundException e){
            // do not rollback transaction otherwise it will mark it as aborted
            // making the whole operation to fail  if not transaction owner
            if (transactionOwner) {
                rollbackTransaction( e, transactionOwner, false );
            }
            throw e;

        } catch ( RuntimeException re ) {
            rollbackTransaction( re,
                                 transactionOwner );
            throw re;
        } catch ( Exception t1 ) {
            rollbackTransaction( t1,
                                 transactionOwner );
            throw new RuntimeException( "Wrapped exception see cause",
                                        t1 );
        }
    }

    protected void initExistingKnowledgeSession(Long sessionId,
                                KieBase kbase,
                                KieSessionConfiguration conf,
                                PersistenceContext persistenceContext) {

        if ( !doRollback && this.ksession != null ) {
            return;
            // nothing to initialise
        }

        this.doRollback = false;
        try {
            // if locking is active, this will also lock the (found) SessionInfo instance
            this.sessionInfo = (SessionInfo) persistenceContext.findSession( sessionId );
        } catch ( Exception e ) {
            throw new SessionNotFoundException( "Could not find session data for id " + sessionId,
                                        e );
        }
        if ( sessionInfo == null ) {
            throw new SessionNotFoundException( "Could not find session data for id " + sessionId );
        }

        if ( this.marshallingHelper == null ) {
            // this should only happen when this class is first constructed
            this.marshallingHelper = new SessionMarshallingHelper( kbase,
                                                                   conf,
                                                                   env );
            MarshallingConfigurationImpl config = (MarshallingConfigurationImpl)
                    this.marshallingHelper.getMarshaller().getMarshallingConfiguration();
            config.setMarshallProcessInstances( false );
            config.setMarshallWorkItems( false );
        }

        this.sessionInfo.setJPASessionMashallingHelper(this.marshallingHelper);

        // if this.ksession is null, it'll create a new one, else it'll use the existing one
        this.ksession = this.marshallingHelper.loadSnapshot( this.sessionInfo.getData(), this.ksession, new JpaSessionInitializer(this) );

        // update the session id to be the same as the session info id
        InternalKnowledgeRuntime kruntime = ((InternalKnowledgeRuntime) ksession);
        kruntime.setIdentifier( this.sessionInfo.getId() );
        kruntime.setEndOperationListener( new EndOperationListenerImpl( this.txm, this.sessionInfo ) );

        if ( this.sessionContext == null ) {
            // this should only happen when this class is first constructed
            this.sessionContext = RequestContext.create(ksession.getClass().getClassLoader()).with( this.ksession );
        }

        this.runner = new TransactionInterceptor();
        // apply interceptors
        Iterator<ChainableRunner> iterator = this.interceptors.descendingIterator();
        while (iterator.hasNext()) {
            addInterceptor(iterator.next(), false);
        }
        
        initKieSessionMBeans(this.ksession);
    }

    public class JpaSessionInitializer implements KieSessionInitializer {

        private final PersistableRunner runner;

        public JpaSessionInitializer( PersistableRunner runner ) {
            this.runner = runner;
        }

        @Override
        public void init( KieSession ksession ) {
            // The ExecutableRunner for the TimerJobFactoryManager must be set before any timer jobs are scheduled.
            // Otherwise, if overdue jobs are scheduled (and then run before the .executorDelegate field can be set),
            //  they will retrieve a null executorDelegate (instead of a reference to this) and fail.
            TimerJobFactoryManager timerJobFactoryManager = ((InternalKnowledgeRuntime) ksession ).getTimerService().getTimerJobFactoryManager();
            if (timerJobFactoryManager instanceof CommandServiceTimerJobFactoryManager) {
                ( (CommandServiceTimerJobFactoryManager) timerJobFactoryManager ).setRunner( runner );
            }
        }
    }

    public void initTransactionManager(Environment env) {
        Object tm = env.get( EnvironmentName.TRANSACTION_MANAGER );
        if ( env.get( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER ) != null &&
             env.get( EnvironmentName.TRANSACTION_MANAGER ) != null ) {
            this.txm = (TransactionManager) tm;
            this.jpm = (PersistenceContextManager) env.get( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER );
        } else {
            if ( tm != null && isSpringTransactionManager(tm.getClass()) ) {
                try {
                    logger.debug( "Instantiating KieSpringTransactionManager" );
                    Class< ? > cls = Class.forName( "org.kie.spring.persistence.KieSpringTransactionManager" );
                    Constructor< ? > con = cls.getConstructors()[0];
                    this.txm = (TransactionManager) con.newInstance( tm );
                    env.set( EnvironmentName.TRANSACTION_MANAGER, this.txm );
                    cls = Class.forName( "org.kie.spring.persistence.KieSpringJpaManager" );
                    con = cls.getConstructors()[0];
                    this.jpm = (PersistenceContextManager) con.newInstance( this.env );
                } catch ( Exception e ) {
                    //fall back for drools5-legacy spring module
                    logger.warn( "Could not instantiate KieSpringTransactionManager. Trying with DroolsSpringTransactionManager." );
                    try {
                        logger.debug( "Instantiating DroolsSpringTransactionManager" );
                        Class< ? > cls = Class.forName( "org.drools.container.spring.beans.persistence.DroolsSpringTransactionManager" );
                        Constructor< ? > con = cls.getConstructors()[0];
                        this.txm = (TransactionManager) con.newInstance( tm );
                        env.set( EnvironmentName.TRANSACTION_MANAGER, this.txm );

                        // configure spring for JPA and local transactions
                        cls = Class.forName( "org.drools.container.spring.beans.persistence.DroolsSpringJpaManager" );
                        con = cls.getConstructors()[0];
                        this.jpm = (PersistenceContextManager) con.newInstance( this.env );
                    } catch ( Exception ex ) {
                        logger.warn( "Could not instantiate DroolsSpringTransactionManager" );
                        throw new RuntimeException( "Could not instantiate org.kie.container.spring.beans.persistence.DroolsSpringTransactionManager", ex );
                    }
                }
            } else {
                logger.debug( "Instantiating JtaTransactionManager" );
                this.txm = TransactionManagerFactory.get().newTransactionManager(env);
                env.set( EnvironmentName.TRANSACTION_MANAGER, this.txm );
                try {
                    Class< ? > jpaPersistenceCtxMngrClass = Class.forName( "org.jbpm.persistence.JpaProcessPersistenceContextManager" );
                    Constructor< ? > jpaPersistenceCtxMngrCtor = jpaPersistenceCtxMngrClass.getConstructors()[0];
                    this.jpm = (PersistenceContextManager) jpaPersistenceCtxMngrCtor.newInstance( this.env );
                } catch ( ClassNotFoundException e ) {
                    this.jpm = new JpaPersistenceContextManager( this.env );
                } catch ( Exception e ) {
                    throw new RuntimeException( "Error creating JpaProcessPersistenceContextManager",
                                                e );
                }
            }
            env.set( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER,
                     this.jpm );
            env.set( EnvironmentName.TRANSACTION_MANAGER,
                     this.txm );
        }
    }

    private static String SPRING_TM_CLASSNAME = "org.springframework.transaction.support.AbstractPlatformTransactionManager";

    public static boolean isSpringTransactionManager( Class<?> clazz ) {
        if ( SPRING_TM_CLASSNAME.equals( clazz.getName() ) ) {
            return true;
        }
        // Try to find from the ancestors
        return clazz.getSuperclass() != null && isSpringTransactionManager( clazz.getSuperclass() );
    }

    public static class EndOperationListenerImpl
        implements
        EndOperationListener {
        private TransactionManager txm;
        private SessionInfo info;

        public EndOperationListenerImpl(TransactionManager txm, SessionInfo info) {
            this.info = info;
            this.txm = txm;
        }

        public void endOperation(InternalKnowledgeRuntime kruntime) {
            this.info.setLastModificationDate( new Date( kruntime.getLastIdleTimestamp() ) );
            TransactionManagerHelper.addToUpdatableSet(txm, info);
        }
    }

    public RequestContext createContext() {
        return RequestContext.create(ksession.getClass().getClassLoader()).with( this.ksession );
    }

    public ChainableRunner getChainableRunner() {
        return runner;
    }

    @Override
    public synchronized RequestContext execute( Executable executable, RequestContext ctx ) {
        runner.execute( executable, ctx );
        return ctx;
    }

    private void rollbackTransaction( Exception t1, boolean transactionOwner ) {
        rollbackTransaction(t1, transactionOwner, true);
    }

    private void rollbackTransaction(Exception cause, boolean transactionOwner, boolean logstack) {
        try {

            if (logstack) {
                logger.warn( "Could not commit session", cause );
            } else {
                logger.warn( "Could not commit session due to {}", cause.getMessage() );
            }
            txm.rollback( transactionOwner );
        } catch ( Exception rollbackError ) {
            String errorMessage = "Could not rollback due to '" + rollbackError.getMessage() + "' rollback caused by " + cause.getMessage();
            // log rollback exception
            logger.error( "Could not rollback", rollbackError );
            // propagate original exception that caused the rollback
            throw new RuntimeException( errorMessage, cause );
        }
    }

    public void dispose() {
        if ( ksession != null ) {
            ksession.dispose();
        }
        this.interceptors.clear();
    }

    @Override
    public void destroy() {
        PersistenceContext persistenceContext = this.jpm.getApplicationScopedPersistenceContext();

        boolean transactionOwner = false;
        try {
            transactionOwner = txm.begin();
            
            persistenceContext.joinTransaction();
            
            initExistingKnowledgeSession( this.sessionInfo.getId(),
                    this.marshallingHelper.getKbase(),
                    this.marshallingHelper.getConf(),
                    persistenceContext );

            persistenceContext.remove(this.sessionInfo);

            txm.commit( transactionOwner );

        } catch ( RuntimeException re ) {
            rollbackTransaction( re, transactionOwner );
            throw re;
        } catch ( Exception t1 ) {
            rollbackTransaction(t1, transactionOwner);
            throw new RuntimeException( "Wrapped exception see cause", t1 );
        }
    }

    public Long getSessionId() {
        return sessionInfo.getId();
    }

    private void registerRollbackSync() {
        TransactionManagerHelper.registerTransactionSyncInContainer(this.txm, new SynchronizationImpl( this ));
    }

    private static class SynchronizationImpl
        extends
        OrderedTransactionSynchronization {

        PersistableRunner service;

        public SynchronizationImpl(PersistableRunner service ) {
            super(1, "SynchronizationImpl-"+service.toString());
            this.service = service;
        }

        public void afterCompletion(int status) {
            if ( status != TransactionManager.STATUS_COMMITTED ) {
                this.service.rollback();
            }


            if (this.service.txm != null) {
                ObjectMarshallingStrategy[] strategies = (ObjectMarshallingStrategy[]) this.service.env.get(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES);

                for (ObjectMarshallingStrategy strategy : strategies) {
                    if (strategy instanceof TransactionAware) {
                        ((TransactionAware) strategy).onEnd(this.service.txm);
                    }
                }
            }

            this.service.jpm.endCommandScopedEntityManager();

            KieSession ksession = this.service.ksession;
            // clean up cached process and work item instances
            if ( ksession != null ) {
                InternalProcessRuntime internalProcessRuntime = ((InternalWorkingMemory) ksession).internalGetProcessRuntime();
                if ( internalProcessRuntime != null ) {
                    if (this.service.doRollback) {
                        internalProcessRuntime.clearProcessInstancesState();
                    } 

                    internalProcessRuntime.clearProcessInstances();
                }
                ((JPAWorkItemManager) ksession.getWorkItemManager()).clearWorkItems();
            }

        }

        public void beforeCompletion() {
            // not used
        }

        @Override
        public String toString() {
            return "SynchronizationImpl{" +
                    "service=" + service.sessionInfo +  " cmd=" + service.toString() +
                    '}';
        }
    }

    public KieSession getKieSession() {
        return this.ksession;
    }

    public void addInterceptor(ChainableRunner interceptor) {
        addInterceptor(interceptor, true);
    }

    protected void addInterceptor( ChainableRunner interceptor, boolean store ) {
        interceptor.setNext( this.runner );
        this.runner = interceptor;
        if (store) {
            // put it on a stack so it can be recreated upon rollback
            this.interceptors.push(interceptor);
        }
    }

    private void rollback() {
        this.doRollback = true;
    }

    private void registerUpdateSync() {
        if (this.txm.getResource("TriggerUpdateTransactionSynchronization-"+this.toString()) == null) {
            this.txm.registerTransactionSynchronization(new TriggerUpdateTransactionSynchronization(txm, env));
            this.txm.putResource("TriggerUpdateTransactionSynchronization-"+this.toString(), true);
        }
    }

    private class TransactionInterceptor extends AbstractInterceptor {

        public TransactionInterceptor() {
            setNext(new PseudoClockRunner());
        }

        @Override
        public RequestContext execute( Executable executable, RequestContext context ) {
            if ( !( (InternalExecutable) executable ).canRunInTransaction() ) {
                executeNext(executable, context);
                jpm.dispose();
                return context;
            }

            // Open the entity manager before the transaction begins.
            PersistenceContext persistenceContext = jpm.getApplicationScopedPersistenceContext();

            boolean transactionOwner = false;
            try {
                transactionOwner = txm.begin();

                persistenceContext.joinTransaction();

                initExistingKnowledgeSession( sessionInfo.getId(),
                        marshallingHelper.getKbase(),
                        marshallingHelper.getConf(),
                        persistenceContext );

                jpm.beginCommandScopedEntityManager();

                registerRollbackSync();

                if (txm != null) {
                    ObjectMarshallingStrategy[] strategies = (ObjectMarshallingStrategy[]) env.get(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES);

                    for (ObjectMarshallingStrategy strategy : strategies) {
                        if (strategy instanceof TransactionAware) {
                            ((TransactionAware) strategy).onStart(txm);
                        }
                    }
                }

                executeNext(executable, context);

                registerUpdateSync();
                txm.commit( transactionOwner );

            } catch ( RuntimeException re ) {
                rollbackTransaction( re,
                        transactionOwner );
                throw re;
            } catch ( Exception t1 ) {
                rollbackTransaction( t1,
                        transactionOwner );
                throw new RuntimeException( "Wrapped exception see cause",
                        t1 );
            }

            return context;
        }
    }

}
