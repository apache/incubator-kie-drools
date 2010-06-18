package org.drools.persistence.session;

import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.drools.KnowledgeBase;
import org.drools.RuleBase;
import org.drools.SessionConfiguration;
import org.drools.command.Context;
import org.drools.command.impl.ContextImpl;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.common.AbstractWorkingMemory.EndOperationListener;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.persistence.processinstance.JPAProcessInstanceManager;
import org.drools.persistence.processinstance.JPASignalManager;
import org.drools.persistence.processinstance.JPAWorkItemManager;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;

public class SingleSessionCommandService
    implements
    org.drools.command.SingleSessionCommandService {

    private EntityManagerFactory        emf;
    private EntityManager               em;
    private SessionInfo                 sessionInfo;
    private JPASessionMarshallingHelper marshallingHelper;
    //private StatefulSession             session;
    private StatefulKnowledgeSession    ksession;
    private Environment                 env;
    private KnowledgeCommandContext     kContext;

    public void checkEnvironment(Environment env) {        
        if ( env.get( EnvironmentName.ENTITY_MANAGER_FACTORY ) == null ) {
            throw new IllegalArgumentException( "Environment must have an EntityManagerFactory" );
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

    public SingleSessionCommandService(int sessionId,
    		                           RuleBase ruleBase,
		                               SessionConfiguration conf,
		                               Environment env) {
		this( sessionId,
		      new KnowledgeBaseImpl( ruleBase ),
		      conf,
		      env );
	}
    
    public static class EndOperationListenerImpl implements EndOperationListener {
        private SessionInfo info;
        
        public EndOperationListenerImpl(SessionInfo info) {
            this.info = info;
        }
        
        public void endOperation(ReteooWorkingMemory wm) {
            this.info.setLastModificationDate( new Date( wm.getLastIdleTimestamp() ) );
        }
    }

    public SingleSessionCommandService(KnowledgeBase kbase,
                                       KnowledgeSessionConfiguration conf,
                                       Environment env) {
        if ( conf == null ) {
            conf = new SessionConfiguration();
        }
        this.env = env;
        this.sessionInfo = new SessionInfo();        
        
        
        ReteooStatefulSession session = ( ReteooStatefulSession ) ((KnowledgeBaseImpl)kbase).ruleBase.newStatefulSession( (SessionConfiguration) conf, 
                                                                                                 this.env );
        this.ksession = new StatefulKnowledgeSessionImpl( session, kbase );
        
        this.kContext = new KnowledgeCommandContext(new ContextImpl( "ksession", null), null, null, this.ksession, null );

        ((JPASignalManager) ((StatefulKnowledgeSessionImpl)ksession).session.getSignalManager()).setCommandService( this );

        this.marshallingHelper = new JPASessionMarshallingHelper( this.ksession,
                                                                  conf );

        this.sessionInfo.setJPASessionMashallingHelper( this.marshallingHelper );
        
        ((StatefulKnowledgeSessionImpl) this.ksession).session.setEndOperationListener( new EndOperationListenerImpl( this.sessionInfo ) );

        this.emf = (EntityManagerFactory) env.get( EnvironmentName.ENTITY_MANAGER_FACTORY );
        this.em = emf.createEntityManager(); // how can I ensure this is an extended entity?

        boolean localTransaction = false;
        UserTransaction ut = null;
        try {
            InitialContext ctx = new InitialContext();
            ut = (UserTransaction) ctx.lookup( "java:comp/UserTransaction" );
            if ( ut.getStatus() == Status.STATUS_NO_TRANSACTION ) {
                // If there is no transaction then start one, we will commit within the same Command
                ut.begin();
                localTransaction = true;
            }
            registerRollbackSync();
            this.em.joinTransaction();

            this.em.persist( this.sessionInfo );

            if ( localTransaction ) {
                // it's a locally created transaction so commit
                ut.commit();
            }
            
        } catch ( Throwable t1 ) {
            try {
                if ( ut != null ) {
                    ut.rollback();
                }
            } catch ( Throwable t2 ) {
                throw new RuntimeException( "Could not rollback transaction", t2 );
            }
            throw new RuntimeException( "Could not commit session", t1 );
        }
        
        // update the session id to be the same as the session info id
        ((StatefulKnowledgeSessionImpl)ksession).session.setId( this.sessionInfo.getId() );

    }

    public SingleSessionCommandService(int sessionId,
                                       KnowledgeBase kbase,
                                       KnowledgeSessionConfiguration conf,
                                       Environment env) {
        if ( conf == null ) {
            conf = new SessionConfiguration();
        }

        this.env = env;

        this.emf = (EntityManagerFactory) env.get( EnvironmentName.ENTITY_MANAGER_FACTORY );
        this.em = emf.createEntityManager(); // how can I ensure this is an extended entity?
        //System.out.println(((EntityManagerImpl) this.em).getFlushMode());
        
        boolean localTransaction = false;
        UserTransaction ut = null;
        try {
            InitialContext ctx = new InitialContext();
            ut = (UserTransaction) ctx.lookup( "java:comp/UserTransaction" );
            if ( ut.getStatus() == Status.STATUS_NO_TRANSACTION ) {
                // If there is no transaction then start one, we will commit within the same Command
                ut.begin();
                localTransaction = true;
            }
            this.em.joinTransaction();
            registerRollbackSync();
            this.sessionInfo = this.em.find( SessionInfo.class, sessionId );
            
            if ( localTransaction ) {
                // it's a locally created transaction so commit
                ut.commit();
            }
        } catch ( Throwable t1 ) {
            try {
                if ( ut != null ) {
                    ut.rollback();
                }
                throw new RuntimeException( "Could not find session data for id " + sessionId,
                                            t1 );
            } catch ( Throwable t2 ) {
                throw new RuntimeException( "Could not rollback transaction",
                                            t2 );
            }
        }
        
        if (sessionInfo == null) {
        	throw new RuntimeException("Could not find session data for id " + sessionId);
        }

        this.marshallingHelper = new JPASessionMarshallingHelper( this.sessionInfo,
                                                                  kbase,
                                                                  conf,
                                                                  env );

        this.sessionInfo.setJPASessionMashallingHelper( this.marshallingHelper );        
		this.ksession = this.marshallingHelper.getObject();
        ((StatefulKnowledgeSessionImpl) this.ksession).session.setEndOperationListener( new EndOperationListenerImpl( this.sessionInfo ) );
		this.kContext = new KnowledgeCommandContext(new ContextImpl( "ksession", null), null, null, this.ksession, null );
        ((JPASignalManager) ((StatefulKnowledgeSessionImpl)ksession).session.getSignalManager()).setCommandService( this );

        // update the session id to be the same as the session info id
        ((StatefulKnowledgeSessionImpl)ksession).session.setId( this.sessionInfo.getId() );
    }

    public Context getContext() {
        return this.kContext;
    }

    public synchronized <T> T execute(GenericCommand<T> command) {
        boolean localTransaction = false;
        UserTransaction ut = null;
        try {
            ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );

            if ( ut.getStatus() == Status.STATUS_NO_TRANSACTION ) {
                // If there is no transaction then start one, we will commit within the same Command
                ut.begin();
                localTransaction = true;
            }

            EntityManager localEm = (EntityManager) env.get( EnvironmentName.ENTITY_MANAGER );
            if (localEm == null ||  !localEm.isOpen()) {
            	localEm = this.emf.createEntityManager(); // no need to call joinTransaction as it will do so if one already exists
            	this.env.set( EnvironmentName.ENTITY_MANAGER, localEm );
            }
            
            if ( this.em == null ) {
                // there must have been a rollback to lazily re-initialise the state
                this.em = this.emf.createEntityManager();
                this.sessionInfo = this.em.find( SessionInfo.class, this.sessionInfo.getId() );
                ((StatefulKnowledgeSessionImpl) this.ksession).session.setEndOperationListener( new EndOperationListenerImpl( this.sessionInfo ) );
                this.sessionInfo.setJPASessionMashallingHelper( this.marshallingHelper );
                // have to create a new localEM as an EM part of a transaction cannot do a find.
                // this.sessionInfo.rollback();
                this.marshallingHelper.loadSnapshot( this.sessionInfo.getData(),
                                                     this.ksession );                
            }

            this.em.joinTransaction();

            registerRollbackSync();

            T result = command.execute( this.kContext );

            if ( localTransaction ) {
                // it's a locally created transaction so commit
                ut.commit();

                // cleanup local entity manager
                if ( localEm.isOpen() ) {
                    localEm.close();
                }
                this.env.set( EnvironmentName.ENTITY_MANAGER, null );
                
                // clean up cached process and work item instances
                ((JPAProcessInstanceManager) ((StatefulKnowledgeSessionImpl)ksession).session.getProcessInstanceManager()).clearProcessInstances();
                ((JPAWorkItemManager) ((StatefulKnowledgeSessionImpl)ksession).session.getWorkItemManager()).clearWorkItems();
            }
            
            return result;

        } catch ( Throwable t1 ) {
            t1.printStackTrace();
            if ( localTransaction ) {
                try {
                    if ( ut != null ) {
                        ut.rollback();
                    }
                    throw new RuntimeException( "Could not execute command",
                                                t1 );
                } catch ( Throwable t2 ) {
                    throw new RuntimeException( "Could not rollback transaction",
                                                t2 );
                }
            } else {
                throw new RuntimeException( "Could not execute command",
                                            t1 );
            }
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

    @SuppressWarnings("unchecked")
	private void registerRollbackSync() throws IllegalStateException,
                                      RollbackException,
                                      SystemException {
        TransactionManager txm = (TransactionManager) env.get( EnvironmentName.TRANSACTION_MANAGER );
        if ( txm == null ) {
            return;
        }

        Map<Object, Object> map = (Map<Object, Object>) env.get( "synchronizations" );
        if ( map == null ) {
            map = new IdentityHashMap<Object, Object>();
            env.set( "synchronizations",
                     map );
        }

        if ( map.get( this ) == null ) {
            txm.getTransaction().registerSynchronization( new SynchronizationImpl() );
			map.put(this, this);
        }

    }

    private class SynchronizationImpl
        implements
        Synchronization {

        @SuppressWarnings("unchecked")
		public void afterCompletion(int status) {
            if ( status != Status.STATUS_COMMITTED ) {
                rollback();
            }

            // always cleanup thread local whatever the result
            //rollbackRegistered.remove();
            Map<Object, Object> map = (Map<Object, Object>) env.get( "synchronizations" );
            map.remove( SingleSessionCommandService.this );

            // cleanup local entity manager
            EntityManager localEm = (EntityManager) env.get( EnvironmentName.ENTITY_MANAGER );
            if ( localEm != null && localEm.isOpen() ) {
                localEm.close();
            }
            env.set( EnvironmentName.ENTITY_MANAGER, null );
            
            
            
            // clean up cached process and work item instances
            if ( ksession != null ) {
                ((JPAProcessInstanceManager) ((StatefulKnowledgeSessionImpl)ksession).session.getProcessInstanceManager()).clearProcessInstances();
                ((JPAWorkItemManager) ((StatefulKnowledgeSessionImpl)ksession).session.getWorkItemManager()).clearWorkItems();
            }

        }

        public void beforeCompletion() {

        }

    }

    private void rollback() {
        // with em null, if someone tries to use this session it'll first restore it's state
        this.em.close();
        this.em = null;
    }
}