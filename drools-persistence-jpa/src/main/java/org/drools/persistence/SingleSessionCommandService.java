package org.drools.persistence;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.RuleBase;
import org.drools.SessionConfiguration;
import org.drools.command.Command;
import org.drools.command.Context;
import org.drools.command.impl.ContextImpl;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.command.runtime.DisposeCommand;
import org.drools.common.EndOperationListener;
import org.drools.common.InternalKnowledgeRuntime;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.jpa.JpaJDKTimerService;
import org.drools.persistence.jpa.JpaPersistenceContextManager;
import org.drools.persistence.jpa.processinstance.JPAWorkItemManager;
import org.drools.persistence.jta.JtaTransactionManager;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleSessionCommandService
    implements
    org.drools.command.SingleSessionCommandService {
    
    Logger                               logger                                            = LoggerFactory.getLogger( getClass() );

    private SessionInfo                 sessionInfo;
    private SessionMarshallingHelper marshallingHelper;

    private StatefulKnowledgeSession    ksession;
    private Environment                 env;
    private KnowledgeCommandContext     kContext;

    private TransactionManager          txm;
    private PersistenceContextManager                  jpm;
    
    private volatile boolean  doRollback;
    
    private static Map<Object, Object> synchronizations = Collections.synchronizedMap( new IdentityHashMap<Object, Object>() );
    
    public static Map<Object, Object> txManagerClasses = Collections.synchronizedMap( new IdentityHashMap<Object, Object>() );

    public void checkEnvironment(Environment env) {
        if ( env.get( EnvironmentName.ENTITY_MANAGER_FACTORY ) == null &&
             env.get( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER ) == null  ) {
            throw new IllegalArgumentException( "Environment must have an EntityManagerFactory " +
                                                "or a PersistenceContextManager instance" );
        }

        // @TODO log a warning that all transactions will be locally scoped using the EntityTransaction
        //        if ( env.get( EnvironmentName.TRANSACTION_MANAGER ) == null ) {
        //            throw new IllegalArgumentException( "Environment must have an EntityManagerFactory" );
        //        }
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

    public SingleSessionCommandService(KnowledgeBase kbase,
                                       KnowledgeSessionConfiguration conf,
                                       Environment env) {
        if ( conf == null ) {
            conf = new SessionConfiguration();
        }
        this.env = env;
        
        checkEnvironment( this.env );
        
        this.sessionInfo = new SessionInfo();

        initTransactionManager( this.env );
        
        // create session but bypass command service
        this.ksession = kbase.newStatefulKnowledgeSession(conf, this.env);
        
        this.kContext = new KnowledgeCommandContext( new ContextImpl( "ksession",
                                                                      null ),
                                                     null,
                                                     null,
                                                     this.ksession,
                                                     null );

        ((JpaJDKTimerService) ((InternalKnowledgeRuntime) ksession).getTimerService()).setCommandService( this );
        
        this.marshallingHelper = new SessionMarshallingHelper( this.ksession,
                                                                  conf );
        this.sessionInfo.setJPASessionMashallingHelper( this.marshallingHelper );
        ((InternalKnowledgeRuntime) this.ksession).setEndOperationListener( new EndOperationListenerImpl( this.sessionInfo ) );
        
        // Use the App scoped EntityManager if the user has provided it, and it is open.

        try {
            this.txm.begin();
 
            //this.appScopedEntityManager.joinTransaction();
            registerRollbackSync();

            jpm.getApplicationScopedPersistenceContext().persist( this.sessionInfo );

            this.txm.commit();

        } catch ( Exception t1 ) {
            try {
                this.txm.rollback();
            } catch ( Throwable t2 ) {
                throw new RuntimeException( "Could not commit session or rollback",
                                            t2 );
            }
            throw new RuntimeException( "Could not commit session",
                                        t1 );
        }

        // update the session id to be the same as the session info id
        ((InternalKnowledgeRuntime) ksession).setId( this.sessionInfo.getId() );

    }

    public SingleSessionCommandService(Integer sessionId,
                                       KnowledgeBase kbase,
                                       KnowledgeSessionConfiguration conf,
                                       Environment env) {
        if ( conf == null ) {
            conf = new SessionConfiguration();
        }
                

        this.env = env;
        
        checkEnvironment( this.env );
        
        initTransactionManager( this.env );

        initKsession( sessionId,
                      kbase,
                      conf );
    }

    public void initKsession(Integer sessionId,
                             KnowledgeBase kbase,
                             KnowledgeSessionConfiguration conf) {
        if ( !doRollback && this.ksession != null ) {
            return;
            // nothing to initialise
        }
        
        this.doRollback = false;

        try {
            this.sessionInfo = jpm.getApplicationScopedPersistenceContext().findSessionInfo( sessionId );
        } catch ( Exception e ) {
            throw new RuntimeException( "Could not find session data for id " + sessionId,
                                        e );
        }

        if ( sessionInfo == null ) {
            throw new RuntimeException( "Could not find session data for id " + sessionId );
        }

        if ( this.marshallingHelper == null ) {
            // this should only happen when this class is first constructed
            this.marshallingHelper = new SessionMarshallingHelper( kbase,
                                                                      conf,
                                                                      env );
        }

        this.sessionInfo.setJPASessionMashallingHelper( this.marshallingHelper );

        // if this.ksession is null, it'll create a new one, else it'll use the existing one
        this.ksession = this.marshallingHelper.loadSnapshot( this.sessionInfo.getData(),
                                                             this.ksession );

        // update the session id to be the same as the session info id
        ((InternalKnowledgeRuntime) ksession).setId( this.sessionInfo.getId() );

        ((InternalKnowledgeRuntime) this.ksession).setEndOperationListener( new EndOperationListenerImpl( this.sessionInfo ) );

        ((JpaJDKTimerService) ((InternalKnowledgeRuntime) ksession).getTimerService()).setCommandService( this );
        
        if ( this.kContext == null ) {
            // this should only happen when this class is first constructed
            this.kContext = new KnowledgeCommandContext( new ContextImpl( "ksession",
                                                                          null ),
                                                         null,
                                                         null,
                                                         this.ksession,
                                                         null );
        }

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
                    Class<?> cls = Class.forName( "org.drools.container.spring.beans.persistence.DroolsSpringTransactionManager" );
                    Constructor<?> con = cls.getConstructors()[0];
                    this.txm = (TransactionManager) con.newInstance( tm );
                    logger.debug( "Instantiating  DroolsSpringTransactionManager" );
                    
                    if ( tm.getClass().getName().toLowerCase().contains( "jpa" ) ) {
                        // configure spring for JPA and local transactions
                        cls = Class.forName( "org.drools.container.spring.beans.persistence.DroolsSpringJpaManager" );
                        con = cls.getConstructors()[0];
                        this.jpm =  ( PersistenceContextManager) con.newInstance( new Object[] { this.env } );
                    } else {
                        // configure spring for JPA and distributed transactions 
                    }
                } catch ( Exception e ) {
                    logger.warn( "Could not instatiate DroolsSpringTransactionManager" );
                    throw new RuntimeException( "Could not instatiate org.drools.container.spring.beans.persistence.DroolsSpringTransactionManager", e );
                }
            } else {
                logger.debug( "Instantiating  JtaTransactionManager" );
                this.txm = new JtaTransactionManager( env.get( EnvironmentName.TRANSACTION ),
                                                      env.get( EnvironmentName.TRANSACTION_SYNCHRONIZATION_REGISTRY ),
                                                      tm );
                try {
                    Class<?> jpaPersistenceCtxMngrClass = Class.forName( "org.jbpm.persistence.JpaProcessPersistenceContextManager" );
                    Constructor<?> jpaPersistenceCtxMngrCtor = jpaPersistenceCtxMngrClass.getConstructors()[0];
                    this.jpm =  ( PersistenceContextManager) jpaPersistenceCtxMngrCtor.newInstance( new Object[] { this.env } );
                } catch ( ClassNotFoundException e ) {
                    this.jpm = new JpaPersistenceContextManager(this.env);
                } catch ( Exception e ) {
                    throw new RuntimeException("Error creating JpaProcessPersistenceContextManager", e);
                }
            }
            env.set( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER, this.jpm );
            env.set( EnvironmentName.TRANSACTION_MANAGER, this.txm );
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
        try {
            txm.begin();
            
            initKsession( this.sessionInfo.getId(),
                          this.marshallingHelper.getKbase(),
                          this.marshallingHelper.getConf() );
            
            this.jpm.beginCommandScopedEntityManager();

            //this.appScopedEntityManager.joinTransaction();
            registerRollbackSync();

            T result = ((GenericCommand<T>) command).execute( this.kContext );

            txm.commit();

            return result;

        }catch (RuntimeException re){
            rollbackTransaction(re);
            throw re;
        } catch ( Exception t1 ) {
            rollbackTransaction(t1);
            throw new RuntimeException("Wrapped exception see cause", t1);
        } finally {
            if ( command instanceof DisposeCommand ) {
                this.jpm.dispose();
            }
        }
    }

    private void rollbackTransaction(Exception t1) {
        try {
            logger.error( "Could not commit session", t1 );
            txm.rollback();
        } catch ( Exception t2 ) {
            logger.error( "Could not rollback", t2 );
            throw new RuntimeException( "Could not commit session or rollback", t2 );
        }
    }

    public void dispose() {
        if ( ksession != null ) {
            ksession.dispose();
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
            SingleSessionCommandService.synchronizations.remove( this.service );
            
            this.service.jpm.endCommandScopedEntityManager();

            StatefulKnowledgeSession ksession = this.service.ksession;
            // clean up cached process and work item instances
            if ( ksession != null ) {
                ((InternalKnowledgeRuntime) ksession).getProcessRuntime().clearProcessInstances();
                ((JPAWorkItemManager) ksession.getWorkItemManager()).clearWorkItems();
            }

        }

        public void beforeCompletion() {

        }

    }

    private void rollback() {
        this.doRollback = true;
    }
}
