package org.drools.repository.security;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.drools.repository.db.HibernateUtil;
import org.drools.repository.db.RepositoryConfig;
import org.hibernate.Session;

/**
 * Provides an administrative interface for setting security parameters.
 * Services in this interface should only be made available to trusted
 * personnel.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class RepositorySecurityManager {
  
    
    
    private static final String SECURITY_ENABLED = "security.enabled";
    //sessions for the manager are stateful
    private Session session;
    
    /**
     * Creates a new instance with a new session.
     * Once a unit of work has completed, commit() the changes.
     */
    public RepositorySecurityManager() {
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
    }
    
    public void saveGroup(PermissionGroup group) {
        session.merge(group);        
    }

    public PermissionGroup loadGroup(String groupName) {
        //Note, group membership maybe determined outside the repo.
        //in which case, this method should not be used to determine what groups a user
        //belongs to.
        Object result = session.createQuery("from PermissionGroup where name = :name")
            .setString("name", groupName).uniqueResult();
        
        return (PermissionGroup) result;
    }
    
    /** List groups by userId */
    public List listGroups(String userId) {
        List result = session.createQuery(
                "select ug from PermissionGroup as ug join ug.userIdentities as users " +
                "where users.userId = :userId")
                .setString("userId", userId).list();
        
        return result;
    }
    
    /** List all groups */
    public List listGroups() {
        List result = session.createQuery("from PermissionGroup").list();               
        return result;
    }
    
    
    /** Load a super set of all users permissions, based on their group membership */
    public Set loadPermissionsForUser(String userId) {
        Set permissions = new HashSet();
        List groups = listGroups(userId);
        for ( Iterator iter = groups.iterator(); iter.hasNext(); ) {
            PermissionGroup group = (PermissionGroup) iter.next();
            permissions.addAll(group.getAssetPermissions());
        }
        return permissions;
    }
    
    
    /** 
     * Load the permission set for a group. This may be useful if the group
     * and user relationship is outside the repository database.
     */
    public Set loadPermissionsForGroup(String groupName) {
        PermissionGroup group = loadGroup(groupName);
        return group.getAssetPermissions();
    }
    
    
    /** 
     * Loads up the enforcer for the specified user based on the repo database
     * knowledge of what groups they belong to.
     * 
     * If the user belongs to a group with the name "admin" then they 
     * automatically get full rights.
     * 
     * NOTE: THIS WILL BE ACCESSED BY THE NON ADMIN API. MUST BE ALLOWED TO BE ACCESSED AS SUCH.
     */
    public ACLEnforcer getEnforcerForUser(String userId) {

        List groups = listGroups(userId);
        Set permissions = new HashSet();
        for ( Iterator iter = groups.iterator(); iter.hasNext(); ) {
            PermissionGroup group = (PermissionGroup) iter.next();
            if (group.getName().equalsIgnoreCase("admin")) {
                return new SuACLEnforcer();
            }
            permissions.addAll(group.getAssetPermissions());
        }

        ACLEnforcer enforcer = new ACLEnforcer();
        enforcer.setPermissions(permissions);
        return enforcer;
    }
    
    
    public void enableSecurity(boolean on) {
        RepositoryConfig config = new RepositoryConfig();
        config.setKey(SECURITY_ENABLED);
        config.setValue(Boolean.toString(on));
        
        session.merge(config);
    }
    
    public boolean isSecurityEnabled() {
        
        RepositoryConfig config = (RepositoryConfig) session.get(RepositoryConfig.class, SECURITY_ENABLED);
        if (config == null) return false;
        return (new Boolean(config.getValue())).booleanValue();
    }
    
    public void commitAndClose() {
        session.getTransaction().commit();
    }

    /**
     * This will add a permission to the named group.
     * If the group doesn't exist, it will create it.
     * 
     * This can be used in tandem with loadPermissionForGroup
     * to determine permissions when group membership is maintained outside
     * of the repository database (such as in LDAP).
     * 
     */
    public void addPermissionToGroup(String groupName, AssetPermission permission) {
        PermissionGroup group = this.loadGroup(groupName);
        if (group == null) {
            group = new PermissionGroup(groupName);
        }
        group.addPermission(permission);
        this.saveGroup(group);
    }
    
    public void addUserToGroup(String userName, String groupName) {
        PermissionGroup group = this.loadGroup(groupName);
        if (group == null) {
            group = new PermissionGroup(groupName);
        }
         group.addUserIdentity(userName);
        session.merge(group);
    }
    
    /**
     * This is for super users. Lets them get away with anything.
     * 
     * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
     */
    static class SuACLEnforcer extends ACLEnforcer {

        public void checkAllowed(Object entity,
                                 Serializable id,
                                 int permission,
                                 String failMessage) {
            
        }

        public void checkDenied(Object entity,
                                Serializable id,
                                int permission,
                                String failMessage) {
        }

        public boolean isAllowed(String assetType,
                                       Serializable id,
                                       int permission) {
            return true;
        }

        public boolean isDenied(String assetType,
                                Serializable id,
                                int permission) {
            return false;

        }
        
        
        
        
        
    }

}
