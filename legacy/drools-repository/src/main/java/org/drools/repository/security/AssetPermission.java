package org.drools.repository.security;

import java.io.Serializable;
import java.security.acl.Permission;

import org.drools.repository.Asset;


/**
 * This represents permissions on an asset.
 * This is like an ACL Entry.
 * The user group maps this to a named group of users.
 * 
 * Permissions may be on instances, or globally, or to a whole class of assets.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 * TODO: make this work with "partial" rules for matching IDs. ie can be more then just ID.
 * May mean that ACLResource needs to have a method for getting the ID for security purposes.
 */
public class AssetPermission implements Permission {

    private Long id;
    
    public static final int READ = 1;
    public static final int WRITE = 2;
    public static final int DELETE = 4;
    
    public static final int DENY_READ = 8;
    public static final int DENY_WRITE = 16;
    public static final int DENY_DELETE = 32;
    
    /**
     * Use this when you want it to apply to ALL instances.
     */
    public static final Serializable ALL_INSTANCES = new Long(-1);
    
    private String assetType;
    private Serializable assetId;
    private int permission = READ;

    /**
     * 
     * @param assetType A string representing the full classname of the asset to be secured.
     * @param assetId The id of the asset. Can be ALL_INSTANCES to apply to whole class.
     * @param permission The permission (can be allow or deny, as appropriate).
     */
    public AssetPermission(String assetType,
                           Serializable assetId,
                           int permission) {
        
        this.assetType = assetType;
        this.assetId = assetId;
        this.permission = permission;
    }
    
    /**
     * Convenience constructor - reads in the id and class name from the object.
     */
    public AssetPermission(Asset asset, int permission) {
        if (!(asset instanceof ACLResource)) {
            throw new IllegalArgumentException("Asset is not an ACL type resource.");
        }
        this.assetId = asset.getId();
        this.assetType = asset.getClass().getName();
        this.permission = permission;
    }
    
    AssetPermission() {}
    
    public Serializable getAssetId() {
        return assetId;
    }
    public void setAssetId(Serializable assetId) {
        this.assetId = assetId;
    }
    public String getAssetType() {
        return assetType;
    }
    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public int getPermission() {
        return permission;
    }
    public void setPermission(int permission) {
        this.permission = permission;
    }
    
    public Long getId() {
        return id;
    }
    private void setId(Long id) {
        this.id = id;
    }
    
    /** Do bitwise comparison to check permission */
    public boolean hasPermission(int permission) {
        return permission == (this.permission & permission);
    }
    
    
    public String toString() {
        return "AssetPermission [" + assetType + "," + assetId + "," + permission + "]";
    }
    
    
    
    
}
