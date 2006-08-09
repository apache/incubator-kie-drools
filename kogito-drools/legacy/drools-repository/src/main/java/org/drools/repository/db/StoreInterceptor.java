package org.drools.repository.db;

import java.io.Serializable;
import java.security.Principal;
import java.sql.Connection;
import java.util.Date;

import org.drools.repository.Asset;
import org.drools.repository.security.ACLEnforcer;
import org.drools.repository.security.ACLResource;
import org.drools.repository.security.AssetPermission;
import org.hibernate.EmptyInterceptor;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.Type;

/**
 * This event listener processes database events using an interceptor. 
 * 
 * When an asset is updated that requires a history record, a history record will be created by 
 * loading and then copying the old copy of the data. 
 * (using an seperate session that is not related to the current session, but sharing the same connection).
 * 
 * Note that it will also save audit information about whom saved the data.
 * 
 * ACLs are also enforced here.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class StoreInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = -5634072610999632779L;
    
    //use a threadlocal to get the currentConnection, 
    //as we may not always use currentSession semantics.
    private static ThreadLocal currentConnection = new ThreadLocal();

    //we also need the current user if it has been set.
    private static ThreadLocal currentUser = new ThreadLocal();
    
    //for enforcing ACLs
    private static ThreadLocal currentACLEnforcer = new ThreadLocal();
    
    /**
     * Create historical records, and log events.
     */
    public boolean onFlushDirty(Object entity,
                                Serializable id,
                                Object[] currentState,
                                Object[] previousState,
                                String[] propertyNames,
                                Type[] types) {
        
        checkACLAllowed( entity, id, "You are not authorized to save this asset.",
                         AssetPermission.WRITE );
        checkACLDenied( entity, id, "You have been denied authority to save this asset.", 
                         AssetPermission.DENY_WRITE);
        
        if ( entity instanceof ISaveHistory ) {
            handleSaveHistory( entity );
        }
        return handleUserSaveInfo( entity,
                                   currentState,
                                   propertyNames );        
    }

    public boolean onLoad(Object entity,
                          Serializable id,
                          Object[] state,
                          String[] propertyNames,
                          Type[] types)
                    {
        checkACLAllowed(entity, id, "You are not authorized to load this asset.", 
                        AssetPermission.READ);
        checkACLDenied(entity, id, "You have been blocked from loading this asset.", 
                       AssetPermission.DENY_READ);
        return false;
    }
    

    /** record who and when */
    public boolean onSave(Object entity,
                          Serializable id,
                          Object[] currentState,
                          String[] propertyNames,
                          Type[] types) {
        
        return handleUserSaveInfo( entity,
                                   currentState,
                                   propertyNames );
        
    }    
    
    public void onDelete(Object entity,
                         Serializable id,
                         Object[] arg2,
                         String[] arg3,
                         Type[] arg4) {
        checkACLAllowed(entity, id, "You are not authorized to delete this asset.", 
                        AssetPermission.DELETE);
        checkACLDenied(entity, id, "You have been disallowed from deleting this asset.", 
                       AssetPermission.DENY_DELETE);
    }
    
    
    
    /**
     * Check that the ACL allows the user the appropriate permission.
     */
    private void checkACLAllowed(Object entity,
                          Serializable assetId, String failMessage, int permission) {
        if (! (entity instanceof ACLResource)) return;
        ACLEnforcer enforcer = (ACLEnforcer) currentACLEnforcer.get();
        if (enforcer != null) {
            //TODO: make it more flexible in what the "ID" is - may be rulename for instance??
            enforcer.checkAllowed(entity, assetId, permission, failMessage);
        }
    }
    
    /**
     * Check for explicitly denied ACL entries.
     */
    private void checkACLDenied(Object entity,
                          Serializable assetId, String failMessage, int permission) {
        if (! (entity instanceof ACLResource)) return;
        ACLEnforcer enforcer = (ACLEnforcer) currentACLEnforcer.get();
        if (enforcer != null) {
            enforcer.checkDenied(entity, assetId, permission, failMessage);
        }
    }
    
    

    /**
     * This will load up the old copy, and save it as a history record
     * (with a different identity).
     * Filters stop the history records from popping up in unwanted places .
     */
    private void handleSaveHistory(Object entity) {
        ISaveHistory versionable = (ISaveHistory) entity;
        
        Session session = getSessionFactory().openSession( (Connection) currentConnection.get() );
        ISaveHistory prev = (ISaveHistory) session.load( entity.getClass(),
                                                         versionable.getId(),
                                                         LockMode.NONE );
        ISaveHistory copy = (ISaveHistory) prev.copy();
        copy.setHistoricalId( versionable.getId() );
        copy.setHistoricalRecord( true );
        
        session.save( copy );
        
        session.flush();                        
        session.close();
    }
    
    /**
     * Used to set the current session so the interceptor can access it.
     * The idea is to share the same connection that any current transactions 
     * are using.
     */
    public static void setCurrentConnection(Connection conn) {
        currentConnection.set(conn);
    }
    
    /**
     * Set the current user for auditing purposes.
     * This is backed by a threadlocal.
     */
    public static void setCurrentUser(Principal user) {
        currentUser.set(user);
    }

    /** Set the ACL enforcer to enforce access rules for the current user */
    public static void setCurrentACLEnforcer(ACLEnforcer enforcer) {
        currentACLEnforcer.set(enforcer);
    }

    private SessionFactory getSessionFactory() {
        return HibernateUtil.getSessionFactory();
    }



    /** Log who saved, what, when */
    private boolean handleUserSaveInfo(Object entity,
                                       Object[] currentState,
                                       String[] propertyNames) {
        if (entity instanceof Asset) {
            Principal user = (Principal) currentUser.get();
            boolean changed = false;
                
                for ( int i=0; i < propertyNames.length; i++ ) {
                    if ( "lastSavedDate".equals( propertyNames[i] ) ) {
                        currentState[i] = new Date();
                        changed = true;
                    } else if (user != null && "lastSavedByUser".equals( propertyNames[i]) ) {
                        currentState[i] = user.getName();
                        changed = true;
                    }
                }
            return changed;
        } else {
            return false;
        }
        
    }



    
}
