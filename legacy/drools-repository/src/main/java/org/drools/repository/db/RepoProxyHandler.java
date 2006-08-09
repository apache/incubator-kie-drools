package org.drools.repository.db;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.drools.repository.RepositoryException;
import org.drools.repository.RepositoryManagerImpl;
import org.drools.repository.security.ACLEnforcer;
import org.drools.repository.security.RepositorySecurityManager;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Dynamic proxy handler for all persistence operations.
 * Keeps the hibernate session and transaction handling away from the repository implementation.
 * 
 * It will enable the history filter before invoking any methods.
 * 
 * This is the glue between the actual implementation and the interface.
 * 
 * It also provides the stateful and stateless behaviour.
 * Stateful simple means that a new session instance is created the first time, and 
 * kept around (long running sessions).
 * Stateless uses getCurrentSession functionality in hibernate 3.
 * 
 * It can also be extended to provide user context to the implementation class 
 * (for auditing, access control and locking for instance).
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class RepoProxyHandler
    implements
    InvocationHandler {
    

    private RepositoryManagerImpl repoImpl = new RepositoryManagerImpl();
    private Session session = null; //for stateful sessions
    private boolean stateful = false;
    private Principal currentUser; //the user context, for auditing (optional)
    private ACLEnforcer aclEnforcer; //the users ACL rights, if applicable.
    
    /** 
     * This is essentially for stateless repository access.
     * Default implementation uses hibernates getCurrentSession to 
     * work with the current session. 
     */
    public RepoProxyHandler() {
        this(false);
    }
    
    /**
     * Allows stateful access of the repository.
     * @param stateful True if stateful operation is desired.
     */
    public RepoProxyHandler(boolean stateful) {
        this.stateful = stateful;
        if (stateful) {
            this.session = HibernateUtil.getSessionFactory().openSession();
        }
    }
    
    /**
     * This version creates a new session from the given datasource. 
     * In this case, close() will need to be called, ideally.
     */
    public RepoProxyHandler(DataSource datasource) {
        this.stateful = true;
        try {
            this.session = HibernateUtil.getSessionFactory().openSession(datasource.getConnection());
        }
        catch ( SQLException e ) {
            throw new RepositoryException("Unable to get connection from datasource.", e);
        }
    }
    
    /**
     * This will initialise the session to the correct state.
     * Allows both stateless and stateful repository options.
     * 
     * If an exception occurs in the Repo Impl, it will rollback
     * the transaction.
     * If the exception is of type RepositoryException, the session will be left
     * alone.
     * If the exception is of any other type then the session will be closed.
     * 
     */
    public Object invoke(Object proxy,
                         Method method,
                         Object[] args) throws Throwable {

        
        Session session = getCurrentSession();

        //here we implement the "close"
        if (this.stateful && method.getName().equals("close")) {
            handleCloseSession( session );
            return null;
        }
        
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            configure( session, currentUser );
            Object result = method.invoke(repoImpl, args);            
            tx.commit();
            return result;
        }
        catch (InvocationTargetException e) {
            rollback( tx );
            checkForRepositoryException( session, e );
            throw e.getTargetException();
        } 
        finally {
           cleanup( );
        }

    }


    private void cleanup() {
        
        this.repoImpl.injectSession(null);
        StoreInterceptor.setCurrentConnection(null);
        StoreInterceptor.setCurrentUser(null);
        StoreInterceptor.setCurrentACLEnforcer(null);
    }
    
    /**
     * If its an instance of RepositoryException, we don't want to close the session.
     * It may just be a validation message being thrown.
     */
    private void checkForRepositoryException(Session session,
                                             InvocationTargetException e) {
        if (! (e.getTargetException() instanceof RepositoryException)) {
            try { 
               
                session.close(); 
            } catch (Exception e2) { /*ignore*/ }
        }
    }

    /**
     * Should really only be called for stateful repository instances.
     */
    private void handleCloseSession(Session session) {
        session.close();
        StoreInterceptor.setCurrentConnection(null);
        StoreInterceptor.setCurrentUser(null);
    }

    private void rollback(Transaction tx) {
        if (tx != null) {
            tx.rollback();
        }
    }


    /**
     * Set the connection for the listeners to use (they use their own session).
     * Enable the default filters for historical stuff
     * and then provide the session to the repo implementation.
     * Inject the currentUser for auditing etc as well.
     */
    private void configure(Session session, Principal user) {
        StoreInterceptor.setCurrentConnection( session.connection() );
        StoreInterceptor.setCurrentUser( user );
        StoreInterceptor.setCurrentACLEnforcer( aclEnforcer );
        
        repoImpl.enableHistoryFilter( session );
        repoImpl.injectSession( session );           
        this.repoImpl.setCurrentUser(currentUser);        
    }
    

    /**
     * Uses a different session depending on if it is stateful or not.
     */
    private Session getCurrentSession() {
        if (stateful) {
            return session;
        } else {
            return HibernateUtil.getSessionFactory().getCurrentSession();
        }
    }
    
    /** 
     * The current user for auditing and control purposes.
     * Also loads the ACL if applicable. */
    public void setCurrentUser(Principal user) {
        this.currentUser = user;        
        if (user != null) {
            RepositorySecurityManager mgr = new RepositorySecurityManager();
            if (mgr.isSecurityEnabled()) {
                
                aclEnforcer = mgr.getEnforcerForUser(user.getName());
            }
        }        
    }
        

}
