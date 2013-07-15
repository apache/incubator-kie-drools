/*
 * Copyright 2011 JBoss Inc
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

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;

import org.drools.core.RuleBase;
import org.drools.core.SessionConfiguration;
import org.drools.core.command.CommandService;
import org.drools.core.command.Interceptor;
import org.drools.core.command.impl.DefaultCommandService;
import org.drools.core.command.impl.FixedKnowledgeCommandContext;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.command.runtime.DisposeCommand;
import org.drools.core.common.EndOperationListener;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.marshalling.impl.MarshallingConfigurationImpl;
import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.jpa.JpaPersistenceContextManager;
import org.drools.persistence.jpa.processinstance.JPAWorkItemManager;
import org.drools.persistence.jta.JtaTransactionManager;
import org.drools.core.runtime.process.InternalProcessRuntime;
import org.drools.core.time.AcceptsTimerJobFactoryManager;
import org.kie.api.KieBase;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.internal.command.Context;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleSessionCommandService
    implements
    org.drools.core.command.SingleSessionCommandService {

    Logger                             logger           = LoggerFactory.getLogger( getClass() );

    private SessionInfo                sessionInfo;
    private SessionMarshallingHelper   marshallingHelper;

    private KieSession                 ksession;
    private Environment                env;
    private KnowledgeCommandContext    kContext;
    private CommandService             commandService;

    private TransactionManager         txm;
    private PersistenceContextManager  jpm;

    private volatile boolean           doRollback;

    private static Map<Object, Object> synchronizations = Collections.synchronizedMap( new IdentityHashMap<Object, Object>() );

    public static Map<Object, Object>  txManagerClasses = Collections.synchronizedMap( new IdentityHashMap<Object, Object>() );

    public void checkEnvironment(Environment env) {
        if ( env.get( EnvironmentName.ENTITY_MANAGER_FACTORY ) == null &&
             env.get( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER ) == null ) {
            throw new IllegalArgumentException( "Environment must have an EntityManagerFactory " +
                                                "or a PersistenceContextManager instance" );
        }
    }

    public SingleSessionCommandService(RuleBase ruleBase,
                                       SessionConfiguration conf,
                                       Environment env) {
        this( new KnowledgeBaseImpl( ruleBase ),
              conf,
              env );
    }

    public SingleSessionCommandService(Integer sessionId,
                                       RuleBase ruleBase,
                                       SessionConfiguration conf,
                                       Environment env) {
        this( sessionId,
              new KnowledgeBaseImpl( ruleBase ),
              conf,
              env );
    }

    public SingleSessionCommandService(KieBase kbase,
                                       KieSessionConfiguration conf,
                                       Environment env) {
        if ( conf == null ) {
            conf = new SessionConfiguration();
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
            persistenceContext.persist( this.sessionInfo );

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
        ((InternalKnowledgeRuntime) ksession).setId( this.sessionInfo.getId() );
    }

    protected void initNewKnowledgeSession(KieBase kbase, KieSessionConfiguration conf) { 
        this.sessionInfo = new SessionInfo();

        // create session but bypass command service
        this.ksession = kbase.newKieSession( conf,
                                             this.env );

        this.marshallingHelper = new SessionMarshallingHelper( this.ksession, conf );
        
        MarshallingConfigurationImpl config = (MarshallingConfigurationImpl) this.marshallingHelper.getMarshaller().getMarshallingConfiguration();
        config.setMarshallProcessInstances( false );
        config.setMarshallWorkItems( false );

        this.sessionInfo.setJPASessionMashallingHelper( this.marshallingHelper );
        
        ((InternalKnowledgeRuntime) this.ksession).setEndOperationListener( new EndOperationListenerImpl( this.sessionInfo ) );
        
        this.kContext = new FixedKnowledgeCommandContext( null,
                                                          null,
                                                          null,
                                                          this.ksession,
                                                          null );

        this.commandService = new DefaultCommandService(kContext);

        ((AcceptsTimerJobFactoryManager) ((InternalKnowledgeRuntime) ksession).getTimerService()).getTimerJobFactoryManager().setCommandService( this );
    }
    
    public SingleSessionCommandService(Integer sessionId,
                                       KieBase kbase,
                                       KieSessionConfiguration conf,
                                       Environment env) {
        if ( conf == null ) {
            conf = new SessionConfiguration();
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

            txm.commit( transactionOwner );
        } catch (SessionNotFoundException e){
            // do not rollback transaction otherwise it will mark it as aborted
            // making the whole operation to fail  if not transaction owner
            if (transactionOwner) {
                rollbackTransaction( e, transactionOwner );
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

    protected void initExistingKnowledgeSession(Integer sessionId,
                                KieBase kbase,
                                KieSessionConfiguration conf,
                                PersistenceContext persistenceContext) {
        if ( !doRollback && this.ksession != null ) {
            return;
            // nothing to initialise
        }

        this.doRollback = false;
        try {
            this.sessionInfo = persistenceContext.findSessionInfo( sessionId );
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

        this.sessionInfo.setJPASessionMashallingHelper( this.marshallingHelper );

        // The CommandService for the TimerJobFactoryManager must be set before any timer jobs are scheduled. 
        // Otherwise, if overdue jobs are scheduled (and then run before the .commandService field can be set), 
        //  they will retrieve a null commandService (instead of a reference to this) and fail.
        ((SessionConfiguration) conf).getTimerJobFactoryManager().setCommandService(this);

        // if this.ksession is null, it'll create a new one, else it'll use the existing one
        this.ksession = (StatefulKnowledgeSession)
    		this.marshallingHelper.loadSnapshot( this.sessionInfo.getData(),
                                                 this.ksession );

        // update the session id to be the same as the session info id
        ((InternalKnowledgeRuntime) ksession).setId( this.sessionInfo.getId() );

        ((InternalKnowledgeRuntime) this.ksession).setEndOperationListener( new EndOperationListenerImpl( this.sessionInfo ) );

        if ( this.kContext == null ) {
            // this should only happen when this class is first constructed
            this.kContext = new FixedKnowledgeCommandContext( null,
                                                              null,
                                                              null,
                                                              this.ksession,
                                                              null );
        }

        this.commandService = new DefaultCommandService(kContext);
    }

    public void initTransactionManager(Environment env) {
        Object tm = env.get( EnvironmentName.TRANSACTION_MANAGER );
        if ( env.get( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER ) != null &&
             env.get( EnvironmentName.TRANSACTION_MANAGER ) != null ) {
            this.txm = (TransactionManager) tm;
            this.jpm = (PersistenceContextManager) env.get( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER );
        } else {
            if ( tm != null && tm.getClass().getName().startsWith( "org.springframework" ) ) {
                try {
                    Class< ? > cls = Class.forName( "org.kie.spring.persistence.KieSpringTransactionManager" );
                    Constructor< ? > con = cls.getConstructors()[0];
                    this.txm = (TransactionManager) con.newInstance( tm );
                    logger.debug( "Instantiating  KieSpringTransactionManager" );
                    cls = Class.forName( "org.kie.spring.persistence.KieSpringJpaManager" );
                    con = cls.getConstructors()[0];
                    this.jpm = (PersistenceContextManager) con.newInstance( new Object[]{this.env} );
                } catch ( Exception e ) {
                    //fall back for drools5-legacy spring module
                    logger.warn( "Could not instantiate KieSpringTransactionManager. Trying with DroolsSpringTransactionManager." );
                    try {
                        Class< ? > cls = Class.forName( "org.drools.container.spring.beans.persistence.DroolsSpringTransactionManager" );
                        Constructor< ? > con = cls.getConstructors()[0];
                        this.txm = (TransactionManager) con.newInstance( tm );
                        logger.debug( "Instantiating  DroolsSpringTransactionManager" );

                        //                    if ( tm.getClass().getName().toLowerCase().contains( "jpa" ) ) {
                        // configure spring for JPA and local transactions
                        cls = Class.forName( "org.drools.container.spring.beans.persistence.DroolsSpringJpaManager" );
                        con = cls.getConstructors()[0];
                        this.jpm = (PersistenceContextManager) con.newInstance( new Object[]{this.env} );
                        //                    } else {
                        //                        // configure spring for JPA and distributed transactions
                        //                    }
                    } catch ( Exception ex ) {
                        logger.warn( "Could not instantiate DroolsSpringTransactionManager" );
                        throw new RuntimeException( "Could not instatiate org.kie.container.spring.beans.persistence.DroolsSpringTransactionManager", ex );
                    }
                }
            } else {
                logger.debug( "Instantiating  JtaTransactionManager" );
                this.txm = new JtaTransactionManager( env.get( EnvironmentName.TRANSACTION ),
                                                      env.get( EnvironmentName.TRANSACTION_SYNCHRONIZATION_REGISTRY ),
                                                      tm );
                try {
                    Class< ? > jpaPersistenceCtxMngrClass = Class.forName( "org.jbpm.persistence.JpaProcessPersistenceContextManager" );
                    Constructor< ? > jpaPersistenceCtxMngrCtor = jpaPersistenceCtxMngrClass.getConstructors()[0];
                    this.jpm = (PersistenceContextManager) jpaPersistenceCtxMngrCtor.newInstance( new Object[]{this.env} );
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

    public static class EndOperationListenerImpl
        implements
        EndOperationListener {
        private SessionInfo info;

        public EndOperationListenerImpl(SessionInfo info) {
            this.info = info;
        }

        public void endOperation(InternalKnowledgeRuntime kruntime) {
            this.info.setLastModificationDate( new Date( kruntime.getLastIdleTimestamp() ) );
        }
    }

    public Context getContext() {
        return this.kContext;
    }

    public synchronized <T> T execute(Command<T> command) {
        if (command instanceof DisposeCommand) {
            T result = commandService.execute( (GenericCommand<T>) command );
            this.jpm.dispose();
            return result;
        }

        // Open the entity manager before the transaction begins. 
        PersistenceContext persistenceContext = this.jpm.getApplicationScopedPersistenceContext();

        boolean transactionOwner = false;
        try {
            transactionOwner = txm.begin();

            persistenceContext.joinTransaction();
            initExistingKnowledgeSession( this.sessionInfo.getId(),
                          this.marshallingHelper.getKbase(),
                          this.marshallingHelper.getConf(),
                          persistenceContext );

            this.jpm.beginCommandScopedEntityManager();

            registerRollbackSync();

            T result = null;
            if( command instanceof BatchExecutionCommand ) { 
                // Batch execution requires the extra logic in 
                //  StatefulSessionKnowledgeImpl.execute(Context,Command);
                result = ksession.execute(command);
            }
            else { 
                result = commandService.execute( (GenericCommand<T>) command );
            }

            txm.commit( transactionOwner );

            return result;

        } catch ( RuntimeException re ) {
            rollbackTransaction( re,
                                 transactionOwner );
            throw re;
        } catch ( Exception t1 ) {
            rollbackTransaction( t1,
                                 transactionOwner );
            throw new RuntimeException( "Wrapped exception see cause",
                                        t1 );
        } finally {
            if ( command instanceof DisposeCommand ) {
                commandService.execute( (GenericCommand<T>) command );
                this.jpm.dispose();
            }
        }
    }

    private void rollbackTransaction(Exception t1,
                                     boolean transactionOwner) {
        try {
            logger.warn( "Could not commit session",
                          t1 );
            txm.rollback( transactionOwner );
        } catch ( Exception t2 ) {
            logger.error( "Could not rollback",
                          t2 );
            throw new RuntimeException( "Could not commit session or rollback",
                                        t2 );
        }
    }

    public void dispose() {
        if ( ksession != null ) {
            ksession.dispose();
        }
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
            rollbackTransaction( t1, transactionOwner );
            throw new RuntimeException( "Wrapped exception see cause", t1 );
        }
    }

    public int getSessionId() {
        return sessionInfo.getId();
    }

    private void registerRollbackSync() {
        if ( synchronizations.get( this ) == null ) {
            txm.registerTransactionSynchronization( new SynchronizationImpl( this ) );
            synchronizations.put( this,
                                  this );
        }

    }

    private static class SynchronizationImpl
        implements
        TransactionSynchronization {

        SingleSessionCommandService service;

        public SynchronizationImpl(SingleSessionCommandService service) {
            this.service = service;
        }

        public void afterCompletion(int status) {
            if ( status != TransactionManager.STATUS_COMMITTED ) {
                this.service.rollback();
            }

            // always cleanup thread local whatever the result
            Object removedSynchronization = SingleSessionCommandService.synchronizations.remove( this.service );

            this.service.jpm.clearPersistenceContext();
            
            this.service.jpm.endCommandScopedEntityManager();

            KieSession ksession = this.service.ksession;
            // clean up cached process and work item instances
            if ( ksession != null ) {
                InternalProcessRuntime internalProcessRuntime = ((InternalKnowledgeRuntime) ksession).getProcessRuntime();
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

        }

    }

    public KieSession getKieSession() {
        return this.ksession;
    }

    public void addInterceptor(Interceptor interceptor) {
        interceptor.setNext( this.commandService );
        this.commandService = interceptor;
    }

    private void rollback() {
        this.doRollback = true;
    }
}
