package org.drools.repository.security;

import java.util.List;
import java.util.Set;

import org.drools.repository.security.RepositorySecurityManager.SuACLEnforcer;

import junit.framework.TestCase;

public class RepositorySecurityManagerTest extends TestCase {

    public void testGroups() {
        RepositorySecurityManager manager = new RepositorySecurityManager();
        
        PermissionGroup group = new PermissionGroup("test1");
        group.addPermission(new AssetPermission("RuleDef", 
                                                new Long(1), 
                                                AssetPermission.READ));
        group.addUserIdentity("michael.neale");
        manager.saveGroup(group);
        
        
        group = new PermissionGroup("test2");
        group.addPermission(new AssetPermission("RuleDef", 
                                                new Long(1), 
                                                AssetPermission.READ));
        group.addUserIdentity("jo.neale");
        manager.saveGroup(group);
        
        group = manager.loadGroup("test1");
        
        List groups = manager.listGroups("michael.neale");
        assertEquals(1, groups.size());
        PermissionGroup g = (PermissionGroup) groups.get(0);
        assertEquals(1, g.getAssetPermissions().size());
        assertTrue(((AssetPermission)g.getAssetPermissions().iterator().next()).hasPermission(AssetPermission.READ));
        
        groups = manager.listGroups("jo.neale");
        assertEquals(1, groups.size());
     
        manager.commitAndClose();
    }
    
    public void testPermissions() {
        RepositorySecurityManager manager = new RepositorySecurityManager();
        
        PermissionGroup group = new PermissionGroup("testPerms");
        group.addPermission(new AssetPermission("RuleDef", 
                                                new Long(1), 
                                                AssetPermission.READ));
        group.addPermission(new AssetPermission("RuleDef", 
                                                new Long(2), 
                                                AssetPermission.READ));
        group.addUserIdentity("test2.user");
        manager.saveGroup(group);
        
        group = new PermissionGroup("test3");
        group.addPermission(new AssetPermission("RuleDef", 
                                                new Long(3), 
                                                AssetPermission.READ));
        group.addPermission(new AssetPermission("RuleDef", 
                                                new Long(4), 
                                                AssetPermission.READ));
        group.addUserIdentity("test2.user");
        manager.saveGroup(group);
        
        Set permissions = manager.loadPermissionsForUser("test2.user");
        assertEquals(4, permissions.size());
        
        
    }
    
    public void testAddPermissionToGroup() {
        RepositorySecurityManager mgr = new RepositorySecurityManager();
        mgr.addPermissionToGroup("newGroup", new AssetPermission("RuleDef", new Long(-1), AssetPermission.READ));
        
        PermissionGroup group = mgr.loadGroup("newGroup");
        assertEquals(1, group.getAssetPermissions().size());
        
        mgr.addPermissionToGroup("newGroup", new AssetPermission("RuleSetDef", new Long(-1), AssetPermission.READ));
        group = mgr.loadGroup("newGroup");
        assertEquals(2, group.getAssetPermissions().size());
        
        Set list = mgr.loadPermissionsForGroup("newGroup");
        assertTrue(list.size() > 0);
    }
    
    public void testEnable() {
        RepositorySecurityManager mgr = new RepositorySecurityManager();
        assertFalse(mgr.isSecurityEnabled());
        mgr.enableSecurity(true);
        assertTrue(mgr.isSecurityEnabled());
        mgr.enableSecurity(false);
        assertFalse(mgr.isSecurityEnabled());
    }
    
    public void testAddToGroup() {
        RepositorySecurityManager mgr = new RepositorySecurityManager();
        mgr.addUserToGroup("new one", "new group");
        
        PermissionGroup group = mgr.loadGroup("new group");
        assertEquals(1, group.getUserIdentities().size());
        
        List groups = mgr.listGroups();
        assertTrue(groups.size() > 0);
    }
    
    public void testSuACL() {
        RepositorySecurityManager mgr = new RepositorySecurityManager();
        mgr.addUserToGroup("superman", "Admin");
        ACLEnforcer enforcer = mgr.getEnforcerForUser("superman");
        assertTrue(enforcer instanceof RepositorySecurityManager.SuACLEnforcer);
        assertTrue(enforcer.isAllowed("anything", new Long(1), AssetPermission.DELETE));
        
        
    }
    
}
