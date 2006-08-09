package org.drools.repository.security;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

public class ACLEnforcerTest extends TestCase {

    public void testACLEnforcement() {
        ACLEnforcer enforcer = new ACLEnforcer();
        
        Set perms = new HashSet();
        perms.add(new AssetPermission("A", new Long(0), AssetPermission.READ));
        perms.add(new AssetPermission("B", new Long(1), AssetPermission.WRITE));
        perms.add(new AssetPermission("B", new Long(2), AssetPermission.DELETE | AssetPermission.READ));
        
        enforcer.setPermissions(perms);
        
        assertTrue(enforcer.isAllowed("A", new Long(0), AssetPermission.READ));
        assertTrue(enforcer.isAllowed("B", new Long(1), AssetPermission.WRITE));
        assertTrue(enforcer.isAllowed("B", new Long(2), AssetPermission.DELETE));
        assertTrue(enforcer.isAllowed("B", new Long(2), AssetPermission.READ));
        
        assertFalse(enforcer.isAllowed("C", new Long(3), AssetPermission.READ));
        assertFalse(enforcer.isAllowed("A", new Long(0), AssetPermission.WRITE));
        assertFalse(enforcer.isAllowed("A", new Long(1), AssetPermission.READ));
    }
    
    public void testACLCheckDeny() {
        ACLEnforcer enforcer = new ACLEnforcer();
        
        Set perms = new HashSet();
        perms.add(new AssetPermission("java.lang.String", new Long(0), AssetPermission.READ));
        perms.add(new AssetPermission("java.lang.String", AssetPermission.ALL_INSTANCES, AssetPermission.DENY_WRITE));
        perms.add(new AssetPermission("java.lang.Integer", AssetPermission.ALL_INSTANCES, AssetPermission.READ | AssetPermission.WRITE));

        enforcer.setPermissions(perms);
        
        //this should be fine, as ALL_INSTANCES have been set
        enforcer.checkAllowed(new Integer(42), new Long(42), AssetPermission.READ, "no fail");
        
        try {
            enforcer.checkDenied("blah", new Long(1), AssetPermission.DENY_WRITE, "something");
            fail("No exception was thrown - should have been as it has been denied for all instances.");
        } catch (RepositorySecurityException e) {
            assertEquals("something", e.getMessage());
        }
        
    }
    
    public void testACLAddToExisting() {
        ACLEnforcer enforcer = new ACLEnforcer();
        
        Set perms = new HashSet();
        perms.add(new AssetPermission("java.lang.String", new Long(0), AssetPermission.READ));
        perms.add(new AssetPermission("java.lang.String", new Long(0), AssetPermission.WRITE));
        enforcer.setPermissions(perms); //will add together above permissions.
        
        assertTrue(enforcer.isAllowed("java.lang.String", new Long(0), AssetPermission.READ));
        assertTrue(enforcer.isAllowed("java.lang.String", new Long(0), AssetPermission.WRITE));
        
        assertFalse(enforcer.isDenied("java.lang.String", new Long(0), AssetPermission.DELETE));
    }
    
    
}
