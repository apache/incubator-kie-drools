package org.drools.repository.security;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This class enforcers ACL rights on an asset, for an individual user
 * who is interacting with the repository.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class ACLEnforcer {

    //this is basically a map of AssetTypes. Each AssetType has a map, keyed on assetId. Each item in this
    //inner map is an AssetPermission object.
    public Map permissionMap;
    
    public ACLEnforcer() {
        permissionMap = new HashMap();
    }
    
    /** 
     * Config it up from a set of AssetPermissions.
     * 
     * @param permissions
     */
    public void setPermissions(Set permissions) {
       for ( Iterator iter = permissions.iterator(); iter.hasNext(); ) {
           AssetPermission perm = (AssetPermission) iter.next();
           Object existingPermMap = permissionMap.get(perm.getAssetType());
           if (existingPermMap != null) {
               Map existing = (Map) existingPermMap;
               if (existing.containsKey(perm.getAssetId())) { //add to existing permission
                   AssetPermission current = (AssetPermission) existing.get(perm.getAssetId());
                   current.setPermission(current.getPermission() | perm.getPermission());
               } else {
                   existing.put(perm.getAssetId(), perm);
               }
           } else {
               Map existing = new HashMap();
               existing.put(perm.getAssetId(), perm);
               permissionMap.put(perm.getAssetType(), existing);
           }
       } 
    }
    
    /**
     * This will check for the positive permission for the given asset.
     */
    public boolean isAllowed(String assetType, Serializable assetId, int permission) {
        Map perms = (Map) permissionMap.get(assetType);
        if (perms == null)  return false;
        
        AssetPermission perm = (AssetPermission) perms.get(assetId);
        if (perm == null)  return false;
        return perm.hasPermission(permission);
    }
    
    /**
     * Checks for a particular DENY* permission has been set. (Negative permission).
     */
    public boolean isDenied(String assetType, Serializable assetId, int permission) {
        return isAllowed(assetType, assetId, permission);
    }

    /** 
     * This will throw an exception if it is not permitted.
     * It will also check for "global" permissiong (ALL_INSTANCES).
     * @param entity The entity being checked, only used to get class name.
     */
    public void checkAllowed(Object entity, Serializable assetId, int permission, String failMessage) {
        String assetType = entity.getClass().getName();
        
        boolean allowed = this.isAllowed(assetType, AssetPermission.ALL_INSTANCES, permission) 
                        || this.isAllowed(assetType, assetId, permission);
        if (!allowed) {
            throw new RepositorySecurityException(failMessage);
        }
        
    }
    
    /**
     * This will throw an exception if it has been explicitly denied.
     * Obviously only applies to negative "DENY_*" permissions.
     * @param entity The entity being checked, only used to get class name.
     */
    public void checkDenied(Object entity, Serializable assetId, int permission, String failMessage) {
        String assetType = entity.getClass().getName();
        boolean denied = this.isDenied(assetType, assetId, permission) ||
                        this.isDenied(assetType, AssetPermission.ALL_INSTANCES, permission);
        if (denied) {
            throw new RepositorySecurityException(failMessage);
        }
    }
    
}
