package org.drools.repository.security;

import java.util.HashSet;
import java.util.Set;

/**
 * A user collection is a loose grouping of users for the purposes of access control lists on
 * resources (ie rules, functions etc).
 * Permissions are assigned to user groups (not to users directly).
 * 
 * User groups have users assigned.
 * (note: in future, user group membership may be provided externally).
 * The group membership may be determined externally to the repo, which means the
 * repo will need to be passed a context of what groups the current user
 * is a member of for the purposes of permissions (as they are all loaded up into
 * the ACLEnforcer object which is used by the interceptor).
 * 
 * These groupings will most likely not correspond to LDAP style groups however. These
 * groups exist for the purpose of setting roles in the repository.
 * 
 * These provide collections of granted permissions, and a set of users for whom these permissions apply.
 * A user may belong to many of these groups, their permissions are the sum of them.
 * 
 * PermissionGroups are not nested, they are simple structures.
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class PermissionGroup {

    private Long id;
    
    private Set userIdentities = new HashSet();
    private Set assetPermissions = new HashSet();
    
    private String name;
    private String description;
    
    public PermissionGroup() {
        
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PermissionGroup(String name) {
        this.name = name;
    }
    
    public Set getUserIdentities() {
        return userIdentities;
    }

    void setUserIdentities(Set userIdentities) {
        this.userIdentities = userIdentities;
    }
    
    /** 
     * Add a users identity to this group.
     * Note that if groups are managed externally, then this will not need to be used.
     * All that is needed is to know what names groups a user belongs to, 
     * and all the relevant permissions can be loaded.
     */
    public void addUserIdentity(String userId) {
        this.userIdentities.add( new RepositoryUser(userId) );
    }
    
    public void addPermission(AssetPermission permission) {
        this.assetPermissions.add(permission);
    }

    public Set getAssetPermissions() {
        return assetPermissions;
    }



    void setAssetPermissions(Set assetPermissions) {
        this.assetPermissions = assetPermissions;
    }



    public Long getId() {
        return id;
    }



    void setId(Long id) {
        this.id = id;
    }



    public String getName() {
        return name;
    }



    void setName(String name) {
        this.name = name;
    }
    
    
    
}
