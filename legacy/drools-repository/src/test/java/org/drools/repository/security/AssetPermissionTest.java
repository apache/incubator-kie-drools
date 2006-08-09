package org.drools.repository.security;

import org.drools.repository.RuleDef;

import junit.framework.TestCase;

public class AssetPermissionTest extends TestCase {

    public void testBasics() {
        RuleDef rule = new RuleDef("fdsafdsf", "fdsfds");
        AssetPermission perm = new AssetPermission(rule, AssetPermission.READ);
        assertTrue( perm.hasPermission(AssetPermission.READ));
        
        perm.setPermission(perm.getPermission() | AssetPermission.WRITE);
        assertTrue( perm.hasPermission(AssetPermission.READ));
        assertTrue( perm.hasPermission(AssetPermission.WRITE));
    }
    
}
