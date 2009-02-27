package org.drools.persistence.session;

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

import org.apache.commons.collections.map.IdentityMap;
import org.drools.KnowledgeBase;
import org.drools.RuleBase;
import org.drools.SessionConfiguration;
import org.drools.StatefulSession;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.persistence.processinstance.JPASignalManager;
import org.drools.process.command.Command;
import org.drools.process.command.CommandService;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;

public class SingleSessionCommandService
    implements
    CommandService {

    private EntityManagerFactory        emf;
    private EntityManager               em;
    private SessionInfo                 sessionInfo;
    private JPASessionMarshallingHelper marshallingHelper;
    private StatefulSession             session;
    private StatefulKnowledgeSession    ksession;
    private Environment                 env;

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
              (SessionConfiguration) conf,
              env );
    }

    public SingleSessionCommandService(KnowledgeBase kbase,
                                       KnowledgeSessionConfiguration conf,
                                       Environment env) {
        if ( conf == null ) {
            conf = new SessionConfiguration();
        }
        this.env = env;
        this.sessionInfo = new SessionInfo();

        this.session = ((KnowledgeBaseImpl) kbase).ruleBase.newStatefulSession( (SessionConfiguration) conf,
                                                                                this.env );
        
        this.ksession = new StatefulKnowledgeSessionImpl( (ReteooWorkingMemory) session );

        ((JPASignalManager) this.session.getSignalManager()).setCommandService( this );

        this.marshallingHelper = new JPASessionMarshallingHelper( this.ksession,
                                                                  conf );

        this.sessionInfo.setJPASessionMashallingHelper( this.marshallingHelper );

        this.emf = (EntityManagerFactory) env.get( EnvironmentName.ENTITY_MANAGER_FACTORY );
        this.em = emf.createEntityManager(); // how can I ensure this is an extended entity?
        //        System.out.println( ((EntityManagerImpl) this.em).getFlushMode() );
        UserTransaction ut = null;
        try {
            InitialContext ctx = new InitialContext();
            ut = (UserTransaction) ctx.lookup( "java:comp/UserTransaction" );
            ut.begin();
            registerRollbackSync();
            this.em.joinTransaction();

            this.em.persist( this.sessionInfo );

            //            System.out.println( "committing" );
            ut.commit();
            //            System.out.println( "commit complete" );
        } catch ( Throwable t1 ) {
            try {
                if ( ut != null ) {
                    ut.rollback();
                }
                throw new RuntimeException( "Could not insert session data",
                                            t1 );
            } catch ( Throwable t2 ) {
                throw new RuntimeException( "Could not rollback transaction",
                                            t2 );
            }
        }
        
        // update the session id to be the same as the session info id
        ((ReteooStatefulSession) this.session).setId( this.sessionInfo.getId() );

        new Thread( new Runnable() {
            public void run() {
                session.fireUntilHalt();
            }
        } );
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
        UserTransaction ut = null;
        try {
            InitialContext ctx = new InitialContext();
            ut = (UserTransaction) ctx.lookup( "java:comp/UserTransaction" );
            ut.begin();
            registerRollbackSync();
            this.em.joinTransaction();

            sessionInfo = this.em.find( SessionInfo.class,
                                        sessionId );

            //	System.out.println("committing");
            ut.commit();
            //	System.out.println("commit complete");
        } catch ( Throwable t1 ) {
            try {
                if ( ut != null ) {
                    ut.rollback();
                }
                throw new RuntimeException( "Could insert session data",
                                            t1 );
            } catch ( Throwable t2 ) {
                throw new RuntimeException( "Could not rollback transaction",
                                            t2 );
            }
        }

        this.session = ((KnowledgeBaseImpl) kbase).ruleBase.newStatefulSession( (SessionConfiguration) conf,
                                                                                this.env );
        
        // update the session id to be the same as the session info id
        ((ReteooStatefulSession) this.session).setId( sessionId );
        
        this.ksession = new StatefulKnowledgeSessionImpl( (ReteooWorkingMemory) session );
        ((JPASignalManager) this.session.getSignalManager()).setCommandService( this );
        
        this.marshallingHelper = new JPASessionMarshallingHelper( this.sessionInfo,
                                                                  kbase,
                                                                  conf,
                                                                  env );


        this.sessionInfo.setJPASessionMashallingHelper( this.marshallingHelper );        
		this.ksession = this.marshallingHelper.getObject();
		this.session = (StatefulSession) ((StatefulKnowledgeSessionImpl) ksession).session;
        ((JPASignalManager) this.session.getSignalManager()).setCommandService( this );

        new Thread( new Runnable() {
            public void run() {
                session.fireUntilHalt();
            }
        } );
    }

    public StatefulSession getSession() {
        return this.session;
    }

    public synchronized <T> T execute(Command<T> command) {
        session.halt();

        boolean localTransaction = false;
        UserTransaction ut = null;
        try {
            ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );

            if ( ut.getStatus() == Status.STATUS_NO_TRANSACTION ) {
                // If there is no transaction then start one, we will commit within the same Command
                ut.begin();
                localTransaction = true;
            }

            EntityManager localEm = this.emf.createEntityManager(); // no need to call joinTransaction as it will do so if one already exists
            this.env.set( EnvironmentName.ENTITY_MANAGER,
                          localEm );
            
            if ( this.em == null ) {
                // there must have been a rollback to lazily re-initialise the state
                this.em = this.emf.createEntityManager();
                this.sessionInfo = this.em.find( SessionInfo.class, this.sessionInfo.getId() );
                this.sessionInfo.setJPASessionMashallingHelper( this.marshallingHelper );
                // have to create a new localEM as an EM part of a transaction cannot do a find.
                // this.sessionInfo.rollback();
                this.marshallingHelper.loadSnapshot( this.sessionInfo.getData(),
                                                     this.ksession );
                this.session = (StatefulSession) ((StatefulKnowledgeSessionImpl) this.ksession).session;                
            }

            this.em.joinTransaction();
            //System.out.println( "1) exec ver : " + this.sessionInfo.getVersion() );
            this.sessionInfo.setDirty();
            //System.out.println( "2) exec ver : " + this.sessionInfo.getVersion() );

            registerRollbackSync();

            T result = command.execute( session );
            //System.out.println( "3) exec ver : " + this.sessionInfo.getVersion() );

            if ( localTransaction ) {
                // it's a locally created transaction so commit
                ut.commit();
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
        } finally {
            new Thread( new Runnable() {
                public void run() {
                    session.fireUntilHalt();
                }
            } );
        }
    }

    public void dispose() {
        if ( session != null ) {
            session.dispose();
        }
    }

    public int getSessionId() {
        return sessionInfo.getId();
    }

    public void registerRollbackSync() throws IllegalStateException,
                                      RollbackException,
                                      SystemException {
        TransactionManager txm = (TransactionManager) env.get( EnvironmentName.TRANSACTION_MANAGER );
        if ( txm == null ) {
            return;
        }

        Map map = (Map) env.get( "synchronizations" );
        if ( map == null ) {
            map = new IdentityMap();
            env.set( "synchronizations",
                     map );
        }

        if ( map.get( this ) == null ) {
            txm.getTransaction().registerSynchronization( new SynchronizationImpl( env,
                                                                                   this ) );
            map.put( this,
                     this );
        }

        //        // lazy registration that ensures we registration the rollback just once
        //        if ( !rollbackRegistered.get() ) {
        //            TransactionManagerServices.getTransactionManager().getTransaction().registerSynchronization( new SynchronizationImpl( rollbackRegistered,
        //                                                                                                                                  ks ) );  
        //            rollbackRegistered.set( true );
        //            System.out.println( "registered rollback sychronisation" );
        //        }
    }

    public static class SynchronizationImpl
        implements
        Synchronization {
        private Environment                 env;
        private SingleSessionCommandService cmdService;

        public SynchronizationImpl(Environment env,
                                   SingleSessionCommandService cmdService) {
            this.env = env;
            this.cmdService = cmdService;
        }

        public void afterCompletion(int status) {
            if ( status != Status.STATUS_COMMITTED ) {
                cmdService.rollback();
                System.out.println( "after with local rollback: " + status );
            }

            // always cleanup thread local whatever the result
            //rollbackRegistered.remove();
            System.out.println( "cleanedup rollback sychronisation" );
            Map map = (Map) env.get( "synchronizations" );
            map.remove( cmdService );

            // cleanup local resource entity manager, normally an EntityManager should be closed with the transaction it was bound to,
            // if it was created inside the scope of the transaction, but adding this anyway just in case.
            EntityManager localEm = (EntityManager) this.env.get( EnvironmentName.ENTITY_MANAGER );
            if ( localEm != null && localEm.isOpen() ) {
                localEm.close();
            }

        }

        public void beforeCompletion() {

        }

    }

    //    public static class SynchronizationImpl
    //    implements
    //    Synchronization {
    //    KnowledgeStore       ks;
    //    ThreadLocal<Boolean> rollbackRegistered;
    //
    //    SynchronizationImpl(ThreadLocal<Boolean> rollbackRegistered,
    //                        KnowledgeStore ks) {
    //        this.ks = ks;
    //        this.rollbackRegistered = rollbackRegistered;
    //    }
    //
    //    public void afterCompletion(int status) {
    //        if ( status == Status.STATUS_COMMITTED) {
    //            ks.commit();
    //            System.out.println( "after with local commit: " + status );
    //        } else {
    //            ks.rollback();
    //            System.out.println( "after with local rollacbk: " + status );
    //        }
    //        
    //        // always cleanup thread local whatever the result
    //        rollbackRegistered.remove();
    //        System.out.println( "cleanedup rollback sychronisation" );
    //    }
    //
    //    public void beforeCompletion() {
    //        System.out.println( "before " );
    //    }
    //}     

    public void rollback() {
        // with em null, if someone tries to use this session it'll first restore it's state
        this.em.close();
        this.em = null;
    }
}